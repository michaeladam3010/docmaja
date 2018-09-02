package com.docmala.parser;

import com.docmala.parser.blocks.Caption;
import com.docmala.parser.blocks.MetaData;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Document implements IBlockHolder {
    private ArrayDeque<Block> _content = new ArrayDeque<>();
    private Map<String, CaptionTypeData> captionTypeData = new HashMap<>();
    private Map<String,MetaData> _metadata = new HashMap<>();

    public Document() {
        captionTypeData.put("figure", new CaptionTypeData("figure", "Figure %s:"));
        captionTypeData.put("table", new CaptionTypeData("table", "Table %s:"));
        captionTypeData.put("list", new CaptionTypeData("list", "List %s:"));
        captionTypeData.put("listing", new CaptionTypeData("listing", "Listing %s:"));
    }

    public Map<String, CaptionTypeData> captionTypeData() {
        return captionTypeData;
    }

    public ArrayDeque<Block> content() {
        return _content;
    }

    public Map<String,MetaData> metadata() {
        return _metadata;
    }

    @Override
    public void append(Block block) {
        if( _content.size() > 0 && _content.getLast().start._position > block.start.position() ) {
            int i = 0;
        }

        if (block instanceof MetaData) {
            MetaData meta = (MetaData)block;
            if( _metadata.containsKey(meta.key) ) {
                _metadata.get(meta.key).modify(meta);
            } else {
                _metadata.put(meta.key, meta);
            }
        }

        if (block instanceof ICaptionable && !_content.isEmpty() && _content.getLast() instanceof Caption) {
            _content.add(((ICaptionable) block).instanceWithCaption((Caption) _content.pollLast()));
        } else {
            _content.addLast(block);
        }
    }

    @Override
    public Block last() {
        if (_content.isEmpty()) {
            return null;
        } else {
            return _content.getLast();
        }
    }


    static public class CaptionTypeData {
        public String identifier;
        public String text;

        public CaptionTypeData(String identifier, String text) {
            this.identifier = identifier;
            this.text = text;
        }
    }
}
