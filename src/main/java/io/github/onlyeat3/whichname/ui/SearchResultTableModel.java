package io.github.onlyeat3.whichname.ui;

import io.github.onlyeat3.whichname.model.SearchResult;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SearchResultTableModel extends AbstractTableModel {
    private List<SearchResult> searchResultList = new ArrayList<>();

    public SearchResultTableModel(List<SearchResult> searchResultList) {
        this.searchResultList = searchResultList;
    }

    public SearchResultTableModel() {
    }

    @Override
    public int getRowCount() {
        return this.searchResultList.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SearchResult row = this.searchResultList.get(rowIndex);
        if (row == null) {
            return null;
        }
        Object value = null;
        switch (columnIndex) {
            case 0:
                value = row.getOrigin();
                break;
            case 1:
                value = row.getWord();
                break;
            case 2:
                value = row.getCamel();
                break;
            case 3:
                value = row.getPascal();
                break;
            case 4:
                value = row.getPlainText();
                break;
            case 5:
                value = row.getUnderline();
                break;
            default:
                value = null;
        }
        return value;
    }

    public List<SearchResult> getSearchResultList() {
        return searchResultList;
    }

    public void setSearchResultList(List<SearchResult> searchResultList) {
        this.searchResultList = searchResultList;
    }
}
