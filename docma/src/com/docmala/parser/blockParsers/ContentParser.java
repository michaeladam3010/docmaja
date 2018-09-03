package com.docmala.parser.blockParsers;

import com.docmala.Error;
import com.docmala.parser.*;
import com.docmala.parser.blocks.Content;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.stream.Collectors;

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

    boolean isImageStart(ISource.Window start) {
        return start.here().equals(':');
    }
    boolean isImageEnd(ISource.Window start) {
        return start.here().equals(':');
    }

    public byte[] getImageData(String imageName) {
        InputStream is = null;
        byte[] bytes = null;

        is = getClass().getClassLoader().getResourceAsStream(imageName + ".svg");
        if( is == null ) {
            is = getClass().getClassLoader().getResourceAsStream(imageName + ".png");
        } else if( is == null ) {
            is = getClass().getClassLoader().getResourceAsStream(imageName + ".jpg");
        }

        try {
            bytes = is.readAllBytes();
        } catch (Exception e) {
        }
        return bytes;
    }

    boolean parseAnchor(ISource.Window start) {
        if (!isAnchorStart(start))
            return false;

        ISource.Position position = start.here().copy();
        String name = "";
        start.moveForward();
        start.moveForward();
        start.skipWhitespaces();

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

    boolean parseImage(ISource.Window start) {
        if (!isImageStart(start))
            return false;

        ISource.Window begin = start.copy();
        ISource.Position position = start.here().copy();
        StringBuilder text = new StringBuilder();
        start.moveForward();

        while (!start.here().isBlockEnd()) {
            if (isImageEnd(start)) {
                byte[] imageData;
                if( !text.toString().contains("/") ) {
                    imageData = getImageData("emoji/" + text.toString());
                } else {
                    imageData = getImageData(text.toString());
                }
                text.append(':');
                text.insert(0, ':');
                formattedText.setText(text.toString());
                start.moveForward();
                if( imageData != null ) {
                    content.addContent(formattedText.buildImage(imageData, "svg+xml"));
                } else {
                    content.addContent(formattedText.build());
                }
                return true;
            } else if( start.here().isWhitespace() ) {
                start.setTo(begin);
                parseFormattedText(start, true);
                // this wasn't an image but we return true here since we parsed some text
                return true;
            }
            text.append(start.here().get());
            start.moveForward();
        }

        return false;
    }

    boolean parseLink(ISource.Window start) {
        if (!isLinkStart(start))
            return false;

        ISource.Position position = start.here().copy();
        StringBuilder text = new StringBuilder();
        StringBuilder url = new StringBuilder();
        start.moveForward();
        start.moveForward();
        start.skipWhitespaces();

        StringBuilder[] destination = {url};

        while (!start.here().isBlockEnd()) {
            if (isLinkEnd(start)) {
                String urlString = url.toString().trim();

                formattedText.setType(FormattedText.Link.Type.IntraFile);
                formattedText.setUrl(urlString);
                formattedText.setText(text.toString());

                if (urlString.contains("://")) {
                    formattedText.setType(FormattedText.Link.Type.Web);
                } else if (urlString.contains(":")) {
                    formattedText.setType(FormattedText.Link.Type.InterFile);
                }
                content.addContent(formattedText.buildLink());
                start.moveForward();
                start.moveForward();
                return true;
            } else if (start.here().equals(',')) {
                destination[0] = text;
            } else {
                destination[0].append(start.here().get());
            }
            start.moveForward();
        }

        errors.addLast(new Error(position, "Error while parsing link. Expected '>>' but got 'EndOfBlock' "));
        return true;
    }

    void parseFormattedText(ISource.Window start) {
        parseFormattedText(start, false);
    }

    void parseFormattedText(ISource.Window start, boolean ignoreSpecialElements) {
        StringBuilder text = new StringBuilder();
        while (!start.here().isBlockEnd() && (ignoreSpecialElements || (!isLinkStart(start) && !isAnchorStart(start) && !isImageStart(start))) ) {
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
            if (parseLink(start) || parseAnchor(start) || parseImage(start)) {
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
