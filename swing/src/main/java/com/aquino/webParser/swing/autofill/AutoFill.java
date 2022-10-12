package com.aquino.webParser.swing.autofill;

import com.aquino.webParser.autofill.AutoFillService;
import com.aquino.webParser.model.*;
import com.aquino.webParser.swing.FileUtility;
import com.aquino.webParser.swing.Handlers;
import com.aquino.webParser.utilities.Connect;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AutoFill extends JFrame {

    private static final Logger LOGGER = LogManager.getLogger();
    private final AutoFillService autoFillService;
    private final MouseAdapter idClickListener = CreateMouseAdapter();
    private XSSFWorkbook workbook;
    private List<BookWindowIds> books;
    private JMenu languageMenu;
    private AutoFillStrategy currentAutoFillStrategy;
    private Map<Type, AutoFillStrategy> strategies;


    public AutoFill(AutoFillService autoFillService) {
        this.autoFillService = autoFillService;
        init();
    }

    public static void main(String[] args) {
        var autoFill = new AutoFill(new AutoFillService() {
            @Override
            public List<BookWindowIds> readBooks(XSSFWorkbook workbook) {
                var fakeAuthor = new Author();
                fakeAuthor.setLanguage(Language.Korean);
                fakeAuthor.setNativeFirstName("박근혜");
                fakeAuthor.setNativeLastName("박근혜");

                var fakeAuthor2 = new Author();
                fakeAuthor2.setLanguage(Language.Korean);
                fakeAuthor2.setNativeFirstName("김정은");
                fakeAuthor2.setNativeLastName("김정은");
                fakeAuthor2.setId(123);

                var publisher = new Publisher();
                publisher.setNativeName("하로");

                var ids = new BookWindowIds();
                ids.author(fakeAuthor);
                ids.author2(fakeAuthor2);
                ids.publisher(publisher);

                return List.of(ids);
            }

            @Override
            public void updateBook(XSSFWorkbook workbook, List<BookWindowIds> books) {

            }

            @Override
            public Author CreateAuthor(String name) {
                return null;
            }

            @Override
            public int insertAuthor(Author author) {
                return 1234;
            }

            @Override
            public int insertPublisher(Publisher publisher) {
                return 0;
            }

            @Override
            public String getAuthorLink(int id) {
                return String.format("https://www.bookswindow.com/admin/author/%s/edit/main", id);
            }

            @Override
            public String getPublisherLink(int id) {
                return String.format("https://www.bookswindow.com/admin/mfg/%s/edit/main", id);
            }

            @Override
            public void setLanguage(Language language) {

            }
        });

        autoFill.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        autoFill.setVisible(true);
    }

    private void init() {
        createStrategies();
        this.setLayout(new BorderLayout());
        this.setSize(1400, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setJMenuBar(CreateMenu());
        this.add(new JButton(Handlers.anonymousEventClass("Auto Fill", event -> autoFill())), BorderLayout.SOUTH);
        this.korean(null);
    }

    private void createStrategies() {
        strategies = Map.of(
            Type.Author, new AuthorStrategy(autoFillService),
            Type.Author2, new AuthorStrategy(autoFillService),
            Type.Publisher, new PublisherStrategy(autoFillService));

        currentAutoFillStrategy = strategies.get(Type.Author);
    }

    private JMenuBar CreateMenu() {
        var menuBar = new JMenuBar();

        var fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem(Handlers.anonymousEventClass("Open", this::open)));
        fileMenu.add(new JMenuItem(Handlers.anonymousEventClass("Save", this::save)));
        fileMenu.add(new JMenuItem(Handlers.anonymousEventClass("Close", this::close)));
        menuBar.add(fileMenu);

        languageMenu = new JMenu("Korean");
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

        tabPane.addChangeListener(this::tabChange);

        return tabPane;
    }

    private void tabChange(ChangeEvent changeEvent) {
        var tabPane = (JTabbedPane) changeEvent.getSource();
        var scrollPane = (JScrollPane) tabPane.getSelectedComponent();
        var viewport = (JViewport) scrollPane.getComponent(0);
        var table = (JTable) viewport.getComponent(0);
        var hasType = (HasType) table.getModel();

        switch (hasType.type()) {
            case Author:
                currentAutoFillStrategy = strategies.get(Type.Author);
                break;
            case Author2:
                currentAutoFillStrategy = strategies.get(Type.Author2);
                break;
            case Publisher:
                currentAutoFillStrategy = strategies.get(Type.Publisher);
                break;
        }
    }

    private Component CreateAuthorTable() {
        var rows = books
            .stream()
            .filter(b -> b.author().getId() < 1)
            .map(b -> new Row<>(b, b.author()))
            .collect(Collectors.toList());

        var strategy = strategies.get(Type.Author);
        strategy.rows(rows);

        var model = new AuthorTableModel(rows);
        var table = CreateTable();
        table.setModel(model);
        AuthorTableModel.setColumn(table);

        table.addMouseListener(idClickListener);

        return new JScrollPane(table);
    }

    private Component CreateAuthor2Table() {
        var rows = books
            .stream()
            .filter(b -> b.author2() != null)
            .filter(b -> !StringUtils.isBlank(b.author2().getNativeLastName()) ||
                !StringUtils.isBlank(b.author2().getNativeFirstName()))
            .filter(b -> b.author2().getId() < 1)
            .map(b -> new Row<>(b, b.author2()))
            .collect(Collectors.toList());

        var strategy = strategies.get(Type.Author2);
        strategy.rows(rows);

        var model = new AuthorTableModel(rows);
        var table = CreateTable();
        table.setModel(model);
        AuthorTableModel.setColumn(table);

        table.addMouseListener(idClickListener);

        return new JScrollPane(table);
    }

    private Component CreatePublisherTable() {
        var rows = books
            .stream()
            .filter(b -> b.publisher() != null)
            .filter(b -> !StringUtils.isBlank(b.publisher().getNativeName()))
            .filter(b -> b.publisher().getId() < 1)
            .map(b -> new Row<>(b, b.publisher()))
            .collect(Collectors.toList());

        var strategy = strategies.get(Type.Author2);
        strategy.rows(rows);

        var model = new PublisherTableModel(rows);
        var table = CreateTable();
        table.setModel(model);
        PublisherTableModel.setColumn(table);

        table.addMouseListener(idClickListener);

        return new JScrollPane(table);
    }

    private JTable CreateTable() {
        var table = new JTable();
        table.setRowHeight(30);
        table.setRowMargin(10);

        return table;
    }

    private void korean(ActionEvent actionEvent) {
        languageMenu.setText("Korean");
        autoFillService.setLanguage(Language.Korean);
    }

    private void japanese(ActionEvent actionEvent) {
        languageMenu.setText("Japanese");
        autoFillService.setLanguage(Language.Japanese);
    }

    private void open(ActionEvent actionEvent) {
        try {
            File file = FileUtility.openFile(this.rootPane);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            workbook = Connect.openExistingWorkbook(file);
            books = autoFillService.readBooks(workbook)
                .stream()
                .filter(b -> b.isMissingIds())
                .collect(Collectors.toList());

            if (books.size() > 0) {
                this.setTitle(file.getName());
                this.add(CreateTabPane(), BorderLayout.CENTER);
                this.revalidate();
                enableActions();
            } else {
                closeWorkbook();
                JOptionPane.showMessageDialog(
                    this,
                    "No data to change",
                    "No data",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.error("Open failed!", e);
            JOptionPane.showMessageDialog(
                this,
                String.format("Open failed: %s", e.getMessage()),
                "Open Problem.",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            this.setCursor(null);
        }
    }

    private void save(ActionEvent actionEvent) {
        try {
            File file = FileUtility.openFile(this.rootPane);
            autoFillService.updateBook(workbook, books);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        } catch (IllegalArgumentException | NullPointerException | IOException e) {
            LOGGER.error("save failed!", e);
            JOptionPane.showMessageDialog(
                this,
                String.format("Save failed: %s", e.getMessage()),
                "Save Problem.",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void close(ActionEvent actionEvent) {
        try {
            closeWorkbook();
            this.setTitle("");
        } catch (IllegalArgumentException | NullPointerException | IOException e) {
            LOGGER.error("Close failed!", e);
            JOptionPane.showMessageDialog(
                this,
                String.format("Close fail: %s", e.getMessage()),
                "Close Problem.",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void autoFill() {
        try {
            var result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to auto fill?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            currentAutoFillStrategy.fill();
            this.repaint();
        } catch (IllegalArgumentException | NullPointerException e) {
            LOGGER.error("Autofill failed!", e);
            JOptionPane.showMessageDialog(
                this,
                String.format("Auto Fill fail: %s", e.getMessage()),
                "Auto Fill Problem.",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            this.setCursor(null);
        }
    }

    private void enableActions() {
        this.getJMenuBar().setEnabled(true);
    }

    private void closeWorkbook() throws IOException {
        if (workbook != null)
            workbook.close();
        workbook = null;
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

    interface HasType {
        Type type();
    }

    enum Type {
        Author,
        Author2,
        Publisher
    }

    interface AutoFillStrategy<T> {
        List<Row<T>> rows();

        void rows(List<Row<T>> rows);

        void fill();
    }
}
