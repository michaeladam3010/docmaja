package com.docmala.plugins.document;

import com.docmala.Error;
import com.docmala.parser.*;
import com.docmala.parser.blockParsers.ContentParser;
import com.docmala.parser.blocks.Admonition;
import com.docmala.plugins.IDocumentPlugin;

import java.io.IOException;
import java.util.ArrayDeque;

public class admonition implements IDocumentPlugin {
    ArrayDeque<Error> errors = new ArrayDeque<>();

    @Override
    public BlockProcessing blockProcessing() {
        return BlockProcessing.Optional;
    }

    @Override
    public ArrayDeque<Error> errors() {
        return errors;
    }

    @Override
    public String defaultParameter() {
        return "text";
    }

    protected String defaultType() {
        return "note";
    }

    @Override
    public void process(SourcePosition start, SourcePosition end, ArrayDeque<Parameter> parameters, DataBlock block, Document document, ISourceProvider sourceProvider) {
        String text = "";
        String type = defaultType();

        if( block != null ) {
            text = block.data;
        }

        for (Parameter parameter : parameters) {
            if (parameter.name().equals("text")) {
                text = parameter.value();
            }
            if (parameter.name().equals("type")) {
                type = parameter.value();
            }
        }

        addToDocument(start, end, document, sourceProvider, type, text);
    }

    protected void addToDocument(SourcePosition start, SourcePosition end, Document document, ISourceProvider sourceProvider, String type, String text ) {
        Admonition.Type typeEnum = Admonition.Type.Note;

        for (Admonition.Type b : Admonition.Type.values()) {
            if (b.name().equalsIgnoreCase(type)) {
                typeEnum = b;
                break;
            }
        }

        Parser parser = new Parser();
        MemorySource memorySource = new MemorySource(start.fileName(), text, start.line() );
        try {
            parser.parse(memorySource, sourceProvider);
            document.append(new Admonition(start, end, new ArrayDeque<Anchor>(), parser.document().content(), typeEnum));
        } catch (IOException e) {
            errors.addLast(new Error(start, "Exception while parsing admonition block: " + e.getMessage()));
        }
    }
}
