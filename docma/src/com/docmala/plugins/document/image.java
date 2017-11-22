package com.docmala.plugins.document;

import com.docmala.Error;
import com.docmala.parser.*;
import com.docmala.parser.blocks.Image;
import com.docmala.plugins.IDocumentPlugin;

import java.io.IOException;
import java.util.ArrayDeque;

public class image implements IDocumentPlugin {
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
        Parameter file = null;
        Image.Builder image = new Image.Builder();

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
            image.setData(sourceProvider.getBinary(file.value()));
        } catch (IOException e) {
            errors.addLast(new Error(file.position(), "Unable to open file: '" + file.value() + "'."));
        }

        image.setFileType(file.value().substring(0, file.value().lastIndexOf('.')));
        document.append(image.build());
    }

}
