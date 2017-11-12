package com.docmala.parser;

import java.util.ArrayDeque;
import com.docmala.Error;

public abstract class Block {
    Source.Position _start;
    Source.Position _end;
    protected ArrayDeque<Anchor> _anchors = new ArrayDeque<>();
    protected ArrayDeque<Error> _errors = new ArrayDeque<>();

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

    public Source.Position start() {
        return _start;
    }

    public Source.Position end() {
        return _end;
    }

    public ArrayDeque<Anchor> anchors() {
        return _anchors;
    }

    public ArrayDeque<Error> errors() {
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
