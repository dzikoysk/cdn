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
import net.dzikoysk.cdn.annotation.AnnotatedMember;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Section;
import panda.std.Option;
import panda.std.Result;
import panda.std.Unit;
import panda.utilities.ObjectUtils;
import java.util.ArrayList;
import java.util.List;

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
        return deserializeToSection(source, instance)
                .map(result -> {
                    if (result instanceof DeserializationHandler) {
                        DeserializationHandler<T> handler = ObjectUtils.cast(result);
                        return handler.handle(ObjectUtils.cast(instance));
                    }

                    return result;
                })
                .mapErr(CdnException::new);
    }

    private <I> Result<I, ? extends Exception> deserializeToSection(Section source, I instance) {
        List<AnnotatedMember> members = new ArrayList<>();
        members.addAll(settings.getAnnotationResolver().getFields(instance));
        members.addAll(settings.getAnnotationResolver().getProperties(instance));

        for (AnnotatedMember annotatedMember : members) {
            Result<Unit, ? extends Exception> result = deserializeMember(source, annotatedMember);

            if (result.isErr()) {
                return result.projectToError();
            }
        }

        return ok(instance);
    }

    private Result<Unit, ? extends Exception> deserializeMember(Section source, AnnotatedMember member) {
        if (member.isIgnored()) {
            return ok();
        }

        Option<Element<?>> elementValue = source.get(member.getName());

        if (elementValue.isEmpty()) {
            return ok();
        }

        Element<?> element = elementValue.get();
        Result<Option<Object>, ReflectiveOperationException> defaultValueResult = member.getValue();

        if (defaultValueResult.isErr()) {
            return defaultValueResult.projectToError();
        }

        Object defaultValue = defaultValueResult.get().get(); // TODO: Sheeesh

        if (member.isAnnotationPresent(Contextual.class)) {
            return deserializeToSection((Section) element, defaultValue).mapToUnit();
        }

        return CdnUtils.findComposer(settings, member.getType(), member.getAnnotatedType(), member)
                .flatMap(deserializer -> deserializer.deserialize(settings, element, member.getAnnotatedType(), defaultValue, false))
                .peek(value -> {
                    if (value != Composer.MEMBER_ALREADY_PROCESSED) {
                        member.setValue(value);
                    }
                })
                .mapToUnit();
    }

}
