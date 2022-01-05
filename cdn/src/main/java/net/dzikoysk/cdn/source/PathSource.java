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

import panda.std.Result;
import panda.utilities.FileUtils;
import panda.utilities.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

final class PathSource implements Resource {

    private final Path path;
    private final Charset encoding;

    PathSource(Path path, Charset encoding) {
        if (path == null) {
            throw new IllegalStateException("Path cannot be null");
        }

        if (encoding == null) {
            throw new IllegalStateException("Encoding cannot be null");
        }

        this.path = path;
        this.encoding = encoding;
    }

    PathSource(Path path) {
        this(path, StandardCharsets.UTF_8);
    }

    @Override
    public Result<String, IOException> save(String content) {
        return Result.attempt(IOException.class, () -> {
            int count = path.getNameCount();

            if (count > 1) {
                Files.createDirectories(path.subpath(0, count - 1));
            }

            FileUtils.overrideFile(path.toFile(), content);
            return content;
        });
    }

    @Override
    public String getSource() {
        if (!Files.exists(path)) {
            return StringUtils.EMPTY;
        }

        try {
            return new String(Files.readAllBytes(path), encoding);
        } catch (IOException ioException) {
            throw new IllegalStateException("Cannot read file", ioException);
        }
    }

}
