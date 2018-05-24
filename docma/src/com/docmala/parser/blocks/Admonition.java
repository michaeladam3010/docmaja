package com.docmala.parser.blocks;

import com.docmala.parser.Anchor;
import com.docmala.parser.Block;
import com.docmala.parser.FormattedText;
import com.docmala.parser.SourcePosition;

import java.util.ArrayDeque;

public class Admonition extends Block {
    final Type type;
    final ArrayDeque<Block> content;

    public Admonition(SourcePosition start, SourcePosition end, ArrayDeque<Anchor> anchors, ArrayDeque<Block> content, Type type) {
        super(start, end, anchors);
        this.content = content;
        this.type = type;
    }

    public enum Type {
        Note,
        Tip,
        Warning,
        Caution,
        Important,
        Idea,
        Reminder
    }
}
