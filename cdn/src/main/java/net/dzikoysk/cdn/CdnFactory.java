package net.dzikoysk.cdn;

import net.dzikoysk.cdn.features.DefaultFeature;
import net.dzikoysk.cdn.features.JsonFeature;
import net.dzikoysk.cdn.features.YamlFeature;

public final class CdnFactory {

    public static Cdn createStandard() {
        return Cdn.configure()
                .installFeature(new DefaultFeature())
                .build();
    }

    public static Cdn createJson() {
        return Cdn.configure()
                .installFeature(new JsonFeature())
                .build();
    }

    public static Cdn createYamlLike() {
        return Cdn.configure()
                .installFeature(new YamlFeature())
                .build();
    }

}
