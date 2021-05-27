package com.aquino.webParser.swing;

import java.awt.event.*;
import java.util.function.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public final class Handlers {
    private Handlers() {}
    
    @SuppressWarnings("serial")
    public static Action anonymousEventClass(String name, Consumer<? super ActionEvent> eventHandler) {
        return new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent event) {
                eventHandler.accept(event);
            }
        };
    }
    
    @SuppressWarnings("serial")
    public static DocumentListener forDocumentUpdate(Consumer<? super DocumentEvent> eventHandler) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                eventHandler.accept(event);
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
            }
        };
    }
        
}