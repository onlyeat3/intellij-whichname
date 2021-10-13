package io.github.onlyeat3.whichname.ui;

import io.github.onlyeat3.whichname.model.SearchResult;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SearchResultTableModel extends DefaultTableModel {
    private List<SearchResult> searchResultList = new ArrayList<>();

    public SearchResultTableModel(List<SearchResult> searchResultList) {
        this.searchResultList = searchResultList;
    }

    public SearchResultTableModel() {
    }

    @Override
    public int getRowCount() {
        return Optional.ofNullable(this.searchResultList)
                .orElse(Collections.emptyList())
                .size();
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
                value = row.getWord();
                break;
            case 1:
                value = row.getPascal();
                break;
            case 2:
                value = row.getCamel();
                break;
            case 3:
                value = row.getUnderline();
                break;
            case 4:
                value = row.getOrigin();
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

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
