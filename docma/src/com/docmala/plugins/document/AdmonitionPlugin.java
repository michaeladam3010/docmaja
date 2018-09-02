package com.docmala.plugins.document;

import com.docmala.Error;
import com.docmala.parser.*;
import com.docmala.plugins.IDocumentPlugin;

import java.io.IOException;
import java.util.ArrayDeque;

@Plugin(value = "admonition", defaultParameters = "type=note")
@Plugin(value = "caution", defaultParameters = "type=caution")
@Plugin(value = "note", defaultParameters = "type=note")
@Plugin(value = "idea", defaultParameters = "type=idea")
@Plugin(value = "important", defaultParameters = "type=important")
@Plugin(value = "tip", defaultParameters = "type=tip")
@Plugin(value = "warning", defaultParameters = "type=warning")
@Plugin(value = "reminder", defaultParameters = "type=reminder")
public class AdmonitionPlugin implements IDocumentPlugin {
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

    @Override
    public void process(SourcePosition start, SourcePosition end, ArrayDeque<Parameter> parameters, DataBlock block, Document document, ISourceProvider sourceProvider) {
        String text = "";
        String type = "";
        SourcePosition textStart = null;

        if( block != null ) {
            text = block.data;
            textStart = block.position;
        }

        for (Parameter parameter : parameters) {
            if (parameter.name().equals("text")) {
                text = parameter.value();
                textStart = parameter.valuePosition();
            }
            if (parameter.name().equals("type")) {
                type = parameter.value();
            }
        }

        if( textStart != null )
            addToDocument(start, start.fromHere(type.length()), textStart, document, sourceProvider, type, text);
    }

    protected void addToDocument(SourcePosition start, SourcePosition end, SourcePosition blockStart, Document document, ISourceProvider sourceProvider, String type, String text ) {
        com.docmala.parser.blocks.Admonition.Type typeEnum = com.docmala.parser.blocks.Admonition.Type.Note;

        for (com.docmala.parser.blocks.Admonition.Type b : com.docmala.parser.blocks.Admonition.Type.values()) {
            if (b.name().equalsIgnoreCase(type)) {
                typeEnum = b;
                break;
            }
        }

        Parser parser = new Parser();
        MemorySource memorySource = new MemorySource(start.fileName(), text, blockStart );
        try {
            parser.parse(memorySource, sourceProvider);
            document.append(new com.docmala.parser.blocks.Admonition(start, end, new ArrayDeque<>(), parser.document().content(), typeEnum));
        } catch (IOException e) {
            errors.addLast(new Error(start, "Exception while parsing Admonition block: " + e.getMessage()));
        }
    }
}
