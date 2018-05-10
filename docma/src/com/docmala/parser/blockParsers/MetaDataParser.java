package com.docmala.parser.blockParsers;

import com.docmala.Error;
import com.docmala.parser.IBlockHolder;
import com.docmala.parser.IBlockParser;
import com.docmala.parser.ISource;
import com.docmala.parser.blocks.MetaData;

import java.util.ArrayDeque;

public class MetaDataParser implements IBlockParser {
    ArrayDeque<Error> errors = new ArrayDeque<>();

    @Override
    public ArrayDeque<Error> errors() {
        return errors;
    }

    @Override
    public boolean tryParse(ISource.Window start, IBlockHolder document) {
        if (start.here().equals('{')) {
            errors.clear();
            MetaData.Builder builder = new MetaData.Builder();
            builder.setStart(start.here());

            start.moveForward();
            start.skipWhitespaces();
            StringBuilder key = new StringBuilder();
            StringBuilder method = new StringBuilder();
            StringBuilder value = new StringBuilder();
            Parsing parsing = Parsing.Key;

            while (!start.here().isBlockEnd()) {
                if (parsing == Parsing.Key) {
                    if (start.here().equals('}')) {
                        parsing = Parsing.Finished;
                        break;
                    } else if (start.here().isWhitespace()) {
                        parsing = Parsing.Method;
                        start.skipWhitespaces();
                        continue;
                    } else {
                        key.append(start.here().get());
                    }
                } else if (parsing == Parsing.Method) {
                    if (start.here().equals('}')) {
                        parsing = Parsing.Finished;
                        break;
                    } else if (start.here().isWhitespace()) {
                        parsing = Parsing.Value;
                        start.skipWhitespaces();
                        continue;
                    } else {
                        method.append(start.here().get());
                    }
                } else if (parsing == Parsing.Value) {
                    if (start.here().equals('}')) {
                        parsing = Parsing.Finished;
                        break;
                    } else {
                        value.append(start.here().get());
                    }
                }
                start.moveForward();
            }
            if (parsing != Parsing.Finished) {
                errors.addLast(new Error(start.here(), "Expected '}'"));
            }

            builder.setKey(key.toString());

            String meth = method.toString();
            boolean isError = false;
            if (meth.equalsIgnoreCase("append")) {
                builder.setModificationType(MetaData.ModificationType.Append);
            } else if (meth.equalsIgnoreCase("set")) {
                builder.setModificationType(MetaData.ModificationType.IgnoreIfPresent);
            } else if (meth.equalsIgnoreCase("overwrite")) {
                builder.setModificationType(MetaData.ModificationType.Overwrite);
            } else {
                if( meth.isEmpty() ) {
                    builder.setModificationType(MetaData.ModificationType.IgnoreIfPresent);
                } else {
                    errors.addLast(new Error(start.here(), "Expected modification method (append, set, overwrite)"));
                    isError = true;
                }
            }

            if( !isError ) {
                builder.setValue(value.toString());
                builder.setEnd(start.here());
                builder.setValue(value.toString());
                document.append(builder.build());
            }
            start.moveForward();
            return true;
        }
        return false;
    }

    enum Parsing {
        Key,
        Method,
        Value,
        Finished
    }

}
