# In-memory Photo Gallery

A small Java Swing application for practicing unit testing with JUnit 5. Each registered account has its own gallery. Accounts and image entries are held in memory only and disappear when the application closes. Image files remain where the user selected them; the gallery stores their paths, not copies.

## Run

Requires Java 17 and Maven:

```shell
mvn test
mvn package
java -cp target/classes org.example.Main
```

Register a username (at least 3 characters) and password (at least 6 characters), then add image files, assign comma-separated tags, search by title and/or tag, mark favorites, show favorites only, and delete selected images. Passwords are salted and hashed while the application is running.

## JUnit examples

`GalleryServiceTest` demonstrates:

- `@BeforeEach` for fresh test state.
- `@Test` with `assertAll`, `assertTrue`, `assertFalse`, and `assertEquals`.
- `assertThrows` for invalid input and duplicate users.
- `@ParameterizedTest` with `@ValueSource`.
- Testing filtering, title search, favorites, ownership, deletion, and immutable return values without opening the Swing UI.
