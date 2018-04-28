package com.docmala.plugins.document;

import com.docmala.Error;
import com.docmala.parser.*;
import com.docmala.parser.blocks.Code;
import com.docmala.parser.blocks.Content;
import com.docmala.parser.blocks.Table;
import com.docmala.plugins.IDocumentPlugin;

import java.io.IOException;
import java.util.ArrayDeque;

public class csvtable implements IDocumentPlugin {
    ArrayDeque<Error> errors = new ArrayDeque<>();

    @Override
    public BlockProcessing blockProcessing() {
        return BlockProcessing.Optional;
    }

    @Override
    public ArrayDeque<Error> errors() {
        return errors;
    }

    Table.Cell buildCell(String value, SourcePosition start, SourcePosition end) {
        ArrayDeque<Block> blocks = new ArrayDeque<>();
        Content.Builder content = new Content.Builder();
        FormattedText.Builder text = new FormattedText.Builder();
        text.setText(value.toString());

        content.setStart(start);
        content.setEnd(end);

        ArrayDeque<FormattedText> contentArray = new ArrayDeque<>();
        contentArray.addLast(text.build());

        content.setContent(contentArray);
        blocks.addLast(content.build());
        return new Table.Cell(blocks, 1, 1, false, false);
    }

    void parse(ISource source, Table.Builder builder) {
        ISource.Window start = source.begin();
        char separator = ',';

        StringBuilder value = new StringBuilder();
        ArrayDeque<ArrayDeque<Table.Cell>> lines = new ArrayDeque<>();
        ISource.Position begin = start.here().copy();
        int longestLineLength = 0;

        while (!start.here().isEof()) {
            ArrayDeque<Table.Cell> line = new ArrayDeque<>();
            while (!start.here().isBlockEnd()) {
                if (start.here().equals(separator) ) {
                    line.addLast(buildCell(value.toString(), begin, start.previous()));
                    begin = start.next().copy();
                    value = new StringBuilder();
                    start.moveForward();
                    start.skipWhitspaces();
                } else {
                    value.append(start.here().get());
                    start.moveForward();
                }
            }
            line.addLast(buildCell(value.toString(), begin, start.previous()));
            if( line.size() > longestLineLength ) {
                longestLineLength = line.size();
            }
            value = new StringBuilder();
            lines.addLast(line);
            start.moveForward();
            start.skipWhitspaces();
            begin = start.next().copy();
        }

        Table.Cell[][] cells = new Table.Cell[lines.size()][longestLineLength];

        int i = 0;
        int j = 0;
        for( ArrayDeque<Table.Cell> l : lines ) {
            for( Table.Cell c : l ) {
                cells[i][j] = c;
                j++;
            }
            j = 0;
            i++;
        }
        builder.setCells(cells);
    }

    @Override
    public void process(SourcePosition start, SourcePosition end, ArrayDeque<Parameter> parameters, DataBlock block, Document document, ISourceProvider sourceProvider) {
        Table.Builder builder = new Table.Builder();
        builder.setStart(start);
        builder.setEnd(end);

        Parameter file = null;

        for (Parameter parameter : parameters) {
            if (parameter.name().equals("file")) {
                file = parameter;
                break;
            }
        }

        if( file == null && block == null ) {
            errors.addLast(new Error(start, "Data block ('--' ... '--') or 'file' parameter required"));
            return;
        } else if( file == null && block == null ) {
            errors.addLast(new Error(start, "Data block ('--' ... '--') and 'file' parameter can't be set at the same time"));
            return;
        }

            if( block != null ) {
            MemorySource source = new MemorySource(start.fileName(), block.data, start.line()+1);
            parse(source, builder);
        }
        else if( file != null ) {

            ISource source = null;
            try {
                source = sourceProvider.get(file.value());
            } catch (IOException e) {
                errors.addLast(new Error(start, "Unable to open file: " + file.value()));
            }
            parse(source, builder);
        }

        document.append(builder.build());
    }

}
