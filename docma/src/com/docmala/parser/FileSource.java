package com.docmala.parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileSource extends MemorySource {
    public FileSource(String fileName, Charset encoding)
            throws IOException {
        _fileName = fileName;
        byte[] encoded = Files.readAllBytes(Paths.get(fileName));
        _memory = new String(encoded, encoding);
    }
}
