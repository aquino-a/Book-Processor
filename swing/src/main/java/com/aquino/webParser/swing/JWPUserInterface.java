/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.swing;

import com.aquino.webParser.DescriptionWriter;
import com.aquino.webParser.ExcelWriter;
import com.aquino.webParser.ProcessorFactoryImpl;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.bookCreators.BookCreatorType;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.DataType;
import com.aquino.webParser.oclc.OCLCChecker;
import com.aquino.webParser.swing.autofill.AutoFill;
import com.aquino.webParser.utilities.Connect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;

/**
 * @author alex
 */
public class JWPUserInterface extends JPanel {

    private static final Logger LOGGER = LogManager.getLogger();
    private JFrame frame;
    private JButton addButton, saveButton;
    private JPanel mainPanel, buttonPanel, checkPanel;
    private JMenuBar menuBar;
    private JMenu file;
    private JTextArea textArea;
    private ExcelWriter writer;
    private DescriptionWriter desWriter;
    private File saveFile;
    private JLabel fileName, state, bookCountLabel;
    private Timer timer;
    private JTextField checkField;
    private String checkedLink;
    private JMenu language;
    private OclcProgress oclcProgress;
    private NewLineFilter newLineFilter;
    private final ProcessorFactoryImpl processorFactory;
    private DataType dataType = DataType.BookPage;
    private BookCreator bookCreator;

    private final DocumentListener addNewLine = Handlers.forDocumentUpdate((event) -> {
        if (event.getLength() > 2) addNewLine();
    });
    private final Action clearText = Handlers.anonymousEventClass("Clear", (event) -> {
        textArea.setText("");
    });
    private final Action saveAsAction = Handlers.anonymousEventClass("Save As", (event) -> {
        try {
            askSaveFile();
            writer.saveFile(saveFile);
            desWriter.saveBooks(saveFile);

        }
        catch (NullPointerException e) {
            saveFile = null;
        }
    });
    private final Action newAction = Handlers.anonymousEventClass("New", (event) -> {
        writer = new ExcelWriter(Connect.newWorkbookFromTemplate());
        setFileLabel("");
        saveFile = null;
        enableActions();
    });
    private final Action saveAction = Handlers.anonymousEventClass("Save", (event) -> {
        try {
            //if(saveFile == null) saveFile = FileUtility.saveLocation(mainPanel);
            if (saveFile == null) askSaveFile();
            writer.saveFile(saveFile);
            desWriter.saveBooks(saveFile);
            state.setText("Saved!");
            timer.start();
        }
        catch (NullPointerException e) {
            saveFile = null;
        }
    });
    private final Action openAction = Handlers.anonymousEventClass("Open", (event) -> {
        try {
            File file = FileUtility.openFile(mainPanel);
            writer = new ExcelWriter(
                Connect.openExistingWorkbook(file));
            setFileLabel(file.getName());
            setSaveFile(file);
            enableActions();
        }
        catch (IllegalArgumentException | NullPointerException | FileNotFoundException e) {
            writer = null;
            state.setText("Open failed.");
            timer.start();
        }
    });

    private final Action scrapeBestOclc = Handlers.anonymousEventClass("Scrape BEST OCLCs", (event) -> {
        getOclcWorker(OCLCChecker.Type.BEST).execute();
    });
    private final Action scrapeOclc = Handlers.anonymousEventClass("Scrape OCLCs", (event) -> {
        getOclcWorker(OCLCChecker.Type.NEW).execute();
    });
    private final Action autoFillTool = Handlers.anonymousEventClass("Author, Publisher Auto Fill", (event) -> {
        openAutoFillTool();
    });

