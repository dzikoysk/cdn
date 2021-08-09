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

package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.Element;
import panda.utilities.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * The main class of Cdn library that expose methods related to process of loading/rendering configurations.
 * You can built an instance using {@link #configure()} method or you can take a look at predefined instances in {@link net.dzikoysk.cdn.CdnFactory}.
 */
public final class Cdn {

    private final CdnSettings settings;

    Cdn(CdnSettings settings) {
        this.settings = settings;
    }

    /**
     * Build custom CDN instance.
     * For standard usage like CDN, JSON, YAML-like formats you can use predefined instances available in {@link net.dzikoysk.cdn.CdnFactory} class.
     *
     * @return default instance of settings
     * @see net.dzikoysk.cdn.CdnFactory
     */
    public static CdnSettings configure() {
        return new CdnSettings();
    }

    /**
     * Load configuration as default {@link net.dzikoysk.cdn.model.Configuration} structure.
     *
     * @param source the source to load
     * @return the parsed configuration
     */
    public Configuration load(String source) {
        return new CdnReader(settings).read(source);
    }

    /**
     * Load configuration located in the given file with UTF-8 charset.
     * To load files with other charset, use {@link #load(java.io.File, java.nio.charset.Charset)}.
     *
     * @param file the file to load
     * @return a loaded configuration
     * @throws IOException in case of any IO related error
     */
    public Configuration load(File file) throws IOException {
        return load(file, StandardCharsets.UTF_8);
    }

    /**
     * Load configuration located in the given file with UTF-8 charset.
     * To load files with other charset, use {@link #load(java.io.File, java.nio.charset.Charset)}.
     *
     * @param file the file to load
     * @return a loaded configuration
     * @throws IOException in case of any IO related error
     */
    public Configuration load(Path file) throws IOException {
        return load(file.toFile(), StandardCharsets.UTF_8);
    }

    /**
     * Load configuration located in the given file with the provided charset.
     *
     * @param file the file to load
     * @param charset the charset to use
     * @return a loaded configuration
     * @throws IOException in case of any IO related error
     */
    public Configuration load(File file, Charset charset) throws IOException {
        return load(CdnUtils.readFile(file, charset));
    }

    /**
     * Load configuration located in the given file with the provided charset.
     *
     * @param file the file to load
     * @param charset the charset to use
     * @return a loaded configuration
     * @throws IOException in case of any IO related error
     */
    public Configuration load(Path file, Charset charset) throws IOException {
        return load(CdnUtils.readFile(file.toFile(), charset));
    }

    /**
     * Load configuration from the given source and map default {@link net.dzikoysk.cdn.model.Configuration} structure into the given configuration class.
     *
     * @param source the source to load
     * @param configurationClass the class to use
     * @param <T> the expected type
     * @return an instance of configuration class mapped from {@link net.dzikoysk.cdn.model.Configuration} structure
     * @throws Exception in case of any deserialization error
     */
    public <T> T load(String source, Class<T> configurationClass) throws Exception {
        return new CdnDeserializer<T>(settings).deserialize(configurationClass, load(source));
    }

    /**
     * Load configuration located in the given file as the given class using UTF-8 charset.
     * To load files with other charset, use {@link #load(java.io.File, java.nio.charset.Charset, Class)}
     *
     * @param file the file to load
     * @param configurationClass the class to use
     * @param <T> the expected type
     * @return an instance of configuration class
     * @throws Exception in case of any deserialization/IO error
     */
    public <T> T load(File file, Class<T> configurationClass) throws Exception {
        return load(file, StandardCharsets.UTF_8, configurationClass);
    }

    /**
     * Load configuration located in the given file as the given class using UTF-8 charset.
     * To load files with other charset, use {@link #load(java.io.File, java.nio.charset.Charset, Class)}
     *
     * @param file the file to load
     * @param configurationClass the class to use
     * @param <T> the expected type
     * @return an instance of configuration class
     * @throws Exception in case of any deserialization/IO error
     */
    public <T> T load(Path file, Class<T> configurationClass) throws Exception {
        return load(file.toFile(), StandardCharsets.UTF_8, configurationClass);
    }

    /**
     * Load configuration located in the given file as the given class using the given charset
     *
     * @param file the file to load
     * @param configurationClass the class to use
     * @param <T> the expected type
     * @return an instance of configuration class
     * @throws Exception in case of any deserialization/IO error
     */
    public <T> T load(File file, Charset charset, Class<T> configurationClass) throws Exception {
        return load(CdnUtils.readFile(file, charset), configurationClass);
    }

    /**
     * Load configuration located in the given file as the given class using the given charset
     *
     * @param file the file to load
     * @param configurationClass the class to use
     * @param <T> the expected type
     * @return an instance of configuration class
     * @throws Exception in case of any deserialization/IO error
     */
    public <T> T load(Path file, Charset charset, Class<T> configurationClass) throws Exception {
        return load(CdnUtils.readFile(file.toFile(), charset), configurationClass);
    }

    /**
     * Convert the given instance to the output string
     *
     * @param entity the instance to convert
     * @return the rendered output
     */
    public String render(Object entity) {
        return render(new CdnSerializer(settings).serialize(entity));
    }

    /**
     * Convert the given instance using {@link #render(Object)} method and save the output in the given file using UTF-8 charset.
     *
     * @param entity the instance to convert
     * @param output the output file
     */
    public void render(Object entity, File output) throws IOException {
        FileUtils.overrideFile(output, render(entity));
    }

    /**
     * Convert the given instance using {@link #render(Object)} method and save the output in the given file using UTF-8 charset.
     *
     * @param entity the instance to convert
     * @param output the output file
     */
    public void render(Object entity, Path output) throws IOException {
        FileUtils.overrideFile(output.toFile(), render(entity));
    }

    /**
     * Convert the given configuration instance to the output string
     *
     * @param element the element to convert
     * @return the rendered output
     */
    public String render(Element<?> element) {
        return new CdnWriter(settings).render(element);
    }

    /**
     * Convert the given instance using {@link #render(net.dzikoysk.cdn.model.Element)} method and save the output in the given file using UTF-8 charset.
     *
     * @param element the element to convert
     * @param output the output file
     */
    public void render(Element<?> element, File output) throws IOException {
        FileUtils.overrideFile(output, render(element));
    }

    /**
     * Convert the given instance using {@link #render(net.dzikoysk.cdn.model.Element)} method and save the output in the given file using UTF-8 charset.
     *
     * @param element the element to convert
     * @param output the output file
     */
    public void render(Element<?> element, Path output) throws IOException {
        FileUtils.overrideFile(output.toFile(), render(element));
    }

    /**
     * Get settings used by this CDN instance
     *
     * @return settings used by this instance
     */
    public CdnSettings getSettings() {
        return settings;
    }

}
