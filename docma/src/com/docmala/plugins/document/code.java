package com.docmala.plugins.document;

import com.docmala.Error;
import com.docmala.parser.*;
import com.docmala.parser.blocks.Code;
import com.docmala.plugins.IDocumentPlugin;

import java.util.ArrayDeque;

public class code implements IDocumentPlugin {
    ArrayDeque<Error> errors = new ArrayDeque<>();

    @Override
    public BlockProcessing blockProcessing() {
        return BlockProcessing.Required;
    }

    @Override
    public ArrayDeque<Error> errors() {
        return errors;
    }

    @Override
    public void process(SourcePosition start, SourcePosition end, ArrayDeque<Parameter> parameters, DataBlock block, Document document, ISourceProvider sourceProvider) {
        Code.Builder code = new Code.Builder();
        code.setStart(start);
        code.setEnd(end);

        for (Parameter parameter : parameters) {
            if (parameter.name().equals("type")) {
                code.setType(parameter.value());
                break;
            }
        }

        code.setCode(block.data);
        document.append(code.build());
    }

}
