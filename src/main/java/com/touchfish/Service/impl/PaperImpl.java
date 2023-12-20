package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.touchfish.Dao.PaperMapper;
import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.MiddleClass.DisplayInfo;
import com.touchfish.Po.Paper;
import com.touchfish.Service.IPaper;
import com.touchfish.Tool.OpenAlex;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PaperImpl extends ServiceImpl<PaperMapper, Paper> implements IPaper {

    public Paper getPaperByAlex(String id){
        Paper paper = (Paper) OpenAlex.sendResponse("works",id);
        return paper;
    }

    private Paper parsePaper(JsonNode jsonNode){
        Paper paper = new Paper();
        paper.setId(findId(jsonNode.get("id").asText()));
        paper.setIssn(jsonNode.get("title").asText());
        if (jsonNode.get("doi")!=null){
            paper.setDoi(jsonNode.get("doi").asText().substring(0,127));
        }
        if (jsonNode.get("oa_url")!=null){
            paper.setOa_url(jsonNode.get("oa_url").asText().substring(0,255));
        }
        paper.setType(jsonNode.get("type").asText());

        paper.setPublication_date(jsonNode.get("publication_date").asText());
        paper.setLan(jsonNode.get("lan").asText());
        paper.setIs_active(true);
        paper.setAbstract(getAbstract(jsonNode));
    }

    private  String findId(String url) {
        Pattern pattern = Pattern.compile("/([A-Za-z0-9]+)$");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }
    private  String getAbstract(JsonNode jsonNode){
        Map<String, List<Integer>> abstractInvertedIndex = new HashMap<>();
        JsonNode jsonNode1 = jsonNode.get("abstract_inverted_index");
        Iterator<Map.Entry<String,JsonNode>> jsonNodes = jsonNode1.fields();
        while (jsonNodes.hasNext()) {
            Map.Entry<String, JsonNode> node = jsonNodes.next();
            List<Integer> list = new ArrayList<>();
            for (JsonNode jsonNode2:node.getValue()){
                list.add(jsonNode2.asInt());
            }
            abstractInvertedIndex.put(node.getKey(), list);
        }
        String anAbstract = getAbstract(abstractInvertedIndex);
        return anAbstract;

    }
    private String getAbstract(Map<String, List<Integer>> abstractInvertedIndex){
        if (abstractInvertedIndex == null) return null;

        int wordCount = abstractInvertedIndex.values().stream().mapToInt(List::size).sum();
        wordCount = Math.min(wordCount, 2047);
        List<String> forwardIndex = new ArrayList<>(wordCount);

        // Initialize forward index
        for (int i = 0; i < wordCount; i++) {
            forwardIndex.add("");
        }

        // Fill the forward index
        for (Map.Entry<String, List<Integer>> entry : abstractInvertedIndex.entrySet()) {
            String word = entry.getKey();
            List<Integer> positions = entry.getValue();
            for (int position : positions) {
                if (position >= wordCount) continue;
                forwardIndex.set(position, word);
            }
        }

        // Join the words to form the original string
        StringBuilder originalString = new StringBuilder();
        for (String word : forwardIndex) {
            originalString.append(word).append(" ");
        }

        // Trim excess whitespace
        String stringWithoutExtraSpaces = originalString.toString().replaceAll("\\s+", " ").trim();

        int maxLength = Math.min(2000, stringWithoutExtraSpaces.length());
        return stringWithoutExtraSpaces.substring(0, maxLength);
    }

    private List<String> getKeywords(JsonNode jsonNode){
        List<String> keys = new ArrayList<>();
        JsonNode concepts = jsonNode.get("concepts");
        for (JsonNode jsonNode1:concepts){
            keys.add(jsonNode1.get("display_name").asText());
        }
        return keys;
    }

    private List<AuthorShip> get_authorships_data(JsonNode jsonNode){
        List<AuthorShip> ans = new ArrayList<>();
        JsonNode authorships = jsonNode.get("authorships");
        for (JsonNode jsonNode1:authorships){
            List<DisplayInfo> institutions = new ArrayList<>();
            AuthorShip authorShip = new AuthorShip();
            for (JsonNode jsonNode2:jsonNode1.get("institutions")){
                institutions.add(new DisplayInfo(findId(jsonNode2.get("id").asText()),jsonNode2.get("display_name").asText()));
            }
            DisplayInfo author = new DisplayInfo(findId(jsonNode1.get("author").get("id").asText()),jsonNode1.get("author").get("display_name").asText());
            authorShip.setAuthor(author);
            authorShip.setInstitutions(institutions);
            ans.add(authorShip);
        }
        return ans;
    }

    private DisplayInfo get_publisher(){

    }
}
