package com.aquino.webParser.swing.review;

import com.aquino.webParser.model.Book;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
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

    private JPanel panel;

    private JLabel coverLabel;
    private JLabel koreanTitleLabel;
    private JLabel romanizedTitleLabel;
    private JLabel originalTitleLabel;
    private JLabel englishTitleLabel;
    private JLabel translatedEnglishTitleLabel;

    private JLabel authorLabel;
    private JLabel authorStoreLabel;
    private JLabel authorBwLabel;
    private JLabel author2Label;
    private JLabel author2StoreLabel;
    private JLabel author2BwLabel;
    private JLabel publisherLabel;
    private JLabel publisherStoreLabel;
    private JLabel publisherBwLabel;

    private JLabel category1Label;
    private JLabel category2Label;
    private JLabel category3Label;
    private JLabel languageCodeLabel;
    private JLabel languageCode2Label;
    private JLabel originalTitleLanguageLabel;
    private JLabel publishedDateLabel;
    private JLabel currencyLabel;
    private JLabel costLabel;

    private JTextArea abstractEnglishArea;
    private JTextArea abstractNativeArea;

    private JLabel groupLabel;
    private JLabel sizeLabel;
    private JLabel bindingLabel;
    private JLabel pageLabel;
    private JLabel weightLabel;
    private JLabel awardsLabel;

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

        koreanTitleLabel = valueLabel();
        romanizedTitleLabel = valueLabel();
        originalTitleLabel = valueLabel();
        englishTitleLabel = valueLabel();
        translatedEnglishTitleLabel = valueLabel();

        authorLabel = valueLabel();
        authorStoreLabel = valueLabel();
        authorBwLabel = valueLabel();
        author2Label = valueLabel();
        author2StoreLabel = valueLabel();
        author2BwLabel = valueLabel();
        publisherLabel = valueLabel();
        publisherStoreLabel = valueLabel();
        publisherBwLabel = valueLabel();

        category1Label = valueLabel();
        category2Label = valueLabel();
        category3Label = valueLabel();
        languageCodeLabel = valueLabel();
        languageCode2Label = valueLabel();
        originalTitleLanguageLabel = valueLabel();
        publishedDateLabel = valueLabel();
        currencyLabel = valueLabel();
        costLabel = valueLabel();

        abstractEnglishArea = textArea(10);
        abstractNativeArea = textArea(10);
        var abstractEnglishScroll = new JScrollPane(abstractEnglishArea);
        var abstractNativeScroll = new JScrollPane(abstractNativeArea);

        groupLabel = valueLabel();
        sizeLabel = valueLabel();
        bindingLabel = valueLabel();
        pageLabel = valueLabel();
        weightLabel = valueLabel();
        awardsLabel = valueLabel();

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
        form.add(physicalSection(), constraints);

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
        setLabel(koreanTitleLabel, data.getTitle());
        setLabel(romanizedTitleLabel, data.getRomanizedTitle());
        setLabel(originalTitleLabel, data.getEnglishTitle());
        setLabel(englishTitleLabel, data.getEnglishTitle());
        setLabel(translatedEnglishTitleLabel, data.getTranslatedTitle());

        setLabel(authorLabel, data.getAuthor());
        setLabel(authorStoreLabel, data.getAuthorBooks());
        setLabel(authorBwLabel, idText(data.getAuthorId()));
        setLabel(author2Label, data.getAuthor2());
        setLabel(author2StoreLabel, data.getAuthor2Books());
        setLabel(author2BwLabel, idText(data.getAuthor2Id()));
        setLabel(publisherLabel, data.getPublisher());
        setLabel(publisherStoreLabel, data.getPublisherBooks());
        setLabel(publisherBwLabel, idText(data.getPublisherId()));

        setLabel(category1Label, data.getCategory());
        setLabel(category2Label, data.getCategory2());
        setLabel(category3Label, data.getCategory3());
        setLabel(languageCodeLabel, data.getLanguageCode());
        // Book stores a single language code. The sheet's second code has no property.
        setLabel(languageCode2Label, "");
        // "Original title language" is a sheet column with no Book property.
        setLabel(originalTitleLanguageLabel, "");
        setLabel(publishedDateLabel, firstNonBlank(data.getPublishDateFormatted(), data.getPublishDate()));
        setLabel(currencyLabel, data.getCurrencyType());
        setLabel(costLabel, costText(data));

        var summary = data.getSummary();
        setArea(abstractEnglishArea, abstractEnglish(summary));
        setArea(abstractNativeArea, firstNonBlank(data.getDescription(), data.getKoreanDescription()));

        setLabel(groupLabel, data.getAgeGroup());
        setLabel(sizeLabel, firstNonBlank(data.getBookSizeFormatted(), data.getBookSize()));
        setLabel(bindingLabel, data.getType());
        setLabel(pageLabel, countText(data.getPages()));
        setLabel(weightLabel, countText(data.getWeight()));
        setLabel(awardsLabel, awards(summary));

        showCover(data.getImageURL());

        panel.repaint();
    }

    private JPanel headerSection() {
        var sourceTitles = vertical(
                labeled("Korean title", koreanTitleLabel),
                labeled("Romanized", romanizedTitleLabel),
                labeled("Original title for translated books", originalTitleLabel));
        var englishTitles = vertical(
                labeled("English title", englishTitleLabel),
                labeled("English title (translated)", translatedEnglishTitleLabel),
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
                        labeled("Author", authorLabel),
                        labeled("Author (store)", authorStoreLabel),
                        labeled("Author (BW)", authorBwLabel)),
                vertical(
                        labeled("Author 2", author2Label),
                        labeled("Author 2 (store)", author2StoreLabel),
                        labeled("Author 2 (BW)", author2BwLabel)),
                vertical(
                        labeled("Publisher", publisherLabel),
                        labeled("Publisher (store)", publisherStoreLabel),
                        labeled("Publisher (BW)", publisherBwLabel)));
    }

    private JPanel metaSection() {
        return row(
                vertical(
                        labeled("Category 1", category1Label),
                        labeled("Category 2", category2Label),
                        labeled("Category 3", category3Label)),
                vertical(
                        labeled("Language code", languageCodeLabel),
                        labeled("Language code 2", languageCode2Label),
                        labeled("Original title language", originalTitleLanguageLabel)),
                vertical(
                        labeled("Published date", publishedDateLabel),
                        labeled("Currency", currencyLabel),
                        labeled("Cost", costLabel)));
    }

    private JPanel physicalSection() {
        return row(
                vertical(
                        labeled("Group", groupLabel),
                        labeled("Size", sizeLabel),
                        labeled("Binding", bindingLabel)),
                vertical(
                        labeled("Page", pageLabel),
                        labeled("Weight", weightLabel),
                        labeled("Awards", awardsLabel)));
    }

    private static JTextArea textArea(int rows) {
        var area = new JTextArea(rows, 30);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setCaretPosition(0);
        return area;
    }

    private static JLabel valueLabel() {
        var label = new JLabel();
        label.setVerticalAlignment(SwingConstants.TOP);
        return label;
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

    private static void setLabel(JLabel label, String value) {
        var shown = text(value);
        label.setText(shown);
        label.setToolTipText(shown.isBlank() ? null : shown);
    }

    private static void setArea(JTextArea area, String value) {
        area.setText(text(value));
        area.setCaretPosition(0);
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
