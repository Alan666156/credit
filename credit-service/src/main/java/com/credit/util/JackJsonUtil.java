package com.credit.util;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class JackJsonUtil {
    public static String objToStr(Object obj) throws JsonGenerationException, JsonMappingException, IOException{
    	ObjectMapper mapper = new ObjectMapper();
    	return mapper.writeValueAsString(obj);
    }
    
    public static Object strToObj(String json, Class cls) throws JsonParseException, JsonMappingException, IOException{
    	ObjectMapper mapper = new ObjectMapper();
    	return mapper.readValue(json, cls);
    }
    
    public static <T> List<T> strToList(String json, Class<T> cls) throws JsonParseException, JsonMappingException, IOException{
    	ObjectMapper mapper = new ObjectMapper();
    	JsonParser parser = mapper.getJsonFactory().createJsonParser(json);
    	JsonNode nodes = parser.readValueAsTree();
    	List<T> list = new ArrayList<T>(nodes.size());
    	for(JsonNode node : nodes){
    		list.add(mapper.readValue(node, cls));
    	}
    	return list;
    }
    
//    public static String getReturnJson(BussErrorCode bussErrorCode,String code,String msg,Object o){
//    	bussErrorCode.getErrorcode()
//    	return null;
//    }
}
