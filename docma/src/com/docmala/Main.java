package com.docmala;

import com.docmala.plugins.ouput.Html;
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
            Html htmlOutput = new Html();
            Html.HtmlDocument doc = htmlOutput.generate(p.document());
            doc.write("/home/michael/temp/out.html");

            for( Error error : p.errors()) {
                System.out.printf("%s:(%d,%d):%s%n", error.position().fileName(), error.position().line(), error.position().column(), error.message());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
