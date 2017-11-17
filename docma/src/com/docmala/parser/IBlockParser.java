package com.docmala.parser;

import com.docmala.Error;

import java.util.ArrayDeque;

public interface IBlockParser {

    ArrayDeque<Error> errors();

    boolean tryParse(ISource.Window start, IBlockHolder document);
}
