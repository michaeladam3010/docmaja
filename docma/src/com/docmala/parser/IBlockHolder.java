package com.docmala.parser;

public interface IBlockHolder {
    void append(Block block);

    Block last();
}
