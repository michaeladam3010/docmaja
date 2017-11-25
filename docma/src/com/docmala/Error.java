package com.docmala;

import com.docmala.parser.SourcePosition;

public class Error {
    private final SourcePosition _position;
    private final String _message;

    public Error(SourcePosition position, String message) {
        _position = position;
        _message = message;
    }

    public SourcePosition position() {
        return _position;
    }

    public String message() {
        return _message;
    }
}