package org.example;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GalleryService {
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH_BITS = 256;
    private static final int HASH_ITERATIONS = 65_536;

    private final Map<String, Account> accounts = new HashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    public void register(String username, String password) {
        String normalizedUsername = normalizeUsername(username);
        validatePassword(password);
        String accountKey = accountKey(normalizedUsername);
        if (accounts.containsKey(accountKey)) {
            throw new IllegalStateException("That username is already registered");
        }

        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        char[] passwordChars = password.toCharArray();
        try {
            accounts.put(accountKey, new Account(salt, hashPassword(passwordChars, salt)));
        } finally {
            Arrays.fill(passwordChars, '\0');
        }
    }

    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        Account account = accounts.get(accountKey(username.trim()));
        if (account == null) {
            return false;
        }

        char[] passwordChars = password.toCharArray();
        try {
            byte[] candidateHash = hashPassword(passwordChars, account.salt);
            return MessageDigest.isEqual(account.passwordHash, candidateHash);
        } finally {
            Arrays.fill(passwordChars, '\0');
        }
    }

    public GalleryImage addImage(String username, String title, String imagePath, Set<String> tags) {
        GalleryImage image = new GalleryImage(title, imagePath, tags);
        requireAccount(username).images.add(image);
        return image;
    }

    public List<GalleryImage> getImages(String username) {
        return List.copyOf(requireAccount(username).images);
    }

    public List<GalleryImage> findByTag(String username, String tag) {
        if (tag == null || tag.isBlank()) {
            throw new IllegalArgumentException("Tag cannot be blank");
        }
        String normalizedTag = tag.trim().toLowerCase(Locale.ROOT);
        return requireAccount(username).images.stream()
                .filter(image -> image.getTags().contains(normalizedTag))
                .toList();
    }

    public List<GalleryImage> searchImages(
            String username, String titleQuery, String tag, boolean favoritesOnly) {
        String normalizedTitle = titleQuery == null ? "" : titleQuery.trim().toLowerCase(Locale.ROOT);
        String normalizedTag = tag == null ? "" : tag.trim().toLowerCase(Locale.ROOT);
        return requireAccount(username).images.stream()
                .filter(image -> normalizedTitle.isEmpty()
                        || image.getTitle().toLowerCase(Locale.ROOT).contains(normalizedTitle))
                .filter(image -> normalizedTag.isEmpty() || image.getTags().contains(normalizedTag))
                .filter(image -> !favoritesOnly || image.isFavorite())
                .toList();
    }

    public boolean setFavorite(String username, UUID imageId, boolean favorite) {
        if (imageId == null) {
            throw new IllegalArgumentException("Image id cannot be null");
        }
        return requireAccount(username).images.stream()
                .filter(image -> image.getId().equals(imageId))
                .findFirst()
                .map(image -> {
                    image.setFavorite(favorite);
                    return true;
                })
                .orElse(false);
    }

    public boolean removeImage(String username, UUID imageId) {
        if (imageId == null) {
            throw new IllegalArgumentException("Image id cannot be null");
        }
        return requireAccount(username).images.removeIf(image -> image.getId().equals(imageId));
    }

    private Account requireAccount(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        Account account = accounts.get(accountKey(username.trim()));
        if (account == null) {
            throw new IllegalArgumentException("No gallery exists for that username");
        }
        return account;
    }

    private static String normalizeUsername(String username) {
        if (username == null || username.isBlank() || username.trim().length() < 3) {
            throw new IllegalArgumentException("Username must contain at least 3 characters");
        }
        return username.trim();
    }

    private static void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters");
        }
    }

    private static String accountKey(String username) {
        return username.toLowerCase(Locale.ROOT);
    }

    private static byte[] hashPassword(char[] password, byte[] salt) {
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, HASH_ITERATIONS, HASH_LENGTH_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(keySpec)
                    .getEncoded();
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Could not verify account credentials", exception);
        } finally {
            keySpec.clearPassword();
        }
    }

    private static final class Account {
        private final byte[] salt;
        private final byte[] passwordHash;
        private final List<GalleryImage> images = new ArrayList<>();

        private Account(byte[] salt, byte[] passwordHash) {
            this.salt = salt;
            this.passwordHash = passwordHash;
        }
    }
}
