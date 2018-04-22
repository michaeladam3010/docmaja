package com.docmala.parser.sourceCodeHandler;

import com.docmala.parser.ISourceCodeHandler;

public class DefaultHandler implements ISourceCodeHandler {
    @Override
    public void init(String label, String memory) {
    }

    @Override
    public boolean isPartOfDocumentation(int index, String memory) {
        return true;
    }
}
