package com.docmala.parser.blocks;

import com.docmala.parser.Block;
import com.docmala.parser.Source;

import java.util.ArrayDeque;

public class Content extends Block {

    ArrayDeque<FormattedText> _content = new ArrayDeque<>();
    boolean _bold = false;
    boolean _italic = false;
    boolean _monospaced = false;
    boolean _stroked = false;
    boolean _underlined = false;

    @Override
    public String indicators() {
        return "\0";
    }

    @Override
    public Block create() {
        return new Content();
    }

    public ArrayDeque<FormattedText> content() { return _content; }


    boolean isLinkStart(Source.Window start) {
        return start.equals('<', '<');
    }

    boolean isLinkEnd(Source.Window start) {
        return start.equals('>', '>');
    }

    boolean isAnchorStart(Source.Window start) {
        return start.equals('[', '[');
    }

    boolean isAnchorEnd(Source.Window start) {
        return start.equals(']', ']');
    }

    Source.Window parseAnchor(Source.Window start) {
        if (!isAnchorStart(start))
            return start;

        Source.Position position = start.here().copy();
        String name = "";
        start.moveForward();
        start.moveForward();
        start.skipWhitspaces();

        Link.Type linkType;


        while (!start.here().isBlockEnd()) {
            if (isAnchorEnd(start)) {
                _anchors.addLast(new Anchor(position, name));
                start.moveForward();
                start.moveForward();
                return start;
            }
            name += start.here().get();
            start.moveForward();
        }

        _errors.addLast(new ParseError(position, "Error while parsing anchor. Expected ']]' but got 'EndOfBlock' "));
        return start;
    }

    Source.Window parseLink(Source.Window start) {
        if (!isLinkStart(start))
            return start;

        Source.Position position = start.here().copy();
        String text = "";
        String url = "";
        start.moveForward();
        start.moveForward();
        start.skipWhitspaces();

        String[] destination = {url};

        while (!start.here().isBlockEnd()) {
            if (isLinkEnd(start)) {

                Link.Type type = Link.Type.IntraFile;
                if( url.contains("://") ) {
                    type = Link.Type.Web;
                } else if( url.contains(":") ) {
                    type = Link.Type.InterFile;
                }
                _content.addLast(new Link(text.trim(), _bold, _italic,_monospaced, _stroked, _underlined, url.trim(), type));
                start.moveForward();
                start.moveForward();
                return start;
            } else if( start.here().equals(',') ) {
                destination[0] = url;
            } else {
                destination[0] += start.here().get();
            }
            start.moveForward();
        }

        _errors.addLast(new ParseError(position, "Error while parsing link. Expected '>>' but got 'EndOfBlock' "));
        return start;
    }

    Source.Window parseFormattedText(Source.Window start) {
        String text = "";
        while (!start.here().isBlockEnd() && !isLinkStart(start) && !isAnchorStart(start)) {
            if( start.equals('*','*') ||
                    start.equals('/','/') ||
                    start.equals('\'','\'') ||
                    start.equals('-','-') ||
                    start.equals('_','_') ) {
                if( !text.isEmpty() ) {
                    _content.addLast( new FormattedText(text, _bold, _italic, _monospaced, _stroked, _underlined));
                    text = "";
                }

                if( start.equals('*', '*') ) {
                    _bold = !_bold;
                } else if( start.equals('/', '/') ) {
                    _italic = !_italic;
                } else if( start.equals('\'', '\'') ) {
                    _monospaced = !_monospaced;
                } else if( start.equals('-', '-') ) {
                    _stroked = !_stroked;
                } else if( start.equals('_', '_') ) {
                    _underlined = !_underlined;
                }
                start.moveForward();
                start.moveForward();
            } else {
                text += start.here().get();
                start.moveForward();
            }
        }

        if( !text.isEmpty() ) {
            if( isAnchorStart(start) ) {
                // remove tailing whitespaces if an anchor is comming
                // this is used so that text like "asdf [[anchor]] ghjk" does not have two whitespaces between asdf and ghjk
                text = text.replaceAll("\\s+$", "");
            }
            _content.addLast( new FormattedText(text, _bold, _italic, _monospaced, _stroked, _underlined));
        }
        return start;
    }

    @Override
    public Source.Window doParse(Source.Window start) {
        String text = "";
        start.skipWhitspaces();

        while (!start.here().isBlockEnd()) {

            start = parseAnchor(start);
            start = parseLink(start);
            start = parseFormattedText(start);
        }

        if( _bold ) {
            _errors.addLast(new ParseError(start.here(), "Bold formating (\"**\") was not closed."));
        }
        if( _italic ) {
            _errors.addLast(new ParseError(start.here(), "Italic formating (\"//\") was not closed."));
        }
        if( _monospaced ) {
            _errors.addLast(new ParseError(start.here(), "Monospaced formating (\"''\") was not closed."));
        }
        if( _stroked ) {
            _errors.addLast(new ParseError(start.here(), "Stroked formating (\"--\") was not closed."));
        }
        if( _underlined ) {
            _errors.addLast(new ParseError(start.here(), "Underlined formating (\"__\") was not closed."));
        }
        return start;
    }

    public static class FormattedText {
        public final String text;

        public final boolean bold;
        public final boolean italic;
        public final boolean monospaced;
        public final boolean stroked;
        public final boolean underlined;

        public FormattedText(String text, boolean bold, boolean italic, boolean monospaced, boolean stroked, boolean underlined) {
            this.text = text;
            this.bold = bold;
            this.italic = italic;
            this.monospaced = monospaced;
            this.stroked = stroked;
            this.underlined = underlined;
        }
    }

    public static class Link extends FormattedText {

        public final String url;
        public final Type type;
        public Link(String text, boolean bold, boolean italic, boolean monospaced, boolean stroked, boolean underlined, String url, Type type) {
            super(text, bold, italic, monospaced, stroked, underlined);
            this.url = url;
            this.type = type;
        }

        public enum Type {
            Web, IntraFile, InterFile
        }

    }

}
