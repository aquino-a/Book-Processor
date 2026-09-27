package com.aquino.webParser.swing.autocopy;

import com.aquino.webParser.model.Book;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

/**
 * Review form for one {@link Book}.
 *
 * <p>The form follows the book-entry sheet: cover, titles, contributors,
 * categories, abstracts, and physical details. It owns its panel; callers
 * embed {@link #getPanel()} and push a book in with {@link #setData(Book)}.
 */
public class Review {

    private static final int COVER_WIDTH = 140;
    private static final int COVER_HEIGHT = 190;
    private static final String COVER_PLACEHOLDER = "Book cover";
    private static final String AWARDS_MARKER = "awards:";

    private static final String[] LANGUAGE_CODES = {"", "JAP", "KOR"};
    private static final String[] CURRENCIES = {"", "Yen", "Won"};
    private static final String[] BINDINGS = {"", "PB", "HC"};

    private JPanel panel;

    private JLabel coverLabel;
    private JTextField koreanTitleField;
    private JTextField romanizedTitleField;
    private JTextField originalTitleField;
    private JTextField englishTitleField;
    private JTextField translatedEnglishTitleField;

    private JTextField authorField;
    private JTextField authorStoreField;
    private JTextField authorBwField;
    private JTextField author2Field;
    private JTextField author2StoreField;
    private JTextField author2BwField;
    private JTextField publisherField;
    private JTextField publisherStoreField;
    private JTextField publisherBwField;

    private JTextField category1Field;
    private JTextField category2Field;
    private JTextField category3Field;
    private JComboBox<String> languageCodeCombo;
    private JTextField languageCode2Field;
    private JTextField originalTitleLanguageField;
    private JTextField publishedDateField;
    private JComboBox<String> currencyCombo;
    private JTextField costField;

    private JTextArea abstractEnglishArea;
    private JTextArea abstractNativeArea;

    private JTextField groupField;
    private JTextField sizeField;
    private JComboBox<String> bindingCombo;
    private JTextField pageField;
    private JTextField weightField;
    private JTextArea awardsArea;

    private int coverRequest;

    public Review() {
        init();
    }

    /**
     * Builds every Swing component and assembles the managed panel.
     */
    private void init() {
        coverLabel = new JLabel(COVER_PLACEHOLDER, SwingConstants.CENTER);
        coverLabel.setPreferredSize(new Dimension(COVER_WIDTH, COVER_HEIGHT));
        coverLabel.setMinimumSize(new Dimension(COVER_WIDTH, COVER_HEIGHT));
        coverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        coverLabel.setVerticalAlignment(SwingConstants.CENTER);
        coverLabel.setOpaque(true);
        coverLabel.setBackground(new Color(245, 245, 245));
        coverLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        koreanTitleField = new JTextField(28);
        romanizedTitleField = new JTextField(28);
        originalTitleField = new JTextField(28);
        englishTitleField = new JTextField(28);
        translatedEnglishTitleField = new JTextField(28);

        authorField = new JTextField(18);
        authorStoreField = new JTextField(18);
        authorBwField = new JTextField(18);
        author2Field = new JTextField(18);
        author2StoreField = new JTextField(18);
        author2BwField = new JTextField(18);
        publisherField = new JTextField(18);
        publisherStoreField = new JTextField(18);
        publisherBwField = new JTextField(18);

        category1Field = new JTextField(16);
        category2Field = new JTextField(16);
        category3Field = new JTextField(16);
        languageCodeCombo = new JComboBox<>(LANGUAGE_CODES);
        languageCodeCombo.setEditable(true);
        languageCode2Field = new JTextField(16);
        originalTitleLanguageField = new JTextField(16);
        publishedDateField = new JTextField(16);
        currencyCombo = new JComboBox<>(CURRENCIES);
        currencyCombo.setEditable(true);
        costField = new JTextField(16);

        abstractEnglishArea = textArea(10);
        abstractNativeArea = textArea(10);
        var abstractEnglishScroll = new JScrollPane(abstractEnglishArea);
        var abstractNativeScroll = new JScrollPane(abstractNativeArea);

        groupField = new JTextField(16);
        sizeField = new JTextField(16);
        bindingCombo = new JComboBox<>(BINDINGS);
        bindingCombo.setEditable(true);
        pageField = new JTextField(16);
        weightField = new JTextField(16);
        awardsArea = textArea(4);
        var awardsScroll = new JScrollPane(awardsArea);

        var form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        var constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(0, 0, 12, 0);

        form.add(headerSection(), constraints);
        constraints.gridy++;
        form.add(peopleSection(), constraints);
        constraints.gridy++;
        form.add(metaSection(), constraints);
        constraints.gridy++;
        form.add(row(
                labeled("Abstract English", abstractEnglishScroll),
                labeled("Abstract Native", abstractNativeScroll)), constraints);
        constraints.gridy++;
        constraints.weighty = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
        form.add(physicalSection(awardsScroll), constraints);

        var scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panel = new JPanel(new BorderLayout());
        panel.add(scroll, BorderLayout.CENTER);
    }

