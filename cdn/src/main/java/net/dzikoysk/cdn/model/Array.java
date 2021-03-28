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

import net.dzikoysk.cdn.CdnConstants;
import net.dzikoysk.cdn.CdnUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents section of direct values such as e.g. {@link net.dzikoysk.cdn.model.Unit}s
 */
public class Array extends Section {

    public Array(List<? extends String> description, String name) {
        super(description, CdnConstants.ARRAY_SEPARATOR, name);
    }

    public Array(List<? extends String> description, String name, List<? extends Element<?>> values) {
        super(description, CdnConstants.ARRAY_SEPARATOR, name, values);
    }

    public List<String> getList() {
        List<String> values = new ArrayList<>(getValue().size());
        int listOperators = 0;

        for (Element<?> element : getValue()) {
            if (element instanceof Unit) {
                Unit unit = (Unit) element;
                String record = unit.getValue();

                if (record.startsWith(CdnConstants.ARRAY)) {
                    listOperators++;
                }

                if (record.endsWith(CdnConstants.SEPARATOR)) {
                    record = record.substring(0, record.length() - CdnConstants.SEPARATOR.length());
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
