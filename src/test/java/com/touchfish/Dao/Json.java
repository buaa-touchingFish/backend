package com.touchfish.Dao;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.touchfish.MiddleClass.AuthorShip;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Json {
    public static void main(String[] args){
        String a="[{\"author\": {\"id\": \"A5046060925\", \"display_name\": \"Laura Mureşan\"}, \"institutions\": [{\"id\": \"I3125347698\", \"display_name\": \"Babeș-Bolyai University\"}]}, {\"author\": {\"id\": \"A5042327654\", \"display_name\": \"A.I. Cadiş\"}, \"institutions\": [{\"id\": \"I3125347698\", \"display_name\": \"Babeș-Bolyai University\"}]}, {\"author\": {\"id\": \"A5075550420\", \"display_name\": \"I. Perhaiţa\"}, \"institutions\": [{\"id\": \"I3125347698\", \"display_name\": \"Babeș-Bolyai University\"}]}, {\"author\": {\"id\": \"A5033290895\", \"display_name\": \"O. Ponta\"}, \"institutions\": [{\"id\": \"I3125347698\", \"display_name\": \"Babeș-Bolyai University\"}]}, {\"author\": {\"id\": \"A5064289237\", \"display_name\": \"O. Pană\"}, \"institutions\": [{\"id\": \"I4210148481\", \"display_name\": \"National Institute for Research and Development of Isotopic and Molecular Technologies\"}]}, {\"author\": {\"id\": \"A5044246727\", \"display_name\": \"L. Trinkler\"}, \"institutions\": [{\"id\": \"I91123046\", \"display_name\": \"University of Latvia\"}]}, {\"author\": {\"id\": \"A5023800736\", \"display_name\": \"B. Bērziņa\"}, \"institutions\": [{\"id\": \"I91123046\", \"display_name\": \"University of Latvia\"}]}, {\"author\": {\"id\": \"A5055791297\", \"display_name\": \"V. Korsaks\"}, \"institutions\": [{\"id\": \"I91123046\", \"display_name\": \"University of Latvia\"}]}]";
        Pattern pattern=Pattern.compile("(\\{(?:(?!\\]\\}).)*?\\]\\})");
        Matcher matcher=pattern.matcher(a);
        List<String>b=new ArrayList<>();
        while (matcher.find())
            b.add(matcher.group());
        AuthorShip authorShip= JSONUtil.toBean(b.get(1),AuthorShip.class);
        System.out.println(authorShip);
    }

    private static List<String> extractDisplayNames(String jsonString) {
        List<String> displayNames = new ArrayList<>();

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = null;
            try {
                obj = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            JSONObject author = null;
            try {
                author = obj.getJSONObject("author");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            String displayName = null;
            try {
                displayName = author.getString("display_name");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            displayNames.add(displayName);
        }

        return displayNames;
    }
}
