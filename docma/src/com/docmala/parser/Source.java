package com.docmala.parser;

import java.util.ListIterator;

public interface Source {
    abstract class Position {
        protected int _line = 1;
        protected int _column = 1;
        protected String _fileName = "";

        Position() {}
        public abstract Position copy();

        public abstract char get();

        public int line() {
            return _line;
        }
        public int column() {
            return _column;
        }

        public String fileName() { return _fileName; }
        public abstract boolean isEof();
        public boolean isWhitespace() {
            char c = get();
            return( c == ' ' || c == '\t' );
        }
        public boolean isNewLine() {
            return get() == '\n';
        }
        public boolean isBlockEnd() { return isNewLine() || isEof(); }
        public boolean equals(char c) {
            return get() == c;
        }
    }

    abstract class Window {
        public abstract Position here();
        public abstract Position previous();
        public abstract Position next();
        public abstract void moveForward();
        public void skipWhitspaces() {
            while( here().isWhitespace() ) {
                moveForward();
            }
        }
        public boolean equals(char c1, char c2) {
            return here().get() == c1 && next().get() == c2 && previous().get() != '\\';
        }

        public abstract Window copy();
        public abstract void setTo(Window window);
    }

    public Window begin();
}
