package com.docmala.parser.blocks;

import com.docmala.parser.Block;
import com.docmala.parser.Source;

public class Caption extends Content {

    @Override
    public String indicators() {
        return ".";
    }

    @Override
    public Block create() {
        return new Caption();
    }

    @Override
    public Source.Window doParse(Source.Window start) {
        start.moveForward();
        return super.doParse(start);
    }
}
