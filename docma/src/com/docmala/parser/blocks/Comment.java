package com.docmala.parser.blocks;

import com.docmala.parser.Anchor;
import com.docmala.parser.Block;
import com.docmala.parser.ISource;
import com.docmala.parser.SourcePosition;

import java.util.ArrayDeque;

public class Comment extends Block {
    String _comment = "";

    public Comment(SourcePosition start, SourcePosition end, ArrayDeque<Anchor> anchors, String comment) {
        super(start, end, anchors);
        this._comment = comment;
    }

    public String comment() {
        return _comment;
    }

    public static class Builder {
        private SourcePosition start;
        private SourcePosition end;
        private ArrayDeque<Anchor> anchors;
        private String comment;

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

        public Builder setComment(String comment) {
            this.comment = comment;
            return this;
        }

        public Comment build() {
            return new Comment(start, end, anchors, comment);
        }
    }
}
