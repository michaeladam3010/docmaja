package com.docmala.parser;

public interface ISource {
    public Window begin();

    static String getExtension(String fileName) {
        int startIndex = fileName.lastIndexOf(".");
        if( startIndex > 0 ) {
            int endIndex = fileName.lastIndexOf(":");
            if( endIndex > 0 ) {
                return fileName.substring(startIndex, endIndex);
            }
            return fileName.substring(startIndex);
        }
        return "";
    }
    static String getLabel(String fileName) {
        int startIndex = fileName.lastIndexOf(".");
        if( startIndex > 0 ) {
            int endIndex = fileName.lastIndexOf(":");
            if( endIndex > 0 ) {
                return fileName.substring(endIndex+1);
            }
        }
        return "";
    }

    static String getFileName(String fileName) {
        int startIndex = fileName.lastIndexOf(".");
        if( startIndex > 0 ) {
            int endIndex = fileName.lastIndexOf(":");
            if( endIndex > 0 ) {
                return fileName.substring(0, endIndex);
            }
            return fileName;
        }
        return "";
    }

    abstract class Position extends SourcePosition {

        public abstract Position copy();

        public abstract char get();

        public abstract boolean isEof();

        public abstract boolean isEscaped();

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
            return next() != null &&
                    here().get() == c1 && !here().isEscaped() &&
                    next().get() == c2 && !next().isEscaped();
            //return here().get() == c1 && next() != null && next().get() == c2 && (previous() == null || previous().get() != '\\');
        }

        public abstract Window copy();

        public abstract void setTo(Window window);
    }
}
