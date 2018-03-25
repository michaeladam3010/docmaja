package com.docmala.parser.blocks;

import com.docmala.parser.Anchor;
import com.docmala.parser.Block;
import com.docmala.parser.ICaptionable;
import com.docmala.parser.SourcePosition;

import java.util.ArrayDeque;

public class Image extends Block implements ICaptionable {
    public final byte[] data;
    public final String fileType;
    public final Caption caption;

    public Image(SourcePosition start, SourcePosition end, ArrayDeque<Anchor> anchors, byte[] data, String fileType, Caption caption) {
        super(start, end, anchors);
        this.data = data;
        this.fileType = fileType;
        this.caption = caption;
    }

    @Override
    public Block instanceWithCaption(Caption caption) {
        return new Image(start, end, anchors, data, fileType, new Caption(caption, "Figure"));
    }

    public static class Builder {
        private SourcePosition start;
        private SourcePosition end;
        private ArrayDeque<Anchor> anchors;
        private byte[] data;
        private String fileType;
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

        public Builder setData(byte[] data) {
            this.data = data;
            return this;
        }

        public Builder setFileType(String fileType) {
            this.fileType = fileType;
            return this;
        }

        public Builder setCaption(Caption caption) {
            this.caption = caption;
            return this;
        }

        public Image build() {
            return new Image(start, end, anchors, data, fileType, caption);
        }
    }
}
