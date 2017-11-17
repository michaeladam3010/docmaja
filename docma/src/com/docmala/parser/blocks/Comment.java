package com.docmala.parser.blocks;

import com.docmala.parser.Anchor;
import com.docmala.parser.Block;
import com.docmala.parser.ISource;

import java.util.ArrayDeque;

public class Comment extends Block {
    String _comment = "";

    public Comment(ISource.Position start, ISource.Position end, ArrayDeque<Anchor> anchors, String comment) {
        super(start, end, anchors);
        this._comment = comment;
    }

    public String comment() {
        return _comment;
    }

    public static class Builder {
        private ISource.Position start;
        private ISource.Position end;
        private ArrayDeque<Anchor> anchors;
        private String comment;

        public Builder setStart(ISource.Position start) {
            this.start = start.copy();
            return this;
        }

        public Builder setEnd(ISource.Position end) {
            this.end = end.copy();
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
