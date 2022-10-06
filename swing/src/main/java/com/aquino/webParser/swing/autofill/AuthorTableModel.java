package com.aquino.webParser.swing.autofill;

import com.aquino.webParser.model.Author;
import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.lang3.NotImplementedException;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorTableModel extends AbstractTableModel {

    private static final List<String> COLUMNS = List.of(
        "",
        "Native First", "Native Last",
        "English First", "English Last",
        "");

    private final List<Row> authors;

    public AuthorTableModel(List<Author> authors) {
        this.authors = new UnmodifiableList<>(authors.stream()
            .map(Row::new)
            .collect(Collectors.toList()));
    }

    @Override
    public int getRowCount() {
        return authors.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var row = authors.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return row.isSelected();
            case 1:
                return getAuthor(row).getNativeFirstName();
            case 2:
                return getAuthor(row).getNativeLastName();
            case 3:
                return getAuthor(row).getEnglishFirstName();
            case 4:
                return getAuthor(row).getEnglishLastName();
            case 5:
                return getAuthor(row).getId();
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
            case 3:
            case 4:
            case 5:
                return String.class;
            default:
                throw new NotImplementedException("not supported");
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex){
            case 0:
                authors.get(rowIndex).setSelected((boolean) aValue);
                break;
            case 1:
                getAuthor(rowIndex).setNativeFirstName((String) aValue);
                break;
            case 2:
                getAuthor(rowIndex).setNativeLastName((String) aValue);
                break;
            case 3:
                getAuthor(rowIndex).setEnglishFirstName((String) aValue);
                break;
            case 4:
                getAuthor(rowIndex).setEnglishLastName((String) aValue);
                break;
            default:
                throw new NotImplementedException("not supported");
        }

        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public List<Author> getAuthors() {
        return authors.stream()
            .map(AuthorTableModel::getAuthor)
            .collect(Collectors.toList());
    }

    private static Author getAuthor(Row row) {
        return (Author) row.getObject();
    }

    private Author getAuthor(int rowIndex) {
        return getAuthor(authors.get(rowIndex));
    }
}
