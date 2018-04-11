/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.various;

import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;

/**
 *
 * @author alex
 */
public class DropListener extends TransferHandler {
    JTextArea area = new JTextArea();
    TransferHandler tr = area.getTransferHandler();
    
    @Override
    public boolean importData(TransferSupport s) {
        tr.importData(s);
        return true;
    }
    
    
    
    
    
}
