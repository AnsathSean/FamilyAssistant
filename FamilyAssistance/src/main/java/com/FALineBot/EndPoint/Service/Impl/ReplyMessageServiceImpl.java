package com.FALineBot.EndPoint.Service.Impl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.FALineBot.EndPoint.Service.ReplyMessageService;

@Component
public class ReplyMessageServiceImpl implements ReplyMessageService{

	@Autowired
	private RestTemplate restTemplate = new RestTemplate();
	private String Reply_Url = "https://api.line.me/v2/bot/message/reply";
	private String LINE_SECRET = "/SG/if6TI6qOaEqeniCsdaX4Y/R7pzzjw6mhQrwsCCQK0aItavD/9jhH/OwsAlDbNAlWcGJU2W5hn9hK2WrkU1kk0bM77KdRfIVcop96uIJurFGCpGMEiNoGlmjo59dGdMNOSUBi8AhTwwTyDHeCuAdB04t89/1O/w1cDnyilFU=";

	@Override
	public void ReplyTextMessage(String Message,String token) {
		//建立回傳訊息標頭
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", String.format("%s %s", "Bearer", LINE_SECRET));
		//建立回傳JSON訊息
		 JSONObject map = new JSONObject();
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
		
       String json = PayloadContent.toString();
		 //-----------
        //測試訊息結果
        System.out.print(json);
        
        //回傳訊息
        HttpEntity<String> entity = new HttpEntity<String>(PayloadContent.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(Reply_Url,HttpMethod.POST, entity, String.class);
	}
	
}