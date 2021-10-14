package io.github.onlyeat3.whichname.utils;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import io.github.onlyeat3.whichname.model.SearchResult;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WhichNameRequestUtils {
    private WhichNameRequestUtils(){}

    public static String search(String keyword){
        try {
            String url = String.format("https://whichname.top/api/lookup_var?word=%s", URLEncoder.encode(keyword, "UTF-8"));
            return HttpUtil.get(url);
        } catch (Exception e) {
            try{
                String url = String.format("https://devtuuls.tk/api/lookup_var?word=%s", URLEncoder.encode(keyword, "UTF-8"));
                return HttpUtil.get(url);
            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }
        }
    }

    public static List<Map<String, String>> searchForMap(String keyword){
        String body = search(keyword);
        return JSONUtil.parseArray(body).toBean(new TypeReference<List<Map<String, String>>>() {
            @Override
            public Type getType() {
                return ArrayList.class;
            }
        });
    }

    public static List<SearchResult> searchForBean(String keyword){
        String body = search(keyword);
        return JSONUtil.parseArray(body).toList(SearchResult.class);
    }
}
