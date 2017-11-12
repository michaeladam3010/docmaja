package com.docmala.plugins.ouput;

import com.docmala.Document;
import com.docmala.parser.blocks.Content;
import com.docmala.parser.blocks.Headline;
import com.docmala.parser.blocks.List;

import java.io.*;
import java.util.ArrayDeque;

public class Html {

    HtmlDocument _html;

    public HtmlDocument generate(Document document) {
        _html = new HtmlDocument();
        for (Object part : document.content()) {
            if (part instanceof Content) {
                if (part instanceof Headline) {
                    generateHeadline((Headline) part);
                } else {
                    generateContent((Content) part);
                }
            } else if( part instanceof List ) {
                generateList((List)part);
            }
        }
        return _html;
    }

    void writeHeader() {

    }

    void generateEscapedText(String text) {
        text = text.replaceAll("<", "&lt;");
        text = text.replaceAll(">", "&gt;");
        _html.body().append(text);
    }

    void generateContent(Content content) {
        if( content == null )
            return;

        for (Content.FormattedText text : content.content()) {
            if (text.bold) {
                _html.body().append("<b>");
            }
            if (text.italic) {
                _html.body().append("<i>");
            }
            if (text.underlined) {
                _html.body().append("<u>");
            }
            if (text.stroked) {
                _html.body().append("<del>");
            }
            if (text.monospaced) {
                _html.body().append("<tt>");
            }
            generateEscapedText(text.text);

            if (text.monospaced) {
                _html.body().append("</tt>");
            }
            if (text.stroked) {
                _html.body().append("</del>");
            }
            if (text.underlined) {
                _html.body().append("</u>");
            }
            if (text.italic) {
                _html.body().append("</i>");
            }
            if (text.bold) {
                _html.body().append("</b>");
            }
        }
    }

    void generateHeadline(Headline headline) {
        if (headline.level() <= 6) {
            _html.body().append("<h").append(headline.level()).append(">");
            generateContent(headline);
            _html.body().append("</h").append(headline.level()).append(">");
        }
    }

    void generateListEntries(ArrayDeque<List.Entry> entries) {
        if (entries.isEmpty())
            return;

        String type = "ul";
        String style = "";
        switch (entries.getFirst().type) {
            case Points:
                type = "ul";
                break;
            case Numbers:
                type = "ol";
                break;
        }

        _html.body().append("<").append(type).append(" ").append(style).append(">\n");

        for (List.Entry entry : entries) {
            _html.body().append("<li> ");
            generateContent(entry.text);
            generateListEntries(entry.entries);
            _html.body().append("</li> ");

        }

        _html.body().append("</").append(type).append(">\n");
    }

    void generateList(List list) {
        generateListEntries(list.entries());
    }

    static public class HtmlDocument {
        String _head = "";
        StringBuffer _body = new StringBuffer();

        public String head() {
            return _head;
        }

        public void setHead(String head) {
            this._head = head;
        }

        public StringBuffer body() {
            return _body;
        }

        public void write(String fileName) throws IOException {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

            writer.write("<!doctype html>\n");
            writer.write("<html>\n");
            writer.write("<head>\n");
            writer.write(_head);
            writer.write("\n");
            writer.write("</head>\n");

            writer.write("<body>\n");
            writer.write(_body.toString());
            writer.write("\n");
            writer.write("</body>\n");

            writer.close();
        }
    }

}
