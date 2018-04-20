package com.docmala.parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileSource extends MemorySource {
    public FileSource(String fileName, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(ISource.getFileName(fileName)));
        String memory = new String(encoded, encoding);
        init(fileName, memory);
    }
}
