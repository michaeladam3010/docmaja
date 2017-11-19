package com.docmala.plugins;

import com.docmala.Error;
import com.docmala.parser.DataBlock;
import com.docmala.parser.Document;
import com.docmala.parser.ISourceProvider;
import com.docmala.parser.Parameter;

import java.util.ArrayDeque;

public interface IDocumentPlugin {
    BlockProcessing blockProcessing();

    ;

    ArrayDeque<Error> errors();

    void process(ArrayDeque<Parameter> parameters, DataBlock block, Document document, ISourceProvider sourceProvider);

    enum BlockProcessing {No, Required, Optional}

}
