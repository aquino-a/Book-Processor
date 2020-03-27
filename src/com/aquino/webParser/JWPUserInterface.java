/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser;

import com.aquino.webParser.bookCreators.BookCreatorType;
import com.aquino.webParser.oclc.OCLCChecker;
import com.aquino.webParser.oclc.OclcProgress;
import com.aquino.webParser.utilities.Connect;
import com.aquino.webParser.bookCreators.BookCreator;
import com.aquino.webParser.filters.NewLineFilter;
import com.aquino.webParser.utilities.FileUtility;
import com.aquino.webParser.utilities.Links;
import com.aquino.webParser.filters.CheckFilter;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

/**
 *
 * @author alex
 */
public class JWPUserInterface extends JPanel {
    
    private static final Logger logger = Logger.getLogger(JWPUserInterface.class.getName());
    
    private  JFrame frame;
    private JButton addButton, saveButton;
    private JPanel mainPanel,buttonPanel,checkPanel;
    private JMenuBar menuBar;
    private JMenu file;
    private JTextArea textArea;
    private ExcelWriter writer;
    private DescriptionWriter desWriter;
    private File saveFile;
    private JLabel fileName, state;
    private Timer timer;
    private JTextField checkField;
    private String checkedLink;
    private JMenu language;
    private OclcProgress oclcProgress;
    private NewLineFilter newLineFilter;

    private ProcessorFactoryImpl processorFactory;
    private DataType dataType = DataType.BookPage;
    private BookCreator bookCreator;
    
    public JWPUserInterface(ProcessorFactoryImpl processorFactory, BookCreator defaultCreator) throws IOException {
        this.processorFactory = processorFactory;
        this.bookCreator = defaultCreator;
        addcomponents();
    }

