package com.docmala.parser;

public class SourcePosition {
    protected int _position = 0;
    protected int _line = 1;
    protected int _column = 1;
    protected String _fileName = "";

    protected SourcePosition() {
    }

    public SourcePosition(SourcePosition other) {
        _position = other.position();
        _line = other.line();
        _column = other.column();
        _fileName = other.fileName();
    }

    public int position() {
        return _position;
    }

    public SourcePosition fromHere(int offset) {
        SourcePosition p = new SourcePosition(this);
        p._position += offset;
        return p;
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

    public void addToLine(int add) {
        _line += add;
    }

    public void addToColumn(int add) {
        _column += add;
    }
}
