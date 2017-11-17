package com.docmala.parser;

import java.util.ArrayDeque;

public abstract class Block {
    public final ISource.Position start;
    public final ISource.Position end;
    public final ArrayDeque<Anchor> anchors;

    public Block(ISource.Position start, ISource.Position end, ArrayDeque<Anchor> anchors) {
        this.start = start;
        this.end = end;
        this.anchors = anchors;
    }
}
