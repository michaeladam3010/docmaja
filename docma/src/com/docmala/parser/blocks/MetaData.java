package com.docmala.parser.blocks;

import com.docmala.parser.Anchor;
import com.docmala.parser.Block;
import com.docmala.parser.SourcePosition;

import java.util.ArrayDeque;

public class MetaData extends Block {
    public final String key;
    public final ArrayDeque<String> value;
    public final ModificationType modificationType;

    public MetaData(SourcePosition start, SourcePosition end, String key, ArrayDeque<String> value, ModificationType modificationType) {
        super(start, end, new ArrayDeque<Anchor>());
        this.key = key;
        this.value = value;
        this.modificationType = modificationType;
    }

    //hint: One drawback of this implementation is, that code locations of other meta data elements are discarded.
    //        Is this bad? I don't know so far.
    public MetaData modify(MetaData other) {
        switch (modificationType) {
            case Append:
                ArrayDeque<String> values = this.value;
                values.addAll(other.value);
                return new MetaData(start, end, key, values, modificationType);
            case Overwrite:
                return other;
            case IgnoreIfPresent:
                return this;
        }
        return this;
    }

    // implement constructor, builder and parser. Don't forget to decide on how to handle included documents.
    public enum ModificationType {
        Append,
        Overwrite,
        IgnoreIfPresent
    }

    public static class Builder {
        public String value;
        ModificationType modificationType;
        private SourcePosition start;
        private SourcePosition end;
        private String key;

        public Builder setStart(SourcePosition start) {
            this.start = start;
            return this;
        }

        public Builder setEnd(SourcePosition end) {
            this.end = end;
            return this;
        }

        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public Builder setModificationType(ModificationType modificationType) {
            this.modificationType = modificationType;
            return this;
        }

        public MetaData build() {
            ArrayDeque<String> values = new ArrayDeque<>();
            values.add(value);
            return new MetaData(start, end, key, values, modificationType);
        }

    }
}
