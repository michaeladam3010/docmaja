package com.docmala.plugins.document;

import com.docmala.parser.*;
import com.docmala.Error;
import com.docmala.plugins.IDocumentPlugin;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayDeque;

public class include implements IDocumentPlugin{
    @Override
    public BlockProcessing blockProcessing() {
        return BlockProcessing.No;
    }

    ArrayDeque<Error> errors = new ArrayDeque<>();

    @Override
    public ArrayDeque<Error> errors() {
        return errors;
    }

    @Override
    public void process(ArrayDeque<Parameter> parameters, Document document, ISourceProvider sourceProvider) {
        Parser parser = new Parser();
        Parameter file = null;

        for( Parameter parameter : parameters ) {
            if( parameter.name().equals("file") ) {
                file = parameter;
                break;
            }
        }

        if( file == null ) {
            errors.addLast(new Error(parameters.getFirst().position(), "Parameter 'file' missing."));
            return;
        }

        try {
            parser.parse( sourceProvider, file.value() );
            for( Block block : parser.document().content() ) {
                document.append(block);
            }
        } catch (IOException e) {
            errors.addLast(new Error(file.position(), "Unable to open file: '" + file.value() + "'."));
        }

        errors.addAll(parser.errors());
        return;
    }

}
