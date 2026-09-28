package com.aquino.webParser.swing.review;

import com.aquino.webParser.model.Book;
import com.aquino.webParser.review.ReviewService;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Objects;

public class ReviewWindow {
    private final Review review;
    private final ReviewService reviewService;
    private final String fileName;
    private final List<Book> books;

    private int index;

    public ReviewWindow(Review review, ReviewService reviewService, String fileName, List<Book> books) {
        this.review = Objects.requireNonNull(review);
        this.reviewService = Objects.requireNonNull(reviewService);
        this.fileName = Objects.requireNonNull(fileName);
        this.books = Objects.requireNonNull(books);
        this.index = 0;
    }

    public void show() {
        if (books.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "No books found in the selected Excel file.",
                    "Review",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        restorePosition();

        var frame = new JFrame("Review - " + fileName);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(review.getPanel());
        frame.setSize(1100, 850);
        frame.setLocationByPlatform(true);

        installKeyBindings(frame.getRootPane());

        updateBook();

        frame.setVisible(true);
    }

    private void restorePosition() {
        reviewService.loadLastPosition(fileName).ifPresent(savedIsbn -> {
            for (int i = 0; i < books.size(); i++) {
                var isbn = String.valueOf(books.get(i).getIsbn());
                if (savedIsbn.equals(isbn)) {
                    index = i;
                    return;
                }
            }
        });
    }

    private void installKeyBindings(JComponent root) {
        var previous = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previousBook();
            }
        };
        var next = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextBook();
            }
        };

        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("LEFT"), "review.previous");
        root.getActionMap().put("review.previous", previous);

        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("RIGHT"), "review.next");
        root.getActionMap().put("review.next", next);
    }

    private void previousBook() {
        if (index <= 0) {
            return;
        }
        index--;
        updateBook();
    }

    private void nextBook() {
        if (index >= books.size() - 1) {
            return;
        }
        index++;
        updateBook();
    }

    private void updateBook() {
        var book = books.get(index);
        review.setData(book);
        reviewService.saveCurrentPosition(fileName, String.valueOf(book.getIsbn()));
    }
}

