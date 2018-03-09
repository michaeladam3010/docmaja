package com.docmala.plugins.ouput;

import com.docmala.parser.Block;
import com.docmala.parser.Document;
import com.docmala.parser.FormattedText;
import com.docmala.parser.blocks.Content;
import com.docmala.parser.blocks.Headline;
import com.docmala.parser.blocks.Image;
import com.docmala.parser.blocks.List;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Html {

    HtmlDocument _html;
    Map<String, Integer> captionNumbers = new HashMap<>();

    public HtmlDocument generate(Document document) {
        _html = new HtmlDocument();
        for (Block part : document.content()) {
            generateBlock(part, document);
        }
        return _html;
    }

    void generateBlock(Block block, Document document) {
        if (block == null)
            return;

        if (block instanceof Headline) {
            generateHeadline((Headline) block, document);
        } else if (block instanceof List) {
            generateList((List) block, document);
        } else if (block instanceof Image) {
            generateImage((Image) block, document);
        } else if (block instanceof Content) {
            generateContent((Content) block);
        }
    }

    private void generateImage(Image image, Document document) {
        _html.body().append("<figure>");
        _html.body().append("<img src=\"data:image/");
        _html.body().append(image.fileType);
        _html.body().append(";base64,");
        _html.body().append(Base64.getEncoder().encodeToString(image.data));
        _html.body().append("\">");

        if (image.caption != null) {
            _html.body().append("<figcaption>");
            Document.CaptionTypeData captionTypeData = document.captionTypeData().get(image.caption.type);
            if (!captionNumbers.containsKey(image.caption.type)) {
                captionNumbers.put(image.caption.type, 1);
            }
            Integer number = captionNumbers.get(image.caption.type);

            captionNumbers.put(image.caption.type, number + 1);

            String format = image.caption.type + "%s: ";
            if (captionTypeData != null) {
                format = captionTypeData.text;
            }
            _html.body().append(String.format(format, number.toString()));

            generateBlock(image.caption.content, document);

            _html.body().append("</figcaption>");
        }

        _html.body().append("</figure>");
    }

    void writeHeader() {

    }

    void generateEscapedText(String text) {
        text = text.replaceAll("<", "&lt;");
        text = text.replaceAll(">", "&gt;");
        _html.body().append(text);
    }

    void generateContent(Content content) {
        if (content == null)
            return;

        for (FormattedText text : content.content()) {
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
        _html.body().append("</br>");
    }

    void generateHeadline(Headline headline, Document document) {
        if (headline.level <= 6) {
            _html.body().append("<h").append(headline.level).append(">");
            generateBlock(headline.content, document);
            _html.body().append("</h").append(headline.level).append(">");
        }
    }

    void generateListEntries(ArrayDeque<List> entries, Document document) {
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

        for (List entry : entries) {
            _html.body().append("<li> ");
            generateBlock(entry.content, document);
            generateListEntries(entry.entries, document);
            _html.body().append("</li> ");

        }

        _html.body().append("</").append(type).append(">\n");
    }

    void generateList(List list, Document document) {
        generateListEntries(list.entries.getFirst().entries, document);
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
