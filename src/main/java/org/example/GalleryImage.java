package org.example;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.time.Instant;

public final class GalleryImage {
    private final UUID id;
    private String title;
    private final String imagePath;
    private Set<String> tags;
    private boolean favorite;
    private final Instant addedAt;

    public GalleryImage(String title, String imagePath, Set<String> tags) {
        this.id = UUID.randomUUID();
        this.title = requireText(title, "Title");
        this.imagePath = requireText(imagePath, "Image path");
        this.tags = normalizeTags(tags);
        this.addedAt = Instant.now();
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }

    private static Set<String> normalizeTags(Set<String> tags) {
        Objects.requireNonNull(tags, "Tags cannot be null");
        Set<String> normalizedTags = new LinkedHashSet<>();
        for (String tag : tags) {
            if (tag != null && !tag.isBlank()) {
                normalizedTags.add(tag.toLowerCase(Locale.ROOT));
            }
        }
        return Collections.unmodifiableSet(normalizedTags);
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

    public Instant getAddedAt() {
        return addedAt;
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

    void updateDetails(String title, Set<String> tags) {
        String validatedTitle = requireText(title, "Title");
        Set<String> normalizedTags = normalizeTags(tags);
        this.title = validatedTitle;
        this.tags = normalizedTags;
    }
}
