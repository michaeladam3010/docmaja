package com.docmala.plugins.document;

import com.docmala.Error;
import com.docmala.parser.Document;
import com.docmala.parser.ISourceProvider;
import com.docmala.parser.Parameter;
import com.docmala.plugins.IDocumentPlugin;

import java.util.ArrayDeque;

public class formula implements IDocumentPlugin {
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
    public void process(ArrayDeque<Parameter> parameters, String block, Document document, ISourceProvider sourceProvider) {
        System.out.printf("%s%n", block);

    }
}
