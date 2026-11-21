package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GalleryServiceTest {

    private GalleryService galleryService;

    // @BeforeEach crea uno stato nuovo per ogni test, indipendente dall'ordine di esecuzione.
    @BeforeEach
    void setUp() {
        galleryService = new GalleryService();
        galleryService.register("alice", "password123");
    }

    @Test
    void testRemoveExistingImage() {
        GalleryImage image = galleryService.addImage("alice", "Sunset", "/sunset.jpg",Set.of("nature"));
        boolean result = galleryService.removeImage("alice", image.getId());

        assertTrue(result);
        assertTrue(galleryService.getImages("alice").isEmpty());
    }

    @Test
    void testRemoveNonExistingImage() {
        GalleryImage image = galleryService.addImage(
                "alice", "Sunset", "/sunset.jpg",
                Set.of("nature"));

        UUID nonExistingId = UUID.randomUUID();

        boolean result = galleryService.removeImage(
                "alice", nonExistingId);

        assertFalse(result);

        // Un tentativo fallito non deve modificare le immagini già presenti.
        assertEquals(1, galleryService.getImages("alice").size());
        assertEquals(image.getId(),
                galleryService.getImages("alice").get(0).getId());
    }

    @Test
    void testRemoveImageFromEmptyGallery() {
        boolean result = galleryService.removeImage(
                "alice", UUID.randomUUID());

        assertFalse(result);
        assertTrue(galleryService.getImages("alice").isEmpty());
    }

    @Test
    void testRemoveImageTwice() {
        GalleryImage image = galleryService.addImage(
                "alice", "Sunset", "/sunset.jpg",
                Set.of("nature"));

        boolean firstRemoval = galleryService.removeImage(
                "alice", image.getId());

        boolean secondRemoval = galleryService.removeImage(
                "alice", image.getId());

        assertTrue(firstRemoval);

        // La seconda rimozione restituisce false perché l'immagine è già stata eliminata.
        assertFalse(secondRemoval);
        assertTrue(galleryService.getImages("alice").isEmpty());
    }

    @Test
    void testRemoveOnlySelectedImage() {
        GalleryImage image1 = galleryService.addImage(
                "alice", "Sunset", "/sunset.jpg",
                Set.of("nature"));

        GalleryImage image2 = galleryService.addImage(
                "alice", "Mountain", "/mountain.jpg",
                Set.of("travel"));

        boolean result = galleryService.removeImage(
                "alice", image1.getId());

        List<GalleryImage> images =
                galleryService.getImages("alice");

        assertTrue(result);
        assertEquals(1, images.size());
        assertEquals(image2.getId(), images.get(0).getId());
    }

    @Test
    void testRemoveImageFromMiddle() {
        GalleryImage image1 = galleryService.addImage(
                "alice", "Sunset", "/sunset.jpg",
                Set.of("nature"));

        GalleryImage image2 = galleryService.addImage(
                "alice", "Mountain", "/mountain.jpg",
                Set.of("travel"));

        GalleryImage image3 = galleryService.addImage(
                "alice", "Ocean", "/ocean.jpg",
                Set.of("water"));

        boolean result = galleryService.removeImage(
                "alice", image2.getId());

        List<GalleryImage> images =
                galleryService.getImages("alice");

        // Verifichiamo anche che le immagini rimaste conservino il loro ordine.
        assertTrue(result);
        assertEquals(2, images.size());
        assertEquals(image1.getId(), images.get(0).getId());
        assertEquals(image3.getId(), images.get(1).getId());
    }

    @Test
    void testRemoveImageFromDifferentAccount() {
        galleryService.register("bob", "password456");

        GalleryImage image = galleryService.addImage(
                "alice", "Sunset", "/sunset.jpg",
                Set.of("nature"));

        boolean result = galleryService.removeImage(
                "bob", image.getId());

        // Un'operazione sulla galleria di Bob non deve cambiare quella di Alice.
        assertFalse(result);
        assertEquals(1, galleryService.getImages("alice").size());
        assertTrue(galleryService.getImages("bob").isEmpty());
    }

    @Test
    void testRemoveImageWithNullId() {
        // Verifica il messaggio esatto quando il testo dell'errore fa parte del comportamento atteso.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> galleryService.removeImage("alice", null));
        assertEquals("Image id cannot be null", exception.getMessage());
    }

    @Test
    void testRemoveImageWithNullUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> galleryService.removeImage(null, UUID.randomUUID()));
    }

    @Test
    void testRemoveImageWithBlankUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> galleryService.removeImage("   ", UUID.randomUUID()));
    }

    @Test
    void testRemoveImageWithUnknownUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> galleryService.removeImage("unknown", UUID.randomUUID()));
    }

    // AGGIUNTA DI IMMAGINI

    @Test
    void testAddImageSuccessfully() {
        GalleryImage image = galleryService.addImage(
                "alice", "Sunset", "/sunset.jpg",
                Set.of("nature"));

        assertNotNull(image);
        assertEquals(1, galleryService.getImages("alice").size());
        // assertSame verifica che il risultato sia proprio l'oggetto conservato nella galleria.
        assertSame(image, galleryService.getImages("alice").get(0));
    }

    @Test
    void testAddImageWithCorrectDetails() {
        GalleryImage image = galleryService.addImage(
                "alice", "Sunset", "/sunset.jpg",
                Set.of("nature", "travel"));

        assertEquals("Sunset", image.getTitle());
        assertEquals("/sunset.jpg", image.getImagePath());
        assertEquals(Set.of("nature", "travel"), image.getTags());
        assertNotNull(image.getId());
    }

    @Test
    void testAddMultipleImages() {
        GalleryImage image1 = galleryService.addImage(
                "alice", "Sunset", "/sunset.jpg",
                Set.of("nature"));

        GalleryImage image2 = galleryService.addImage(
                "alice", "Mountain", "/mountain.jpg",
                Set.of("travel"));

        List<GalleryImage> images =
                galleryService.getImages("alice");

        // Gli ID verificano quali immagini sono presenti e in quale ordine.
        assertEquals(2, images.size());
        assertEquals(image1.getId(), images.get(0).getId());
        assertEquals(image2.getId(), images.get(1).getId());
    }

    @Test
    void testAddImageToDifferentAccounts() {
        galleryService.register("bob", "password456");

        GalleryImage image1 = galleryService.addImage(
                "alice", "Sunset", "/sunset.jpg",
                Set.of("nature"));

        GalleryImage image2 = galleryService.addImage(
                "bob", "Mountain", "/mountain.jpg",
                Set.of("travel"));

        assertEquals(1, galleryService.getImages("alice").size());
        assertEquals(1, galleryService.getImages("bob").size());

        assertEquals(image1.getId(), galleryService.getImages("alice").get(0).getId());
        assertEquals(image2.getId(), galleryService.getImages("bob").get(0).getId());
    }

    @Test
    void testAddImageWithCaseInsensitiveUsername() {
        GalleryImage image = galleryService.addImage(
                "ALICE", "Sunset", "/sunset.jpg",
                Set.of("nature"));

        assertNotNull(image);
        assertEquals(1, galleryService.getImages("alice").size());
    }

    @Test
    void testAddImageWithSpacesInUsername() {
        GalleryImage image = galleryService.addImage(
                "  alice  ", "Sunset", "/sunset.jpg",
                Set.of("nature"));

        assertNotNull(image);
        assertEquals(1, galleryService.getImages("alice").size());
    }

    @Test
    void testAddImageWithNullUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> galleryService.addImage(
                        null, "Sunset", "/sunset.jpg", Set.of("nature")));
        // Dopo l'eccezione, verifichiamo che non sia stata aggiunta alcuna immagine.
        assertTrue(galleryService.getImages("alice").isEmpty());
    }

    @Test
    void testAddImageWithBlankUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> galleryService.addImage(
                        "   ", "Sunset", "/sunset.jpg", Set.of("nature")));
        assertTrue(galleryService.getImages("alice").isEmpty());
    }

    @Test
    void testAddImageWithUnknownUsername() {
        // Verifica il messaggio esatto quando il testo dell'errore fa parte del comportamento atteso.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> galleryService.addImage(
                        "unknown", "Sunset", "/sunset.jpg", Set.of("nature")));
        assertEquals("No gallery exists for that username", exception.getMessage());
        assertTrue(galleryService.getImages("alice").isEmpty());
    }


}
