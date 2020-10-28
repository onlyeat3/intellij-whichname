package io.github.onlyeat3.whichname;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.util.concurrency.AppExecutorUtil;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class WhichNameAction extends AnAction {
    public static final Map<String, String> languageNamingRuleMap = new HashMap<>();
    static {
        languageNamingRuleMap.put("java","camel");
        languageNamingRuleMap.put("objective-c","camel");
        languageNamingRuleMap.put("swift","camel");
        languageNamingRuleMap.put("sql","underline");
        languageNamingRuleMap.put("go","underline");
        languageNamingRuleMap.put("php","underline");
        languageNamingRuleMap.put("python","underline");
        languageNamingRuleMap.put("ruby","underline");
        languageNamingRuleMap.put("javascript","camel");
        languageNamingRuleMap.put("rust","underline");
        languageNamingRuleMap.put("kotlin","camel");
        languageNamingRuleMap.put("scala","camel");
        languageNamingRuleMap.put("r","pascal");
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = CommonDataKeys.EDITOR.getData(e.getDataContext());
        if (!isWriteable(editor.getProject(),editor.getDocument())) {
            return;
        }
        String languageName = LangDataKeys.LANGUAGE.getData(e.getDataContext()).getDisplayName().toLowerCase();
        editor.getSelectionModel().selectWordAtCaret(true);
        String selectedText = editor.getSelectionModel().getSelectedText(true);
        try {
            List<String> list = AppExecutorUtil.getAppExecutorService().submit(() -> {
                Gson gson = new Gson();
                String body = HttpRequest.get(String.format("https://devtuuls.tk/api/lookup_var?word=%s", URLEncoder.encode(selectedText, "UTF-8")))
                        .body();
                List<Map<String,String>> list1 = gson.fromJson(body, ArrayList.class);
                return list1.stream()
                        .map(e1 -> {
                            String namingRule = languageNamingRuleMap.getOrDefault(languageName, "camel");
                            return e1.get(namingRule);
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
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
