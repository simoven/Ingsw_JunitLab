package org.example;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GalleryFrame extends JFrame {
    private static final String LOGIN_CARD = "login";
    private static final String GALLERY_CARD = "gallery";

    private final GalleryService galleryService;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final DefaultListModel<GalleryImage> imageListModel = new DefaultListModel<>();
    private final JList<GalleryImage> imageList = new JList<>(imageListModel);
    private final JLabel previewLabel = new JLabel("Select an image to preview", SwingConstants.CENTER);
    private final JLabel accountLabel = new JLabel();
    private final JLabel galleryStatusLabel = new JLabel("No images");
    private final JTextField titleSearchField = new JTextField(16);
    private final JTextField tagFilterField = new JTextField(14);
    private final JCheckBox favoritesOnlyCheckBox = new JCheckBox("Favorites only");
    private final JButton favoriteButton = new JButton("☆ Add to favorites");
    private String currentUsername;

    public GalleryFrame(GalleryService galleryService) {
        super("In-memory Photo Gallery");
        this.galleryService = galleryService;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 540));
        setSize(1000, 680);
        setLocationRelativeTo(null);

        cards.add(createLoginPanel(), LOGIN_CARD);
        cards.add(createGalleryPanel(), GALLERY_CARD);
        setContentPane(cards);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(50, 220, 50, 220),
                BorderFactory.createTitledBorder("Welcome to your photo gallery")));
        JLabel description = new JLabel(
                "<html>Create an account or log in to organize your photos.<br>"
                        + "Your gallery is kept in memory for this session.</html>");
        form.add(description);
        form.add(new JLabel("Username"));
        form.add(usernameField);
        form.add(new JLabel("Password"));
        form.add(passwordField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loginButton = new JButton("Log in");
        JButton registerButton = new JButton("Register");
        loginButton.setPreferredSize(new Dimension(120, 34));
        registerButton.setPreferredSize(new Dimension(120, 34));
        loginButton.addActionListener(event -> logIn());
        registerButton.addActionListener(event -> register());
        buttons.add(loginButton);
        buttons.add(registerButton);
        form.add(buttons);
        panel.add(form);
        return panel;
    }

    private JPanel createGalleryPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JPanel north = new JPanel(new BorderLayout(8, 8));
        JPanel heading = new JPanel(new BorderLayout());
        JLabel title = new JLabel("My Photo Gallery");
        title.setFont(title.getFont().deriveFont(22f));
        accountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        heading.setBorder(BorderFactory.createEmptyBorder(0, 2, 4, 2));
        heading.add(title, BorderLayout.WEST);
        heading.add(accountLabel, BorderLayout.EAST);
        north.add(heading, BorderLayout.NORTH);

        JPanel controls = new JPanel(new GridLayout(0, 1, 4, 4));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        JButton addButton = new JButton("Add image");
        JButton deleteButton = new JButton("Delete selected");
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Show all");
        JButton logoutButton = new JButton("Log out");
        addButton.addActionListener(event -> addImage());
        deleteButton.addActionListener(event -> deleteSelectedImage());
        favoriteButton.addActionListener(event -> toggleSelectedFavorite());
        searchButton.addActionListener(event -> applyFilters());
        clearButton.addActionListener(event -> {
            titleSearchField.setText("");
            tagFilterField.setText("");
            favoritesOnlyCheckBox.setSelected(false);
            applyFilters();
        });
        logoutButton.addActionListener(event -> logOut());
        actions.add(addButton);
        actions.add(deleteButton);
        actions.add(favoriteButton);
        actions.add(logoutButton);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        filters.add(new JLabel("Title:"));
        filters.add(titleSearchField);
        filters.add(new JLabel("Tag:"));
        filters.add(tagFilterField);
        filters.add(favoritesOnlyCheckBox);
        filters.add(searchButton);
        filters.add(clearButton);
        controls.add(actions);
        controls.add(filters);
        north.add(controls, BorderLayout.CENTER);
        panel.add(north, BorderLayout.NORTH);

        imageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        imageList.setFixedCellHeight(34);
        imageList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (value instanceof GalleryImage image) {
                    String tags = String.join(", ", image.getTags());
                    String favoriteMark = image.isFavorite() ? "★  " : "";
                    label.setText(favoriteMark + image.getTitle()
                            + (tags.isEmpty() ? "" : "  ·  " + tags));
                    label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                }
                return label;
            }
        });
        imageList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                showPreview(imageList.getSelectedValue());
                updateFavoriteButton();
            }
        });
        JScrollPane listScrollPane = new JScrollPane(imageList);
        listScrollPane.setBorder(BorderFactory.createTitledBorder("Your photos"));
        previewLabel.setBorder(BorderFactory.createTitledBorder("Preview"));
        previewLabel.setPreferredSize(new Dimension(500, 440));
        JPanel content = new JPanel(new GridLayout(1, 2, 12, 0));
        content.add(listScrollPane);
        content.add(previewLabel);
        panel.add(content, BorderLayout.CENTER);
        galleryStatusLabel.setBorder(BorderFactory.createEmptyBorder(6, 2, 0, 2));
        panel.add(galleryStatusLabel, BorderLayout.SOUTH);
        return panel;
    }

    private void logIn() {
        String username = usernameField.getText().trim();
        if (!galleryService.login(username, new String(passwordField.getPassword()))) {
            showError("Username or password is incorrect.");
            return;
        }
        openGallery(username);
    }

    private void register() {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();
        try {
            galleryService.register(username, new String(password));
            openGallery(username);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            showError(exception.getMessage());
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private void openGallery(String username) {
        currentUsername = username;
        accountLabel.setText("Signed in as " + username);
        passwordField.setText("");
        titleSearchField.setText("");
        tagFilterField.setText("");
        favoritesOnlyCheckBox.setSelected(false);
        applyFilters();
        cardLayout.show(cards, GALLERY_CARD);
    }

    private void logOut() {
        currentUsername = null;
        accountLabel.setText("");
        usernameField.setText("");
        passwordField.setText("");
        imageListModel.clear();
        previewLabel.setIcon(null);
        previewLabel.setText("Select an image to preview");
        galleryStatusLabel.setText("No images");
        cardLayout.show(cards, LOGIN_CARD);
    }

    private void addImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Image files (jpg, png, gif, bmp)", "jpg", "jpeg", "png", "gif", "bmp"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = chooser.getSelectedFile();
        String defaultTitle = selectedFile.getName();
        int extensionIndex = defaultTitle.lastIndexOf('.');
        if (extensionIndex > 0) {
            defaultTitle = defaultTitle.substring(0, extensionIndex);
        }

        JTextField titleField = new JTextField(defaultTitle);
        JTextField tagsField = new JTextField();
        JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
        form.add(new JLabel("Title"));
        form.add(titleField);
        form.add(new JLabel("Tags, separated by commas"));
        form.add(tagsField);
        if (JOptionPane.showConfirmDialog(this, form, "Add image",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }

        Set<String> tags = new LinkedHashSet<>();
        for (String tag : tagsField.getText().split(",")) {
            if (!tag.isBlank()) {
                tags.add(tag.trim());
            }
        }
        try {
            galleryService.addImage(currentUsername, titleField.getText(), selectedFile.getAbsolutePath(), tags);
            applyFilters();
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void applyFilters() {
        List<GalleryImage> filteredImages = galleryService.searchImages(
                currentUsername,
                titleSearchField.getText(),
                tagFilterField.getText(),
                favoritesOnlyCheckBox.isSelected());
        refreshImages(filteredImages);
    }

    private void deleteSelectedImage() {
        GalleryImage selectedImage = imageList.getSelectedValue();
        if (selectedImage == null) {
            showError("Select an image to delete.");
            return;
        }
        galleryService.removeImage(currentUsername, selectedImage.getId());
        applyFilters();
    }

    private void toggleSelectedFavorite() {
        GalleryImage selectedImage = imageList.getSelectedValue();
        if (selectedImage == null) {
            showError("Select an image to add to or remove from favorites.");
            return;
        }
        galleryService.setFavorite(currentUsername, selectedImage.getId(), !selectedImage.isFavorite());
        applyFilters();
    }

    private void updateFavoriteButton() {
        GalleryImage selectedImage = imageList.getSelectedValue();
        favoriteButton.setEnabled(selectedImage != null);
        favoriteButton.setText(selectedImage != null && selectedImage.isFavorite()
                ? "★ Remove from favorites"
                : "☆ Add to favorites");
    }

    private void refreshImages(List<GalleryImage> images) {
        imageListModel.clear();
        images.forEach(imageListModel::addElement);
        int totalImages = galleryService.getImages(currentUsername).size();
        galleryStatusLabel.setText(images.size() + " shown · " + totalImages + " total");
        if (images.isEmpty()) {
            showPreview(null);
        }
        updateFavoriteButton();
    }

    private void showPreview(GalleryImage image) {
        previewLabel.setIcon(null);
        if (image == null) {
            previewLabel.setText("Select an image to preview");
            return;
        }
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(image.getImagePath()));
            if (bufferedImage == null) {
                previewLabel.setText("Preview unavailable");
                return;
            }
            double scale = Math.min(1.0, Math.min(
                    460.0 / bufferedImage.getWidth(), 400.0 / bufferedImage.getHeight()));
            Image scaledImage = bufferedImage.getScaledInstance(
                    (int) (bufferedImage.getWidth() * scale),
                    (int) (bufferedImage.getHeight() * scale),
                    Image.SCALE_SMOOTH);
            previewLabel.setText("");
            previewLabel.setIcon(new javax.swing.ImageIcon(scaledImage));
        } catch (IOException exception) {
            previewLabel.setText("Preview unavailable");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Gallery", JOptionPane.ERROR_MESSAGE);
    }
}
