/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.various;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;

/**
 *
 * @author alex
 */
public class DragAndDropListener extends MouseAdapter{
    boolean dragged =false;
    @Override
    public void mouseReleased(MouseEvent e) {
        
        JTextArea area = (JTextArea) e.getComponent();
        area.append(System.getProperty("line.separator"));
        
    }
    
}
