package com.docmala.plugins;

import java.util.ArrayDeque;

public interface IDocumentPlugin {
    enum BlockProcessing { No, Required, Optional };

    BlockProcessing blockProcessing();

    ArrayDeque<Error> process();

}
