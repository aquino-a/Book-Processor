/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aquino.webParser.various;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JTextArea;

/**
 *
 * @author alex
 */
public class DragAdapter extends DropTargetAdapter {
     private DropTarget dropTarget;
     private JTextArea area;
     
     public DragAdapter(JTextArea area) {
         this.area = area;
         dropTarget = new DropTarget(area, DnDConstants.ACTION_COPY_OR_MOVE, this, true);
     }
    
    @Override
    public void drop(DropTargetDropEvent event) {
        try {
        Transferable tr = event.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                //  System.out.println("Possible flavor: "
                //        + flavors[i].getMimeType());
                // Check for file lists specifically
                
                // Ok, is it another Java object?
                if (flavors[i].isFlavorSerializedObjectType()) {
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                    Object o = tr.getTransferData(flavors[i]);
                    area.append(String.valueOf(o));
                    event.dropComplete(true);
                    return;
                }
                // How about an input stream?
                else if (flavors[i].isRepresentationClassInputStream()) {
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                    area.read(new InputStreamReader((InputStream) tr
                                    .getTransferData(flavors[i])),
                            "from system clipboard"
                    );
                    event.dropComplete(true);
                    return;
                }
                // How about plain text?
                else if (flavors[i].isFlavorTextType()) {
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    InputStreamReader stream=(InputStreamReader)tr.getTransferData(flavors[i]);
                    BufferedReader in = new BufferedReader(stream);
                    String line = null;
                    while((line = in.readLine()) != null) {
                        area.append(line+"\n");
                    }
                    event.dropComplete(true);
                    return;
                }
            }
            // Hmm, the user must not have dropped a file list
            System.out.println("Drop failed: " + event);
            event.rejectDrop();
        } catch (Exception e) {
            e.printStackTrace();
            event.rejectDrop();
        }
//        Transferable tr = event.getTransferable();
//        try {
//            event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
//            String data = (String) tr.getTransferData(DataFlavor.fragmentHtmlFlavor);
//            area.append(data);
//        } catch (Exception e) {
//            
//        }
//        DataFlavor[] df = tr.getTransferDataFlavors();
//        for(DataFlavor dataf : df) {
//            if(dataf.isFlavorTextType()) {
//                event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
//                try (
//                        InputStreamReader in = (InputStreamReader) tr.getTransferData(dataf);
//                        BufferedReader br = new BufferedReader(in);
//                        ){
//                    String line = null;
//                    while ((line = br.readLine()) != null) {
//                        area.append(line+"\n");
//                        
//                    }
//                    area.append("\n");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    event.dropComplete(false);
//                }
//                event.dropComplete(true);
//                return;
//            }
//        }
//        System.out.println("drop rejected");
//        event.rejectDrop();

          
    }
    
    
    
}
