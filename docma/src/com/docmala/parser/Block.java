package com.docmala.parser;

import java.util.ArrayDeque;

public abstract class Block {
    Source.Position _start;
    Source.Position _end;
    protected ArrayDeque<Anchor> _anchors = new ArrayDeque<>();
    protected ArrayDeque<ParseError> _errors = new ArrayDeque<>(

    );

    static public class Anchor {
        private Source.Position _position;
        private String _name;

        public Anchor(Anchor other) {
            _position = other.position();
            _name = other.name();
        }

        public Anchor( Source.Position position, String name ) {
            _position = position;
            _name = name;
        }

        public Source.Position position() {
            return _position;
        }

        public String name() {
            return _name;
        }
    }

    static public class ParseError {
        private final Source.Position _position;
        private final String _message;

        public ParseError(Source.Position position, String message) {
            _position = position;
            _message = message;
        }

        public Source.Position position() {
            return _position;
        }

        public String message() {
            return _message;
        }
    }

    public Source.Position start() {
        return _start;
    }

    public Source.Position end() {
        return _end;
    }

    public ArrayDeque<Anchor> anchors() {
        return _anchors;
    }

    public ArrayDeque<ParseError> errors() {
        return _errors;
    }

    public Source.Window parse(Source.Window start) {
        _start = start.here().copy();
        Source.Window w = doParse(start);
        _end = start.here().copy();
        return w;
    }

    public abstract String indicators();

    public abstract Block create();

    protected abstract Source.Window doParse(Source.Window start);
}
