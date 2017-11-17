package com.docmala.parser;

public final class Parameter {
    final String name;
    final String value;
    final ISource.Position position;

    public Parameter(String name, String value, ISource.Position position) {
        this.name = name;
        this.value = value;
        this.position = position;
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }

    public ISource.Position position() {
        return position;
    }

    public static class Builder {
        private String name;
        private String value;
        private ISource.Position position;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public Builder setPosition(ISource.Position position) {
            this.position = position;
            return this;
        }

        public Parameter build() {
            return new Parameter(name, value, position);
        }
    }
}
