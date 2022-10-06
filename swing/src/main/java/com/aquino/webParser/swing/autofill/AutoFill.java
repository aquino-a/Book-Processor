package com.aquino.webParser.swing.autofill;

import com.aquino.webParser.autofill.AutoFillService;
import com.aquino.webParser.model.*;
import com.aquino.webParser.swing.FileUtility;
import com.aquino.webParser.swing.Handlers;
import com.aquino.webParser.utilities.Connect;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AutoFill extends JFrame{

    private final AutoFillService autoFillService;

    public AutoFill(AutoFillService autoFillService) {
        this.autoFillService = autoFillService;
        init();
    }

    public static void main(String[] args) {
        var autoFill = new AutoFill(new AutoFillService() {
            @Override
            public List<AutoFillModel> readBooks(XSSFWorkbook workbook) {
                return null;
            }

            @Override
            public void updateBook(XSSFWorkbook workbook, List<Pair<Integer, Book>> books) {

            }

            @Override
            public Author CreateAuthor(Book book) {
                return null;
            }

            @Override
            public int insertAuthor(Author author) {
                return 0;
            }

            @Override
            public int insertPublisher(Publisher publisher) {
                return 0;
            }

            @Override
            public String getAuthorLink(int id) {
                return null;
            }

            @Override
            public void setLanguage(Language language) {

            }
        });

        autoFill.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        autoFill.setVisible(true);
    }

    private void init() {
        this.setLayout(new BorderLayout());
        this.setSize(1400, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.add(CreateMenu());
        this.add(CreateTabPane(), BorderLayout.CENTER);

    }

    private JMenuBar CreateMenu() {
        var menuBar = new JMenuBar();

        var fileMenu = new JMenu();
        fileMenu.add(new JMenuItem("File"));
        fileMenu.add(new JMenuItem(Handlers.anonymousEventClass("Open", this::open)));
        fileMenu.add(new JMenuItem(Handlers.anonymousEventClass("Save", this::save)));
        fileMenu.add(new JMenuItem(Handlers.anonymousEventClass("Close", this::close)));
        menuBar.add(fileMenu);

        var languageMenu = new JMenu("Korean");
        languageMenu.add(new JMenuItem(Handlers.anonymousEventClass("Korean", this::korean)));
        languageMenu.add(new JMenuItem(Handlers.anonymousEventClass("Japanese", this::japanese)));
        menuBar.add(languageMenu);

        return menuBar;
    }

    private JTabbedPane CreateTabPane() {
        var tabPane = new JTabbedPane();
        tabPane.addTab("Author", CreateAuthorTable());
        tabPane.addTab("Author 2", CreateAuthor2Table());
        tabPane.addTab("Publisher", CreatePublisherTable());

        return tabPane;
    }

    private Component CreateAuthorTable() {
        var fakeAuthor = new Author();
        fakeAuthor.setLanguage(Language.Korean);
        fakeAuthor.setNativeFirstName("박근혜");
        fakeAuthor.setNativeLastName("박근혜");

        var table = new JTable(new AuthorTableModel(List.of(fakeAuthor)));

        return new JScrollPane(table);
    }

    private Component CreateAuthor2Table() {
        return null;
    }

    private Component CreatePublisherTable() {
        return null;
    }

    private void korean(ActionEvent actionEvent) {
    }

    private void japanese(ActionEvent actionEvent) {
    }

    private void open(ActionEvent actionEvent) {
    }

    private void save(ActionEvent actionEvent) {
    }

    private void close(ActionEvent actionEvent) {

    }






}
