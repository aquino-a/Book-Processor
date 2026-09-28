package com.aquino.webParser.review;

import com.aquino.webParser.ExcelReader;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.utilities.Connect;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SessionFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReviewServiceImpl implements ReviewService {
    private final Map<String, Integer> locationMap;
    private final HibernateReviewPositionRepository repository;

    public ReviewServiceImpl(SessionFactory sessionFactory, Map<String, Integer> locationMap) {
        this.locationMap = locationMap;
        this.repository = new HibernateReviewPositionRepository(sessionFactory);
    }

    @Override
    public List<Book> loadBooks(String filePath) {
        try (var workbook = Connect.openExistingWorkbook(new File(filePath))) {
            var reader = new ExcelReader(workbook);
            reader.setLocationMap(locationMap);
            List<Pair<Integer, Book>> pairs = reader.ReadBooks();
            return pairs.stream().map(Pair::getRight).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Excel file not found: " + filePath, e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to load books from: " + filePath, e);
        }
    }

    @Override
    public void saveCurrentPosition(String fileName, String isbn) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }
        repository.saveLastIsbn(fileName, isbn);
    }

    @Override
    public Optional<String> loadLastPosition(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return Optional.empty();
        }
        return repository.loadLastIsbn(fileName);
    }
}

