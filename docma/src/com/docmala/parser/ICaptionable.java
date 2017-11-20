package com.docmala.parser;

import com.docmala.parser.blocks.Caption;

public interface ICaptionable {
    Block instanceWithCaption(Caption caption);
}
