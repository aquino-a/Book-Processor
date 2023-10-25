package com.aquino.webParser.swing;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;

import com.aquino.webParser.DescriptionWriter;
import com.aquino.webParser.ExcelWriter;
import com.aquino.webParser.bookCreators.amazon.AmazonJapanBookCreator;
import com.aquino.webParser.model.Book;

public class ManualAdd extends JFrame {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String SEARCH_URL = AmazonJapanBookCreator.SEARCH_URL_FORMAT;

    private final ExcelWriter writer;
    private final DescriptionWriter descriptionWriter;
    private final AmazonJapanBookCreator amazonJapanBookCreator;

    private JPanel rowPanel;

    public ManualAdd(
            ExcelWriter writer,
            DescriptionWriter descriptionWriter,
            AmazonJapanBookCreator amazonJapanBookCreator) {
        this.writer = writer;
        this.descriptionWriter = descriptionWriter;
        this.amazonJapanBookCreator = amazonJapanBookCreator;

        init();
    }

    private void init() {
        var pane = this.getContentPane();
        pane.setLayout(new BorderLayout());
        this.setSize(600, 1000);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.Y_AXIS));
        var pasteTextField = createPasteTextField();

        pane.add(pasteTextField, BorderLayout.NORTH);
        pane.add(rowPanel, BorderLayout.CENTER);
    }

    private JTextArea createPasteTextField() {
        var textArea = new JTextArea();
        textArea.getDocument().addDocumentListener(new RowGenListener(rowPanel, this::addRow));

        return textArea;
    }

    private void addRow(String isbn) {
        var panel = new JPanel(new GridLayout(1, 4));
        panel.add(new JButton(Handlers.anonymousEventClass("Open", (event) -> openLink(isbn))));
        panel.add(new JLabel(isbn));

        var sourceTextArea = new JTextArea();
        var scrollPane = new JScrollPane(sourceTextArea);
        panel.add(scrollPane);
        panel.add(new JButton(Handlers.anonymousEventClass("Add", (event) -> addBook(isbn, sourceTextArea))));

        rowPanel.add(panel);
    }

    private void addBook(String isbn, JTextArea sourceTextArea) {
        if (sourceTextArea == null || sourceTextArea.getDocument() == null) {
            return;
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        var document = sourceTextArea.getDocument();

        try {
            var source = document.getText(0, document.getLength());
            var doc = Jsoup.parse(source);

            var book = new Book();
            book.setIsbn(Long.parseLong(isbn));

            amazonJapanBookCreator.fillInBasicData(book, doc);
            amazonJapanBookCreator.fillInAllDetails(book);

            if (book.isTitleExists()) {
                JOptionPane.showMessageDialog(this, "Book exists in BooksWindow", "Done", JOptionPane.INFORMATION_MESSAGE);

                return;
            }

            var books = List.of(book);
            writer.writeBooks(books);
            descriptionWriter.writeBooks(books);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding book.", "Done", JOptionPane.ERROR_MESSAGE);
            LOGGER.error("Problem adding book", e);
        } finally {
            this.setCursor(null);
        }
    }

    private void openLink(String isbn) {
        try {
            Desktop.getDesktop().browse(URI.create(String.format(SEARCH_URL, isbn)));
        } catch (IOException e) {
            LOGGER.error("Problem opening link", e);
        }
    }

    private static class RowGenListener implements DocumentListener {

        private final Consumer<String> addRowConsumer;
        private final JPanel rowPanel;

        public RowGenListener(JPanel rowPanel, Consumer<String> addRowConsumer) {
            this.rowPanel = rowPanel;
            this.addRowConsumer = addRowConsumer;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            rowPanel.removeAll();
            generateRows(e.getDocument());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            rowPanel.removeAll();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            // rowPanel.removeAll();
        }

        private void generateRows(Document document) {
            try {
                var length = document.getLength();
                var text = document.getText(0, length);
                text.lines().forEach(addRowConsumer);
            } catch (Exception ex) {
                LOGGER.error("Problem adding isbn rows", ex);
            }
        }
    }

}
