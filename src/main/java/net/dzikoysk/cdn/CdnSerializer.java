package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.CdnRoot;

final class CdnSerializer {

    private final Cdn cdn;

    CdnSerializer(Cdn cdn) {
        this.cdn = cdn;
    }

    public String serialize(Object entity) {
        Class<?> scheme = entity.getClass();
        CdnRoot root = new CdnRoot();

        return null;
    }

}
