package com.docmala;

import com.docmala.parser.Block;

import java.util.ArrayDeque;

public class Document {

    ArrayDeque<Block> _content = new ArrayDeque<>();

    public ArrayDeque<Block> content() {
        return _content;
    }

    public void append(Block block) {
        _content.addLast(block);
    }
}
