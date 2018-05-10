package com.docmala.parser.blocks;

import com.docmala.parser.Anchor;
import com.docmala.parser.Block;
import com.docmala.parser.ICaptionable;
import com.docmala.parser.SourcePosition;

import java.util.ArrayDeque;

public class List extends Block implements ICaptionable {
    public final Block content;
    public final Type type;
    public final ArrayDeque<List> entries;
    public final Caption caption;

    public List(SourcePosition start, SourcePosition end, ArrayDeque<Anchor> anchors, Block content, Type type, ArrayDeque<List> entries, Caption caption) {
        super(start, end, anchors);
        this.content = content;
        this.type = type;
        this.entries = entries;
        this.caption = caption;
    }

    @Override
    public Block instanceWithCaption(Caption caption) {
        return new List(start, end, anchors, content, type, entries, new Caption(caption, "list"));
    }

    public enum Type {
        Points, Numbers
    }

    public static class Builder {
        private SourcePosition start;
        private SourcePosition end;
        private ArrayDeque<Anchor> anchors = new ArrayDeque<>();
        private Block content;
        private List.Type type;
        private ArrayDeque<Builder> entries = new ArrayDeque<>();
        private Caption caption = null;

        public Builder setCaption(Caption caption) {
            this.caption = caption;
            return this;
        }

        public Builder setStart(SourcePosition start) {
            this.start = new SourcePosition(start);
            return this;
        }

        public Builder setEnd(SourcePosition end) {
            this.end = new SourcePosition(end);
            return this;
        }

        public Builder addAnchors(ArrayDeque<Anchor> anchors) {
            this.anchors.addAll(anchors);
            return this;
        }

        public Builder setContent(Block content) {
            this.content = content;
            return this;
        }

        public Builder setType(List.Type type) {
            this.type = type;
            return this;
        }

        public Builder add(int level, Builder entry) {
            if (level == 0) {
                entries.add(entry);
            } else {
                if (entries.isEmpty()) {
                    Builder builder = new Builder();
                    builder.setStart(entry.start);
                    builder.setEnd(entry.end);
                    builder.setType(entry.type);
                    entries.add(builder);

                }
                entries.getLast().add(level - 1, entry);
            }
            return this;
        }

        public List build() {

            ArrayDeque<List> e = new ArrayDeque<>();
            for (Builder b : entries)
                e.add(b.build());

            return new List(start, end, anchors, content, type, e, caption);
        }
    }

    /*
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
    protected ISource.Window doParse(ISource.Window start, Document document) {
        while (!start.here().isEof()) {

            ISource.Window begin = start.copy();
            if (start.here().isNewLine()) {
                start.moveForward();
                start.skipWhitespaces();
                if (!start.here().equals('*') && !start.here().equals('#')) {
                    return begin;
                }
            }

            int level = 0;
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

            Content content = new Builder().createContent();
            start = content.parse(start, document);
            ArrayDeque<Entry> entries = _entries;
            for (int i = 1; i < level; i++) {
                if (entries.isEmpty()) {
                    entries.addLast(new Entry(null, type));
                }
                entries = entries.getLast().entries;
            }
            entries.addLast(new Entry(content, type));
        }

        return start;
    }

    public enum Type {
        Points, Numbers
    }

    public static class Entry {
        public final Content content;
        public final Type type;
        public ArrayDeque<Entry> entries = new ArrayDeque<>();

        public Entry(Content content, Type type) {
            this.content = content;
            this.type = type;
        }
    }
    */
}
