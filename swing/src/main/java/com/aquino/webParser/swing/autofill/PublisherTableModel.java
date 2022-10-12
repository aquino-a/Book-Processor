package com.aquino.webParser.swing.autofill;

import com.aquino.webParser.model.Author;
import com.aquino.webParser.model.Publisher;
import org.apache.commons.lang3.NotImplementedException;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PublisherTableModel extends AbstractTableModel implements AutoFill.HasType {

    private static final PublisherTableModel.LinkButtonRenderer LINK_BUTTON_RENDERER = new PublisherTableModel.LinkButtonRenderer();
    private static final List<String> COLUMNS = List.of(
        "",
        "Native", "English",
        "");

    private final List<Row<Publisher>> publishers;
    private AutoFill.Type type = AutoFill.Type.Publisher;

    public PublisherTableModel(List<Row<Publisher>> publishers) {
        this.publishers = publishers;
    }

    @Override
    public int getRowCount() {
        return publishers.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var row = publishers.get(rowIndex);
        var publisher = row.object();

        switch (columnIndex) {
            case 0:
                return row.isSelected();
            case 1:
                return publisher.getNativeName();
            case 2:
                return publisher.getEnglishName();
            case 3:
                return row;
            default:
                throw new NotImplementedException("not supported");
        }
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS.get(column);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Boolean.class;
            case 1:
            case 2:
                return String.class;
            case 3:
                return Publisher.class;
            default:
                throw new NotImplementedException("not supported");
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
            case 1:
            case 2:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                publishers.get(rowIndex).isSelected((boolean) aValue);
                break;
            case 1:
                getPublisher(rowIndex).setNativeName((String) aValue);
                break;
            case 2:
                getPublisher(rowIndex).setEnglishName((String) aValue);
                break;
            default:
                throw new NotImplementedException("not supported");
        }

        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public static void setColumn(JTable jTable) {
        var idColumn = jTable.getColumnModel().getColumn(3);
        idColumn.setCellRenderer(LINK_BUTTON_RENDERER);
    }

    private Publisher getPublisher(int rowIndex) {
        return publishers.get(rowIndex).object();
    }

    @Override
    public AutoFill.Type type() {
        return type;
    }

    public void type(AutoFill.Type type){
        this.type = type;
    }


    private static class LinkButtonRenderer implements TableCellRenderer {

        private static final JLabel NO_LINK_LABEL = new JLabel("No id", SwingConstants.CENTER);
        private static final JButton BUTTON = new JButton();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex) {
            var row = (Row<Publisher>) value;
            var publisher = row.object();
            var id = publisher.getId();

            if (id > 0) {
                BUTTON.setText(String.valueOf(id));

                return BUTTON;
            } else {
                return NO_LINK_LABEL;
            }
        }
    }
}
