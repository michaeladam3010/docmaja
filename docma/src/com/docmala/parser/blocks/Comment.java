package com.docmala.parser.blocks;

import com.docmala.parser.Block;
import com.docmala.parser.Source;

public class Comment extends Block {
    String _comment = "";

    @Override
    public String indicators() {
        return ";";
    }

    @Override
    public Block create() {
        return new Comment();
    }

    @Override
    public Source.Window doParse(Source.Window start) {
        // skip ';'
        start.moveForward();
        start.skipWhitspaces();
        while( !start.here().isBlockEnd() ) {
            _comment += start.here().get();
            start.moveForward();
        }
        return start;
    }
}
