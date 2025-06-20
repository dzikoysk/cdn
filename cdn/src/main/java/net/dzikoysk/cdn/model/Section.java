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

package net.dzikoysk.cdn.model;

import net.dzikoysk.cdn.CdnUtils;
import net.dzikoysk.cdn.module.standard.StandardOperators;
import org.jetbrains.annotations.Contract;
import panda.std.Option;
import panda.utilities.ObjectUtils;
import panda.utilities.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Represents a list of configuration elements
 */
public class Section extends AbstractNamedElement<List<? extends Element<?>>> {

    private final String[] operators;

    public Section(List<? extends String> description, String name) {
        this(description, StandardOperators.OBJECT_SEPARATOR, name);
    }

    public Section(List<? extends String> description, String[] operators, String name) {
        this(description, operators, name, new ArrayList<>());
    }

    public Section(List<? extends String> description, String name, List<? extends Element<?>> value) {
        this(description, StandardOperators.OBJECT_SEPARATOR, name, value);
    }

    public Section(List<? extends String> description, String[] operators, String name, List<? extends Element<?>> value) {
        super(description, name, value);
        this.operators = operators;
    }

    public <E extends Element<?>> E append(E element) {
        super.value.add(ObjectUtils.cast(element));
        return element;
    }

    public void setString(String key, String value) {
        getEntry(key)
                .peek(entry -> entry.setValue(new Piece(value)))
                .orElseGet(() -> append(new Entry(Collections.emptyList(), key, value)));
    }

    @Contract("null -> false")
    public boolean has(String key) {
        return get(key).isDefined();
    }

    public int size() {
        return getValue().size();
    }

    private <T> Option<T> get(int index, Class<T> type) {
        return get(index).map(element -> ObjectUtils.cast(type, element));
    }

    public Option<Element<?>> get(int index) {
        return Option.when(index > -1 && index < size(), () -> getValue().get(index));
    }

    private <T> Option<T> get(String key, Class<T> type) {
        return get(key).map(element -> {
            T value = ObjectUtils.cast(type, element);

            if (value == null) {
                throw new IllegalStateException("Property '" + key + "' of type " + element.getClass() + " cannot be queried as " + type);
            }

            return value;
        });
    }

    @Contract("null -> null")
    public Option<Element<?>> get(String key) {
        if (key == null) {
            return Option.none();
        }

        for (Element<?> element : getValue()) {
            if (element instanceof NamedElement) {
                NamedElement<?> namedElement = (NamedElement<?>) element;

                if (key.equals(CdnUtils.destringify(namedElement.getName()))) {
                    return Option.of(element);
                }
            }
        }

        return Option.when(key.contains("."), StringUtils.split(key, "."))
                .flatMap(qualifier -> {
                    Option<Section> section = Option.of(this);

                    for (int index = 0; index < qualifier.length - 1 && section.isDefined(); index++) {
                        int currentIndex = index;
                        section = section.flatMap(value -> value.getSection(qualifier[currentIndex]));
                    }

                    return section.flatMap(value -> value.get(qualifier[qualifier.length - 1]));
                });
    }

    @Contract("_, null -> null")
    public List<String> getList(String key, List<String> defaultValue) {
        return getList(key).orElseGet(() -> defaultValue);
    }

    public Option<List<String>> getList(String key) {
        return getArray(key).map(Array::getList);
    }

    public Option<Boolean> getBoolean(String key) {
        return getString(key).map(Boolean::parseBoolean);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return getBoolean(key).orElseGet(defaultValue);
    }

    public Option<Integer> getInt(String key) {
        return getString(key).map(Integer::parseInt);
    }

    public int getInt(String key, int defaultValue) {
        return getInt(key).orElseGet(defaultValue);
    }

    public Option<String> getString(String key) {
        return getEntry(key)
                .map(Entry::getPieceValue)
                .map(CdnUtils::destringify);
    }

    @Contract("_, null -> null")
    public String getString(String key, String defaultValue) {
        return getString(key).orElseGet(defaultValue);
    }

    public Option<Entry> getEntry(int index) {
        return get(index, Entry.class);
    }

    public Option<Entry> getEntry(String key) {
        return get(key, Entry.class);
    }

    public Option<Array> getArray(int index) {
        return getSection(index).map(Section::toArray);
    }

    public Option<Array> getArray(String key) {
        return getSection(key).map(Section::toArray);
    }

    private Array toArray() {
        return new Array(description, name, value);
    }

    public Option<Section> getSection(int index) {
        return get(index, Section.class);
    }

    public Option<Section> getSection(String key) {
        return get(key, Section.class);
    }

    public String[] getOperators() {
        return operators;
    }

    public static Collector<Element<?>, Section, Section> collector(Supplier<Section> sectionSupplier) {
        return Collector.of(
                sectionSupplier,
                Section::append,
                (left, right) -> {
                    right.getValue().forEach(left::append);
                    return left;
                }
        );
    }

}
