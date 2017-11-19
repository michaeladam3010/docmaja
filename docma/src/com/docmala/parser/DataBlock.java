package com.docmala.parser;

public class DataBlock {
    public final ISource.Position position;
    public final String data;

    public DataBlock(ISource.Position position, String data) {
        this.position = position;
        this.data = data;
    }

    public static class Builder {
        private ISource.Position position;
        private String data;

        public Builder setPosition(ISource.Position position) {
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
