package com.docmala.plugins;

import com.docmala.parser.Document;
import com.docmala.Error;
import com.docmala.parser.ISourceProvider;
import com.docmala.parser.Parameter;

import java.util.ArrayDeque;

public interface IDocumentPlugin {
    enum BlockProcessing { No, Required, Optional };

    BlockProcessing blockProcessing();

    ArrayDeque<Error> errors();

    void process(ArrayDeque<Parameter> parameters, Document document, ISourceProvider sourceProvider);

}
