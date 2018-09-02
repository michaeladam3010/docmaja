package com.docmala.parser;

public final class Parameter {
    final String name;
    final String value;
    final SourcePosition position;
    final SourcePosition valuePosition;

    public Parameter(String name, String value, SourcePosition position, SourcePosition valuePosition) {
        this.name = name;
        this.value = value;
        this.position = position;
        this.valuePosition = valuePosition;
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }

    public SourcePosition position() {
        return position;
    }

    public SourcePosition valuePosition() {
        return valuePosition;
    }

    public static class Builder {
        private String name;
        private String value;
        private SourcePosition position;
        private SourcePosition valuePosition = null;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public Builder setPosition(SourcePosition position) {
            this.position = position;
            return this;
        }

        public Builder setValuePosition(SourcePosition valuePosition) {
            this.valuePosition = valuePosition;
            return this;
        }

        public Parameter build() {
            return new Parameter(name, value, position, valuePosition);
        }
    }
}
