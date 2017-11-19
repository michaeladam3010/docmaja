package com.docmala.parser;

public final class Anchor {
    private final ISource.Position position;
    private final String name;

    public Anchor(Anchor other) {
        position = other.position;
        name = other.name;
    }

    public Anchor(ISource.Position position, String name) {
        this.position = position;
        this.name = name;
    }
}
