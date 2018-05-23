package com.docmala.parser.blocks;

import com.docmala.parser.*;

import java.util.ArrayDeque;

public class Code extends Content implements ICaptionable {
    public final String type;
    public final Caption caption;

    public Code(SourcePosition start, SourcePosition end, ArrayDeque<Anchor> anchors, ArrayDeque<FormattedText> code, String type, Caption caption) {
        super(start, end, anchors, code);
        this.type = type;
        this.caption = caption;
    }

    @Override
    public Block instanceWithCaption(Caption caption) {
        return new Code(start, end, anchors, content, type, new Caption(caption, "listing"));
    }

    public static class Builder {
        private SourcePosition start;
        private SourcePosition end;
        private ArrayDeque<Anchor> anchors;
        private ArrayDeque<FormattedText> code = new ArrayDeque<>();
        private String type;
        private Caption caption = null;

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

        public Builder addCode(FormattedText code) {
            this.code.addLast(code);
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setCaption(Caption caption) {
            this.caption = caption;
            return this;
        }

        public Code build() {
            return new Code(start, end, anchors, code, type, caption);
        }

    }
}
