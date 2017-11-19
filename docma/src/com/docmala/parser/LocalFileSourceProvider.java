package com.docmala.parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class LocalFileSourceProvider implements ISourceProvider {
    final Path basePath;

    public LocalFileSourceProvider(Path basePath) {
        this.basePath = basePath;
    }

    @Override
    public ISource get(String fileName) throws IOException {
        return new FileSource(basePath.resolve(fileName).toString(), Charset.defaultCharset());
    }

    @Override
    public byte[] getBinary(String fileName) throws IOException {
        return Files.readAllBytes(basePath.resolve(fileName));
    }

    @Override
    public ISourceProvider subProvider(String fileName) {
        Path p = basePath.resolve(fileName);
        return new LocalFileSourceProvider(p.toAbsolutePath().getParent());
    }

    @Override
    public String getFileName(String fileName) {
        return basePath.resolve(fileName).getFileName().toString(); //  Paths.get(basePath, fileName)
    }
}
