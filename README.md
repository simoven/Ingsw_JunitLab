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

## Main classes

- `Main` starts the application on Swing's event-dispatch thread.
- `GalleryFrame` builds the Swing screens and connects user actions to the gallery service.
- `GalleryService` manages in-memory accounts and galleries. It validates registration, checks logins, and supports adding, searching, favoriting, and removing images.
- `GalleryImage` represents one gallery entry, including its title, file path, tags, and favorite status.

## Student testing exercise

Write JUnit 5 unit tests that aim to cover all the application's functionalities. Focus primarily on `GalleryService` and `GalleryImage`, so tests can run without opening the Swing interface. Consider normal behavior, invalid inputs, boundary cases, and interactions between multiple users. For example, test registration and login, gallery ownership, adding and removing images, tag and title searches, favorites, and the contents and mutability of returned collections.

## Useful JUnit 5 features

The starter tests in `src/test/java/org/example/GalleryServiceTest.java` demonstrate:

- `@Test` for an individual test and `@BeforeEach` to set up fresh state.
- `@ParameterizedTest` with `@ValueSource` to run one test with multiple inputs.
- `assertEquals` to compare expected and actual values.
- `assertTrue` and `assertFalse` to check boolean conditions.
- `assertThrows` to verify that invalid operations throw the expected exception.
- `assertAll` to group related assertions so each one is evaluated.

Other useful assertions include `assertNull`, `assertNotNull`, `assertNotEquals`, and `assertDoesNotThrow`. Run the suite with `mvn test`.
