package com.docmala.server;

import com.docmala.parser.ISource;
import com.docmala.parser.ISourceProvider;
import com.docmala.parser.MemorySource;

import java.io.IOException;

public class RemoteSourceProvider implements ISourceProvider{
    String baseFileName = "";
    String baseContent = "";

    public void setBaseContent(String fileName, String base) {
        baseFileName = fileName;
        baseContent = base;
    }

    @Override
    public ISource get(String fileName) throws IOException {
        if( fileName.equals(baseFileName) )
            return new MemorySource(baseFileName, baseContent);
        return null;
    }

    @Override
    public byte[] getBinary(String fileName) throws IOException {
        return new byte[0];
    }

    @Override
    public ISourceProvider subProvider(String fileName) {
        if( fileName.equals(baseFileName) )
            return this;
        return null;
    }

    @Override
    public String getFileName(String fileName) {
        return fileName;
    }
}
