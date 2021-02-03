package io.github.onlyeat3.whichname;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.util.concurrency.AppExecutorUtil;
import org.apache.commons.lang.StringUtils;
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
        languageNamingRuleMap.put("golang","underline");
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
        if (StringUtils.isEmpty(editor.getSelectionModel().getSelectedText())) {
            editor.getSelectionModel().selectWordAtCaret(true);
        }

        String selectedText = editor.getSelectionModel().getSelectedText(true);
        String currentLanguageName = getCurrentLanguage(e.getDataContext());
        try {
            List<LookupElement> list = AppExecutorUtil.getAppExecutorService().submit(new Callable<List<LookupElement>>() {
                @Override
                public List<LookupElement> call() throws Exception {
                    Gson gson = new Gson();
                    String body = HttpRequest.get(String.format("https://devtuuls.tk/api/lookup_var?word=%s", URLEncoder.encode(selectedText, "UTF-8")))
                            .body();
                    List<LookupElement> elements = new ArrayList<>();
                    List<Map<String,String>> list1 = gson.fromJson(body, ArrayList.class);
                    for (Map<String, String> map : list1) {
                        String namingRule = languageNamingRuleMap.getOrDefault(currentLanguageName, "camel");
                        String varName = map.get(namingRule);
                        if (varName != null) {
                            LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(varName)
                                    .withTypeText("from " + map.get("origin"))
                                    .withTailText(" " + map.get("word"));
                            elements.add(lookupElementBuilder);
                        }
                    }
                    return elements;
                }
            }).get();
            LookupManager lookupManager = LookupManager.getInstance(project);
            lookupManager.showLookup(editor, list.toArray(new LookupElement[]{}));
        } catch (InterruptedException | ExecutionException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    public boolean isWriteable(Project project, Document document){
        return ReadonlyStatusHandler.ensureDocumentWritable(project, document);
    }

    public String getCurrentLanguage(DataContext dataContext){
        Language lang = LangDataKeys.LANGUAGE.getData(dataContext);
        if (lang != null) {
            if (lang.getBaseLanguage() != null) {
                return lang.getBaseLanguage().getDisplayName().toLowerCase();
            }
            return lang.getID().toLowerCase();
        }
        return null;
    }
}
