package com.aquino.webParser.swing.autofill;

import com.aquino.webParser.autofill.AutoFillService;
import com.aquino.webParser.model.*;
import com.aquino.webParser.swing.FileUtility;
import com.aquino.webParser.swing.Handlers;
import com.aquino.webParser.utilities.Connect;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AutoFill extends JFrame {

    private final AutoFillService autoFillService;
    private final MouseAdapter idClickListener = CreateMouseAdapter();


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

        var fakeAuthor2 = new Author();
        fakeAuthor2.setLanguage(Language.Korean);
        fakeAuthor2.setNativeFirstName("김정은");
        fakeAuthor2.setNativeLastName("김정은");
        fakeAuthor2.setId(123);
        var secondRow = new Row<>(fakeAuthor2);
        secondRow.link("http://bookswindow.com");

        var model = new AuthorTableModel(List.of(new Row<>(fakeAuthor), secondRow));
        var table = CreateTable();
        table.setModel(model);
        AuthorTableModel.setColumn(table);

        table.addMouseListener(idClickListener);

        return new JScrollPane(table);
    }

    private Component CreateAuthor2Table() {
        return null;
    }

    private Component CreatePublisherTable() {
        return null;
    }

    private JTable CreateTable() {
        var table = new JTable();
        table.setRowHeight(30);
        table.setRowMargin(10);

        return table;
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

    private MouseAdapter CreateMouseAdapter() {
        var mouseParent = this;
        return new MouseAdapter() {
            private final Component parent = mouseParent;

            @Override
            public void mouseClicked(MouseEvent e) {
                JTable table = (JTable) e.getSource();
                int rowIndex = table.getSelectedRow();
                int columnIndex = table.getSelectedColumn();

                if (columnIndex != 5) {
                    return;
                }

                var row = (Row) table.getValueAt(rowIndex, columnIndex);

                if (!StringUtils.isBlank(row.link())) {
                    openLink(row.link());
                }
            }

            private void openLink(String link) {
                try {
                    Desktop.getDesktop().browse(URI.create(link));
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                        parent,
                        String.format("Error occured opening link %s !", link),
                        "Link Problem.",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }
}
