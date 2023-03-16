package com.FALineBot.EndPoint.Dao.Impl;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.FALineBot.EndPoint.Dao.ReplyMessageDao;

@Component
public class ReplyMessageDaoImpl implements ReplyMessageDao{

	private RestTemplate restTemplate = new RestTemplate();
	private String Reply_Url = "https://api.line.me/v2/bot/message/reply";
	private String LINE_SECRET = "/SG/if6TI6qOaEqeniCsdaX4Y/R7pzzjw6mhQrwsCCQK0aItavD/9jhH/OwsAlDbNAlWcGJU2W5hn9hK2WrkU1kk0bM77KdRfIVcop96uIJurFGCpGMEiNoGlmjo59dGdMNOSUBi8AhTwwTyDHeCuAdB04t89/1O/w1cDnyilFU=";

	public void ReplyTextMessage(String Message,String token) {
		//建立回傳訊息標頭
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", String.format("%s %s", "Bearer", LINE_SECRET));
		//建立回傳JSON訊息
		 JSONObject PayloadContent = new JSONObject();
		 JSONObject MessagesContent = new JSONObject();
		 JSONArray Messages = new JSONArray();
		 
		//處理Message 
		MessagesContent.put("type","text"); 
		MessagesContent.put("text",Message); 
		Messages.put(MessagesContent);

		//處理PayLoadContent 
		PayloadContent.put("replyToken",token); 
		PayloadContent.put("messages",Messages); 
		        
        //回傳訊息
        HttpEntity<String> entity = new HttpEntity<String>(PayloadContent.toString(), headers);
        restTemplate.exchange(Reply_Url,HttpMethod.POST, entity, String.class);
	}
	
	public void ReplyFlexWishListMessage(String token)
	{
		//建立回傳訊息標頭
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", String.format("%s %s", "Bearer", LINE_SECRET));
		//建立回傳JSON訊息
		 JSONObject PayloadContent = new JSONObject();
		 JSONObject FlexMessage = new JSONObject();
		 
		 JSONObject FMcontents = new JSONObject();
		 
		 JSONObject FMBody = new JSONObject();
		 JSONArray FMBody_Contents = new JSONArray();
		 JSONObject FMBody_Contents_Contents1 = new JSONObject();
		 JSONObject FMBody_Contents_Contents2 = new JSONObject();
		 
		 JSONObject FMFooter = new JSONObject();
		 JSONArray FMFooter_Contents = new JSONArray();
		 JSONObject FMFooter_Contents_Contents1 = new JSONObject();
		 JSONObject FMFooter_Contents_Contents2 = new JSONObject();
		 
		//處理Message 
		FlexMessage.put("type","flex"); 
		FlexMessage.put("altText","This is a Flex Message"); 
		//處理contents
		FMcontents.put("type", "bubble");
		FMBody.put("type", "box");
		FMBody.put("layout", "vertical");
		//處理Body下的Contents
		FMBody_Contents_Contents1.put("type","text");
		FMBody_Contents_Contents1.put("text", "Hello,");
		FMBody_Contents_Contents1.put("weight","bold");
		
		FMBody_Contents_Contents2.put("type","text");
		FMBody_Contents_Contents2.put("text", "World!,");
		FMBody_Contents_Contents2.put("weight","bold");
		
		FMBody_Contents.put(FMBody_Contents_Contents1);
		FMBody_Contents.put(FMBody_Contents_Contents2);
		
		//處理PayLoadContent 
		PayloadContent.put("replyToken",token); 
		
		//處理Footer
		FMFooter.put("type","box"); 
		FMFooter.put("layout","horizontal"); 
		FMFooter_Contents_Contents1.put("type","button");
			FMFooter_Contents_Contents2.put("type","uri");
			FMFooter_Contents_Contents2.put("label","Go to EMMA Shop");
			FMFooter_Contents_Contents2.put("uri","https://www.y-bio.net/");
		FMFooter_Contents_Contents1.put("action",FMFooter_Contents_Contents2);
		FMFooter_Contents.put(FMFooter_Contents_Contents1);
		FMFooter.put("contents",FMFooter_Contents); 
		//處理所有訊息
		FlexMessage.put("contents", FMcontents);
		FlexMessage.put("footer", FMFooter);
		PayloadContent.put("messages",FlexMessage); 
        //回傳訊息
        HttpEntity<String> entity = new HttpEntity<String>(PayloadContent.toString(), headers);
        restTemplate.exchange(Reply_Url,HttpMethod.POST, entity, String.class);
		
	}
	
	
}