    //TODO fix savebutton
    private void addcomponents() throws IOException {

        frame = new JFrame("Jeein's OldBook Processor");
        desWriter = new DescriptionWriter();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        mainPanel = new JPanel();
        buttonPanel = new JPanel();
        checkPanel = new JPanel();
        textArea = new JTextArea(37,35);
        textArea.setLineWrap(true);
        newLineFilter = new NewLineFilter(dataType, bookCreator);
        ((AbstractDocument) textArea.getDocument()).
                setDocumentFilter(newLineFilter);
        addButton = new JButton(addAction);
        saveButton = new JButton(saveAction);
        fileName = new JLabel("",SwingConstants.LEFT);
        state = new JLabel("",SwingConstants.RIGHT);
        
        //Textfield checker
        checkField = new JTextField(25);
        ((AbstractDocument)checkField.getDocument()).
                setDocumentFilter(new CheckFilter(new Consumer() {
            @Override
            public void accept(Object t) {
                checkedLink = (String) t;
            }
        }, frame, processorFactory.CreateBookCreator(BookCreatorType.AladinApi)));
        
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
        language = new JMenu("Language");
        language.add(new JMenuItem(koreanAction));
        language.add(new JMenuItem(japaneseAction));


        menuBar.add(file);
        menuBar.add(tools);
        menuBar.add(language);
        //panel
        mainPanel.add(new JScrollPane(textArea,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
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

    private final Action japaneseAction = Handlers.anonymousEventClass("Japanese", (event) -> {
        try {
            changeBookCreator(BookCreatorType.AmazonJapan);
            changeDataType(DataType.Isbn);
            language.setText("Japanese");
        } catch (IOException e) {
            e.printStackTrace();
        }
    });

    private final Action koreanAction = Handlers.anonymousEventClass("Korean", (event) -> {
        try {
            changeBookCreator(BookCreatorType.AladinApi);
            changeDataType(DataType.BookPage);
            language.setText("Korean");
        } catch (IOException e) {
            e.printStackTrace();
        }
    });

    private final Action addAction = Handlers.anonymousEventClass("Add", (event) -> {
        getAddWorker().execute();
    });
    
    private final Action scrapeOclc = Handlers.anonymousEventClass("Scrape OCLCs", (event) ->{
        getOclcWorker(Links.Type.NEW).execute();
    });
    
    private final Action scrapeBestOclc = Handlers.anonymousEventClass("Scrape BEST OCLCs", (event) ->{
        getOclcWorker(Links.Type.BEST).execute();
    });
    
    private final Action saveAction = Handlers.anonymousEventClass("Save", (event) -> {
        try {
            //if(saveFile == null) saveFile = FileUtility.saveLocation(mainPanel);
            if(saveFile == null) askSaveFile();
            writer.saveFile(saveFile);
            desWriter.saveBooks(saveFile);
            state.setText("Saved!");
            timer.start();
        } catch (NullPointerException e) {
            saveFile = null;
        }
    });
    
    private final Action saveAsAction = Handlers.anonymousEventClass("Save As", (event) ->{
        try {
            askSaveFile();
            writer.saveFile(saveFile);
            desWriter.saveBooks(saveFile);
            
        } catch (NullPointerException e) {
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
        } catch (IllegalArgumentException | NullPointerException | FileNotFoundException e) {
            writer = null;
            state.setText("Open failed.");
            timer.start();
        }
    });
    
    private final Action newAction = Handlers.anonymousEventClass("New", (event) -> {
        writer = new ExcelWriter(Connect.newWorkbookFromTemplate());
        setFileLabel("");
        saveFile = null;
        enableActions();
    });
    private final Action clearText = Handlers.anonymousEventClass("Clear", (event) -> {
        textArea.setText("");
    });
    private final Action deleteState = Handlers.anonymousEventClass("", (event) -> {
        state.setText("");
    });
    private final Action useAction = Handlers.anonymousEventClass("Use", (event) -> {
        textArea.append(checkedLink);
    });
    
    private final DocumentListener addNewLine = Handlers.forDocumentUpdate((event) -> {
        if(event.getLength() > 2 ) addNewLine();
    });
    
    public void createAndShowGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        //content pane
        frame.setContentPane(this);
        frame.setJMenuBar(menuBar);
        
        //pack
        frame.pack();
        frame.setLocation((int)
                java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().
                        getMaximumWindowBounds().getWidth()-frame.getWidth(), 0);
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
    private void askSaveFile() throws NullPointerException{
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
        return new SwingWorker<Void,Void>() {
            @Override
            public Void doInBackground() {
                try {
                    disableActions();
//                    Book[] books = OldBook.retrieveBookArray(textArea.getText());
                    if(textArea.getText().trim().equals(""))
                        return null;
                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    List<Book> books = fetchBooks();
                    writer.writeBooks(books);
                    desWriter.writeBooks(books);
                } catch (Exception e ) {
                    logger.log(Level.SEVERE, "Problem adding.");
                    e.printStackTrace();
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
        List<Book> books = null;
        if(dataType == DataType.BookPage)
            books = bookCreator.bookListFromLink(textArea.getText());
        else if (dataType == DataType.Isbn)
            books = GetBookListFromIsbns(textArea.getText());
        books.stream().forEach(book -> bookCreator.fillInAllDetails(book));
        return books;
    }

    private List<Book> GetBookListFromIsbns(String text) {
        List<Book> list = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(text);
        while(st.hasMoreTokens()) {
            String isbn = st.nextToken();
            try {
                Book book = bookCreator.createBookFromIsbn(isbn);
                if(book.getIsbn() == 0)
                    throw new Exception(String.format("ISBN is 0"));
                list.add(book);
            }
            catch (Exception e){
                JOptionPane.showMessageDialog(frame, String.format("Problem with isbn: {0}", isbn));
                logger.log(Level.SEVERE, String.format("Problem with isbn: {0}", isbn));
                logger.log(Level.SEVERE, e.getMessage());
                continue;
            }
        }
        return list;
    }

    private SwingWorker getOclcWorker(Links.Type type) {
        return new SwingWorker<Void,Void>() {
            @Override
            public Void doInBackground() {
                try {
                    disableActions();
                    Links.setType(type);
                    OCLCChecker checker = new OCLCChecker(processorFactory.CreateBookCreator(BookCreatorType.AladinApi));
                    if(oclcProgress == null)
                        oclcProgress = new OclcProgress(frame);
                    oclcProgress.start();
                    checker.getHitsAndWrite(1, type.getPages(), mainPanel, oclcProgress::setProgress);
                    logger.log(Level.INFO, "Done scraping for oclc numbers.");
                }
                catch(IOException ex){
                    JOptionPane.showMessageDialog(frame,"Reached end of pages", "Done",JOptionPane.INFORMATION_MESSAGE);
                }
                catch (Exception e ) {
                    Logger.getLogger("oclc").log(Level.SEVERE, "oclc problems");
                    JOptionPane.showMessageDialog(frame,"Error occured during oclc scraping", "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
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
        SwingUtilities.invokeLater( () -> {
            textArea.append(System.lineSeparator());
        });
    }
    private void setCheckField(String str) {
        checkField.setText(str);
    }

    private void changeBookCreator(BookCreatorType creatorType) throws IOException {
        bookCreator = processorFactory.CreateBookCreator(creatorType);
        newLineFilter.setBookCreator(bookCreator);
    }

    private void changeDataType(DataType dataType) {
        this.dataType = dataType;
        newLineFilter.setDataType(dataType);

    }
    
    


    

  
    
    
    
    
}
