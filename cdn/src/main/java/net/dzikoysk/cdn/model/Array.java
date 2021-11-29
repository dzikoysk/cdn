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

import net.dzikoysk.cdn.module.standard.StandardOperators;
import net.dzikoysk.cdn.CdnUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents section of direct values such as e.g. {@link Piece}s
 */
public class Array extends Section {

    public Array(List<? extends String> description, String name) {
        super(description, StandardOperators.ARRAY_SEPARATOR, name);
    }

    public Array(List<? extends String> description, String name, List<? extends Element<?>> values) {
        super(description, StandardOperators.ARRAY_SEPARATOR, name, values);
    }

    public List<String> getList() {
        List<String> values = new ArrayList<>(getValue().size());
        int listOperators = 0;

        for (Element<?> element : getValue()) {
            if (element instanceof Piece) {
                Piece piece = (Piece) element;
                String record = piece.getValue();

                if (record.startsWith(StandardOperators.ARRAY)) {
                    listOperators++;
                }

                if (record.endsWith(StandardOperators.SEPARATOR)) {
                    record = record.substring(0, record.length() - StandardOperators.SEPARATOR.length());
                }

                values.add(record);
            }
        }

        for (int index = 0; index < values.size(); index++) {
            String element = values.get(index);

            if (listOperators == values.size()) {
                element = element.substring(1).trim();
            }

            values.set(index, CdnUtils.destringify(element));
        }

        return values;
    }

}
