package org.example;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class GalleryImage {
    private final UUID id;
    private final String title;
    private final String imagePath;
    private final Set<String> tags;
    private boolean favorite;

    public GalleryImage(String title, String imagePath, Set<String> tags) {
        this.id = UUID.randomUUID();
        this.title = requireText(title, "Title");
        this.imagePath = requireText(imagePath, "Image path");
        Objects.requireNonNull(tags, "Tags cannot be null");

        Set<String> normalizedTags = new LinkedHashSet<>();
        for (String tag : tags) {
            if (tag != null && !tag.isBlank()) {
                normalizedTags.add(tag.trim().toLowerCase(Locale.ROOT));
            }
        }
        this.tags = Collections.unmodifiableSet(normalizedTags);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Set<String> getTags() {
        return tags;
    }

    public boolean isFavorite() {
        return favorite;
    }

    void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
