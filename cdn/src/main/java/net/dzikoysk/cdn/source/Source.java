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

package net.dzikoysk.cdn.source;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public interface Source {

    String getSource();

    static Source empty() {
        return of("");
    }

    static Source of(String source) {
        return new StringSource(source);
    }

    static Source of(File file) {
        return of(file, StandardCharsets.UTF_8);
    }

    static Source of(File file, Charset encoding) {
        return new PathSource(file.getAbsoluteFile().toPath(), encoding);
    }

    static Source of(Path path) {
        return new PathSource(path);
    }

    static Source of(Path path, Charset encoding) {
        return new PathSource(path, encoding);
    }

    static Source of(InputStream inputStream) {
        return of(inputStream, StandardCharsets.UTF_8);
    }

    static Source of(InputStream inputStream, Charset encoding) {
        return new InputStreamSource(inputStream, encoding);
    }

}