package com.docmala.parser;

import java.util.ArrayDeque;

public abstract class Block {
    public final SourcePosition start;
    public final SourcePosition end;
    public final ArrayDeque<Anchor> anchors;

    public Block(SourcePosition start, SourcePosition end, ArrayDeque<Anchor> anchors) {
        this.start = start;
        this.end = end;
        this.anchors = anchors;
    }
}
