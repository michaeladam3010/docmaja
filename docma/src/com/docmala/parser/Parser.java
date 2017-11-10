package com.docmala.parser;

import com.docmala.parser.blocks.*;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class Parser {
    Map<Character, Block> _blockParsers = new HashMap<>();

    ArrayDeque<Block> _document = new ArrayDeque<>();

    public Parser() {
        addBlockParser(new Comment());
        addBlockParser(new List());
        addBlockParser(new Headline());
    }

    public void addBlockParser( Block block ) {
        String indicators = block.indicators();
        for( char c: indicators.toCharArray()) {
            _blockParsers.put(c, block);
        }
    }

    public void parse(Source source) {
        Source.Window window = source.begin();

        while( !window.here().isEof() ) {

            Block bl = _blockParsers.get(window.here().get());
            if( bl != null ) {
                Block b = bl.create();
                if (b != null) {
                    window = b.parse(window);
                    _document.add(b);
                }
            } else {
                Block b = new Content();
                window = b.parse(window);
                _document.add(b);
            }
            while( window.here().isNewLine() )
                window.moveForward();
        }
    }
}
