package com.docmala.parser.sourceCodeHandler;

import com.docmala.parser.ISourceCodeHandler;

import java.util.Arrays;

public class SourceCodeHandlerFactory {
    static boolean contains(String fileExtension, String [] extensions) {
        return Arrays.asList(extensions).contains(fileExtension);
    }

    public static ISourceCodeHandler create(String fileExtension) {
        if( contains( fileExtension, CStyleHandler.fileExtensions() ) ) {
            return new CStyleHandler();
        }

        return new DefaultHandler();
    }
}
