package io.github.onlyeat3.whichname.ui;

import com.intellij.openapi.wm.ToolWindow;
import io.github.onlyeat3.whichname.model.SearchResult;
import io.github.onlyeat3.whichname.utils.WhichNameRequestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SearchFormWindow {
    private ToolWindow toolWindow;
    private JPanel panelMain;
    private JButton btnSearch;
    private JTextField txtKeyword;
    private JPanel topPanel;
    private JScrollPane listPane;
    private JTable tableList;
    private JList lsWord;

    public SearchFormWindow(ToolWindow toolWindow){
        this.toolWindow = toolWindow;
        TableColumn headerA = new TableColumn();
        headerA.setHeaderValue("aaaaa");
        tableList.addColumn(headerA);
        SearchResultTableModel tableModel = new SearchResultTableModel();
        tableList.setModel(tableModel);
        this.btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableList.removeAll();

                String keyword = txtKeyword.getText();
                if (StringUtils.isEmpty(keyword)) {
                    return;
                }
                List<SearchResult> searchResults = WhichNameRequestUtils.searchForBean(keyword);
                tableModel.setSearchResultList(searchResults);
            }
        });
    }

    public JPanel getContent() {
        return this.panelMain;
    }

}
