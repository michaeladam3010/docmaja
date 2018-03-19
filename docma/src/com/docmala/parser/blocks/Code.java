package com.docmala.parser.blocks;

import com.docmala.parser.Anchor;
import com.docmala.parser.Block;
import com.docmala.parser.SourcePosition;

import java.util.ArrayDeque;

public class Code extends Block {
    final String code;
    final String type;

    public Code(SourcePosition start, SourcePosition end, ArrayDeque<Anchor> anchors, String code, String type) {
        super(start, end, anchors);
        this.code = code;
        this.type = type;
    }

    public String code() {
        return code;
    }

    public static class Builder {
        private SourcePosition start;
        private SourcePosition end;
        private ArrayDeque<Anchor> anchors;
        private String code;
        private String type;

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

        public Builder setCode(String code) {
            this.code = code;
            return this;
        }
        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Code build() {
            return new Code(start, end, anchors, code, type);
        }

    }
}
