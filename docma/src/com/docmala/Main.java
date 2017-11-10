package com.docmala;

import com.docmala.parser.FileSource;
import com.docmala.parser.Parser;

import java.io.IOException;
import java.nio.charset.Charset;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Parser p = new Parser();

        try {
            p.parse( new FileSource("/home/michael/test.docma", Charset.defaultCharset() ) );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
