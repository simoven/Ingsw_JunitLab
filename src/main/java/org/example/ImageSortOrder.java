package org.example;

public enum ImageSortOrder {
    TITLE_ASCENDING("Title (A-Z)"),
    TITLE_DESCENDING("Title (Z-A)"),
    NEWEST_FIRST("Recently added");

    private final String label;

    ImageSortOrder(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
