package io.github.onlyeat3.whichname;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.textarea.TextComponentEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.util.io.HttpRequests;
import com.intellij.util.io.RequestBuilder;
import org.apache.http.client.HttpClient;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WhichNameAction extends AnAction {
    public static final ExecutorService EXECUTORS = Executors.newWorkStealingPool(2);
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = CommonDataKeys.EDITOR.getData(e.getDataContext());
        if (!isWriteable(editor.getProject(),editor.getDocument())) {
            return;
        }
        String selectedText = editor.getSelectionModel().getSelectedText(true);
        EXECUTORS.submit(() -> {
            try {
                RequestBuilder requestBuilder = HttpRequests.request("https://devtuuls.tk/api/keyword_search");
                requestBuilder.write(String.format("keyword=%s", URLEncoder.encode(selectedText)));
                String responseJSON = requestBuilder
                        .connect(HttpRequests.Request::readString);
                LookupManager lookupManager = LookupManager.getInstance(project);
                @NotNull LookupElement items = new LookupElement() {
                    @Override
                    public @NotNull String getLookupString() {
                        return responseJSON;
                    }
                };
                lookupManager.showLookup(editor,items);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public boolean isWriteable(Project project, Document document){
        return ReadonlyStatusHandler.ensureDocumentWritable(project, document);
    }
}
