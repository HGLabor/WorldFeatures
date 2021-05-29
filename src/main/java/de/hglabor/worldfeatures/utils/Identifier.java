package de.hglabor.worldfeatures.utils;

public class Identifier {

    private final String namespace;
    private final String key;

    public Identifier(String namespace, String key) {
        this.namespace = namespace;
        this.key = key;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getKey() {
        return key;
    }

    public Identifier(String key) {
        this.namespace = "hglaborsurvival";
        this.key = key;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        } else {
            Identifier anotherIdentifier = (Identifier) obj;
            return anotherIdentifier.getNamespace().equals(getNamespace()) && anotherIdentifier.getKey().equals(getKey());
        }
    }

    @Override
    public String toString() {
        return getNamespace() + ":" + getKey();
    }
}
