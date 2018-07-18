package com.docmala.plugins.document;

import com.docmala.Error;
import com.docmala.parser.*;
import com.docmala.parser.blocks.Headline;
import com.docmala.parser.blocks.MetaData;
import com.docmala.plugins.IDocumentPlugin;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Iterator;

@Plugin("include")
public class IncludePlugin implements IDocumentPlugin {
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
    public String defaultParameter() {
        return "file";
    }

    @Override
    public void process(SourcePosition start, SourcePosition end, ArrayDeque<Parameter> parameters, DataBlock block, Document document, ISourceProvider sourceProvider) {
        errors.clear();

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

        int lastLevel = 0;

        Iterator it = document.content().descendingIterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof Headline) {
                lastLevel = ((Headline) next).level;
                break;
            }
        }

        try {
            parser.parse(sourceProvider, file.value());
            for (Block documentBlock : parser.document().content()) {
                if (documentBlock instanceof Headline) {
                    Headline headline = (Headline) documentBlock;
                    document.append(new Headline(headline.start, headline.end, headline.anchors, headline.level + lastLevel, headline.content));
                } else if (documentBlock instanceof MetaData) {
                    MetaData meta = (MetaData)documentBlock;
                    if( document.metadata().containsKey(meta.key) ) {
                        MetaData metaData = document.metadata().get(meta.key);
                        // only allow included documents to append data
                        if(metaData.modificationType == MetaData.ModificationType.Append ) {
                            metaData.modify(meta);
                        }
                    }
                } else {
                    document.append(documentBlock);
                }
            }
        } catch (IOException e) {
            errors.addLast(new Error(file.position(), "Unable to open file: '" + file.value() + "'."));
        }

        errors.addAll(parser.errors());
    }

}
