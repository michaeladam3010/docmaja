package com.docmala.parser.blocks;

import com.docmala.parser.Anchor;
import com.docmala.parser.Block;
import com.docmala.parser.SourcePosition;

import java.util.ArrayDeque;

public class NextParagraph extends Block {
    public NextParagraph(SourcePosition start, SourcePosition end, ArrayDeque<Anchor> anchors) {
        super(start, end, anchors);
    }

    public static class Builder {
        private SourcePosition start;
        private SourcePosition end;
        private ArrayDeque<Anchor> anchors;

        public Builder setStart(SourcePosition start) {
            this.start = start;
            return this;
        }

        public Builder setEnd(SourcePosition end) {
            this.end = end;
            return this;
        }

        public Builder setAnchors(ArrayDeque<Anchor> anchors) {
            this.anchors = anchors;
            return this;
        }

        public NextParagraph build() {
            return new NextParagraph(start, end, anchors);
        }
    }
}
