/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.utilities;

import java.io.File;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author alex
 */
public class FileUtility {
    
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
        if(fc.showSaveDialog(component) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }
            
        return null;
    }
    private static JFileChooser getFileChooser() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Excel", "xlsx"));
        return fc;
    }
}
