package com.docmala.plugins.ouput;

import com.docmala.parser.Anchor;
import com.docmala.parser.Block;
import com.docmala.parser.Document;
import com.docmala.parser.FormattedText;
import com.docmala.parser.blocks.*;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Html {

    private HtmlDocument _html;
    private StringBuilder _htmlBody;
    private Map<String, Integer> captionNumbers = new HashMap<>();
    private String _css;
    private String _js = "";

    public Html() throws IOException {
        _css = getResourceFileAsString("admonitions.css");
    }

    public String getResourceFileAsString(String resourceFileName) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourceFileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public HtmlDocument generate(Document document) {
        _html = new HtmlDocument();
        _htmlBody = _html.body();

        _html.head().append("<style>\n");
        _html.head().append(_css);
        _html.head().append("\n</style>\n");

        _html.head().append("<script>\n");
        _html.head().append(_js);
        _html.head().append("\n</script>\n");

        _htmlBody.append("<p>");
        for (Block part : document.content()) {
            generateBlock(part, document);
        }
        _htmlBody.append("</p>");
        return _html;
    }

    private String id(Block b) {
        return " id=\"line_" + String.valueOf(b.start.line()) + "\"";
    }

    void generateBlock(Block block, Document document) {
        if (block == null)
            return;

        generateAnchors(block, document);

        if (block instanceof Headline) {
            generateHeadline((Headline) block, document);
        } else if (block instanceof List) {
            generateList((List) block, document);
        } else if (block instanceof Table) {
            generateTable((Table) block, document);
        } else if (block instanceof Image) {
            generateImage((Image) block, document);
        } else if (block instanceof Code) { // has to be checked prior to Content since it extends Content
            generateCode((Code) block, document);
        } else if (block instanceof Content) {
            generateContent((Content) block);
        } else if (block instanceof Admonition) {
            generateAdmonition((Admonition) block, document);
        } else if (block instanceof NextParagraph) {
            _htmlBody.append("\n<p>");
        }
    }

    private void generateAdmonition(Admonition block, Document document) {
        _htmlBody.append("<div class=\"admonition\">\n");
        _htmlBody.append("<table>\n");
        _htmlBody.append("  <tr>");
        _htmlBody.append("    <td class=\"icon ").append(block.type.name().toLowerCase()).append("\"></td>\n");
        _htmlBody.append("    <td class=\"content\">\n");
        for (Block part : block.content) {
            generateBlock(part, document);
        }

        _htmlBody.append("\n    </td>\n");
        _htmlBody.append("  </tr>\n");
        _htmlBody.append("</table>\n");
        _htmlBody.append("</div>\n");
    }

    private void generateAnchors(Block block, Document document) {
        if (block.anchors != null) {
            for (Anchor anchor : block.anchors) {
                _htmlBody.append("<a name=\"").append(anchor.name).append("\"></a>");
            }
        }
    }

    private void generateCaption(Caption caption, Document document) {
        if (caption != null) {
            _htmlBody.append("<figcaption>");
            Document.CaptionTypeData captionTypeData = document.captionTypeData().get(caption.type);
            if (!captionNumbers.containsKey(caption.type)) {
                captionNumbers.put(caption.type, 1);
            }
            Integer number = captionNumbers.get(caption.type);

            captionNumbers.put(caption.type, number + 1);

            String format = caption.type + " %s: ";
            if (captionTypeData != null) {
                format = captionTypeData.text;
            }
            _htmlBody.append(String.format(format, number.toString()));

            generateBlock(caption.content, document);

            _htmlBody.append("</figcaption>");
        }

    }

    private void generateImage(Image image, Document document) {
        _htmlBody.append("<figure>");
        _htmlBody.append("<img src=\"data:image/");
        _htmlBody.append(image.fileType);
        _htmlBody.append(";base64,");
        _htmlBody.append(Base64.getEncoder().encodeToString(image.data));
        _htmlBody.append("\">");

        generateCaption(image.caption, document);

        _htmlBody.append("</figure>");
    }

    void generateEscapedText(String text) {
        text = text.replaceAll("<", "&lt;");
        text = text.replaceAll(">", "&gt;");
        _htmlBody.append(text);
    }

    void generateContent(Content content) {
        if (content == null)
            return;
        _htmlBody.append("<span");
        _htmlBody.append(id(content));
        _htmlBody.append(">");
        for (FormattedText text : content.content()) {
            if (text instanceof FormattedText.Image) {
                FormattedText.Image image = (FormattedText.Image) text;
                _htmlBody.append("<img style=\"height:1.2em; vertical-align:text-bottom;\" src=\"data:image/");
                _htmlBody.append(image.fileType);
                _htmlBody.append(";base64,");
                _htmlBody.append(Base64.getEncoder().encodeToString(image.data));
                _htmlBody.append("\">");
                _htmlBody.append(" ");
            } else if (text instanceof FormattedText.Link) {
                FormattedText.Link link = (FormattedText.Link) text;
                String visualText = link.text;
                if (visualText.isEmpty()) {
                    visualText = link.url;
                }

                if (link.type == FormattedText.Link.Type.IntraFile) {
                    _htmlBody.append("<a href=\"#").append(link.url).append("\">");
                }
                _htmlBody.append(visualText).append("</a>");
            } else {
                FormattedText.Style style = text.style;

                if(style == null) {
                    generateEscapedText(text.text);
                } else {
                    _htmlBody.append("<span style=\"");

                    if (style.color.isColored()) {
                        _htmlBody.append(String.format("color: #%02x%02x%02x;", style.color.r, style.color.g, style.color.b));
                    }
                    if (style.bold) {
                        _htmlBody.append("font-weight: bold;");
                    }
                    if (style.italic) {
                        _htmlBody.append("font-style: italic;");
                    }
                    if (style.underlined) {
                        _htmlBody.append("text-decoration: underline;");
                    }
                    if (style.stroked) {
                        _htmlBody.append("text-decoration: line-through;");
                    }
                    if (style.monospaced) {
                        _htmlBody.append("font-family: 'Lucida Console', monospace;");
                    }

                    _htmlBody.append("\">");

                    generateEscapedText(text.text);

                    _htmlBody.append("</span>");
                }
            }
        }
        _htmlBody.append("</span>");
    }

    void generateHeadline(Headline headline, Document document) {
        if (headline.level <= 6) {
            _htmlBody.append("<h").append(headline.level).append(">");
            generateBlock(headline.content, document);
            _htmlBody.append("</h").append(headline.level).append(">");
        }
    }

    void generateCode(Code code, Document document) {
        _htmlBody.append("<figure>");
        if (code.type != null && !code.type.isEmpty()) {
            _htmlBody.append("<pre").append(id(code)).append("><code class=\"").append(code.type).append("\">\n");
        } else {
            _htmlBody.append("<pre").append(id(code)).append("><code>\n");
        }

        generateContent(code);

        _htmlBody.append("</code></pre>\n");
        generateCaption(code.caption, document);
        _htmlBody.append("</figure>");
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

        _htmlBody.append("<").append(type).append(" ").append(style).append(">\n");

        for (List entry : entries) {
            _htmlBody.append("<li> ");
            generateBlock(entry.content, document);
            generateListEntries(entry.entries, document);
            _htmlBody.append("</li> ");

        }

        _htmlBody.append("</").append(type).append(">\n");
    }

    void generateList(List list, Document document) {
        _htmlBody.append("<figure>");
        generateListEntries(list.entries.getFirst().entries, document);
        generateCaption(list.caption, document);
        _htmlBody.append("</figure>");

    }

    void generateTable(Table table, Document document) {
        _htmlBody.append("<table border=\"1\">\n");
        boolean firstRow = true;
        for (Table.Cell[] row : table.cells()) {
            boolean firstColumn = true;
            _htmlBody.append("<tr>");
            String end = "";

            for (Table.Cell cell : row) {
                if (cell == null) {
                    continue;
                }
                StringBuilder span = new StringBuilder();
                if (cell.isHiddenBySpan) {
                    continue;
                }

                if (cell.columnSpan > 1) {
                    span.append(" colspan=\"").append(cell.columnSpan).append("\"");
                }
                if (cell.rowSpan > 1) {
                    span.append(" rowspan=\"").append(cell.rowSpan).append("\"");
                }
                if (cell.isHeading && firstRow) {
                    _htmlBody.append("<th scope=\"col\"").append(span).append(">\n");
                    end = "</th>";
                } else if (cell.isHeading && firstColumn) {
                    _htmlBody.append("<th scope=\"row\"").append(span).append(">\n");
                    end = "</th>";
                } else {
                    _htmlBody.append("<td").append(span).append(">\n");
                    end = "</td>";
                }

                for (Block block : cell.content) {
                    generateBlock(block, document);
                }
                _htmlBody.append(end);

                firstColumn = false;
            }
            _htmlBody.append("</tr>\n");
            firstRow = false;
        }
        _htmlBody.append("</table>\n");
    }

    static public class HtmlDocument {
        StringBuilder _head = new StringBuilder();
        StringBuilder _body = new StringBuilder();

        public StringBuilder head() {
            return _head;
        }

        public StringBuilder body() {
            return _body;
        }

        public void write(String fileName) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                writer.write("<!doctype html>\n");
                writer.write("<html>\n");
                writer.write("<head>\n");
                writer.write("<meta charset=\"UTF-8\">");
                writer.write(_head.toString());
                writer.write("\n");
                writer.write("</head>\n");

                writer.write("<body>\n");
                writer.write(_body.toString());
                writer.write("\n");
                writer.write("</body>\n");
            }
        }
    }

}
