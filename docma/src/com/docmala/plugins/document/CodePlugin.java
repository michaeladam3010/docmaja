package com.docmala.plugins.document;

import com.docmala.Error;
import com.docmala.parser.*;
import com.docmala.plugins.IDocumentPlugin;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

@Plugin("Code")
public class CodePlugin implements IDocumentPlugin {
    private ArrayDeque<Error> errors = new ArrayDeque<>();
    private static final Invocable engine = createScriptEngine();
    private static Exception scriptEngineError = null;
    private static final Map<String, FormattedText.Style> styleSheet = parseCSSFile();

    private static Invocable createScriptEngine() {
        try {
            InputStream highlight_js_input = CodePlugin.class.getClassLoader().getResourceAsStream("highlight.pack.js");

            ScriptEngineManager engineManager =
                    new ScriptEngineManager();
            ScriptEngine engine =
                    engineManager.getEngineByName("nashorn");

            engine.eval("self = {}");
            engine.eval(new InputStreamReader(highlight_js_input));

            engine.eval("function highlightCode(lang, str) { return self.hljs.highlight(lang, str).value; }");
            engine.eval("function autoHighlightCode(str) { return self.hljs.highlightAuto(str).value; }");

            Invocable invocable = (Invocable) engine;

            // we have to call doit() once, to ensure highlight.js is probably initialized, without this call the output is broken
            invocable.invokeFunction("highlightCode", "java", "");
            invocable.invokeFunction("autoHighlightCode", "");

            return invocable;
        } catch (Exception e) {
            scriptEngineError = e;
            return null;
        }
    }

    private static String removeComments(String str) {
        // from https://www.w3.org/TR/CSS2/grammar.html#scanner
        String regex = "\\/\\*[^*]*\\*+([^/*][^*]*\\*+)*\\/";
        Matcher matcher = Pattern.compile(regex).matcher(str);
        StringBuffer sb = new StringBuffer(str.length());

        while (matcher.find())
            matcher.appendReplacement(sb, "");

        matcher.appendTail(sb);

        return sb.toString();
    }

    private static String removeWhitespaces(String str) {
        StringBuilder sb = new StringBuilder();

        for (char c : str.toCharArray()) {
            if (!Character.isWhitespace(c))
                sb.append(c);
        }

        return sb.toString();
    }

    /**
     * @return Map of css class names and the stylesheet
     */
    private static Map<String, FormattedText.Style> parseCSSFile() {
        Map<String, FormattedText.Style> ret = new HashMap<>();
        try {
            String inputCSS = new String(CodePlugin.class.getClassLoader().getResourceAsStream("codeHighlight.css").readAllBytes());
            String withoutComments = removeComments(inputCSS);
            char[] css = removeWhitespaces(withoutComments).toCharArray();

            int pos = 0;
            while (pos < css.length) {
                int start = pos;
                // extract the name
                while (pos < css.length) {
                    if (css[pos++] == '{')
                        break;
                }
                String name = new String(css, start, pos - start - 1);

                // extract the content
                start = pos;
                while (pos < css.length) {
                    if (css[pos++] == '}')
                        break;
                }
                String content = new String(css, start, pos - start - 1);

                FormattedText.Style style = new FormattedText.Style();
                {
                    Matcher matcher = Pattern.compile("(.*?):(.*?)[;$]").matcher(content);
                    while (matcher.find()) {
                        String value = matcher.group(2);
                        switch (matcher.group(1)) {
                            case "color":
                                if (!value.startsWith("#"))
                                    throw new Exception("unsupported color string");
                                int r = Integer.parseInt(value.substring(1, 3), 16);
                                int g = Integer.parseInt(value.substring(3, 5), 16);
                                int b = Integer.parseInt(value.substring(5), 16);
                                style.color = new FormattedText.Color(r, g, b);
                                break;
                            case "font-style":
                                switch (value) {
                                    case "italic":
                                        style.italic = true;
                                        break;
                                    default:
                                        throw new Exception("unknown font-style");
                                }
                                break;
                            case "font-weight":
                                switch (value) {
                                    case "bold":
                                        style.bold = true;
                                        break;
                                    default:
                                        throw new Exception("unknown font-weight");
                                }
                                break;
                            case "font-decoration":
                                switch (value) {
                                    case "underline":
                                        style.underlined = true;
                                        break;
                                    default:
                                        throw new Exception("unknown font-decoration");
                                }
                                break;
                            case "display":
                            case "overflow-x":
                            case "padding":
                            case "background":
                                break;
                        }
                    }
                }

                {
                    String[] splitted = name.split(",");
                    for (int i = 0; i < splitted.length; ++i) {
                        if (splitted[i].startsWith("."))
                            ret.put(splitted[i].substring(1), style);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Something went wrong " + e.getMessage());
        }
        return ret;
    }

    @Override
    public BlockProcessing blockProcessing() {
        return BlockProcessing.Required;
    }

    @Override
    public ArrayDeque<Error> errors() {
        return errors;
    }

    @Override
    public String defaultParameter() {
        return null;
    }

    @Override
    public void process(SourcePosition start, SourcePosition end, ArrayDeque<Parameter> parameters, DataBlock block, Document document, ISourceProvider sourceProvider) {
        com.docmala.parser.blocks.Code.Builder code = new com.docmala.parser.blocks.Code.Builder();
        code.setStart(start);
        code.setEnd(end);
        String type = null;

        for (Parameter parameter : parameters) {
            if (parameter.name().equals("type")) {
                type = parameter.value();
                break;
            }
        }

        if (engine == null) {
            errors.add(new Error(start, "Couldn't initialize highlight.js: " + scriptEngineError.getMessage()));
            return;
        }

        try {
            String str;
            if (type != null) {
                str = engine.invokeFunction("highlightCode", type, block.data).toString();
            } else {
                str = engine.invokeFunction("autoHighlightCode", block.data).toString();
            }

            str = str.replaceAll("\n", "<br>");
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parseBodyFragment(str);
            org.jsoup.nodes.Element body = doc.body();

            FormattingVisitor formatter = new FormattingVisitor(code);
            NodeTraversor.traverse(formatter, body);
        } catch (Exception e) {
            errors.add(new Error(start, e.getMessage()));
        }
        document.append(code.build());
    }

    private class FormattingVisitor implements NodeVisitor {
        private com.docmala.parser.blocks.Code.Builder out;
        private Stack<FormattedText.Style> style = new Stack<>();

        FormattingVisitor(com.docmala.parser.blocks.Code.Builder out) {
            this.out = out;
        }

        public void head(Node node, int depth) {
            String name = node.nodeName();
            if (node instanceof TextNode) {
                append(((TextNode) node).getWholeText());
            } else if (name.equals("span")) {
                style.push(styleSheet.getOrDefault(((Element) node).className(), null));
            }
        }

        public void tail(Node node, int depth) {
            String name = node.nodeName();
            if (StringUtil.in(name, "br")) {
                append("\n");
            } else if (name.equals("span")) {
                style.pop();
            }
        }

        private void append(String str) {
            FormattedText.Builder text = new FormattedText.Builder();
            if (!style.empty())
                text.setStyle(style.lastElement());
            text.setText(str);
            out.addCode(text.build());
        }
    }
}
