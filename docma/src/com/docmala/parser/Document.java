package com.docmala.parser;

import com.docmala.parser.Block;

import java.util.ArrayDeque;

public class Document implements IBlockHolder {
    ArrayDeque<Block> _content = new ArrayDeque<>();

    public ArrayDeque<Block> content() {
        return _content;
    }

    @Override
    public void append(Block block) {
        _content.addLast(block);
    }
}