    public JPanel getPanel() {
        return panel;
    }

    /**
     * Fills every field from {@code book}. A null book clears the form.
     */
    public void setData(Book book) {
        var data = book == null ? new Book() : book;

        // englishTitle is both the sheet's English Title and the original
        // title of a translated book (the only title Aladin stores for that).
        setField(koreanTitleField, data.getTitle());
        setField(romanizedTitleField, data.getRomanizedTitle());
        setField(originalTitleField, data.getEnglishTitle());
        setField(englishTitleField, data.getEnglishTitle());
        setField(translatedEnglishTitleField, data.getTranslatedTitle());

        setField(authorField, data.getAuthor());
        setField(authorStoreField, data.getAuthorBooks());
        setField(authorBwField, idText(data.getAuthorId()));
        setField(author2Field, data.getAuthor2());
        setField(author2StoreField, data.getAuthor2Books());
        setField(author2BwField, idText(data.getAuthor2Id()));
        setField(publisherField, data.getPublisher());
        setField(publisherStoreField, data.getPublisherBooks());
        setField(publisherBwField, idText(data.getPublisherId()));

        setField(category1Field, data.getCategory());
        setField(category2Field, data.getCategory2());
        setField(category3Field, data.getCategory3());
        setCombo(languageCodeCombo, data.getLanguageCode(), LANGUAGE_CODES);
        // Book stores a single language code. The sheet's second code has no property.
        setField(languageCode2Field, "");
        // "Original title language" is a sheet column with no Book property.
        setField(originalTitleLanguageField, "");
        setField(publishedDateField, firstNonBlank(data.getPublishDateFormatted(), data.getPublishDate()));
        setCombo(currencyCombo, data.getCurrencyType(), CURRENCIES);
        setField(costField, costText(data));

        var summary = data.getSummary();
        setArea(abstractEnglishArea, abstractEnglish(summary));
        setArea(abstractNativeArea, firstNonBlank(data.getDescription(), data.getKoreanDescription()));

        setField(groupField, data.getAgeGroup());
        setField(sizeField, firstNonBlank(data.getBookSizeFormatted(), data.getBookSize()));
        setCombo(bindingCombo, data.getType(), BINDINGS);
        setField(pageField, countText(data.getPages()));
        setField(weightField, countText(data.getWeight()));
        setArea(awardsArea, awards(summary));

        showCover(data.getImageURL());
    }

    private JPanel headerSection() {
        var sourceTitles = vertical(
                labeled("Korean title", koreanTitleField),
                labeled("Romanized", romanizedTitleField),
                labeled("Original title for translated books", originalTitleField));
        var englishTitles = vertical(
                labeled("English title", englishTitleField),
                labeled("English title (translated)", translatedEnglishTitleField),
                new JPanel());
        var titles = row(sourceTitles, englishTitles);

        var header = new JPanel(new BorderLayout(12, 0));
        header.add(coverLabel, BorderLayout.WEST);
        header.add(titles, BorderLayout.CENTER);
        return header;
    }

    private JPanel peopleSection() {
        return row(
                vertical(
                        labeled("Author", authorField),
                        labeled("Author (store)", authorStoreField),
                        labeled("Author (BW)", authorBwField)),
                vertical(
                        labeled("Author 2", author2Field),
                        labeled("Author 2 (store)", author2StoreField),
                        labeled("Author 2 (BW)", author2BwField)),
                vertical(
                        labeled("Publisher", publisherField),
                        labeled("Publisher (store)", publisherStoreField),
                        labeled("Publisher (BW)", publisherBwField)));
    }

    private JPanel metaSection() {
        return row(
                vertical(
                        labeled("Category 1", category1Field),
                        labeled("Category 2", category2Field),
                        labeled("Category 3", category3Field)),
                vertical(
                        labeled("Language code", languageCodeCombo),
                        labeled("Language code 2", languageCode2Field),
                        labeled("Original title language", originalTitleLanguageField)),
                vertical(
                        labeled("Published date", publishedDateField),
                        labeled("Currency", currencyCombo),
                        labeled("Cost", costField)));
    }

