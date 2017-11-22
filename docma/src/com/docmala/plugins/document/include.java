package com.docmala.plugins.document;

import com.docmala.Error;
import com.docmala.parser.*;
import com.docmala.plugins.IDocumentPlugin;

import java.io.IOException;
import java.util.ArrayDeque;

public class include implements IDocumentPlugin {
    ArrayDeque<Error> errors = new ArrayDeque<>();

    @Override
    public BlockProcessing blockProcessing() {
        return BlockProcessing.No;
    }

    @Override
    public ArrayDeque<Error> errors() {
        return errors;
    }

    @Override
    public void process(SourcePosition start, SourcePosition end, ArrayDeque<Parameter> parameters, DataBlock block, Document document, ISourceProvider sourceProvider) {
        Parser parser = new Parser();
        Parameter file = null;

        for (Parameter parameter : parameters) {
            if (parameter.name().equals("file")) {
                file = parameter;
                break;
            }
        }

        if (file == null) {
            errors.addLast(new Error(parameters.getFirst().position(), "Parameter 'file' missing."));
            return;
        }

        try {
            parser.parse(sourceProvider, file.value());
            for (Block documentBlock : parser.document().content()) {
                document.append(documentBlock);
            }
        } catch (IOException e) {
            errors.addLast(new Error(file.position(), "Unable to open file: '" + file.value() + "'."));
        }

        errors.addAll(parser.errors());
        return;
    }

}
