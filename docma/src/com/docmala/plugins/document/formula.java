package com.docmala.plugins.document;

import com.docmala.Error;
import com.docmala.parser.DataBlock;
import com.docmala.parser.Document;
import com.docmala.parser.ISourceProvider;
import com.docmala.parser.Parameter;
import com.docmala.parser.blocks.Image;
import com.docmala.plugins.IDocumentPlugin;
import com.docmala.plugins.document.internal_formula.antlr.VanesaFormulaLexer;
import com.docmala.plugins.document.internal_formula.antlr.VanesaFormulaParser;
import com.docmala.plugins.document.internal_formula.VanesaFormulaParseRules;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.antlr.v4.runtime.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    public void process(ArrayDeque<Parameter> parameters, DataBlock block, Document document, ISourceProvider sourceProvider) {
        System.out.printf("%s%n", test(block.data));

        TeXFormula latexFormula = new TeXFormula(test(block.data));

        // render the formla to an icon of the same size as the formula.
        TeXIcon icon = latexFormula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);

        // insert a border
        icon.setInsets(new Insets(5, 5, 5, 5));

        BufferedImage image = new BufferedImage(icon.getIconWidth(),
                icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.white);
        g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
        icon.paintIcon(null, g2, 0, 0);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) { }


        byte[] bytes = baos.toByteArray();

        com.docmala.parser.blocks.Image.Builder imageBuilder = new Image.Builder();
        imageBuilder.setData( bytes );
        imageBuilder.setFileType("png");
        document.append(imageBuilder.build());
    }

    class ErrorListener extends BaseErrorListener {

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {

        }
    }
}
