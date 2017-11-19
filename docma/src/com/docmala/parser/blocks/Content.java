package com.docmala.parser.blocks;

import com.docmala.parser.Anchor;
import com.docmala.parser.Block;
import com.docmala.parser.FormattedText;
import com.docmala.parser.ISource;

import java.util.ArrayDeque;

public class Content extends Block {

    final ArrayDeque<FormattedText> content;

    public Content(ISource.Position start, ISource.Position end, ArrayDeque<Anchor> anchors, ArrayDeque<FormattedText> content) {
        super(start, end, anchors);
        this.content = content;
    }

    public ArrayDeque<FormattedText> content() {
        return content;
    }

    public static class Builder {
        private ISource.Position start;
        private ISource.Position end;
        private ArrayDeque<Anchor> anchors = new ArrayDeque<>();
        private ArrayDeque<FormattedText> content = new ArrayDeque<>();

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

        public Builder addAnchor(Anchor anchor) {
            this.anchors.addLast(anchor);
            return this;
        }

        public Builder setContent(ArrayDeque<FormattedText> content) {
            this.content = content;
            return this;
        }

        public Builder addContent(FormattedText content) {
            this.content.addLast(content);
            return this;
        }

        public Content build() {
            return new Content(start, end, anchors, content);
        }
    }

}
