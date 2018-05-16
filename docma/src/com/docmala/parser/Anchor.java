package com.docmala.parser;

public final class Anchor {
    public final SourcePosition position;
    public final String name;

    public Anchor(Anchor other) {
        position = other.position;
        name = other.name;
    }

    public Anchor(SourcePosition position, String name) {
        this.position = position;
        this.name = name;
    }
}
