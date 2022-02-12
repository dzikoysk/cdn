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

package net.dzikoysk.cdn.module.standard;

public class StandardOperators {

    public static final String FAKE_LINE_SEPARATOR = "\\n";

    public static final String LINE_SEPARATOR = "\n";

    public static final String OPERATOR = ":";

    public static final String ARRAY = "-";

    public static final String SEPARATOR = ",";

    public static final String[] STRING_OPERATORS = { "'", "\"", "`" };

    public static final String[] COMMENT_OPERATORS = { "#", "//" };

    public static final String[] OBJECT_SEPARATOR = { "{", "}" };

    public static final String[] ARRAY_SEPARATOR = { "[", "]" };

    private StandardOperators() { }

}
