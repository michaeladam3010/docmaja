package com.docmala.parser.blocks;

import com.docmala.parser.Block;
import com.docmala.parser.Source;

import java.util.ArrayDeque;

public class List extends Block {
    ArrayDeque<Entry> _entries = new ArrayDeque<>();

    public ArrayDeque<Entry> entries() {
        return _entries;
    }

    @Override
    public String indicators() {
        return "*#";
    }

    @Override
    public Block create() {
        return new List();
    }

    @Override
    protected Source.Window doParse(Source.Window start) {
        while (!start.here().isEof()) {

            Source.Window begin = start.copy();
            if (start.here().isNewLine()) {
                start.moveForward();
                start.skipWhitspaces();
                if (!start.here().equals('*') && !start.here().equals('#')) {
                    return begin;
                }
            }

            int level = 1;
            Type type = Type.Points;
            char startChar = start.here().get();
            switch (startChar) {
                case '*':
                    type = Type.Points;
                    break;
                case '#':
                    type = Type.Numbers;
                    break;
            }

            while (start.here().equals(startChar)) {
                level++;
                start.moveForward();
            }

            Content content = new Content();
            start = content.parse(start);
            ArrayDeque<Entry> entries = _entries;
            for (int i = 1; i < level; i++) {
                if (entries.isEmpty()) {
                    entries.addLast(new Entry(null, type));
                }
                entries = entries.getFirst().entries;
            }
            entries.addLast(new Entry(content, type));
        }

        return start;
    }

    public enum Type {
        Points, Numbers
    }

    public static class Entry {
        Content text;
        Type type;
        ArrayDeque<Entry> entries = new ArrayDeque<>();

        public Entry(Content text, Type type) {
            this.text = text;
            this.type = type;
        }
    }
}
