package com.test.service;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by pangjian on 16-7-1.
 */
public class ProductServicesImpl implements ProductService{

    public void add(String id) {

    }

    public void remove(String id) {

    }

    public void query(String jsonObject) {
        System.out.println("develop add commit");
        System.out.println("develop add commit - 2");
    }

    public static void main(String[] args) throws  Exception{

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree("{\"id\":null,\"factoryId\":null,\"createTime\":null,\"pwd\":null,\"protocol\":null,\"updateTime\":null,\"status\":1,\"remark\":null,\"role\":null,\"account\":\"pangjian\",\"mobile\":null}\n");


        System.out.println(jsonNode.get("account").asText());
        System.out.println(jsonNode.get("mobile").getTextValue().equals("null"));


    }
}
