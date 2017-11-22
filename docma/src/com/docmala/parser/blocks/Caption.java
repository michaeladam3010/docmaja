package com.docmala.parser.blocks;

import com.docmala.parser.Anchor;
import com.docmala.parser.Block;
import com.docmala.parser.ISource;
import com.docmala.parser.SourcePosition;

import java.util.ArrayDeque;

public class Caption extends Block {
    public final String type;
    public final Block content;

    public Caption(SourcePosition start, SourcePosition end, ArrayDeque<Anchor> anchors, String type, Block content) {
        super(start, end, anchors);
        this.type = type;
        this.content = content;
    }

    public Caption(Caption caption, String defaultType) {
        super(caption.start, caption.end, caption.anchors);
        if (caption.type == null) {
            this.type = defaultType;
        } else {
            this.type = caption.type;
        }
        this.content = caption.content;
    }

    public static class Builder {
        private SourcePosition start;
        private SourcePosition end;
        private ArrayDeque<Anchor> anchors;
        private String type = null;
        private Block content;

        public Builder setStart(SourcePosition start) {
            this.start = new SourcePosition(start);
            return this;
        }

        public Builder setEnd(SourcePosition end) {
            this.end = new SourcePosition(end);
            return this;
        }

        public Builder setAnchors(ArrayDeque<Anchor> anchors) {
            this.anchors = anchors;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setContent(Block content) {
            this.content = content;
            return this;
        }

        public Caption build() {
            return new Caption(start, end, anchors, type, content);
        }
    }


    /*extends Content {

    @Override
    public String indicators() {
        return ".";
    }

    @Override
    public Block create() {
        return new Caption();
    }

    @Override
    public ISource.Window doParse(ISource.Window start, Document document) {
        start.moveForward();
        return super.doParse(start, document);
    }*/
}
