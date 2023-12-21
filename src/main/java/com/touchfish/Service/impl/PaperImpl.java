package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.ElasticSearchRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.touchfish.Dao.PaperMapper;
import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.MiddleClass.DisplayInfo;
import com.touchfish.Po.Paper;
import com.touchfish.Po.PaperDoc;
import com.touchfish.Service.IPaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

import java.util.List;
import com.touchfish.Tool.OpenAlex;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PaperImpl extends ServiceImpl<PaperMapper, Paper> implements IPaper {

    public Paper getPaperByAlex(String id){
        JsonNode jsonNode = (JsonNode) OpenAlex.sendResponse("work",id);
        Paper paper = parsePaper(jsonNode);
        return paper;
    }

    private Paper parsePaper(JsonNode jsonNode){
        Paper paper = new Paper();
        paper.setId(findId(jsonNode.get("id").asText()));
        if (jsonNode.get("doi")!=null){
            String doi = jsonNode.get("doi").asText();
            paper.setDoi(doi.substring(0,Math.min(127,doi.length()-1)));
        }
        if (jsonNode.get("oa_url")!=null){
            String oa_url = jsonNode.get("oa_url").asText();
            paper.setOa_url(oa_url.substring(0,Math.min(255,oa_url.length()-1)));
        }
        if (jsonNode.get("type")!=null){
            paper.setType(jsonNode.get("type").asText());
        }

        if (jsonNode.get("publication_date")!=null){
            paper.setPublication_date(jsonNode.get("publication_date").asText());
        }

        if (jsonNode.get("language")!=null){
            paper.setLan(jsonNode.get("language").asText());
        }

        if (jsonNode.get("cited_by_count")!=null){
            paper.setCited_by_count(jsonNode.get("cited_by_count").asInt());
        }
        paper.setIs_active(true);
        paper.setAbstract(getAbstract(jsonNode));
        paper.setIssn(get_issn(jsonNode));
        paper.setAuthorships(get_authorships_data(jsonNode));
        paper.setKeywords(getKeywords(jsonNode));
        paper.setPublisher(get_publisher(jsonNode));
        paper.setReferenced_works(get_ref_work(jsonNode));
        paper.setRelated_works(get_related_work(jsonNode));

        if (jsonNode.get("title")!=null){
            paper.setTitle(jsonNode.get("title").asText());
        }
        return paper;
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

    private DisplayInfo get_publisher(JsonNode jsonNode){
        DisplayInfo ans  = new DisplayInfo();
        JsonNode jsonNode1 = jsonNode.get("primary_location");
        if (jsonNode1 == null) return null;
        JsonNode jsonNode2 = jsonNode.get("source");
        if (jsonNode2 == null) return null;
        ans.setDisplay_name(jsonNode2.get("display_name").asText());
        ans.setId(findId(jsonNode2.get("id").asText()));
        return ans;
    }

    private String get_issn(JsonNode jsonNode){
        JsonNode jsonNode1 = jsonNode.get("primary_location");
        if (jsonNode1 == null) return null;
        JsonNode jsonNode2 = jsonNode.get("source");
        if (jsonNode2 == null) return null;
        return jsonNode2.get("issn_l").asText();
    }

    private List<String> get_related_work(JsonNode jsonNode){
        List<String> ans  = new ArrayList<>();
        JsonNode jsonNode1 = jsonNode.get("related_works");
        for (JsonNode jsonNode2:jsonNode1){
            ans.add(findId(jsonNode2.asText()));
        }
        return ans;
    }

    private List<String> get_ref_work(JsonNode jsonNode){
        List<String> ans  = new ArrayList<>();
        JsonNode jsonNode1 = jsonNode.get("referenced_works");
        for (JsonNode jsonNode2:jsonNode1){
            ans.add(findId(jsonNode2.asText()));
        }
        return ans;
    }


}
