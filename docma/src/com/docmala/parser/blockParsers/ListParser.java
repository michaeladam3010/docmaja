package com.docmala.parser.blockParsers;

import com.docmala.Error;
import com.docmala.parser.Block;
import com.docmala.parser.IBlockHolder;
import com.docmala.parser.IBlockParser;
import com.docmala.parser.ISource;
import com.docmala.parser.blocks.List;

import java.util.ArrayDeque;

public class ListParser implements IBlockParser, IBlockHolder {
    ArrayDeque<Error> errors = new ArrayDeque<>();
    ContentParser contentParser = new ContentParser();
    List.Builder list = new List.Builder();
    List.Builder subList;

    @Override
    public void append(Block block) {
        subList.setContent(block);
        list.addAnchors(block.anchors);
    }

    @Override
    public ArrayDeque<Error> errors() {
        return errors;
    }

    @Override
    public boolean tryParse(ISource.Window start, IBlockHolder document) {
        char[] listIndicators = {'*', '#'};

        if (start.here().equals(listIndicators)) {
            list.setStart(start.here());
            while (!start.here().isEof()) {

                ISource.Window begin = start.copy();
                if (start.here().isNewLine()) {
                    start.moveForward();
                    start.skipWhitspaces();
                    if (!start.here().equals('*') && !start.here().equals('#')) {
                        start = begin;
                        list.setEnd(start.here());
                        document.append(list.build());
                        return true;
                    }
                }

                int level = 0;

                subList = new List.Builder();
                subList.setStart(start.here());

                char startChar = start.here().get();
                switch (startChar) {
                    case '*':
                        subList.setType(List.Type.Points);
                        break;
                    case '#':
                        subList.setType(List.Type.Numbers);
                        break;
                }
                while (start.here().equals(startChar)) {
                    level++;
                    start.moveForward();
                }

                contentParser.tryParse(start, this);
                subList.setEnd(start.here());


                errors.addAll(contentParser.errors());

                list.add(level, subList);
            }
        }
        return false;
    }
}
