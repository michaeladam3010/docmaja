package com.docmala.parser.blocks;

import com.docmala.parser.Block;
import com.docmala.parser.Source;

public class Headline extends Content {

    int _level = 0;

    public int level() {
        return _level;
    }

    @Override
    public String indicators() {
        return "=";
    }

    @Override
    public Block create() {
        return new Headline();
    }

    @Override
    public Source.Window doParse(Source.Window start) {

        while (!start.here().equals('=')) {
            _level++;
            start.moveForward();
        }

        return super.doParse(start);
    }
}
