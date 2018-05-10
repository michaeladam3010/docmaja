package com.docmala.parser.blockParsers;

import com.docmala.Error;
import com.docmala.parser.IBlockHolder;
import com.docmala.parser.IBlockParser;
import com.docmala.parser.ISource;
import com.docmala.parser.blocks.Comment;

import java.util.ArrayDeque;

public class CommentParser implements IBlockParser {
    ArrayDeque<Error> errors = new ArrayDeque<>();

    @Override
    public ArrayDeque<Error> errors() {
        return errors;
    }

    @Override
    public boolean tryParse(ISource.Window start, IBlockHolder document) {
        if (start.here().equals(';')) {
            Comment.Builder builder = new Comment.Builder();
            builder.setStart(start.here());

            start.moveForward();
            start.skipWhitespaces();
            StringBuilder comment = new StringBuilder();

            while (!start.here().isBlockEnd()) {
                comment.append(start.here().get());
                start.moveForward();
            }
            builder.setEnd(start.here());
            builder.setComment(comment.toString());
            document.append(builder.build());
            start.moveForward();
            return true;
        }
        return false;
    }
}
