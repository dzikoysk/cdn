/*
 * Copyright (c) 2021 dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dzikoysk.cdn.serdes;

import net.dzikoysk.cdn.CdnException;
import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.CdnUtils;
import net.dzikoysk.cdn.reflect.AnnotatedMember;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.reflect.TargetType;
import panda.std.Option;
import panda.std.Pair;
import panda.std.Result;
import panda.utilities.ObjectUtils;
import java.util.ArrayList;
import java.util.List;

import static panda.std.Option.none;
import static panda.std.Result.ok;

public final class CdnDeserializer<T> {

    private final CdnSettings settings;

    public CdnDeserializer(CdnSettings settings) {
        this.settings = settings;
    }

    public Result<T, ? extends CdnException> deserialize(Section source, Class<T> template) {
        return Result.<T, Exception>attempt(ReflectiveOperationException.class, () -> template.getConstructor().newInstance())
                .flatMap(instance -> deserialize(source, instance))
                .mapErr(CdnException::new);
    }

    public Result<T, ? extends CdnException> deserialize(Section source, T instance) {
        return deserializeToSection(source, false, instance)
                .map(result -> {
                    if (result instanceof DeserializationHandler) {
                        DeserializationHandler<T> handler = ObjectUtils.cast(result);
                        return handler.handle(ObjectUtils.cast(instance));
                    }

                    return result;
                })
                .mapErr(CdnException::new);
    }

    private <I> Result<I, ? extends Exception> deserializeToSection(Section source, boolean immutableParent, I instance) {
        Class<?> type = instance.getClass();
        boolean immutable = immutableParent || CdnUtils.isKotlinDataClass(type);

        List<AnnotatedMember> members = new ArrayList<>();
        members.addAll(settings.getAnnotationResolver().getFields(type));
        members.addAll(settings.getAnnotationResolver().getProperties(type));
        List<Pair<Class<?>, Object>> args = new ArrayList<>();

        for (AnnotatedMember annotatedMember : members) {
            Result<Option<Object>, ? extends Exception> result = deserializeMember(source, immutable, annotatedMember, instance);

            if (result.isErr()) {
                return result.projectToError();
            }

            Object argumentValue = result.get()
                    .orElse(() -> annotatedMember.getValue(instance).orElseThrow(RuntimeException::new))
                    .orNull();

            args.add(new Pair<>(annotatedMember.getType(), argumentValue));
        }

        if (immutable) {
            return Result.attempt(ReflectiveOperationException.class, () -> {
                Class<?>[] argsTypes = args.stream()
                        .map(Pair::getFirst)
                        .toArray(Class[]::new);

                Object[] values = args.stream()
                        .map(Pair::getSecond)
                        .toArray();

                //noinspection unchecked
                return (I) instance.getClass()
                        .getConstructor(argsTypes)
                        .newInstance(values);
            });
        }

        return ok(instance);
    }

    private Result<Option<Object>, ? extends Exception> deserializeMember(Section source, boolean immutable, AnnotatedMember member, Object instance) {
        if (member.isIgnored()) {
            return ok(none());
        }

        Option<Element<?>> elementValue = source.get(member.getName());

        if (elementValue.isEmpty()) {
            return ok(none());
        }

        Element<?> element = elementValue.get();
        Result<Option<Object>, ReflectiveOperationException> defaultValueResult = member.getValue(instance);

        if (defaultValueResult.isErr()) {
            return defaultValueResult.projectToError();
        }

        Option<Object> defaultValue = defaultValueResult.get();

        if (defaultValue.isEmpty()) {
            return ok(none());
        }

        if (member.isAnnotationPresent(Contextual.class)) {
            return deserializeToSection((Section) element, immutable, defaultValue.get()).map(Option::of);
        }

        TargetType targetType = member.getTargetType();

        return CdnUtils.findComposer(settings, targetType, member)
                .flatMap(deserializer -> deserializer.deserialize(settings, element, targetType, defaultValue.get(), false))
                .peek(value -> {
                    if (!immutable && value != Composer.MEMBER_ALREADY_PROCESSED) {
                        member.setValue(instance, value).orElseThrow(IllegalStateException::new);
                    }
                })
                .map(Option::of);
    }

}
