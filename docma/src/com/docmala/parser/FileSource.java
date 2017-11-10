package com.docmala.parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ListIterator;
import java.nio.file.*;

public class FileSource extends MemorySource {
    public FileSource(String fileName, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(fileName));
        _memory = new String(encoded, encoding);
    }
}
