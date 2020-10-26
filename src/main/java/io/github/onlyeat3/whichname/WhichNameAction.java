package io.github.onlyeat3.whichname;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.util.concurrency.AppExecutorUtil;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class WhichNameAction extends AnAction {
    public static final ExecutorService EXECUTORS = Executors.newWorkStealingPool(2);
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = CommonDataKeys.EDITOR.getData(e.getDataContext());
        if (!isWriteable(editor.getProject(),editor.getDocument())) {
            return;
        }
        editor.getSelectionModel().selectWordAtCaret(true);
        String selectedText = editor.getSelectionModel().getSelectedText(true);
        try {
            List<String> list = AppExecutorUtil.getAppExecutorService().submit(new Callable<List<String>>() {
                @Override
                public List<String> call() throws Exception {
                    Gson gson = new Gson();
                    String body = HttpRequest.get(String.format("https://devtuuls.tk/api/lookup_var?word=%s", URLEncoder.encode(selectedText, "UTF-8")))
                            .body();
                    ArrayList<Map<String,String>> list = gson.fromJson(body, ArrayList.class);
                    return list.stream()
                            .map(e -> e.get("camel"))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                }
            }).get();
            LookupManager lookupManager = LookupManager.getInstance(project);
            List<LookupElement> elements = new ArrayList<>();
            for (String varName : list) {
                @NotNull LookupElement items = new LookupElement() {
                    @Override
                    public @NotNull String getLookupString() {
                        return varName;
                    }
                };
                elements.add(items);
            }
            lookupManager.showLookup(editor, elements.toArray(new LookupElement[]{}));
        } catch (InterruptedException | ExecutionException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    public boolean isWriteable(Project project, Document document){
        return ReadonlyStatusHandler.ensureDocumentWritable(project, document);
    }
}
