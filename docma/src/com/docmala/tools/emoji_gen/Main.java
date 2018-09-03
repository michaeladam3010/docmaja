package com.docmala.tools.emoji_gen;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.BreakIterator;
import java.util.Map;
import java.util.Set;

public class Main {
    /**
     * @param args
     * [0] json file (emojis.json from https://github.com/muan/emojilib)
     * [1] directory of unicode svg files (2/svg folder from https://github.com/twitter/twemoji)
     * [2] output directory
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        FileInputStream input = new FileInputStream(args[0]);
        byte[] bytes = input.readAllBytes();
        Any data = JsonIterator.deserialize(bytes);
        Map<String, Any> stringAnyMap = data.asMap();
        Set<String> keys = stringAnyMap.keySet();

        for( String key : keys) {
            String character = stringAnyMap.get(key).get("char").toString();
            String name = "";

            BreakIterator characterInstance = BreakIterator.getCharacterInstance();
            characterInstance.setText(character);

            int len = character.length();
            for( int i = 0; i < len; ) {
                int hex = character.codePointAt(i);
                i += Character.charCount(hex);
                if( i >= len && hex == 0xfe0f )
                    continue;
                if( !name.isEmpty() ) {
                    name = name + "-";
                }
                name = name + Integer.toHexString(hex);
            }
            try {
                File from = new File(args[1] + "/" + name + ".svg");
                if( from.exists() ) {
                    File to = new File(args[2] + "/" + key + ".svg");
                    Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    name = name.replaceAll("-fe0f", "");
                    from = new File(args[1] + "/" + name + ".svg");
                    if( from.exists() ) {
                        File to = new File(args[2] + "/" + key + ".svg");
                        Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        System.out.println("Unable to create: " + key + ": " + name);
                    }
                }
            } catch (Exception e) {
                System.out.println("Unable to create: " + key + ": " + name);
            }
        }
    }
}
