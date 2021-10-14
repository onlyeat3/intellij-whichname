package io.github.onlyeat3.whichname.ui;

import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import io.github.onlyeat3.whichname.listener.TableCopyAdapter;
import io.github.onlyeat3.whichname.model.SearchResult;
import io.github.onlyeat3.whichname.utils.WhichNameRequestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class SearchFormWindow {
    private ToolWindow toolWindow;
    private JPanel panelMain;
    private JButton btnSearch;
    private JTextField txtKeyword;
    private JScrollPane listPane;
    private JTable tableList;
    private JPanel topPanel;
    private JTextPane loadingTextPane;

    public SearchFormWindow(ToolWindow toolWindow) {
        this.loadingTextPane.setVisible(false);
        StyledDocument doc = this.loadingTextPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        this.toolWindow = toolWindow;

        SearchResultTableModel tableModel = new SearchResultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"word", "pascal", "camel", "underline", "origin"});
        tableList.setModel(tableModel);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        tableList.setDefaultRenderer(Object.class, renderer);

        new TableCopyAdapter(tableList);

        this.btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData(tableModel);
            }
        });
        this.txtKeyword.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loadData(tableModel);
                }
            }
        });
    }


    private void loadData(SearchResultTableModel tableModel) {
        tableList.removeAll();
        String keyword = txtKeyword.getText();
        if (StringUtils.isEmpty(keyword)) {
            return;
        }

        loadingTextPane.setVisible(true);
        listPane.setVisible(false);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                List<SearchResult> searchResults = WhichNameRequestUtils.searchForBean(keyword);
                tableModel.setSearchResultList(searchResults);

                loadingTextPane.setVisible(false);
                listPane.setVisible(true);
            }
        });

    }

    public JPanel getContent() {
        return this.panelMain;
    }

}
