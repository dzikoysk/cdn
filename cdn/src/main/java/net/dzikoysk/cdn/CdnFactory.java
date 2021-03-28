package net.dzikoysk.cdn;

import net.dzikoysk.cdn.features.DefaultFeature;
import net.dzikoysk.cdn.features.JsonFeature;
import net.dzikoysk.cdn.features.YamlFeature;

/**
 * Factory creates some predefined CDN instances
 */
public final class CdnFactory {

    /**
     * Create standard CDN instance with CDN format
     *
     * @return the standard implementation
     * @see net.dzikoysk.cdn.features.DefaultFeature
     */
    public static Cdn createStandard() {
        return Cdn.configure()
                .installFeature(new DefaultFeature())
                .build();
    }

    /**
     * Create CDN with JSON feature
     *
     * @return the json implementation
     * @see net.dzikoysk.cdn.features.JsonFeature
     */
    public static Cdn createJson() {
        return Cdn.configure()
                .installFeature(new JsonFeature())
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
     * @see net.dzikoysk.cdn.features.YamlFeature
     */
    public static Cdn createYamlLike() {
        return Cdn.configure()
                .installFeature(new YamlFeature())
                .build();
    }

}
