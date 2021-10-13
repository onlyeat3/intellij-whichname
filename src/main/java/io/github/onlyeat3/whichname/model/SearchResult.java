package io.github.onlyeat3.whichname.model;

public class SearchResult {
    private String origin;
    private String word;

    private String camel;
    private String pascal;
    private String underline;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getCamel() {
        return camel;
    }

    public void setCamel(String camel) {
        this.camel = camel;
    }

    public String getPascal() {
        return pascal;
    }

    public void setPascal(String pascal) {
        this.pascal = pascal;
    }

    public String getUnderline() {
        return underline;
    }

    public void setUnderline(String underline) {
        this.underline = underline;
    }
}
