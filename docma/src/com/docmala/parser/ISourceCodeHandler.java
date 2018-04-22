package com.docmala.parser;

public interface ISourceCodeHandler {
    void init(String label, String memory);

    boolean isPartOfDocumentation(int index, String memory);
}
