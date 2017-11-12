package com.docmala;

import com.docmala.parser.Source;

public final class Parameter {
    public Parameter(String name, String value, Source.Position position) {
        this._name = name;
        this._value = value;
        this._position = position;
    }

    public String name() {
        return _name;
    }

    public String value() {
        return _value;
    }

    public Source.Position position() {
        return _position;
    }

    String _name;
    String _value;
    Source.Position _position;
}
