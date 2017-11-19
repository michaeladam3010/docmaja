// Generated from VanesaFormula.g4 by ANTLR 4.6
package com.docmala.plugins.document.internal_formula.antlr;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link VanesaFormulaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface VanesaFormulaVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link VanesaFormulaParser#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(VanesaFormulaParser.ExprContext ctx);

    /**
     * Visit a parse tree produced by {@link VanesaFormulaParser#term}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTerm(VanesaFormulaParser.TermContext ctx);

    /**
     * Visit a parse tree produced by {@link VanesaFormulaParser#atom}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAtom(VanesaFormulaParser.AtomContext ctx);

    /**
     * Visit a parse tree produced by {@link VanesaFormulaParser#number}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNumber(VanesaFormulaParser.NumberContext ctx);

    /**
     * Visit a parse tree produced by {@link VanesaFormulaParser#neg_number}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNeg_number(VanesaFormulaParser.Neg_numberContext ctx);

    /**
     * Visit a parse tree produced by {@link VanesaFormulaParser#variable}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVariable(VanesaFormulaParser.VariableContext ctx);

    /**
     * Visit a parse tree produced by {@link VanesaFormulaParser#neg_variable}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNeg_variable(VanesaFormulaParser.Neg_variableContext ctx);

    /**
     * Visit a parse tree produced by {@link VanesaFormulaParser#function}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunction(VanesaFormulaParser.FunctionContext ctx);
}