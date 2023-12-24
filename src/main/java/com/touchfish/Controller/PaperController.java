package com.touchfish.Controller;


import cn.hutool.json.JSONObject;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import cn.hutool.json.JSONArray;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggest;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.qiniu.util.Json;
import com.touchfish.Dao.ElasticSearchRepository;
import com.touchfish.Dto.HotPaper;
import com.touchfish.Dto.SearchInfo;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touchfish.Dto.PaperInfo;
import com.touchfish.Dto.SuggestInfo;
import com.touchfish.MiddleClass.AuthorShip;
import com.touchfish.MiddleClass.CollectInfo;
import com.touchfish.MiddleClass.RefWork;
import com.touchfish.MiddleClass.RelWork;
import com.touchfish.Po.*;
import com.touchfish.Service.impl.*;
import com.touchfish.Service.impl.PaperAppealImpl;
import com.touchfish.Service.impl.PaperImpl;
import com.touchfish.Tool.*;
import com.touchfish.Po.PaperDoc;
import com.touchfish.Service.impl.PaperImpl;
import com.touchfish.Tool.RedisKey;
import com.touchfish.Tool.Result;

import com.touchfish.Tool.ZsetRedis;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.awt.*;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/paper")
@Tag(name = "论文相关接口")
public class PaperController {
    Integer pageSize = 10;

    @Autowired
    private PaperImpl paper;
    @Autowired
    private ElasticSearchRepository es;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private PaperImpl paperImpl;
    @Autowired
    private PaperAppealImpl paperAppeal;

    @Autowired
    private AuthorPaperImpl authorPaperImpl;

