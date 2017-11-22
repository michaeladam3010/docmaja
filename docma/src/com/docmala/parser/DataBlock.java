package com.docmala.parser;

public class DataBlock {
    public final SourcePosition position;
    public final String data;

    public DataBlock(SourcePosition position, String data) {
        this.position = position;
        this.data = data;
    }

    public static class Builder {
        private SourcePosition position;
        private String data;

        public Builder setPosition(SourcePosition position) {
            this.position = position;
            return this;
        }

        public Builder setData(String data) {
            this.data = data;
            return this;
        }

        public DataBlock build() {
            return new DataBlock(position, data);
        }
    }
}
