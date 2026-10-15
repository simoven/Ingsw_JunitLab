# In-memory Photo Gallery

This Java Swing application is a small photo-gallery manager and a practice project for learning JUnit 5. Users can create an account and manage a personal gallery during the current run of the program. Gallery data is kept in memory and is lost when the program closes. Image files are not copied; the gallery stores their paths.

## What the application can do

- Register an account and log in.
- Add image files with a title and tags.
- Search images by title and/or tag.
- Sort images by title or date added.
- Mark images as favorites and show favorites only.
- Edit image titles and tags, or delete images.
- Keep each user's gallery associated with that account.

## Assignment

Write JUnit 5 unit tests to check the application's behavior. Focus on the behavior and rules of the gallery rather than testing Swing components directly. The `GalleryService` and `GalleryImage` classes are useful places to start because most of their behavior can be tested without opening a window.

Think about successful operations, invalid input, boundary values, repeated operations, and interactions between multiple users. For example, consider what should happen when:

- A user registers with a username at the minimum allowed length.
- A title or image path is blank.
- A tag contains different capitalization or surrounding whitespace.
- A user searches with more than one filter at the same time.
- An image is edited, favorited, or deleted.
- Two users have entries for the same image path.
- A returned collection is read or modified by the caller.

Add your tests under `src/test/java` and run them with:

```shell
mvn test
```

## JUnit 5 examples

Use `@BeforeEach` to create fresh test state and `@Test` for a single behavior:

```java
import org.example.GalleryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GalleryServiceTest {
    private GalleryService gallery;

    @BeforeEach
    void setUp() {
        gallery = new GalleryService();
    }

    @Test
    void registeredUserCanLogInWithTheCorrectPassword() {
        gallery.register("alice", "secret1");

        assertTrue(gallery.login("alice", "secret1"));
        assertFalse(gallery.login("alice", "wrong-password"));
    }
}
```

Use `assertThrows` to check validation behavior:

```java
import static org.junit.jupiter.api.Assertions.assertThrows;

@Test
void registrationRejectsShortUsernames() {
    assertThrows(IllegalArgumentException.class,
            () -> gallery.register("ab", "secret1"));
}
```

Use a parameterized test to check several invalid values with one test method:

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ParameterizedTest
@ValueSource(strings = {"", " ", "ab"})
void rejectsBlankOrShortUsernames(String username) {
    assertThrows(IllegalArgumentException.class,
            () -> gallery.register(username, "secret1"));
}
```

Other useful assertions include `assertEquals`, `assertNotEquals`, `assertNull`, `assertNotNull`, `assertTrue`, `assertFalse`, `assertAll`, and `assertDoesNotThrow`.