    public static ElasticsearchClient createClient() {
        String serverUrl = "http://121.36.81.4:9200";
        String apiKey = "SFlvd2pZd0JYVnpqeFRvVjYyUXI6ZFo3SmFFVTBTT09kNl9ZWjJmYjB1QQ==";

        // Create the low-level client
        RestClient restClient = RestClient
                .builder(HttpHost.create(serverUrl))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "ApiKey " + apiKey)
                })
                .build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        // And create the API client
        return new ElasticsearchClient(transport);
    }

    private final ElasticsearchClient client = createClient();
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ZsetRedis zsetRedis;

    @Autowired
    private CollectImpl collectImpl;
    /*@GetMapping("/id")
    public Result<PaperDoc>getWork(){
        System.out.println(es.findById("W2029916517"));
        return Result.ok("200");
    }*/

    private static String getTimeNow(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    @PostMapping ("/single")
    @LoginCheck
    @Operation(summary = "点击获取单个文献",security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "文献id号  格式:\"id\":\"文献id号\"")
    public Result<PaperInfo> getSingleWork( @RequestBody  Map<String,String> json){
        User myUser = UserContext.getUser();
        ObjectMapper mapper = new ObjectMapper();
        ThreadUtil.execute(()->{
            stringRedisTemplate.opsForValue().increment(RedisKey.SUM_LOOK_KEY,1);
            zsetRedis.incrementScore(RedisKey.BROWSE_CNT_KEY+RedisKey.getEveryDayKey(),json.get("id"),1.0);
            zsetRedis.setTTL(RedisKey.BROWSE_CNT_KEY+RedisKey.getEveryDayKey(),2l,TimeUnit.DAYS);

        });
        Integer browse = zsetRedis.incrementScore(RedisKey.BROWSE_CNT_KEY, json.get("id"), 1.0);
        String id1 = stringRedisTemplate.opsForValue().get(RedisKey.PAPER_KEY+json.get("id"));
        if (id1 != null){
            PaperInfo paperInfo = JSONUtil.toBean(id1, PaperInfo.class);
            if (paperInfo.is_active == false){
                return Result.fail("该文章已被下架");
            }
            paperInfo.setBrowse(browse);
            paperInfo.setGood(zsetRedis.getScore(RedisKey.GOOD_CNT_KEY,json.get("id")));
            paperInfo.setCollect(zsetRedis.getScore(RedisKey.COLLECT_CNT_KEY,json.get("id")));
            Collect byId = collectImpl.getById(myUser.getUid());
            if (byId != null){
                List<CollectInfo> collectInfos = byId.getCollectInfos();
                List<CollectInfo> collectInfoList = mapper.convertValue(collectInfos,new TypeReference<>() {});
                for (CollectInfo collectInfo:collectInfoList){
                    if (collectInfo.getPaper_id().equals(json.get("id"))){
                        paperInfo.setCollected(true);
                        break;
                    }
                }
            }
            return Result.ok("成功返回",paperInfo);
        }
        Paper paper = paperImpl.lambdaQuery().eq(Paper::getId,json.get("id")).one();
        if (paper.getIs_active() == false){
            return Result.fail("该文章已被下架");
        }
        List<AuthorShip> authorships = paper.getAuthorships();
        List<AuthorShip> authorShipList = mapper.convertValue(authorships, new TypeReference<>() {
        });
        List<String> referenced_works = paper.getReferenced_works();
        List<String> related_works = paper.getRelated_works();

        Collect byId = collectImpl.getById(myUser.getUid());


        PaperInfo paperInfo = new PaperInfo();
        paperInfo.setOa_url(paper.getOa_url());
        paperInfo.setAbstract(paper.getAbstract());
        paperInfo.setIssn(paper.getIssn());
        paperInfo.setDoi(paper.getDoi());
        paperInfo.setLan(paper.getLan());
        paperInfo.setKeywords(paper.getKeywords());
        paperInfo.setTitle(paper.getTitle());
        paperInfo.setCited_by_count(paper.getCited_by_count());
        paperInfo.setPublication_date(paper.getPublication_date());
        paperInfo.setPublisher(paperInfo.getPublisher());
        paperInfo.setAuthorships(paper.getAuthorships());
        paperInfo.setType(paper.getType());
        paperInfo.setId(paper.getId());
        paperInfo.setReferenced_works(paper.getReferenced_works());
        paperInfo.setRelated_works(paper.getRelated_works());
        paperInfo.setBrowse(browse);
        paperInfo.setGood(zsetRedis.getScore(RedisKey.GOOD_CNT_KEY,json.get("id")));
        paperInfo.setCollect(zsetRedis.getScore(RedisKey.COLLECT_CNT_KEY,json.get("id")));
        if (byId != null){
            List<CollectInfo> collectInfos = byId.getCollectInfos();
            List<CollectInfo> collectInfoList = mapper.convertValue(collectInfos,new TypeReference<>() {});
            for (CollectInfo collectInfo:collectInfoList){
                if (collectInfo.getPaper_id().equals(json.get("id"))){
                    paperInfo.setCollected(true);
                    break;
                }
            }
        }
        ThreadUtil.execute(()->{
            for (String id:referenced_works){
                Paper one = paperImpl.getPaperByAlex(id);
                paperImpl.saveOrUpdate(one);
                List<AuthorShip> authorShipList1 = one.getAuthorships();
                for (AuthorShip authorShip:authorShipList1){
                    if (authorPaperImpl.lambdaQuery().eq(AuthorPaper::getId,authorShip.getAuthor().id).exists()){
                        AuthorPaper one1 = authorPaperImpl.lambdaQuery().eq(AuthorPaper::getId, authorShip.getAuthor().id).one();
                        if (!one1.getPapers().contains(one.getId())){
                            one1.getPapers().add(one.getId());
                        }
                        authorPaperImpl.updateById(one1);
                    }
                }
            }
        });

        ThreadUtil.execute(() -> {
            for (String id : related_works) {
                Paper one = paperImpl.getPaperByAlex(id);
                paperImpl.saveOrUpdate(one);
                List<AuthorShip> authorShipList1 = one.getAuthorships();
                for (AuthorShip authorShip:authorShipList1){
                    if (authorPaperImpl.lambdaQuery().eq(AuthorPaper::getId,authorShip.getAuthor().id).exists()){
                        AuthorPaper one1 = authorPaperImpl.lambdaQuery().eq(AuthorPaper::getId, authorShip.getAuthor().id).one();
                        if (!one1.getPapers().contains(one.getId())){
                            one1.getPapers().add(one.getId());
                        }
                        authorPaperImpl.updateById(one1);
                    }
                }
            }
        });
        String s = JSONUtil.toJsonStr(paperInfo);
        stringRedisTemplate.opsForValue().set(RedisKey.PAPER_KEY+paperInfo.getId(),s,1, TimeUnit.DAYS);
        return Result.ok("成功返回",paperInfo);
    }

    @PostMapping("/search")
    @Operation(summary = "根据关键词查询文献")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"pageNum\":\"页数\",\"keyword\":\"内容相关（title、abstract、keyword）\",\"author\":\"作者姓名\",\"publisher\":\"刊物\",\"institution\":\"机构\"")
    public Result<List<Paper>> searchKeyword(@RequestBody SearchInfo searchInfo) {
        final int pageNum = Math.max(searchInfo.getPageNum(), 0);
        String keyword = searchInfo.getKeyword();
        String author = searchInfo.getAuthor();
        String institution = searchInfo.getInstitution();
        String publisher = searchInfo.getPublisher();
        Page<PaperDoc> page;
        String redisId = stringRedisTemplate.opsForValue().get(RedisKey.SEARCH_KEY + searchInfo);
        if (redisId != null) {
            JSONArray objects = JSONUtil.parseArray(redisId);
            List<Paper> papers = JSONUtil.toList(objects, Paper.class);
            return Result.ok("查询成功", papers);
        }
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        if (!keyword.equals(""))
            page = es.findByInformation(keyword, pageable);
        else if (!author.equals(""))
            page = es.findByAuthorships(author, pageable);
        else if (!institution.equals(""))
            page = es.findByAuthorships(institution, pageable);
        else
            page = es.findByPublishers(publisher, pageable);
        List<Paper> papers = new ArrayList<>();
        for (PaperDoc paperDoc : page) {
            papers.add(new Paper(paperDoc));
        }
        /*if(pageNum==0){
            try{
                co.elastic.clients.elasticsearch._types.query_dsl.Query query1 = MatchQuery.of(m -> m
                        .field(searchField)
                        .query(searchText)
                )._toQuery();
                SearchResponse<Void> searchresponse = client.search(b -> b
                                .index("papers")
                                .size(0)
                                .query(query1)
                                .aggregations("lan", a -> a
                                        .terms(ta -> ta.field("lan"))
                                )
                                .aggregations("type",a->a.terms(ta -> ta.field("type")))
                                .aggregations("publisher",a->a.terms(ta -> ta.field("publisher")))
                                .aggregations("date",a->a.terms(ta -> ta.field("publication_date"))),
                        Void.class
                );
                List<StringTermsBucket> lan = searchresponse.aggregations()
                        .get("lan").sterms().buckets().array();
                List<StringTermsBucket>type=searchresponse.aggregations().get("type").sterms().buckets().array();
                List<StringTermsBucket>publisherBucket=searchresponse.aggregations().get("publisher").sterms().buckets().array();
                List<LongTermsBucket>date=searchresponse.aggregations().get("date").lterms().buckets().array();
                for (StringTermsBucket bucket : lan) {
                    System.out.println(bucket.docCount() + " " + bucket.key()._get().toString());
                }
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }
*/
        String result = JSONUtil.toJsonStr(papers);
        stringRedisTemplate.opsForValue().set(RedisKey.SEARCH_KEY + searchInfo, result, 1, TimeUnit.DAYS);
        return Result.ok("查询成功", papers);
    }

    @PostMapping("/aggregate")
    @Operation(summary = "分类数据")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"pageNum\":\"页数\",剩下的见类中定义，参数同其他")

    public Result<JSONObject> aggregate(@RequestBody SearchInfo searchInfo) {
        final int pageNum = Math.max(searchInfo.getPageNum(), 0);
        final List<String> searchText = new ArrayList<>(), searchField = new ArrayList<>();
        if (!searchInfo.getKeyword().equals("")) {
            searchField.add("information");
            searchText.add(searchInfo.getKeyword());
        }
        if (!searchInfo.getAuthor().equals("")) {
            searchField.add("authorships");
            searchText.add(searchInfo.getAuthor());
        }
        if (!searchInfo.getInstitution().equals("")) {
            searchField.add("authorships");
            searchText.add(searchInfo.getInstitution());
        }
        if (!searchInfo.getPublisher().equals("")) {
            searchField.add("publishers");
            searchText.add(searchInfo.getPublisher());
        }
        if (pageNum == 0) {
            try {
                final co.elastic.clients.elasticsearch._types.query_dsl.Query query = getQuery(searchText, searchField);
                SearchResponse<Void> searchresponse = client.search(b -> b
                                .index("papers")
                                .size(0)
                                .query(query)
                                .aggregations("lan", a -> a
                                        .terms(ta -> ta.field("lan"))
                                )
                                .aggregations("type", a -> a.terms(ta -> ta.field("type")))
                                .aggregations("publisher", a -> a.terms(ta -> ta.field("publisher")))
                                .aggregations("date", a -> a.
                                        dateRange(d -> d.
                                                field("publication_date").
                                                ranges(r -> r.
                                                        to(FieldDateMath.of(f -> f.expr("2020-01-01")))).
                                                ranges(r -> r.
                                                        from(FieldDateMath.of(f -> f.expr("2020-01-01"))).
                                                        to(FieldDateMath.of(f -> f.expr("2021-01-01")))).
                                                ranges(r -> r.
                                                        from(FieldDateMath.of(f -> f.expr("2021-01-01"))).
                                                        to(FieldDateMath.of(f -> f.expr("2022-01-01")))).
                                                ranges(r -> r.
                                                        from(FieldDateMath.of(f -> f.expr("2022-01-01"))).
                                                        to(FieldDateMath.of(f -> f.expr("2023-01-01")))).
                                                ranges(r -> r.
                                                        from(FieldDateMath.of(f -> f.expr("2023-01-01"))).
                                                        to(FieldDateMath.of(f -> f.expr("now")))))),
                        Void.class
                );
                long count = client.count(c -> c.query(query)).count();
                List<StringTermsBucket> lan = searchresponse.aggregations()
                        .get("lan").sterms().buckets().array();
                List<StringTermsBucket> type = searchresponse.aggregations().get("type").sterms().buckets().array();
                List<StringTermsBucket> publisherBucket = searchresponse.aggregations().get("publisher").sterms().buckets().array();
                List<RangeBucket> date = searchresponse.aggregations().get("date").dateRange().buckets().array();
                JSONObject combined=new JSONObject();
                combined.put("sum",count);
                combined.put("lan",new JSONObject());
                combined.put("type",new JSONObject());
                combined.put("publisher",new JSONObject());
                combined.put("date",new JSONObject());
                for (StringTermsBucket bucket : lan) {
                    ((JSONObject)combined.get("lan")).put(bucket.key()._get().toString(),bucket.docCount());
                }
                for (StringTermsBucket bucket : type) {
                    ((JSONObject)combined.get("type")).put(bucket.key()._get().toString(),bucket.docCount());
                }
                for (StringTermsBucket bucket : publisherBucket) {
                    ((JSONObject)combined.get("publisher")).put(bucket.key()._get().toString(),bucket.docCount());
                }
                for (RangeBucket bucket : date) {
                    ((JSONObject)combined.get("date")).put(bucket.toAsString(), bucket.docCount());
                }
                return Result.ok("",combined);
            } catch (Exception e) {
                return Result.fail(e.toString());
            }
        }
        return Result.ok("");
    }

    //关键词作者刊物日期机构
    @PostMapping("/ultraSearch")
    @Operation(summary = "高级搜索")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"pageNum\":\"页数\",剩下的见类中定义")
    public Result<List<Paper>> ultraSearch(@RequestBody SearchInfo searchInfo) {
        final Integer pageNum = Math.max(searchInfo.getPageNum(), 0);
        final List<String> searchText = new ArrayList<>(), searchField = new ArrayList<>();
        String fromDate=searchInfo.getFrom_date().equals("") ? null : searchInfo.getFrom_date();
        String toDate=searchInfo.getTo_date().equals("")? null : searchInfo.getTo_date();
        if (!searchInfo.getKeyword().equals("")) {
            searchField.add("information");
            searchText.add(searchInfo.getKeyword());
        }
        if (!searchInfo.getAuthor().equals("")) {
            searchField.add("authorships");
            searchText.add(searchInfo.getAuthor());
        }
        if (!searchInfo.getInstitution().equals("")) {
            searchField.add("authorships");
            if(!searchInfo.getAuthor().equals("")) {
                searchText.remove(searchText.size() - 1);
                searchText.add(searchInfo.getAuthor()+" "+searchInfo.getInstitution());
            }
            else
                searchText.add(searchInfo.getInstitution());
        }
        if (!searchInfo.getPublisher().equals("")) {
            searchField.add("publishers");
            searchText.add(searchInfo.getPublisher());
        }
        if(!searchInfo.getType().equals("")){
            searchField.add("type");
            searchText.add(searchInfo.getType());
        }
        if(!searchInfo.getIssn().equals("")){
            searchField.add("issn");
            searchText.add(searchInfo.getIssn());
        }
        if(!searchInfo.getLanguage().equals("")){
            searchField.add("lan");
            searchText.add(searchInfo.getLanguage());
        }
        Query query=getQuery(searchText,searchField,fromDate,toDate);

        try {
           /* SearchResponse<PaperDoc>searchResponse;
                searchResponse = client.search(builder -> builder
                                .index("papers")
                                .size(10)
                                .from(pageNum).
                                query(query)
                        , PaperDoc.class);*/
            SearchResponse<PaperDoc>response=client.search(builder -> builder.index("papers").size(10).from(pageNum).query(query),PaperDoc.class);
            List<Paper> papers=new ArrayList<>();
            for(Hit<PaperDoc>hit:response.hits().hits()){
                papers.add(new Paper(hit.source()));
            }
            return Result.ok("查询成功",papers);
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.ok("");
        }
        /*Query query = NativeQuery.builder()
                .withAggregation("publisher", Aggregation.of(a -> a
                        .terms(ta -> ta.field("lan"))))
                .withQuery(q -> q
                        .match(m -> m
                                .field("information")
                                .query("computer")
                        )
                )
                //.withPageable(pageable)
                .build();*/
        //ElasticsearchTemplate elasticsearchTemplate1=new ElasticsearchTemplate();
        //elasticsearchTemplate1.search();
        /*SearchHits<PaperDoc> paperDocSearchHits = elasticsearchTemplate.search(query, PaperDoc.class);
        System.out.println(paperDocSearchHits);*/
        //long a = elasticsearchOperations.count(query,PaperDoc.class);
        /*for(SearchHit<PaperDoc>paper:searchHits){
            System.out.println(paper.getContent());
        }
        system.out.println(a);
        /*try {

            String searchText = "computer";

            co.elastic.clients.elasticsearch._types.query_dsl.Query query1 = MatchQuery.of(m -> m
                    .field("information")
                    .query(searchText)
            )._toQuery();
            SearchResponse<Void> searchresponse = client.search(b -> b
                            .index("papers")
                            .size(0)
                            .query(query1)
                            .aggregations("publisher", a -> a
                                    .terms(ta -> ta.field("lan"))
                            ),
                    Void.class
            );
            List<StringTermsBucket> buckets = searchresponse.aggregations()
                    .get("publisher").sterms().buckets().array();

            for (StringTermsBucket bucket : buckets) {
                System.out.println(bucket.docCount() + " " + bucket.key()._get().toString());
            }
        } catch (Exception e) {
            System.out.println(e);
        }*/
        //return Result.ok("");
    }
    @PostMapping("/suggest")
    @Operation(summary = "获取搜索推荐")
    private Result<List<String>> getSuggestions(@RequestBody SuggestInfo suggestInfo){
        try {
            List<String>list = new ArrayList<String>();
            String query = suggestInfo.getQuery();
            SearchResponse<Void> searchResponse=client.search(c->c.index("works").size(5).suggest(s->s.suggesters("mysuggest",s1->s1.prefix(query).completion(co->co.field("suggestInform")))),Void.class);
            Collection<List<Suggestion<Void>>> completionSuggestOptions=searchResponse.suggest().values();
            List<Suggestion<Void>> suggestions=completionSuggestOptions.stream().toList().get(0);
            Suggestion<Void> suggestion=suggestions.get(0)._get()._toSuggestion();
            for(CompletionSuggestOption<Void> option:((CompletionSuggest<Void>)suggestion._get()).options())
                list.add(option.text());
            return Result.ok("",list);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.ok("");
        }
    }
    private Query getQuery(List<String> searchText, List<String> searchField) {
        List<Query>queryList=new ArrayList<>();
        int i=0;
        for(;i<searchText.size();i++){
            final int num=i;
            Query query=MatchQuery.of(m -> m
                            .field(searchField.get(num))
                            .query(searchText.get(num)))
                    ._toQuery();
            queryList.add(query);
        }
        //Query query= RangeQuery.of(builder -> builder.field("publication_date").from(""))
        Query query=BoolQuery.of(builder -> builder.must(queryList))._toQuery();
        return queryList.size()==1?queryList.get(0):query;
    }
    private Query getQuery(List<String> searchText, List<String> searchField, String fromDate,String toDate) {
        List<Query>queryList=new ArrayList<>();
        int i=0;
        for(;i<searchText.size();i++){
            final int num=i;
            Query query=MatchQuery.of(m -> m
                    .field(searchField.get(num))
                    .query(searchText.get(num)))
                    ._toQuery();
            queryList.add(query);
        }
        if(fromDate!=null&&toDate!=null) {
            Query query1 = RangeQuery.of(builder -> builder.field("publication_date").from(fromDate).to(toDate))._toQuery();
            queryList.add(query1);
        }
        else if(fromDate!=null&&toDate==null){
            Query query1 = RangeQuery.of(builder -> builder.field("publication_date").from(fromDate))._toQuery();
            queryList.add(query1);
        }
        else if(fromDate==null&&toDate!=null) {
            Query query1 = RangeQuery.of(builder -> builder.field("publication_date").to(toDate))._toQuery();
            queryList.add(query1);
        }
        Query query=BoolQuery.of(builder -> builder.must(queryList))._toQuery();
        return queryList.size()==1?queryList.get(0):query;
    }

    @PostMapping("/getRef")
    @Operation(summary = "点击获取文献的参考文献")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "格式:\"ref\":[id1,id2,id3....]")
    public Result<List<RefWork>> getRefWork(@RequestBody Map<String, List<String>> mp) {
        List<String> refs = mp.get("ref");
        ObjectMapper mapper = new ObjectMapper();
        List<RefWork> ans = new ArrayList<>();
        for (String id : refs) {
            RefWork relWork = new RefWork();
            Paper one = null;
            if (paperImpl.lambdaQuery().eq(Paper::getId, id).exists()) {
                one = paperImpl.lambdaQuery().eq(Paper::getId, id).one();
            } else {
                continue;
            }
            List<AuthorShip> authorships1 = one.getAuthorships();
            List<AuthorShip> authorShipList1 = mapper.convertValue(authorships1, new TypeReference<>() {
            });
            relWork.setAbstract(one.getAbstract());
            relWork.setId(one.getId());
            relWork.setTitle(one.getTitle());
            if (one.getPublisher() != null) {
                relWork.setPublisher(one.getPublisher().display_name);
            }
            relWork.setCited_by_count(one.getCited_by_count());
            relWork.setPublication_date(one.getPublication_date());
            for (int i = 0; i < 3 && i < authorShipList1.size(); i++) { //展示至多3位
                relWork.getAuthors().add(authorShipList1.get(i).getAuthor());
            }
            ans.add(relWork);
        }
        return Result.ok("成功返回", ans);
    }

    @PostMapping("/getRel")
    @Operation(summary = "点击获取文献的参考文献")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "格式:\"rel\":[id1,id2,id3....]")
    public Result<List<RelWork>> getRelWork(@RequestBody Map<String, List<String>> mp) {
        List<String> rels = mp.get("rel");
        ObjectMapper mapper = new ObjectMapper();
        List<RelWork> ans = new ArrayList<>();
        for (String id : rels) {
            RelWork relWork = new RelWork();
            Paper one = null;
            if (paperImpl.lambdaQuery().eq(Paper::getId, id).exists()) {
                one = paperImpl.lambdaQuery().eq(Paper::getId, id).one();
            } else {
                continue;
            }
            List<AuthorShip> authorships1 = one.getAuthorships();
            List<AuthorShip> authorShipList1 = mapper.convertValue(authorships1, new TypeReference<>() {
            });
            relWork.setAbstract(one.getAbstract());
            relWork.setId(one.getId());
            relWork.setTitle(one.getTitle());
            if (one.getPublisher() != null) {
                relWork.setPublisher(one.getPublisher().display_name);
            }
            relWork.setCited_by_count(one.getCited_by_count());
            relWork.setPublication_date(one.getPublication_date());
            for (int i = 0; i < 3 && i < authorShipList1.size(); i++) { //展示至多3位
                relWork.getAuthors().add(authorShipList1.get(i).getAuthor());
            }
            ans.add(relWork);
        }
        return Result.ok("成功返回", ans);
    }

    @GetMapping()
    @Operation(summary = "获取文献总数")
    public Result<Integer> getCount(){
        return Result.ok("成功返回",7541000);
    }

    @GetMapping("/sumlook")
    @Operation(summary = "获取文献总浏览数")
    public Result<Integer> getSumLook(){
        Integer sumLook = Integer.parseInt(stringRedisTemplate.opsForValue().get(RedisKey.SUM_LOOK_KEY));
        return Result.ok("成功返回",sumLook);
    }

    @PostMapping("/good")
    @Operation(summary = "点赞")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "格式:\"id\":")
    public Result<Integer> addGood(@RequestBody Map<String,String> mp){
        Integer ans = zsetRedis.incrementScore(RedisKey.GOOD_CNT_KEY,mp.get("id"),1.0);
        zsetRedis.incrementScore(RedisKey.GOOD_CNT_KEY+RedisKey.getEveryDayKey(),mp.get("id"),1.0);
        zsetRedis.setTTL(RedisKey.GOOD_CNT_KEY+RedisKey.getEveryDayKey(),2l,TimeUnit.DAYS);
        return Result.ok("点赞成功",ans);
    }

    @PostMapping("/nogood")
    @Operation(summary = "取消点赞")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "格式:\"id\":")
    public Result<Integer> addNoGood(@RequestBody Map<String,String> mp){
        Integer ans = zsetRedis.incrementScore(RedisKey.GOOD_CNT_KEY,mp.get("id"),-1.0);
        zsetRedis.incrementScore(RedisKey.GOOD_CNT_KEY+RedisKey.getEveryDayKey(),mp.get("id"),-1.0);
        zsetRedis.setTTL(RedisKey.GOOD_CNT_KEY+RedisKey.getEveryDayKey(),2l,TimeUnit.DAYS);
        return Result.ok("取消点赞",ans);
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门文献")
    public Result<List<HotPaper>> getHot(){
        List<HotPaper> ans = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet().reverseRangeWithScores(RedisKey.BROWSE_CNT_KEY + RedisKey.getEveryDayKey(), 0, 9);
        for (var a:typedTuples){
            Paper one = paperImpl.lambdaQuery().eq(Paper::getId, a.getValue()).one();
            ans.add(new HotPaper(a.getValue(),one.getTitle(),zsetRedis.getScore(RedisKey.BROWSE_CNT_KEY + RedisKey.getEveryDayKey(),one.getId())));
        }
        return Result.ok("成功获取",ans);
    }

    @PostMapping("/getcitation")
    @Operation(summary = "获取文献引用")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "格式:\"id\":")
    public Result<String>  getCite(@RequestBody Map<String,String> mp){
        Paper one = paperImpl.lambdaQuery().eq(Paper::getId,mp.get("id")).one();
        StringBuilder s = new StringBuilder();
        if (one.getAuthorships().size()>0){
            AuthorShip authorShip = one.getAuthorships().get(0);
            String[] s1 = authorShip.getAuthor().getDisplay_name().split(" ");
            s.append(s1[0]);
            if (s1.length>1){
                s.append(Character.toUpperCase(s1[1].charAt(0)));
            }
        }
        String s1 = one.getPublication_date().split("-")[0];
        s.append("("+s1+").").append(one.getTitle()).append(".").append(one.getPublisher());
        return Result.ok(s.toString());
    }
}
