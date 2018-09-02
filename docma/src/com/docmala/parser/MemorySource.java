package com.docmala.parser;

import com.docmala.parser.sourceCodeHandler.SourceCodeHandlerFactory;

public class MemorySource implements ISource {

    ISourceCodeHandler _sourceCodeHandler;
    private String _fileName;
    private String _fileExtension = "";
    private String _fileLabel = "";
    private String _memory;
    private int _lineOffset = 0;
    private int _positionOffset = 0;

    public MemorySource() {
    }

    public MemorySource(String fileName, String memory, SourcePosition start) {
        _lineOffset = start.line();
        _positionOffset = start.position();
        init(fileName, memory);
    }

    public MemorySource(String fileName, String memory) {
        init(fileName, memory);
    }

    public void init(String fileName, String memory) {
        this._memory = memory;

        _fileExtension = ISource.getExtension(fileName);
        _fileLabel = ISource.getLabel(fileName);
        _fileName = ISource.getFileName(fileName);

        _sourceCodeHandler = SourceCodeHandlerFactory.create(_fileExtension);
        _sourceCodeHandler.init(_fileLabel, memory);
    }

    @Override
    public Window begin() {
        Window win = new MemoryWindow();
        return win;
    }

    class MemoryWindow extends Window {
        MemoryPosition _previous;
        MemoryPosition _here = new MemoryPosition();
        MemoryPosition _next = new MemoryPosition();

        public MemoryWindow() {
            _here.moveForward();
            _next.moveForward();
            _next.moveForward();
        }

        public MemoryWindow(MemoryPosition _previous, MemoryPosition _here, MemoryPosition _next) {
            if (_previous == null) {
                this._previous = null;
            } else {
                this._previous = new MemoryPosition(_previous);
            }
            this._here = new MemoryPosition(_here);
            this._next = new MemoryPosition(_next);
        }

        @Override
        public Position here() {
            return _here;
        }

        @Override
        public Position previous() {
            return _previous;
        }

        @Override
        public Position next() {
            return _next;
        }

        @Override
        public void moveForward() {
            _previous = _here.copy();
            _here = _next.copy();
            _next.moveForward();
        }

        @Override
        public Window copy() {
            return new MemoryWindow(_previous, _here, _next);
        }

        @Override
        public void setTo(Window window) {
            if( window.previous() == null ) {
                _previous = null;
            } else {
                _previous = new MemoryPosition((MemoryPosition) window.previous());
            }
            _here = new MemoryPosition((MemoryPosition) window.here());
            _next = new MemoryPosition((MemoryPosition) window.next());
        }
    }

    public class MemoryPosition extends Position {

        MemoryPosition() {
            this._fileName = MemorySource.this._fileName;
            _line += _lineOffset;
            _position = -1;
        }

        @Override
        public int position() {
            return _position + _positionOffset;
        }

        MemoryPosition(MemoryPosition other) {
            _position = other._position;
            _fileName = other._fileName;
            _column = other._column;
            _line = other._line;
        }

        @Override
        public MemoryPosition copy() {
            MemoryPosition mp = new MemoryPosition(this);
            return mp;
        }

        @Override
        public char get() {
            return _position >= 0 && _position < _memory.length() ? _memory.charAt(_position) : '\0';
        }

        @Override
        public boolean isEof() {
            return _position >= _memory.length();
        }

        @Override
        public boolean isEscaped() {
            int pos = 1;
            boolean escaped = false;
            while (_position - pos >= 0 && _memory.charAt(_position - pos) == '\\') {
                escaped = !escaped;
                pos++;
            }
            return escaped;
        }

        public void moveForward() {
            do {
                if (_position >= 0) {
                    if (isEof()) {
                        return;
                    }

                    if (_memory.charAt(_position) == '\r') {
                        _position++;
                    }

                    if (_memory.charAt(_position) == '\n') {
                        _line++;
                        _column = 1;
                    } else {
                        _column++;
                    }
                }

                _position++;


                if (isEof()) {
                    return;
                }
            } while (!_sourceCodeHandler.isPartOfDocumentation(_position, _memory));

            if (_memory.charAt(_position) == '\\') {
                _column++;
                _position++;
            }

            if (isEof()) {
                return;
            }

            if (_memory.charAt(_position) == '.' && (_position == 0 || _memory.charAt(_position - 1) != '\\')) {
                if (_memory.length() > _position + 3 && _memory.charAt(_position + 1) == '.' && _memory.charAt(_position + 2) == '.') {
                    MemoryPosition tst = new MemoryPosition();
                    tst._position = _position + 3;
                    tst._column = _column + 3;
                    tst._line = _line;
                    if (tst.isNewLine()) {
                        tst.moveForward();
                        while (tst.isWhitespace()) {
                            tst.moveForward();
                        }
                        _position = tst._position;
                        _column = tst._column;
                        _line = tst._line;
                    }
                }
            }
        }
    }
}
