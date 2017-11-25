package com.docmala.parser;

public interface ISource {
    public Window begin();

    abstract class Position extends SourcePosition {

        public abstract Position copy();

        public abstract char get();

        public abstract boolean isEof();

        public boolean isWhitespace() {
            char c = get();
            return (c == ' ' || c == '\t');
        }

        public boolean isNewLine() {
            return get() == '\n';
        }

        public boolean isBlockEnd() {
            return isNewLine() || isEof();
        }

        public boolean equals(char c) {
            return get() == c;
        }

        public boolean equals(char[] chars) {
            for (char c : chars) {
                if (get() == c) {
                    return true;
                }
            }
            return false;
        }


    }

    abstract class Window {
        public abstract Position here();

        public abstract Position previous();

        public abstract Position next();

        public abstract void moveForward();

        public void skipWhitspaces() {
            while (here().isWhitespace()) {
                moveForward();
            }
        }

        public boolean equals(char c1, char c2) {
            return here().get() == c1 && next().get() == c2 && previous().get() != '\\';
        }

        public abstract Window copy();

        public abstract void setTo(Window window);
    }
}
