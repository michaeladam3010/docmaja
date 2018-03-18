package com.docmala.parser.blockParsers;

import com.docmala.Error;
import com.docmala.parser.Block;
import com.docmala.parser.IBlockHolder;
import com.docmala.parser.IBlockParser;
import com.docmala.parser.ISource;
import com.docmala.parser.blocks.Headline;

import java.util.ArrayDeque;

public class HeadlineParser implements IBlockParser, IBlockHolder {
    ArrayDeque<Error> errors = new ArrayDeque<>();
    ContentParser contentParser = new ContentParser();
    Headline.Builder headline = new Headline.Builder();

    @Override
    public ArrayDeque<Error> errors() {
        return errors;
    }

    @Override
    public boolean tryParse(ISource.Window start, IBlockHolder document) {
        errors.clear();

        if (start.here().equals('=')) {
            headline = new Headline.Builder();
            headline.setStart(start.here());

            while (start.here().equals('=')) {
                headline.increaseLevel();
                start.moveForward();
            }

            contentParser.tryParse(start, this);

            errors.addAll(contentParser.errors());
            headline.setEnd(start.here());
            document.append(headline.build());
            return true;
        }
        return false;
    }

    @Override
    public void append(Block block) {
        headline.setContent(block);
    }

    @Override
    public Block last() {
        return null;
    }
}
