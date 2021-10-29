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

import net.dzikoysk.cdn.features.DefaultStandardFeature;
import net.dzikoysk.cdn.features.JsonLikeFeature;
import net.dzikoysk.cdn.features.YamlLikeFeature;

/**
 * Factory creates some predefined CDN instances
 */
public final class CdnFactory {

    /**
     * Create standard CDN instance with CDN format
     *
     * @return the standard implementation
     * @see net.dzikoysk.cdn.features.DefaultStandardFeature
     */
    public static Cdn createStandard() {
        return Cdn.configure()
                .installFeature(new DefaultStandardFeature())
                .build();
    }

    /**
     * Create CDN with JSON feature
     *
     * @return the json implementation
     * @see net.dzikoysk.cdn.features.JsonLikeFeature
     */
    public static Cdn createJsonLike() {
        return Cdn.configure()
                .installFeature(new JsonLikeFeature())
                .build();
    }

    /**
     * Create CDN with YAML-like feature.
     * YAML-like configuration uses:
     * <ul>
     *     <li>Indentation based formatting</li>
     *     <li>Colon operator after section names</li>
     *     <li>Dash operator before array entry</li>
     * </ul>
     *
     * @return the yaml-like implementation
     * @see net.dzikoysk.cdn.features.YamlLikeFeature
     */
    public static Cdn createYamlLike() {
        return Cdn.configure()
                .installFeature(new YamlLikeFeature())
                .build();
    }

}
