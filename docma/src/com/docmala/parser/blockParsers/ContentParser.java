package com.docmala.parser.blockParsers;

import com.docmala.Error;
import com.docmala.parser.*;
import com.docmala.parser.blocks.Content;

import java.util.ArrayDeque;

public class ContentParser implements IBlockParser {
    ArrayDeque<Error> errors = new ArrayDeque<>();
    Content.Builder content;
    FormattedText.Builder formattedText;

    @Override
    public ArrayDeque<Error> errors() {
        return errors;
    }

    boolean isLinkStart(ISource.Window start) {
        return start.equals('<', '<');
    }

    boolean isLinkEnd(ISource.Window start) {
        return start.equals('>', '>');
    }

    boolean isAnchorStart(ISource.Window start) {
        return start.equals('[', '[');
    }

    boolean isAnchorEnd(ISource.Window start) {
        return start.equals(']', ']');
    }

    boolean parseAnchor(ISource.Window start) {
        if (!isAnchorStart(start))
            return false;

        ISource.Position position = start.here().copy();
        String name = "";
        start.moveForward();
        start.moveForward();
        start.skipWhitspaces();

        while (!start.here().isBlockEnd()) {
            if (isAnchorEnd(start)) {
                content.addAnchor(new Anchor(position, name));
                start.moveForward();
                start.moveForward();
                return true;
            }
            name += start.here().get();
            start.moveForward();
        }

        errors.addLast(new Error(position, "Error while parsing anchor. Expected ']]' but got 'EndOfBlock' "));
        return true;
    }

    boolean parseLink(ISource.Window start) {
        if (!isLinkStart(start))
            return false;

        ISource.Position position = start.here().copy();
        String text = "";
        String url = "";
        start.moveForward();
        start.moveForward();
        start.skipWhitspaces();

        String[] destination = {url};

        while (!start.here().isBlockEnd()) {
            if (isLinkEnd(start)) {

                formattedText.setType(FormattedText.Link.Type.IntraFile);
                if (url.contains("://")) {
                    formattedText.setType(FormattedText.Link.Type.Web);
                } else if (url.contains(":")) {
                    formattedText.setType(FormattedText.Link.Type.InterFile);
                }
                content.addContent(formattedText.buildLink());
                start.moveForward();
                start.moveForward();
                return true;
            } else if (start.here().equals(',')) {
                destination[0] = url;
            } else {
                destination[0] += start.here().get();
            }
            start.moveForward();
        }

        errors.addLast(new Error(position, "Error while parsing link. Expected '>>' but got 'EndOfBlock' "));
        return true;
    }

    void parseFormattedText(ISource.Window start) {
        StringBuilder text = new StringBuilder();
        while (!start.here().isBlockEnd() && !isLinkStart(start) && !isAnchorStart(start)) {
            if (!start.here().isEscaped() && !start.next().isEscaped() && (
                    start.equals('*', '*') ||
                            start.equals('/', '/') ||
                            start.equals('\'', '\'') ||
                            start.equals('-', '-') ||
                            start.equals('_', '_'))) {
                if (text.length() != 0) {
                    content.addContent(formattedText.setText(text.toString()).build());
                    text.setLength(0);
                }

                if (start.equals('*', '*')) {
                    formattedText.toggleBold();
                } else if (start.equals('/', '/')) {
                    formattedText.toggleItalic();
                } else if (start.equals('\'', '\'')) {
                    formattedText.toggleMonospaced();
                } else if (start.equals('-', '-')) {
                    formattedText.toggleStroked();
                } else if (start.equals('_', '_')) {
                    formattedText.toggleUnderlined();
                }
                start.moveForward();
                start.moveForward();
            } else {
                text.append(start.here().get());
                start.moveForward();
            }
        }

        if (text.length() != 0) {
            if (isAnchorStart(start)) {
                // remove tailing whitespaces if an anchor is coming
                // this is used so that content like "asdf [[anchor]] ghjk" does not have two whitespaces between asdf and ghjk
                String tmp = text.toString().replaceAll("\\s+$", "");
                text.setLength(0);
                text.append(tmp);
            }
            content.addContent(formattedText.setText(text.toString()).build());
        }
    }


    @Override
    public boolean tryParse(ISource.Window start, IBlockHolder document) {
        errors.clear();
        content = new Content.Builder();
        formattedText = new FormattedText.Builder();
        content.setStart(start.here());
        while (!start.here().isBlockEnd()) {
            if (parseLink(start) || parseAnchor(start)) {
                continue;
            }
            parseFormattedText(start);
        }
        content.setEnd(start.here());
        document.append(content.build());


        if (formattedText.isBold()) {
            errors.addLast(new Error(start.here(), "Bold formating (\"**\") was not closed."));
        }
        if (formattedText.isItalic()) {
            errors.addLast(new Error(start.here(), "Italic formating (\"//\") was not closed."));
        }
        if (formattedText.isMonospaced()) {
            errors.addLast(new Error(start.here(), "Monospaced formating (\"''\") was not closed."));
        }
        if (formattedText.isStroked()) {
            errors.addLast(new Error(start.here(), "Stroked formating (\"--\") was not closed."));
        }
        if (formattedText.isUnderlined()) {
            errors.addLast(new Error(start.here(), "Underlined formating (\"__\") was not closed."));
        }

        return errors.isEmpty();
    }
}
