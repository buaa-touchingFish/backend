package com.touchfish.Tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchfish.MiddleClass.LastKnownInstitution;
import com.touchfish.Po.Author;
import com.touchfish.Po.Institution;
import com.touchfish.Po.InstitutionRelation;
import com.touchfish.Po.Paper;
import com.touchfish.Service.impl.PaperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            String str = "https://api.openalex.org/" + table + "s/" + id;
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
                System.out.println(jsonResponse);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(jsonResponse);
                switch (table) {
                    case "author" -> {
                        object = mapper.readValue(jsonResponse, Author.class);
                        Author author = (Author) object;
                        Integer h_index = jsonNode.get("summary_stats").get("h_index").asInt();
                        List<String> authorFields = new ArrayList<>();
                        JsonNode authorXConceptNodes = jsonNode.get("x_concepts");
                        for (JsonNode XConceptNode : authorXConceptNodes) {
                            String field = XConceptNode.get("display_name").asText();
                            authorFields.add(field);
                        }
                        author.setId(findId(author.getId()));
                        LastKnownInstitution lastKnownInstitution = author.getLast_known_institution();
                        lastKnownInstitution.setId(findId(lastKnownInstitution.getId()));
                        author.setLast_known_institution(lastKnownInstitution);
                        author.setH_index(h_index);
                        author.setFields(authorFields);
                    }
                    case "institution" -> {
                        object = mapper.readValue(jsonResponse, Institution.class);
                        Institution institution = (Institution) object;
                        List<String> instFields = new ArrayList<>();
                        JsonNode XConceptNodes = jsonNode.get("x_concepts");
                        for (JsonNode XConceptNode : XConceptNodes) {
                            String field = XConceptNode.get("display_name").asText();
                            instFields.add(field);
                        }
                        List<InstitutionRelation> associatedInstitutions = institution.getAssociated_institutions();
                        for (InstitutionRelation associatedInstitution : associatedInstitutions) {
                            associatedInstitution.setId(findId(associatedInstitution.getId()));
                        }
                        institution.setAssociated_institutions(associatedInstitutions);
                        institution.setId(findId(institution.getId()));
                        institution.setFields(instFields);
                    }
                    case "work" -> {
                        return jsonNode;
                    }
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
        String table, id;
//        table = "author";
//        id = "A5077915689";
//        Author author = (Author) sendResponse(table, id);
//        System.out.println(author);
        table = "work";
        id = "W100240748";
        Paper paper = (Paper) sendResponse(table,id);
        System.out.println(paper);
    }
}
