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
import net.dzikoysk.cdn.serdes.CdnDeserializer;
import net.dzikoysk.cdn.serdes.CdnSerializer;
import net.dzikoysk.cdn.source.Output;
import net.dzikoysk.cdn.source.Source;
import org.jetbrains.annotations.NotNull;
import panda.std.Result;

/**
 * The main class of Cdn library that expose methods related to process of loading/rendering configurations.
 * You can build an instance using {@link #configure()} method or you can take a look at predefined instances in {@link net.dzikoysk.cdn.CdnFactory}.
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
     * @see net.dzikoysk.cdn.source.Source
     */
    public Result<Configuration, CdnException> load(@NotNull Source source) {
        return new CdnReader(settings).read(source).mapErr(CdnException.identity());
    }

    /**
     * Load configuration from the given source and map default {@link net.dzikoysk.cdn.model.Configuration} structure into the given configuration class.
     *
     * @param source the source to load
     * @param configurationClass the class to use
     * @param <T> the expected type
     * @return an instance of configuration class mapped from {@link net.dzikoysk.cdn.model.Configuration} structure
     */
    public <T> Result<T, CdnException> load(@NotNull Source source, @NotNull Class<T> configurationClass) {
        return load(source).flatMap(entity -> new CdnDeserializer<T>(settings).deserialize(entity, configurationClass));
    }

    /**
     * Load configuration from the given source and map default {@link net.dzikoysk.cdn.model.Configuration} structure into the given configuration class.
     *
     * @param source the source to load
     * @param instance the instance to use
     * @param <T> the expected type
     * @return an instance of configuration class mapped from {@link net.dzikoysk.cdn.model.Configuration} structure
     */
    public <T> Result<T, CdnException> load(@NotNull Source source, @NotNull T instance) {
        return load(source).flatMap(entity -> new CdnDeserializer<T>(settings).deserialize(entity, instance));
    }

    /**
     * Convert the given instance to the output string
     *
     * @param entity the instance to convert
     * @return the rendered output
     */
    public Result<String, CdnException> render(@NotNull Object entity) {
        return new CdnSerializer(settings)
                .serialize(entity)
                .flatMap(this::render);
    }

    /**
     * Convert the given instance using {@link #render(Object)} method and save the output in the given file using UTF-8 charset.
     *
     * @param entity the instance to convert
     * @param output the output file
     */
    public Result<String, CdnException> render(@NotNull Object entity, @NotNull Output output) {
        return render(entity)
                .flatMap(result -> output
                    .save(result)
                    .mapErr(CdnException::new));
    }

    /**
     * Convert the given configuration instance to the output string
     *
     * @param element the element to convert
     * @return the rendered output
     */
    public Result<String, CdnException> render(@NotNull Element<?> element) {
        return new CdnWriter(settings).render(element);
    }

    /**
     * Convert the given instance using {@link #render(net.dzikoysk.cdn.model.Element)} method and save the output in the given file using UTF-8 charset.
     *
     * @param element the element to convert
     * @param output the output file
     */
    public Result<String, CdnException> render(@NotNull Element<?> element, @NotNull Output output) {
        return render(element)
                .flatMap(result -> output
                        .save(result)
                        .mapErr(CdnException::new));
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
