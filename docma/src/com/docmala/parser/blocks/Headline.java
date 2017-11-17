package com.docmala.parser.blocks;

import com.docmala.parser.Anchor;
import com.docmala.parser.Block;
import com.docmala.parser.ISource;

import java.util.ArrayDeque;

public class Headline extends Block {
    public final int level;
    public final Block content;

    public Headline(ISource.Position start, ISource.Position end, ArrayDeque<Anchor> anchors, int level, Block content) {
        super(start, end, anchors);
        this.level = level;
        this.content = content;
    }

    public static class Builder {
        private ISource.Position start;
        private ISource.Position end;
        private ArrayDeque<Anchor> anchors;
        private int level = 0;
        private Block content;

        public Builder setStart(ISource.Position start) {
            this.start = start;
            return this;
        }

        public Builder setEnd(ISource.Position end) {
            this.end = end;
            return this;
        }

        public Builder setAnchors(ArrayDeque<Anchor> anchors) {
            this.anchors = anchors;
            return this;
        }

        public Builder setLevel(int level) {
            this.level = level;
            return this;
        }

        public Builder increaseLevel() {
            this.level++;
            return this;
        }

        public Builder setContent(Block content) {
            this.content = content;
            return this;
        }

        public Headline build() {
            return new Headline(start, end, anchors, level, content);
        }
    }


/*    @Override
    public ISource.Window doParse(ISource.Window start, Document document) {

        while (start.here().equals('=')) {
            _level++;
            start.moveForward();
        }

        return super.doParse(start, document);
    }*/
}
