package com.docmala.parser;

public class SourcePosition {
    protected int _line = 1;
    protected int _column = 1;
    protected String _fileName = "";

    protected SourcePosition() {}

    public SourcePosition(SourcePosition other) {
        _line = other.line();
        _column = other.column();
        _fileName = other.fileName();
    }

    public int line() {
        return _line;
    }
    public int column() {
        return _column;
    }
    public String fileName() {
        return _fileName;
    }

    public void addToLine( int add ) {
        _line += add;
    }
    public void addToColumn( int add ) {
        _column += add;
    }
}
