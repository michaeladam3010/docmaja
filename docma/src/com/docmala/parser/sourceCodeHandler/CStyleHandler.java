package com.docmala.parser.sourceCodeHandler;

import com.docmala.parser.ISourceCodeHandler;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * The CStyleHandler allows for including docma documentation from c-syle commented files.
 * Docma comments must follow the style: /*docma or /*docma:part
 * Users can parse docma comments from files using filename.ext or filename.ext:part as filename
 */
public class CStyleHandler implements ISourceCodeHandler{
    boolean isDocumentation = false;
    NavigableMap<Integer, Integer> content;

    static public String[] fileExtensions() {
        String[] extensions = {".cpp", ".hpp", ".c", ".h", ".java"};
        return extensions;
    }

    @Override
    public void init(String label, String memory) {
        content = new TreeMap<>();
        String tag = "";
        if( label == null || label.isEmpty() ) {
            tag = "/*docma";
        } else {
            tag = "/*docma:"+label;
        }
        int startIndex = 0;
        while( true ) {
            startIndex = memory.indexOf(tag, startIndex);
            if (startIndex > -1) {
                int endIndex = memory.indexOf("*/", startIndex);
                startIndex = memory.indexOf("\n", startIndex) + 1;
                while( startIndex < endIndex ) {
                    if (startIndex > 0) {
                        startIndex = memory.indexOf("*", startIndex) + 1;
                        if(memory.charAt(startIndex) == '/')
                            break;
                        while( memory.charAt(startIndex) == ' ' || memory.charAt(startIndex) == '\t' ) {
                            startIndex++;
                        }

                        int lineStartIndex = startIndex;
                        if (startIndex > 0) {
                            int lineEndIndex = memory.indexOf("\n", startIndex) + 1;
                            if (lineEndIndex <= 0) {
                                lineEndIndex = memory.length();
                                startIndex = -1;
                            } else {
                                startIndex = lineEndIndex;
                            }
                            content.put(lineStartIndex, lineEndIndex);
                        }
                    }
                }
            } else {
                break;
            }
        }
    }

    @Override
    public boolean isPartOfDocumentation(int index, String memory) {
        Map.Entry<Integer, Integer> entry = content.floorEntry(index);
        if( entry != null ) {
            return index < entry.getValue();
        }
        return false;
    }
}
