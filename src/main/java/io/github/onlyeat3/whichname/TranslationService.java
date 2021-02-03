package io.github.onlyeat3.whichname;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileChooser.ex.LocalFsFinder;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.local.CoreLocalVirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;


public class TranslationService{

    public void loadAll() {
        Path pluginRootPath = PluginManagerCore.getPlugin(PluginId.getId("io.github.onlyeat3.which-name")).getPluginPath();
        Path cacheDirPath = pluginRootPath.resolve("cache");

        String lastId;
        try {
            if (!Files.exists(cacheDirPath)) {
                Files.createDirectory(cacheDirPath);
            }
            lastId = Files.find(cacheDirPath, 1, (path, basicFileAttributes) -> path.endsWith(".word"))
                    .map(Path::toString)
                    .sorted()
                    .findFirst()
                    .orElse(null);
            boolean hasMore;
            do {
                String body = request(lastId);
                //prepare next
                Gson gson = new Gson();
                List<Map<String,String>> list = gson.fromJson(body, ArrayList.class);
                hasMore = list.isEmpty();
                if (!hasMore) {
                    lastId = list.get(list.size() - 1).get("_id");
                }else{
                    lastId = null;
                }
                Files.write(cacheDirPath.resolve(String.format("%s/%s.word",cacheDirPath,lastId)),body.getBytes(StandardCharsets.UTF_8));
            }while(hasMore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String request(String lastId){
        if (lastId == null) {
            return HttpRequest.get("https://devtuuls.tk/api/load_all")
                    .body();
        }else{
            return HttpRequest.get(String.format("https://devtuuls.tk/api/load_all?currentId=%s",lastId))
                    .body();
        }
    }
}
