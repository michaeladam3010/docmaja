package com.docmala.parser;

public class FormattedText {
    public final String text;
    public final boolean bold;
    public final boolean italic;
    public final boolean monospaced;
    public final boolean stroked;
    public final boolean underlined;

    public FormattedText(String text, boolean bold, boolean italic, boolean monospaced, boolean stroked, boolean underlined) {
        this.text = text;
        this.bold = bold;
        this.italic = italic;
        this.monospaced = monospaced;
        this.stroked = stroked;
        this.underlined = underlined;
    }

    public static class Link extends FormattedText {
        public final String url;
        public final Type type;

        public Link(String text, boolean bold, boolean italic, boolean monospaced, boolean stroked, boolean underlined, String url, Type type) {
            super(text, bold, italic, monospaced, stroked, underlined);
            this.url = url;
            this.type = type;
        }

        public enum Type {
            Web, IntraFile, InterFile
        }
    }

    public static final class Builder {
        private String text;
        private boolean bold = false;
        private boolean italic = false;
        private boolean monospaced = false;
        private boolean stroked = false;
        private boolean underlined = false;
        private String url;
        private Link.Type type;

        public boolean isBold() {
            return bold;
        }

        public Builder setBold(boolean bold) {
            this.bold = bold;
            return this;
        }

        public boolean isItalic() {
            return italic;
        }

        public Builder setItalic(boolean italic) {
            this.italic = italic;
            return this;
        }

        public boolean isMonospaced() {
            return monospaced;
        }

        public Builder setMonospaced(boolean monospaced) {
            this.monospaced = monospaced;
            return this;
        }

        public boolean isStroked() {
            return stroked;
        }

        public Builder setStroked(boolean stroked) {
            this.stroked = stroked;
            return this;
        }

        public boolean isUnderlined() {
            return underlined;
        }

        public Builder setUnderlined(boolean underlined) {
            this.underlined = underlined;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder toggleBold() {
            bold = !bold;
            return this;
        }

        public Builder toggleItalic() {
            italic = !italic;
            return this;
        }

        public Builder toggleMonospaced() {
            monospaced = !monospaced;
            return this;
        }

        public Builder toggleStroked() {
            stroked = !stroked;
            return this;
        }

        public Builder toggleUnderlined() {
            underlined = !underlined;
            return this;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setType(Link.Type type) {
            this.type = type;
        }

        public FormattedText build() {
            return new FormattedText(text, bold, italic, monospaced, stroked, underlined);
        }

        public FormattedText.Link buildLink() {
            return new Link(text, bold, italic, monospaced, stroked, underlined, url, type);
        }
    }
}

