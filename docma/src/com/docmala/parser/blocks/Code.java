package com.docmala.parser.blocks;

import com.docmala.parser.Anchor;
import com.docmala.parser.Block;
import com.docmala.parser.ICaptionable;
import com.docmala.parser.SourcePosition;

import java.util.ArrayDeque;

public class Code extends Block implements ICaptionable {
    public final String code;
    public final String type;
    public final Caption caption;

    public Code(SourcePosition start, SourcePosition end, ArrayDeque<Anchor> anchors, String code, String type, Caption caption) {
        super(start, end, anchors);
        this.code = code;
        this.type = type;
        this.caption = caption;
    }

    @Override
    public Block instanceWithCaption(Caption caption) {
        return new Code(start, end, anchors, code, type, new Caption(caption, "listing"));
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

        public Builder setCode(String code) {
            this.code = code;
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