    private JPanel physicalSection(JScrollPane awardsScroll) {
        return row(
                vertical(
                        labeled("Group", groupField),
                        labeled("Size", sizeField),
                        labeled("Binding", bindingCombo)),
                vertical(
                        labeled("Page", pageField),
                        labeled("Weight", weightField),
                        labeled("Awards", awardsScroll)));
    }

    private static JTextArea textArea(int rows) {
        var area = new JTextArea(rows, 30);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private static JPanel labeled(String caption, JComponent field) {
        var box = new JPanel(new BorderLayout(0, 2));
        box.add(new JLabel(caption), BorderLayout.NORTH);
        box.add(field, BorderLayout.CENTER);
        return box;
    }

    private static JPanel vertical(JComponent... components) {
        var column = new JPanel(new GridLayout(components.length, 1, 0, 6));
        for (var component : components) {
            column.add(component);
        }
        return column;
    }

    private static JPanel row(JComponent... components) {
        var line = new JPanel(new GridLayout(1, components.length, 12, 0));
        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (var component : components) {
            line.add(component);
        }
        return line;
    }

    private static void setField(JTextField field, String value) {
        field.setText(text(value));
        field.setCaretPosition(0);
    }

    private static void setArea(JTextArea area, String value) {
        area.setText(text(value));
        area.setCaretPosition(0);
    }

    private static void setCombo(JComboBox<String> combo, String value, String[] defaults) {
        var allowed = new HashSet<>(Arrays.asList(defaults));
        for (int i = combo.getItemCount() - 1; i >= 0; i--) {
            if (!allowed.contains(combo.getItemAt(i))) {
                combo.removeItemAt(i);
            }
        }
        var shown = text(value);
        if (!allowed.contains(shown)) {
            combo.addItem(shown);
        }
        combo.setSelectedItem(shown);
    }

    private void showCover(String imageUrl) {
        var request = ++coverRequest;
        coverLabel.setIcon(null);
        coverLabel.setText(COVER_PLACEHOLDER);
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        new SwingWorker<Image, Void>() {
            @Override
            protected Image doInBackground() throws Exception {
                BufferedImage image = ImageIO.read(new URL(imageUrl));
                if (image == null) {
                    return null;
                }
                return scaleCover(image);
            }

            @Override
            protected void done() {
                if (request != coverRequest) {
                    return;
                }
                try {
                    var image = get();
                    if (image == null) {
                        return;
                    }
                    coverLabel.setText(null);
                    coverLabel.setIcon(new ImageIcon(image));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (Exception ex) {
                    coverLabel.setIcon(null);
                    coverLabel.setText(COVER_PLACEHOLDER);
                }
            }
        }.execute();
    }

    private static Image scaleCover(BufferedImage image) {
        var width = image.getWidth();
        var height = image.getHeight();
        if (width <= 0 || height <= 0) {
            return image;
        }
        var scale = Math.min(COVER_WIDTH / (double) width, COVER_HEIGHT / (double) height);
        var scaledWidth = Math.max(1, (int) Math.round(width * scale));
        var scaledHeight = Math.max(1, (int) Math.round(height * scale));
        return image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
    }

    private static String text(String value) {
        return value == null ? "" : value;
    }

    private static String idText(int id) {
        return id < 0 ? "" : Integer.toString(id);
    }

    private static String countText(int value) {
        return value <= 0 ? "" : Integer.toString(value);
    }

    private static String costText(Book book) {
        if (book.getOriginalPriceNumber() > 0) {
            return Integer.toString(book.getOriginalPriceNumber());
        }
        if (book.getOriginalPriceFormatted() > 0) {
            return Double.toString(book.getOriginalPriceFormatted());
        }
        return text(book.getOriginalPrice());
    }

    private static String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return text(fallback);
    }

    private static String abstractEnglish(String summary) {
        if (summary == null || summary.isBlank()) {
            return "";
        }
        var awardsAt = awardsIndex(summary);
        if (awardsAt < 0) {
            return summary.trim();
        }
        return summary.substring(0, awardsAt).trim();
    }

    private static String awards(String summary) {
        if (summary == null || summary.isBlank()) {
            return "";
        }
        var awardsAt = awardsIndex(summary);
        if (awardsAt < 0) {
            return "";
        }
        return summary.substring(awardsAt + AWARDS_MARKER.length()).trim();
    }

    private static int awardsIndex(String summary) {
        return summary.toLowerCase(Locale.ROOT).indexOf(AWARDS_MARKER);
    }
}
