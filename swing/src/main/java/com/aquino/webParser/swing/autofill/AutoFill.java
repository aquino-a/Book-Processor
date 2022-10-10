package com.aquino.webParser.swing.autofill;

import com.aquino.webParser.autofill.AutoFillService;
import com.aquino.webParser.model.*;
import com.aquino.webParser.swing.FileUtility;
import com.aquino.webParser.swing.Handlers;
import com.aquino.webParser.utilities.Connect;
import org.apache.commons.lang3.NotImplementedException;
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
import java.util.stream.Stream;

public class AutoFill extends JFrame {

    private static final Logger LOGGER = LogManager.getLogger();
    private final AutoFillService autoFillService;
    private final MouseAdapter idClickListener = CreateMouseAdapter();
    private XSSFWorkbook workbook;
    private List<BookWindowIds> books;
    private List<BookWindowIds> currentAuthors;
    private List<BookWindowIds> currentAuthor2s;
    private List<BookWindowIds> currentPublishers;
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
                return null;
            }

            @Override
            public void updateBook(XSSFWorkbook workbook, List<BookWindowIds> books) {

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
        strategies = createStrategies();
        this.setLayout(new BorderLayout());
        this.setSize(1400, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setJMenuBar(CreateMenu());
        this.add(new JButton(Handlers.anonymousEventClass("Auto Fill", event -> autoFill())), BorderLayout.SOUTH);
    }

    private Map<Type, AutoFillStrategy> createStrategies() {
        throw new NotImplementedException("");
    }

    private JMenuBar CreateMenu() {
        var menuBar = new JMenuBar();

        var fileMenu = new JMenu();
        fileMenu.add(new JMenuItem("File"));
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
            books = autoFillService.readBooks(workbook);

            if (books.size() > 0) {
                this.setTitle(file.getName());
                this.add(CreateTabPane(), BorderLayout.CENTER);
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
                String.format("Open failed: %s", e.getCause().getMessage()),
                "Open Problem.",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            this.setCursor(null);
        }
    }

    private void save(ActionEvent actionEvent) {
        try {
            throw new NotImplementedException("");
            File file = FileUtility.openFile(this.rootPane);
            autoFillService.updateBook(workbook, books);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        } catch (IllegalArgumentException | NullPointerException | IOException e) {
            LOGGER.error("save failed!", e);
            JOptionPane.showMessageDialog(
                this,
                String.format("Save failed: %s", e.getCause().getMessage()),
                "Save Problem.",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void close(ActionEvent actionEvent) {
        throw new NotImplementedException("");
    }

    private void autoFill() {
        throw new NotImplementedException("");
        try {
            var result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to auto fill?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            Stream.of(bookRowContainer.getComponents())
                .filter(c -> c instanceof BookRow)
                .map(c -> (BookRow) c)
                .filter(br -> br.isSelected())
                .forEach(br -> {
                    var afm = br.getAutoFillModel();
                    InsertAuthor(br, afm.author());
                    //TODO insert pub and add link
                    afm.UpdateBook();
                });
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

    public interface HasType {
        Type type();
    }
    public enum Type {
        Author,
        Author2,
        Publisher
    }

    private interface AutoFillStrategy {
    }
}