    private final Action addAction = Handlers.anonymousEventClass("Add", (event) -> {
        getAddWorker().execute();
    });
    private final Action useAction = Handlers.anonymousEventClass("Use", (event) -> {
        textArea.append(checkedLink);
    });
    private final Action deleteState = Handlers.anonymousEventClass("", (event) -> {
        state.setText("");
    });
    private final Action japaneseAction = Handlers.anonymousEventClass("Japanese", (event) -> {
        try {
            changeBookCreator(BookCreatorType.KinoHontoHonya);
            changeDataType(DataType.Isbn);
            language.setText("Japanese");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    });
    private final Action koreanAction = Handlers.anonymousEventClass("Korean", (event) -> {
        try {
            changeBookCreator(BookCreatorType.AladinApi);
            changeDataType(DataType.BookPage);
            language.setText("Korean");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    });

    public JWPUserInterface(ProcessorFactoryImpl processorFactory, BookCreator defaultCreator) throws IOException {
        this.processorFactory = processorFactory;
        this.bookCreator = defaultCreator;
        addcomponents();
    }

    //TODO fix savebutton
    private void addcomponents() throws IOException {

        frame = new JFrame("Jeein's Book Processor");
        desWriter = new DescriptionWriter();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        mainPanel = new JPanel();
        buttonPanel = new JPanel();
        checkPanel = new JPanel();
        textArea = new JTextArea(37, 35);
        textArea.setLineWrap(true);
        newLineFilter = new NewLineFilter(dataType, bookCreator);
        ((AbstractDocument) textArea.getDocument()).
            setDocumentFilter(newLineFilter);
        
        addButton = new JButton(addAction);
        saveButton = new JButton(saveAction);
        fileName = new JLabel("", SwingConstants.LEFT);
        state = new JLabel("", SwingConstants.RIGHT);
        bookCountLabel = new JLabel("0", SwingConstants.LEFT);
        textArea.getDocument().addDocumentListener(new BookCountListener(bookCountLabel));

        //Textfield checker
        checkField = new JTextField(25);
        ((AbstractDocument) checkField.getDocument()).
            setDocumentFilter(new CheckFilter(new Consumer() {
                @Override
                public void accept(Object t) {
                    checkedLink = (String) t;
                }
            }, frame, checkField, processorFactory.createBookCreator(BookCreatorType.AladinApi)));
        
        //timer for state
        timer = new Timer(2000, deleteState);
        timer.setRepeats(true);

        //menu
        menuBar = new JMenuBar();
        file = new JMenu("File");
        file.add(new JMenuItem(newAction));
        file.add(new JMenuItem(openAction));
        file.add(new JMenuItem(saveAction));
        file.add(new JMenuItem(saveAsAction));
        JMenu tools = new JMenu("Tools");
        tools.add(new JMenuItem(scrapeOclc));
        tools.add(new JMenuItem(scrapeBestOclc));
        tools.add(new JMenuItem(autoFillTool));
        language = new JMenu("Language");
        language.add(new JMenuItem(koreanAction));
        language.add(new JMenuItem(japaneseAction));


        menuBar.add(file);
        menuBar.add(tools);
        menuBar.add(language);
        //panel
        mainPanel.add(new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
        buttonPanel.add(bookCountLabel);
        buttonPanel.add(fileName);
        buttonPanel.add(addButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(new JButton(clearText));
        buttonPanel.add(state);
        checkPanel.add(checkField);
        checkPanel.add(new JButton(useAction));
        this.add(checkPanel);
        this.add(buttonPanel);
        this.add(mainPanel);


        //states
        saveAction.setEnabled(false);
        saveAsAction.setEnabled(false);
        addAction.setEnabled(false);


    }

    private void openAutoFillTool() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    var autoFill = new AutoFill(processorFactory.GetAutoFillService());
                    autoFill.setVisible(true);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    JOptionPane.showMessageDialog(
                        frame,
                        "Error occured opening auto fill",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void createAndShowGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        //content pane
        frame.setContentPane(this);
        frame.setJMenuBar(menuBar);

        //pack
        frame.pack();
        frame.setLocation((int)
            GraphicsEnvironment.getLocalGraphicsEnvironment().
                getMaximumWindowBounds().getWidth() - frame.getWidth(), 0);
        frame.setVisible(true);
    }

    private void enableActions() {
        scrapeOclc.setEnabled(true);
        scrapeBestOclc.setEnabled(true);
        saveAction.setEnabled(true);
        saveAsAction.setEnabled(true);
        openAction.setEnabled(true);
        newAction.setEnabled(true);
        addAction.setEnabled(true);
    }

    private void askSaveFile() throws NullPointerException {
        setSaveFile(FileUtility.saveLocation(mainPanel));
        setFileLabel(saveFile.getName());
    }

    private void setSaveFile(File file) {
        saveFile = file;
    }

    private void setFileLabel(String openedFileName) {
        fileName.setText(openedFileName);
    }

    private void setStateLabel(String state) {
        this.state.setText(state);
    }

    private void disableActions() {
        scrapeOclc.setEnabled(false);
        scrapeBestOclc.setEnabled(false);
        saveAction.setEnabled(false);
        saveAsAction.setEnabled(false);
        openAction.setEnabled(false);
        newAction.setEnabled(false);
        addAction.setEnabled(false);
    }

    private SwingWorker getAddWorker() {
        return new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                try {
                    disableActions();
                    if (textArea.getText().trim().equals("")) {
                        return null;
                    }
                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    List<Book> books = fetchBooks();
                    writer.writeBooks(books);
                    desWriter.writeBooks(books);
                }
                catch (Exception e) {
                    LOGGER.error("Problem adding.");
                    LOGGER.error(e.getMessage(), e);
                }
                return null;
            }

            @Override
            public void done() {
                frame.setCursor(null);
                state.setText("Added!");
                timer.start();
                enableActions();
            }
        };
    }

    private List<Book> fetchBooks() throws IOException {
        var text = textArea.getText();
        LOGGER.info(System.lineSeparator() + text);
        List<Book> books = null;
        if (dataType == DataType.BookPage) {
            books = bookCreator.bookListFromLink(text);
        }
        else if (dataType == DataType.Isbn) {
            books = GetBookListFromIsbns(text);
        }
        else {
            throw new UnsupportedOperationException("Only book page and isbn data types are supported.");
        }
        books.stream().forEach(book -> bookCreator.fillInAllDetails(book));
        return books;
    }

    private List<Book> GetBookListFromIsbns(String text) {
        List<Book> list = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(text);
        while (st.hasMoreTokens()) {
            String isbn = st.nextToken().replace('\u00A0',' ').strip();
            try {
                Book book = bookCreator.createBookFromIsbn(isbn);
                if (book.getIsbn() == 0)
                    throw new Exception("ISBN is 0");
                list.add(book);
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(frame, e.getMessage());
                LOGGER.error(String.format("Problem with isbn: %s", isbn));
                LOGGER.error(e.getMessage(), e);
                continue;
            }
        }
        return list;
    }

    private SwingWorker getOclcWorker(OCLCChecker.Type type) {
        return new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                try {
                    disableActions();
                    OCLCChecker checker = new OCLCChecker(processorFactory.createBookCreator(BookCreatorType.AladinApi));
                    checker.type(type);
                    if (oclcProgress == null)
                        oclcProgress = new OclcProgress(frame);
                    oclcProgress.start();
                    var save = FileUtility.saveLocation(mainPanel);
                    if (save == null) {
                        throw new IllegalArgumentException("No save file selected");
                    }
                    checker.getHitsAndWrite(1, type.getPages(), oclcProgress::setProgress, save);
                    LOGGER.info("Done scraping for oclc numbers.");
                }
                catch (IOException ex) {
                    LOGGER.error("IOException in oclc worker.  Could be abnormal.");
                    LOGGER.error(ex.getMessage(), ex);
                    JOptionPane.showMessageDialog(frame, "Reached end of pages", "Done", JOptionPane.INFORMATION_MESSAGE);
                }
                catch (Exception e) {
                    LOGGER.error("oclc problems");
                    LOGGER.error(e.getMessage(), e);
                    JOptionPane.showMessageDialog(frame, "Error occured during oclc scraping", "Error", JOptionPane.ERROR_MESSAGE);
                }
                return null;
            }

            @Override
            public void done() {
                state.setText("Done scraping!");
                timer.start();
                enableActions();
            }
        };
    }

    private void addNewLine() {
        SwingUtilities.invokeLater(() -> {
            textArea.append(System.lineSeparator());
        });
    }

    private void setCheckField(String str) {
        checkField.setText(str);
    }

    private void changeBookCreator(BookCreatorType creatorType) throws IOException {
        bookCreator = processorFactory.createBookCreator(creatorType);
        newLineFilter.setBookCreator(bookCreator);
    }

    private void changeDataType(DataType dataType) {
        this.dataType = dataType;
        newLineFilter.setDataType(dataType);

    }

    private static class BookCountListener implements DocumentListener {

        private final JLabel countLabel;

        public BookCountListener(JLabel countLabel) {
            this.countLabel = countLabel;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            SetCount(e.getDocument());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            SetCount(e.getDocument());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            SetCount(e.getDocument());
        }

        private void SetCount(Document document) {
            try {
                var length = document.getLength();
                var text = document.getText(0, length);
                var count = countBooks(text);
                
                countLabel.setText("" + count);
            } catch (Exception ex) {
                JWPUserInterface.LOGGER.error("Problem with book count", ex);
            }
        }

        private long countBooks(String text) {
            return Arrays.stream(text.split("\n"))
                    .filter(s -> !s.isBlank())
                    .count();
        }
    }
}
