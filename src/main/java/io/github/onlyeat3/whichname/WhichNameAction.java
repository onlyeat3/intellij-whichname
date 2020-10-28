package io.github.onlyeat3.whichname;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.*;
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
        editor.getSelectionModel().selectWordAtCaret(true);
        String selectedText = editor.getSelectionModel().getSelectedText(true);
        String currentLanguageName = getCurrentLanguage(e.getDataContext());
        try {
            List<String> list = AppExecutorUtil.getAppExecutorService().submit(new Callable<List<String>>() {
                @Override
                public List<String> call() throws Exception {
                    Gson gson = new Gson();
                    String body = HttpRequest.get(String.format("https://devtuuls.tk/api/lookup_var?word=%s", URLEncoder.encode(selectedText, "UTF-8")))
                            .body();
                    List<String> resultList = new ArrayList<>();
                    List<Map<String,String>> list1 = gson.fromJson(body, ArrayList.class);
                    for (Map<String, String> map : list1) {
                        String namingRule = languageNamingRuleMap.getOrDefault(currentLanguageName, "camel");
                        String varName = map.get(namingRule);
                        if (varName != null) {
                            resultList.add(varName);
                        }
                    }
                    return resultList;
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

    public String getCurrentLanguage(DataContext dataContext){
        Language lang = LangDataKeys.LANGUAGE.getData(dataContext);
        if (lang != null && lang.getBaseLanguage() != null) {
            return lang.getBaseLanguage().getDisplayName().toLowerCase();
        }
        return null;
    }
}
