package com.docmala;

import com.docmala.parser.Source;

public class Error {
    private final Source.Position _position;
    private final String _message;

    public Error(Source.Position position, String message) {
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