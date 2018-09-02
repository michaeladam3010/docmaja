package com.docmala.parser;

public class FormattedText {
    public final String text;
    public final Style style;

    public FormattedText(String text, Style style) {
        this.text = text;
        this.style = style;
    }

    public static class Color {
        public final int r; ///< range 0-255
        public final int g; ///< range 0-255
        public final int b; ///< range 0-255

        public Color() {
            this.r = 0;
            this.g = 0;
            this.b = 0;
        }

        public Color(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public boolean isColored() {
            return !(r == 0 && g == 0 && b == 0);
        }
    }

    public static class Style {
        public boolean bold = false;
        public boolean italic = false;
        public boolean monospaced = false;
        public boolean stroked = false;
        public boolean underlined = false;
        public Color color = new Color();

        public Style() {}

        public Style(Style other) {
            bold = other.bold;
            italic = other.italic;
            monospaced = other.monospaced;
            stroked = other.stroked;
            underlined = other.underlined;
            color = other.color;
        }
    }

    public static class Link extends FormattedText {
        public final String url;
        public final Type type;

        public Link(String text, String url, Type type, Style style) {
            super(text, style);
            this.url = url;
            this.type = type;
        }

        public enum Type {
            Web, IntraFile, InterFile
        }
    }

    public static class Image extends FormattedText {
        public final byte[] data;
        public final String fileType;

        public Image(String text, byte[] data, String fileType, Style style) {
            super(text, style);
            this.data = data;
            this.fileType = fileType;
        }
    }

    public static final class Builder {
        private String text;
        private Style style = new Style();
        private String url;
        private Link.Type type;

        public boolean isBold() {
            return style.bold;
        }

        public Builder setBold(boolean bold) {
            this.style.bold = bold;
            return this;
        }

        public boolean isItalic() {
            return style.italic;
        }

        public Builder setItalic(boolean italic) {
            this.style.italic = italic;
            return this;
        }

        public boolean isMonospaced() {
            return style.monospaced;
        }

        public Builder setMonospaced(boolean monospaced) {
            this.style.monospaced = monospaced;
            return this;
        }

        public boolean isStroked() {
            return style.stroked;
        }

        public Builder setStroked(boolean stroked) {
            this.style.stroked = stroked;
            return this;
        }

        public boolean isUnderlined() {
            return style.underlined;
        }

        public Builder setUnderlined(boolean underlined) {
            this.style.underlined = underlined;
            return this;
        }

        public Color color() {
            return this.style.color;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder toggleBold() {
            style.bold = !style.bold;
            return this;
        }

        public Builder toggleItalic() {
            style.italic = !style.italic;
            return this;
        }

        public Builder toggleMonospaced() {
            style.monospaced = !style.monospaced;
            return this;
        }

        public Builder toggleStroked() {
            style.stroked = !style.stroked;
            return this;
        }

        public Builder toggleUnderlined() {
            style.underlined = !style.underlined;
            return this;
        }

        public Builder setColor(Color color) {
            this.style.color = color;
            return this;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setType(Link.Type type) {
            this.type = type;
        }

        public void setStyle(Style style) {
            this.style = style;
        }

        public FormattedText build() {
            if( style == null )
                return new FormattedText(text, null);
            return new FormattedText(text, new Style(style));
        }

        public FormattedText.Link buildLink() {
            Link link;
            if( style == null )
                link = new Link(text, url, type, null);
            else
                link = new Link(text, url, type, new Style(style));

            url = "";
            return link;
        }

        public FormattedText.Image buildImage(byte[] data, String fileType) {
            if( style == null )
                return new Image(text, data, fileType, null);
            return new Image(text, data, fileType, new Style(style));
        }
    }
}

