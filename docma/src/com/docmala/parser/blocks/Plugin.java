package com.docmala.parser.blocks;

import com.docmala.Parameter;
import com.docmala.parser.Block;
import com.docmala.parser.ParameterParser;
import com.docmala.parser.Source;
import com.docmala.Error;

import java.util.ArrayDeque;

public class Plugin extends Block {
    @Override
    public String indicators() {
        return "[";
    }

    @Override
    public Block create() {
        return new Plugin();
    }

    @Override
    protected Source.Window doParse(Source.Window start) {

        final char[] end = {']', ','};

        ParameterParser parameterParser = new ParameterParser();
        ArrayDeque<Parameter> parameters = new ArrayDeque<>();

        while (!start.here().isBlockEnd()) {
            start.skipWhitspaces();

            if( start.here().equals(']') ) {
                break;
            }
            start.moveForward();

            start = parameterParser.parse(start, end);
            parameters.addLast(parameterParser.parameter());
            _errors.addAll(parameterParser.errors());
        }

        if( !start.here().equals(']') ) {
            _errors.addLast(new Error(start.here(), "Expected ']'"));
        }


        return start;
    }
}
