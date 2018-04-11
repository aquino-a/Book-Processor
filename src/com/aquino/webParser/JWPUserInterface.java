/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser;

import com.aquino.webParser.Utilities.Connect;
import com.aquino.webParser.filters.NewLineFilter;
import com.aquino.webParser.Utilities.FileUtility;
import com.aquino.webParser.filters.CheckFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

/**
 *
 * @author alex
 */
public class JWPUserInterface extends JPanel {
    private static JFrame frame;
    private JButton addButton, saveButton;
    private JPanel mainPanel,buttonPanel,checkPanel;
    static JMenuBar menuBar;
    private JMenu file;
    private JTextArea textArea;
    private ExcelWriter writer;
    private File saveFile;
    private JLabel fileName, state;
    private Timer timer;
    private JTextField checkField;
    private String checkedLink;
    
    private JWPUserInterface() {
        addcomponents();
    }
    public static JWPUserInterface getInstance() {
        return new JWPUserInterface();
    }
//TODO fix savebutton
    private void addcomponents() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        mainPanel = new JPanel();
        buttonPanel = new JPanel();
        checkPanel = new JPanel();
        textArea = new JTextArea(37,35);
        textArea.setLineWrap(true);
        ((AbstractDocument) textArea.getDocument()).
                setDocumentFilter(new NewLineFilter());
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
        }));
        
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
        menuBar.add(file);
        
        //panel
        mainPanel.add(textArea);
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
    private final Action addAction = Handlers.anonymousEventClass("Add", (event) -> {
        getAddWorker().execute();
    });
    
    private final Action saveAction = Handlers.anonymousEventClass("Save", (event) -> {
        try {
            //if(saveFile == null) saveFile = FileUtility.saveLocation(mainPanel);
            if(saveFile == null) askSaveFile();
            writer.saveFile(saveFile);
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
    
    public static void createAndShowGUI() {
        frame = new JFrame("Jeein's Book Processor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        //content pane
        frame.setContentPane(getInstance());
        frame.setJMenuBar(menuBar);
        
        //pack
        frame.pack();
        frame.setLocation((int)
                java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().
                        getMaximumWindowBounds().getWidth()-frame.getWidth(), 0);
        frame.setVisible(true);
    }
    private void enableActions() {
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
                    writer.writeBooks(Book.retrieveBookArray(textArea.getText()));
                } catch (Exception e ) {
                }
                return null;
            }
            @Override
            public void done() {
                state.setText("Added!");
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
    
    


    

  
    
    
    
    
}
