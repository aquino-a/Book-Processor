/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.utilities;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

/**
 * @author alex
 */
public class FileUtility {

    private static final String DIRECTORY_PROPERTY = "directory";
    private static final String SAVE_EXTENSION = ".xlsm";
    private static final JFileChooser FILE_CHOOSER = createFileChooser();

    private static JFileChooser createFileChooser() {
        var path = getCurrentDirectoryPath();

        var fc = new JFileChooser();
        if (path.isPresent()) {
            fc.setCurrentDirectory(new File(path.get()));
        }

        var filter = new FileNameExtensionFilter("Excel", "xlsx", "xlsm");
        fc.setFileFilter(filter);
        fc.setAcceptAllFileFilterUsed(false);

        fc.setPreferredSize(new Dimension(800,600));
        return fc;
    }

    private static Optional<String> getCurrentDirectoryPath() {

        try {
            var prop = new Properties();
            prop.load(FileUtility.class.getClassLoader()
                .getResourceAsStream("config.properties"));
            if (!prop.containsKey(DIRECTORY_PROPERTY)) {
                return Optional.empty();
            }

            var directory = prop.getProperty(DIRECTORY_PROPERTY);
            var path = Path.of(directory);
            if (!Files.exists(path)) {
                return Optional.empty();
            }

            return Optional.of(path.toAbsolutePath().toString());
        }
        catch (IOException e) {
            return Optional.empty();
        }
    }

    //Returns null if nothing is selected.  Check for null.
    public static File openFile(JComponent component) {
        JFileChooser fc = getFileChooser();
        if (fc.showOpenDialog(component) == JFileChooser.APPROVE_OPTION)
            return fc.getSelectedFile();
        return null;
    }

    //Returns null if nothing is selected. Check for null
    public static File saveLocation(JComponent component) {
        JFileChooser fc = getFileChooser();
        if (fc.showSaveDialog(component) == JFileChooser.APPROVE_OPTION) {
            if (!fc.getSelectedFile().getName().endsWith(SAVE_EXTENSION)) {
                return new File(fc.getSelectedFile() + SAVE_EXTENSION);
            }
            else {
                return fc.getSelectedFile();
            }
        }

        return null;
    }

    private static JFileChooser getFileChooser() {
        return FILE_CHOOSER;
    }
}
