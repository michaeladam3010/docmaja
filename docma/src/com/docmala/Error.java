package com.docmala;

import com.docmala.parser.ISource;

public class Error {
    private final ISource.Position _position;
    private final String _message;

    public Error(ISource.Position position, String message) {
        _position = position;
        _message = message;
    }

    public ISource.Position position() {
        return _position;
    }

    public String message() {
        return _message;
    }
}