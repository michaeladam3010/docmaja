package com.docmala.plugins.document;

import com.docmala.Error;
import com.docmala.parser.*;
import com.docmala.parser.blocks.Image;
import com.docmala.plugins.IDocumentPlugin;
import com.docmala.plugins.document.internal_formula.VanesaFormulaParseRules;
import com.docmala.plugins.document.internal_formula.antlr.VanesaFormulaLexer;
import com.docmala.plugins.document.internal_formula.antlr.VanesaFormulaParser;
import org.antlr.v4.runtime.*;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.w3c.dom.DOMImplementation;

import java.awt.*;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayDeque;

public class formula implements IDocumentPlugin {
    ArrayDeque<Error> errors = new ArrayDeque<>();
    VanesaFormulaParseRules extractor = new VanesaFormulaParseRules();

    @Override
    public BlockProcessing blockProcessing() {
        return BlockProcessing.Required;
    }

    @Override
    public ArrayDeque<Error> errors() {
        return errors;
    }


    String test(String formula) {
        VanesaFormulaLexer lexer = new VanesaFormulaLexer(CharStreams.fromString(formula));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ErrorListener());

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        VanesaFormulaParser parser = new VanesaFormulaParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new ErrorListener());

        ParserRuleContext tree = parser.expr();

        return extractor.visit(tree);
    }

    @Override
    public void process(SourcePosition start, SourcePosition end, ArrayDeque<Parameter> parameters, DataBlock block, Document document, ISourceProvider sourceProvider) {

        TeXFormula latexFormula = new TeXFormula(test(block.data));

        // render the formla to an icon of the same size as the formula.
        TeXIcon icon = latexFormula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);

        // insert a border
        icon.setInsets(new Insets(5, 5, 5, 5));

        DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();
        org.w3c.dom.Document documentx = domImpl.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(documentx);

        SVGGraphics2D g2 = new SVGGraphics2D(ctx, true);
        g2.setSVGCanvasSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));

        icon.paintIcon(null, g2, 0, 0);

        byte[] bytes = "Error".getBytes();

        Writer out = new StringWriter();
        try {
            g2.stream(out, true);
        } catch (SVGGraphics2DIOException e) {
        }

        String svgData = out.toString();
        bytes = svgData.getBytes();

        Image.Builder imageBuilder = new Image.Builder();
        imageBuilder.setData(bytes);
        imageBuilder.setFileType("svg+xml");
        document.append(imageBuilder.build());
    }

    class ErrorListener extends BaseErrorListener {

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {

        }
    }
}
