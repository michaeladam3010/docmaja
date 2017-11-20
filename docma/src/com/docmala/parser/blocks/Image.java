package com.docmala.parser.blocks;

import com.docmala.parser.Anchor;
import com.docmala.parser.Block;
import com.docmala.parser.ICaptionable;
import com.docmala.parser.ISource;

import java.util.ArrayDeque;

public class Image extends Block implements ICaptionable {
    public final byte[] data;
    public final String fileType;
    public final Caption caption;

    public Image(ISource.Position start, ISource.Position end, ArrayDeque<Anchor> anchors, byte[] data, String fileType, Caption caption) {
        super(start, end, anchors);
        this.data = data;
        this.fileType = fileType;
        this.caption = caption;
    }

    @Override
    public Block instanceWithCaption(Caption caption) {
        return new Image(start, end, anchors, data, fileType, new Caption(caption, "figure"));
    }

    public static class Builder {
        private ISource.Position start;
        private ISource.Position end;
        private ArrayDeque<Anchor> anchors;
        private byte[] data;
        private String fileType;
        private Caption caption = null;

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
