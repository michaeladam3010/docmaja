package com.docmala.parser.blockParsers;

import com.docmala.Error;
import com.docmala.parser.IBlockHolder;
import com.docmala.parser.IBlockParser;
import com.docmala.parser.ISource;
import com.docmala.parser.blocks.NextParagraph;

import java.util.ArrayDeque;

public class NextParagraphParser implements IBlockParser {
    ArrayDeque<Error> errors = new ArrayDeque<>();

    @Override
    public ArrayDeque<Error> errors() {
        return errors;
    }

    @Override
    public boolean tryParse(ISource.Window start, IBlockHolder document) {
        ISource.Window storedStart = start.copy();
        start.skipWhitespaces();
        if (start.here().isBlockEnd()) {
            if (!(document.last() instanceof NextParagraph)) {
                NextParagraph.Builder builder = new NextParagraph.Builder();
                builder.setStart(start.here());
                builder.setEnd(start.here());
                document.append(builder.build());
            }
            start.moveForward();
            return true;
        }
        start.setTo(storedStart);
        return false;
    }
}
