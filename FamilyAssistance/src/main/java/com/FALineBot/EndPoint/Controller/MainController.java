package com.FALineBot.EndPoint.Controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.FALineBot.EndPoint.Dto.WishListParam;
import com.FALineBot.EndPoint.Model.WishList;
import com.FALineBot.EndPoint.Service.WishListService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@RequestMapping("/robot")
@RestController
public class MainController {
	

	private String LINE_SECRET = "/SG/if6TI6qOaEqeniCsdaX4Y/R7pzzjw6mhQrwsCCQK0aItavD/9jhH/OwsAlDbNAlWcGJU2W5hn9hK2WrkU1kk0bM77KdRfIVcop96uIJurFGCpGMEiNoGlmjo59dGdMNOSUBi8AhTwwTyDHeCuAdB04t89/1O/w1cDnyilFU=";
	@Autowired
	private WishListService wishListService;

	private RestTemplate restTemplate = new RestTemplate();
	private String Reply_Url = "https://api.line.me/v2/bot/message/reply";
	
	//測試用，回傳Hello Java
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping("/test")
	public ResponseEntity test(){
		return new ResponseEntity("Hello Java", HttpStatus.OK);
	}

	@PostMapping("/products/")
	public ResponseEntity<WishList> createProduct(@RequestBody WishListParam wishListParam){
	
	int ID = wishListService.createProduct(wishListParam);
	//Product product = WishListService.getProductById(productId);
	
	return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/messaging")
	public ResponseEntity messagingAPI(@RequestHeader("X-Line-Signature") String X_Line_Signature,
			@RequestBody String requestBody) {
		RestTemplate restTemplate = new RestTemplate();
		JSONObject object = new JSONObject(requestBody);	
		JSONObject event = new JSONObject();
		
		for(int i=0; i<object.getJSONArray("events").length(); i++) {
			 if(object.getJSONArray("events").getJSONObject(i).getString("type").equals("message")) {
				 String token = object.getJSONArray("events").getJSONObject(0).getString("replyToken").toString();
				 event = object.getJSONArray("events").getJSONObject(i);
				 String Message =event.getJSONObject("message").getString("text").toString();
			//String TextMessage = object.getJSONArray("events").getJSONObject(0).getString("").toString();
				 System.out.println("token: "+token); 
				 System.out.println("Message: "+Message); 
				 System.out.println("i:"+i); 
				 //建立回傳數值
				 //建立Http標頭
		         HttpHeaders headers = new HttpHeaders();
		         headers.setContentType(MediaType.APPLICATION_JSON);
		         headers.add("Authorization", String.format("%s %s", "Bearer", LINE_SECRET));
		         
				 //建立回傳訊息格式
		         //HashMap ReplyObject = new HashMap<>();
		         //HashMap payload = new HashMap<>();

		         //建立回傳訊息
		         //@SuppressWarnings("rawtypes")
				 //List messageArray = new ArrayList();
		         
		         @SuppressWarnings("rawtypes")
				 //HashMap msg = new HashMap<>();
		         //msg.put("text", Message);
		         //msg.put("type", "text");

		         
		         //messageArray.add(msg);
		         
		         //payload.put("replyToken", token);
		         //payload.put("messages", messageArray);
		         
		         //ReplyObject.put("method", "POST");
		         //ReplyObject.put("payload", payload);
		         
		         //測試JSONObject
		 		 JSONObject map = new JSONObject();
		 		 JSONObject PayloadContent = new JSONObject();
		 		 JSONObject MessagesContent = new JSONObject();
		 		 JSONArray Messages = new JSONArray();
		 		 
		 		//處理Message 
		 		MessagesContent.put("type","text"); 
		 		MessagesContent.put("text","message"); 
		 		Messages.put(MessagesContent);

		 		//處理PayLoadContent 
		 		PayloadContent.put("replyToken",token); 
		 		PayloadContent.put("messages",Messages); 
		 		
		        map.put("method", "post"); 
		        map.put("payload", PayloadContent); 
		        String json = map.toString();
		 		 //-----------
		         //測試訊息結果
		         //String json = ReplyObject.toString();
		         System.out.print(json);
		         
		         //回傳訊息
		         HttpEntity<String> entity = new HttpEntity<String>(map.toString(), headers);
		         ResponseEntity<String> response = restTemplate.exchange(Reply_Url,HttpMethod.POST, entity, String.class);

			 }
		}

		return new ResponseEntity<String>("OK", HttpStatus.OK);
	}
	
	@SuppressWarnings("null")
	@PostMapping("/TestMessage")
	public String Test(@RequestBody String requestBody) throws JsonProcessingException {
		JSONObject map = new JSONObject();
		JSONObject headerContent = new JSONObject();
		JSONObject PayloadContent = new JSONObject();
		JSONObject MessagesContent = new JSONObject();
		JSONArray Messages = new JSONArray();
		
		//處理Message 
		MessagesContent.put("type","text"); 
		MessagesContent.put("text","message"); 
		Messages.put(MessagesContent);

		//處理PayLoadContent 
		PayloadContent.put("replyToken","12312234234"); 
		PayloadContent.put("messages",Messages); 
		
		//處理HeaderContent 
		headerContent.put("Content-Type", "application/json; charset=UTF-8"); 
		headerContent.put("Authorization", "Bearer line_token"); 
		
        map.put("headers", headerContent); 
        map.put("method", "post"); 
        map.put("payload", PayloadContent); 
        String json = map.toString();
        return json; 
        
        
	}
	

}