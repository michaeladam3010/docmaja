package com.docmala.parser;

import java.io.IOException;

public interface ISourceProvider {
    ISource get(String fileName) throws IOException;

    byte[] getBinary(String fileName) throws IOException;

    ISourceProvider subProvider(String fileName);

    String getFileName(String fileName);
}
