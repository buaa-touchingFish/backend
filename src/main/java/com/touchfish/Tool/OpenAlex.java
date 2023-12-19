package com.touchfish.Tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchfish.Po.Author;
import com.touchfish.Po.Paper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenAlex {

    public static Object sendResponse(String table, String id) {
        try {
            Object object = null;
            String str = "https://api.openalex.org/" + table + "s/" + findId(id);
            URL url = new URL(str);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                String jsonResponse = response.toString();
//                System.out.println(jsonResponse);
                ObjectMapper mapper = new ObjectMapper();
                if (table.equals("author")) {
                    object = mapper.readValue(jsonResponse, Author.class);
                    Author author = (Author)object;
                    JsonNode jsonNode = mapper.readTree(jsonResponse);
                    Integer h_index = jsonNode.get("summary_stats").get("h_index").asInt();
                    List<String> fields = new ArrayList<>();
                    JsonNode XConceptNodes = jsonNode.get("x_concepts");
                    for(JsonNode XConceptNode: XConceptNodes) {
                        String field = XConceptNode.get("display_name").asText();
                        fields.add(field);
                    }
                    author.setH_index(h_index);
                    author.setFields(fields);

                } else if (table.equals("paper")) {
                    object = mapper.readValue(jsonResponse, Paper.class);
                }
            } else {
                System.out.println("HTTP request failed with response code: " + responseCode);
            }
            connection.disconnect();
            return object;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String findId(String url) {
        Pattern pattern = Pattern.compile("/([A-Za-z0-9]+)$");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        String table = "author";
//        String id = "https://openalex.org/A5000000036";
//        String id = "https://openalex.org/A5051522788";
        String id = "https://openalex.org/A5075452443";
        Author author = (Author) sendResponse(table, id);
        System.out.println(author);
    }
}
