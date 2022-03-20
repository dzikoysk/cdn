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

import java.util.List;

/**
 * Represents key-value relation of {@link java.lang.String} and {@link Piece}
 */
public class Entry extends AbstractNamedElement<Piece> {

    private final String raw;

    public Entry(List<? extends String> description, String raw, String name, Piece value) {
        super(description, name, value);
        this.raw = raw;
    }

    public Entry(List<? extends String> description, String raw, String name, String value) {
        this(description, raw, name, new Piece(value));
    }

    public Entry(List<? extends String> description, String name, Piece value) {
        this(description, name + StandardOperators.OPERATOR + value, name, value);
    }

    public Entry(List<? extends String> description, String name, String value) {
        this(description, name + StandardOperators.OPERATOR + value, name, value);
    }

    /**
     * Get entry as 'key: value' text.
     *
     * @return entry as string
     */
    public String getRecord() {
        return name + ": " + getPieceValue();
    }

    /**
     * Get raw representation of entry
     *
     * @return raw record
     */
    public String getRaw() {
        return raw;
    }

    /**
     * Get unit's value using {@link Piece#getValue()} method.
     *
     * @return the unit's value
     */
    public String getPieceValue() {
        return getValue().getValue();
    }

    @Override
    public String toString() {
        return "Entry { " + getRecord() + " }";
    }

}
