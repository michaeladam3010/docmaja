package com.docmala.parser.blockParsers;

import com.docmala.Error;
import com.docmala.parser.Block;
import com.docmala.parser.IBlockHolder;
import com.docmala.parser.IBlockParser;
import com.docmala.parser.ISource;
import com.docmala.parser.blocks.Caption;

import java.util.ArrayDeque;

public class CaptionParser implements IBlockParser, IBlockHolder {
    ArrayDeque<Error> errors = new ArrayDeque<>();
    ContentParser contentParser = new ContentParser();
    Caption.Builder caption = new Caption.Builder();

    @Override
    public ArrayDeque<Error> errors() {
        return errors;
    }

    @Override
    public boolean tryParse(ISource.Window start, IBlockHolder document) {
        errors.clear();

        if (start.here().equals('.')) {
            start.moveForward();
            caption = new Caption.Builder();
            caption.setStart(start.here());

            start.skipWhitspaces();
            if (start.here().equals('[') && !start.next().equals('[')) {
                start.moveForward();
                StringBuilder type = new StringBuilder();
                while (!start.here().isBlockEnd() && !start.here().equals(']')) {
                    type.append(start.here().get());
                    start.moveForward();
                }
                if (start.here().equals(']')) {
                    caption.setType(type.toString().trim());
                    start.moveForward();
                } else {
                    errors.addLast(new Error(start.here(), "Caption type definition (\"[<type>]\") was not closed."));
                }
            }

            contentParser.tryParse(start, this);

            errors.addAll(contentParser.errors());
            caption.setEnd(start.here());
            document.append(caption.build());
            return true;
        }
        return false;
    }

    @Override
    public void append(Block block) {
        caption.setContent(block);
    }

    @Override
    public Block last() {
        return null;
    }
}
