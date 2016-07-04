package com.test.service;

import com.test.entity.Image;
import com.test.entity.Product;
import org.apache.lucene.search.Query;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.sort.SortParseElement;
import sun.security.provider.MD5;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by pangjian on 16-6-27.
 */
public class Test {

    public static void main(String[] args) throws Exception{


        ProductServicesImpl impl = new ProductServicesImpl();

        Class<?>[] interfaces = impl.getClass().getInterfaces();

        for (Class clazz: interfaces) {
            System.out.println(clazz.getCanonicalName());
        }


        System.out.println("--------------------");

        /*
        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", "pj_test_es").build();
        TransportClient client = TransportClient.builder().settings(settings).build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.31.145"), 9300));
        */
        //addIndexDoc(client);

        //searchLike(client);

        //clearAll(client);

        //searchExact2(client);
    }


    public static void clearAll(TransportClient client){
        SearchResponse searchResponse = client.prepareSearch("htw").
                setTypes("product").setFrom(0).setSize(1000).execute().actionGet();

        SearchHits hits = searchResponse.getHits();

        for (SearchHit searchHit: hits.getHits()) {
                client.prepareDelete("htw","product",searchHit.getId()).execute().actionGet();
        }

    }


    /* 不自定字段所有字段精确匹配 */
    public static  void searchLike(TransportClient client) throws  Exception{

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        //query.must(QueryBuilders.rangeQtermQueryuery("price").gte(0).lte(133));
        query.must(QueryBuilders.fuzzyQuery("content","庞"));

        SearchResponse searchResponse = client.prepareSearch("htw").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setTypes("product")
                .setQuery(query).setFrom(0).setSize(10).execute().actionGet();

        SearchHits hits = searchResponse.getHits();

        System.out.println("总数：" + hits.getTotalHits());

        int i =0 ;
        for (SearchHit searchit:hits.getHits()) {
            System.out.println(searchit.getSourceAsString());
            i++;
        }

    }


    /* 不自定字段所有字段精确匹配 */
    public static  void searchExact2(TransportClient client){

        SearchResponse searchResponse = client.prepareSearch("htw")
                .setQuery(QueryBuilders.queryStringQuery("周末说去吃饭"))
                .setSize(50).execute().actionGet();

        SearchHits hits = searchResponse.getHits();

        System.out.println("总数：" + hits.getTotalHits());

        int i =0 ;
        for (SearchHit searchit:hits.getHits()) {
            System.out.println(i+ "-->"+ searchit.getSourceAsString());
            i++;
        }

    }

    public static  void searchExact(TransportClient client){

        /*精确字段搜索*/
        SearchResponse searchResponse = client.prepareSearch("htw")
                .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
                .setQuery(QueryBuilders.matchQuery("price","123.6"))
                .setTypes("product").setFrom(0).setSize(20).execute().actionGet();

        SearchHits hits = searchResponse.getHits();

        System.out.println("总数：" + hits.getTotalHits());

        int i =0 ;
        for (SearchHit searchit:hits.getHits()) {
            System.out.println(i+ "-->"+ searchit.getSourceAsString());
            i++;
        }

    }

    public static void createType(TransportClient client) throws Exception{

        XContentFactory xContentFactory = new XContentFactory();
        XContentBuilder builder = xContentFactory.jsonBuilder();

        builder.startObject("employee").startObject("properties")
                .startObject("id").field("type","string")
                .startObject("name").field("type","string").field("store","yes")
                .startObject("content").field("type","string")
                .startObject("age").field("type","integer")
                .startObject("createTime").field("type","date");


        System.out.println(builder.toString());
        /*
        PutMappingRequest mapping = Requests.putMappingRequest("htw").type("employee").source(builder);
        client.admin().indices().putMapping(mapping).actionGet();
        client.close();*/
    }


    public static void createMapping(String indices,String mappingType,TransportClient client)throws Exception{
        new XContentFactory();
        XContentBuilder builder=XContentFactory.jsonBuilder()
                .startObject()
                .startObject(indices)
                .startObject("properties")
                .startObject("id").field("type", "integer").field("store", "yes").endObject()
                .startObject("kw").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
                .startObject("edate").field("type", "date").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
                .endObject()
                .endObject()
                .endObject();
        PutMappingRequest mapping = Requests.putMappingRequest(indices).type(mappingType).source(builder);
        client.admin().indices().putMapping(mapping).actionGet();
        client.close();
    }




    public static void addIndexDoc(TransportClient client) throws  Exception{

        //for (int i=0 ;i<10;i++) {

            Product product = new Product();
            product.setId(UUID.randomUUID().toString());
            product.setName(".net");
            product.setPrice(389.0);
            product.setContent("kotlin doo cat");

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(product);

            System.out.println(json);

            client.prepareIndex("htw", "product").setSource(json).execute().actionGet();
        //}
    }

    public void query(){
        
    }

}
