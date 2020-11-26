/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser;

import com.aquino.webParser.bookCreators.BookCreatorType;
import com.aquino.webParser.model.Author;
import com.aquino.webParser.model.Book;
import com.aquino.webParser.model.DataType;
import com.aquino.webParser.model.Language;
import com.aquino.webParser.utilities.Connect;
import com.aquino.webParser.utilities.FileUtility;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Stream;
import javax.swing.*;

/**
 *
 * @author alex
 */
public class AuthorPublisherAutoFill extends javax.swing.JFrame {

    private XSSFWorkbook workbook;
    private AutoFillService autoFillService;

    /**
     * Creates new form AuthorPublisherAutoFill2
     */
    public AuthorPublisherAutoFill() {
        initComponents();
        setupActions();
        disableActions();
    }

    public AuthorPublisherAutoFill(AutoFillService autoFillService) throws HeadlessException {
        this();
        this.autoFillService = autoFillService;
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if(workbook != null) {
                    try {
                        workbook.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        });
    }

    private void setupActions() {
        openMenuItem.setAction(openAction);
        saveMenuItem.setAction(saveAction);
        closeMenuItem.setAction(closeAction);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        autoFillButton = new javax.swing.JButton();
        bookRowContainer1 = new javax.swing.JPanel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(130, 0), new java.awt.Dimension(20, 32767));
        jLabel1 = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(20, 32767));
        jLabel2 = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(20, 32767));
        jLabel3 = new javax.swing.JLabel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(50, 0), new java.awt.Dimension(20, 32767));
        jLabel4 = new javax.swing.JLabel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(20, 32767));
        jLabel5 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(10, 32767));
        jLabel6 = new javax.swing.JLabel();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        jLabel7 = new javax.swing.JLabel();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(20, 32767));
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        bookRowContainer = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        textFileName = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textConsole = new javax.swing.JTextPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        closeMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.BorderLayout());

        autoFillButton.setText("Auto Fill");
        jPanel1.add(autoFillButton, java.awt.BorderLayout.PAGE_END);

        bookRowContainer1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        bookRowContainer1.add(filler3);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("native first");
        bookRowContainer1.add(jLabel1);
        bookRowContainer1.add(filler4);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("native last");
        bookRowContainer1.add(jLabel2);
        bookRowContainer1.add(filler5);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("english first");
        bookRowContainer1.add(jLabel3);
        bookRowContainer1.add(filler6);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("english last");
        bookRowContainer1.add(jLabel4);
        bookRowContainer1.add(filler7);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("author link");
        bookRowContainer1.add(jLabel5);
        bookRowContainer1.add(filler2);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("native publisher");
        bookRowContainer1.add(jLabel6);
        bookRowContainer1.add(filler8);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("english publisher");
        bookRowContainer1.add(jLabel7);
        bookRowContainer1.add(filler9);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("publisher link");
        bookRowContainer1.add(jLabel8);

        jPanel1.add(bookRowContainer1, java.awt.BorderLayout.PAGE_START);

        bookRowContainer.setLayout(new javax.swing.BoxLayout(bookRowContainer, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane2.setViewportView(bookRowContainer);

        jPanel1.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jScrollPane1.setViewportView(textConsole);

        fileMenu.setText("File");

        openMenuItem.setText("Open");
        fileMenu.add(openMenuItem);

        saveMenuItem.setText("Save");
        fileMenu.add(saveMenuItem);

        closeMenuItem.setText("Close");
        fileMenu.add(closeMenuItem);

        jMenuBar1.add(fileMenu);

        editMenu.setText("Edit");
        jMenuBar1.add(editMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 970, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(textFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(filler1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .addComponent(textFileName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AuthorPublisherAutoFill.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AuthorPublisherAutoFill.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AuthorPublisherAutoFill.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AuthorPublisherAutoFill.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AuthorPublisherAutoFill frame =  new AuthorPublisherAutoFill();
                frame.setSize(1400, 600);
                frame.setLocationRelativeTo(null);
                frame.bookRowContainer.add(new BookRow());
                frame.bookRowContainer.add(new BookRow());
                frame.bookRowContainer.add(new BookRow());

                frame.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton autoFillButton;
    private javax.swing.JPanel bookRowContainer;
    private javax.swing.JPanel bookRowContainer1;
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JTextPane textConsole;
    private javax.swing.JLabel textFileName;
    // End of variables declaration//GEN-END:variables

    private final Action openAction = Handlers.anonymousEventClass("Open", (event) -> {
        try {
            File file = FileUtility.openFile(jPanel1);
            workbook = Connect.openExistingWorkbook(file);
            autoFillService.readBooks(workbook)
                    .stream()
                    .forEach(model -> {
                        bookRowContainer.add(new BookRow(model));
                    });
            if(Arrays.stream(bookRowContainer.getComponents()).filter(c -> c instanceof BookRow).count() > 0){
                setFileLabel(file.getName());
                enableActions();
            } else {
                closeWorkbook();
                textConsole.setText("No data to change");
            }
        } catch (IllegalArgumentException | NullPointerException | IOException e) {
            textConsole.setText(String.format("Open failed: %s", e.getCause().getMessage()));
        }
    });

    private final Action autoFillAction = Handlers.anonymousEventClass("Auto Fill", (event) -> {
        try {
            Stream.of(bookRowContainer.getComponents())
                    .filter(c -> c instanceof BookRow)
                    .map(c -> (BookRow) c)
                    .forEach(br ->{
                        var afm = br.getAutoFillModel();
                        InsertAuthor(br, afm.getAuthor());
                        //TODO insert pub and add link
                        afm.UpdateBook();
                    });
//            enableActions();
        } catch (IllegalArgumentException | NullPointerException e) {
            textConsole.setText(String.format("Auto Fill fail: %s", e.getMessage()));
        }
    });

    private void InsertAuthor(BookRow br, Author author) {
        if(author == null)
            return;
        var id = autoFillService.insertAuthor(author);
        var link = autoFillService.getAuthorLink(id);
        br.setAuthorLink(id, link);
    }

    private final Action saveAction = Handlers.anonymousEventClass("Save", (event) -> {
        try {
            File file = FileUtility.openFile(jPanel1);
            if(file == null)
                return;
            var updater = new ExcelUpdater(workbook);
            Stream.of(bookRowContainer.getComponents())
                    .filter(c -> c instanceof BookRow)
                    .map(c -> (BookRow) c)
                    .forEach(br ->{
                        var afm = br.getAutoFillModel();
                        updater.UpdateBook(afm.getBookPair().getLeft(), afm.getBookPair().getRight());
                    });

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
//            enableActions();
        } catch (IllegalArgumentException | NullPointerException | IOException e) {
            textConsole.setText(String.format("save fail: %s", e.getMessage()));
            e.printStackTrace();
        }
    });

    private final Action closeAction = Handlers.anonymousEventClass("Close", (event) -> {
        try {
            closeWorkbook();
            textFileName.setText("");
            disableActions();
        } catch (IllegalArgumentException | NullPointerException | IOException e) {
            textConsole.setText(String.format("close fail: %s", e.getMessage()));
            e.printStackTrace();
        }
    });

    private final Action japaneseAction = Handlers.anonymousEventClass("Japanese", (event) -> {
        autoFillService.setLanguage(Language.Japanese);
//        language.setText("Japanese");
    });

    private final Action koreanAction = Handlers.anonymousEventClass("Korean", (event) -> {
        textConsole.setText("Korean not ready at the moment");
//        autoFillService.setLanguage(Language.Korean);
    });

    private void closeWorkbook() throws IOException {
        if(workbook != null)
            workbook.close();
        workbook = null;
    }

    private void enableActions() {
        saveAction.setEnabled(true);
        closeAction.setEnabled(true);
    }

    private void disableActions() {
        saveAction.setEnabled(false);
        closeAction.setEnabled(false);
    }


    private void setFileLabel(String fileName) {
        textFileName.setText(fileName);
    }
}
