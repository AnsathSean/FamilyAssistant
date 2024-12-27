package com.FALineBot.EndPoint.Dao.Impl;

import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.FALineBot.EndPoint.Dao.ReplyMessageDao;
import com.FALineBot.EndPoint.Model.WishList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ReplyMessageDaoImpl implements ReplyMessageDao{

	private RestTemplate restTemplate = new RestTemplate();
	private String Reply_Url = "https://api.line.me/v2/bot/message/reply";
	private String LINE_SECRET = "/SG/if6TI6qOaEqeniCsdaX4Y/R7pzzjw6mhQrwsCCQK0aItavD/9jhH/OwsAlDbNAlWcGJU2W5hn9hK2WrkU1kk0bM77KdRfIVcop96uIJurFGCpGMEiNoGlmjo59dGdMNOSUBi8AhTwwTyDHeCuAdB04t89/1O/w1cDnyilFU=";

	public void ReplyVocFlexMessage(String replyToken, String word, List<String> definition, List<String> example, List<String> partOfSpeechList,String tempId) {
	//建立單字的Flex Message
		try {
		        // 創建 Body
		        LinkedHashMap<String, Object> bodyMap = new LinkedHashMap<>();
		        bodyMap.put("type", "box");
		        bodyMap.put("layout", "vertical");

		        JSONArray bodyContents = new JSONArray();
		        bodyContents.put(new JSONObject().put("type", "text").put("text", "單字查詢結果").put("weight", "bold")
		                .put("color", "#1DB446").put("size", "sm"));
		        bodyContents.put(new JSONObject().put("type", "text").put("text", word).put("weight", "bold")
		                .put("size", "xxl").put("margin", "md"));

		        // 動態生成詞性
		        String partOfSpeech = partOfSpeechList != null && !partOfSpeechList.isEmpty()
		                ? "詞性：" + String.join(", ", partOfSpeechList)
		                : "詞性：none";
		        bodyContents.put(new JSONObject().put("type", "text").put("text", partOfSpeech).put("size", "sm")
		                .put("color", "#aaaaaa").put("margin", "sm").put("wrap", true));

		        bodyContents.put(new JSONObject().put("type", "text").put("text", "定義").put("weight", "bold")
		                .put("size", "sm").put("margin", "lg").put("color", "#555555"));

		        // 定義內容
		        LinkedHashMap<String, Object> definitionBoxMap = new LinkedHashMap<>();
		        definitionBoxMap.put("type", "box");
		        definitionBoxMap.put("layout", "vertical");
		        JSONArray definitionContents = new JSONArray();
		        for (int i = 0; i < definition.size(); i++) {
		            definitionContents.put(new JSONObject().put("type", "text")
		                    .put("text", (i + 1) + ". " + definition.get(i))
		                    .put("size", "sm")
		                    .put("color", "#111111")
		                    .put("wrap", true)
		                    .put("margin", "md"));
		        }
		        definitionBoxMap.put("contents", definitionContents);
		        bodyContents.put(new JSONObject(definitionBoxMap));

		        bodyContents.put(new JSONObject().put("type", "separator").put("margin", "xxl"));
		        bodyContents.put(new JSONObject().put("type", "text").put("text", "例句").put("weight", "bold")
		                .put("size", "sm").put("margin", "lg").put("color", "#555555"));

		        // 例句內容
		        for (String sentence : example) {
		            bodyContents.put(new JSONObject().put("type", "text")
		                    .put("text", sentence)
		                    .put("size", "sm")
		                    .put("color", "#111111")
		                    .put("wrap", true)
		                    .put("margin", "md"));
		        }
		        bodyMap.put("contents", bodyContents);
		        JSONObject body = new JSONObject(bodyMap);

		        // 創建 Footer
		        LinkedHashMap<String, Object> footerMap = new LinkedHashMap<>();
		        footerMap.put("type", "box");
		        footerMap.put("layout", "vertical");
		        footerMap.put("spacing", "sm");

		        JSONArray footerContents = new JSONArray();
		        footerContents.put(new JSONObject().put("type", "button")
		                .put("action", new JSONObject()
		                        .put("type", "postback")
		                        .put("label", "儲存單字")
		                        .put("data", "action=save&id=" + tempId)
		                        .put("displayText", "我要儲存這個單字：" + word))
		                        //.put("displayText", "這個單字在Temp id為：" + tempId))
		                .put("style", "primary")
		                .put("color", "#1DB446")
		                .put("margin", "xs")
		                .put("height", "sm"));
		        footerMap.put("contents", footerContents);
		        JSONObject footer = new JSONObject(footerMap);

		        // 創建 Flex Message
		        LinkedHashMap<String, Object> flexMessageMap = new LinkedHashMap<>();
		        flexMessageMap.put("type", "bubble");
		        flexMessageMap.put("body", body);
		        flexMessageMap.put("footer", footer);
		        JSONObject flexMessage = new JSONObject(flexMessageMap);

		        // 創建主訊息結構
		        LinkedHashMap<String, Object> messageMap = new LinkedHashMap<>();
		        messageMap.put("type", "flex");
		        messageMap.put("altText", "單字查詢結果：" + word);
		        messageMap.put("contents", flexMessage);
		        JSONObject message = new JSONObject(messageMap);

		        JSONArray messages = new JSONArray();
		        messages.put(message);

		        LinkedHashMap<String, Object> requestBodyMap = new LinkedHashMap<>();
		        requestBodyMap.put("replyToken", replyToken);
		        requestBodyMap.put("messages", messages);
		        JSONObject requestBody = new JSONObject(requestBodyMap);

		        // 打印出完整的 JSON 結構
		        //System.out.println("Request JSON: " + requestBody.toString(4)); // 格式化輸出

		        // 設定 Headers
		        HttpHeaders headers = new HttpHeaders();
		        headers.setContentType(MediaType.APPLICATION_JSON);
		        headers.setBearerAuth(LINE_SECRET);

		        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

		        // 發送 POST 請求
		        ResponseEntity<String> response = restTemplate.exchange(Reply_Url, HttpMethod.POST, entity, String.class);

		        // 檢查回應狀態
		        if (response.getStatusCode() == HttpStatus.OK) {
		            System.out.println("Flex Message 發送成功");
		        } else {
		            System.err.println("Flex Message 發送失敗: " + response.getBody());
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
	}

	public void ReplyRecapVocFlexMessage(String replyToken, String word, String WordId) {
	    try {
	        // 創建 Body
	        JSONObject body = new JSONObject();
	        body.put("type", "box");
	        body.put("layout", "vertical");

	        JSONArray bodyContents = new JSONArray();
	        bodyContents.put(new JSONObject()
	                .put("type", "text")
	                .put("text", "複習單字")
	                .put("weight", "bold")
	                .put("color", "#1DB446")
	                .put("size", "sm"));
	        bodyContents.put(new JSONObject()
	                .put("type", "text")
	                .put("text", word)
	                .put("weight", "bold")
	                .put("size", "xxl")
	                .put("margin", "md"));

	        body.put("contents", bodyContents);

	        // 創建 Footer
	        JSONObject footer = new JSONObject();
	        footer.put("type", "box");
	        footer.put("layout", "vertical");
	        footer.put("spacing", "sm");

	        JSONArray footerContents = new JSONArray();

	        footerContents.put(new JSONObject()
	                .put("type", "button")
	                .put("action", new JSONObject()
	                        .put("type", "postback")
	                        .put("label", "困難")
	                        .put("data", "action=difficulty&level=hard&id=" + WordId)
	                        .put("displayText", "這個單字我覺得困難"))
	                .put("style", "primary")
	                .put("color", "#FF0000"));

	        footerContents.put(new JSONObject()
	                .put("type", "button")
	                .put("action", new JSONObject()
	                        .put("type", "postback")
	                        .put("label", "中等")
	                        .put("data", "action=difficulty&level=medium&id=" + WordId)
	                        .put("displayText", "這個單字我覺得中等"))
	                .put("style", "primary")
	                .put("color", "#FFA500"));

	        footerContents.put(new JSONObject()
	                .put("type", "button")
	                .put("action", new JSONObject()
	                        .put("type", "postback")
	                        .put("label", "簡單")
	                        .put("data", "action=difficulty&level=easy&id=" + WordId)
	                        .put("displayText", "這個單字我覺得簡單"))
	                .put("style", "primary")
	                .put("color", "#1DB446"));

	        footer.put("contents", footerContents);

	        // 創建 Flex Message
	        JSONObject flexMessage = new JSONObject();
	        flexMessage.put("type", "bubble");
	        flexMessage.put("body", body);
	        flexMessage.put("footer", footer);

	        // 創建主訊息結構
	        JSONObject message = new JSONObject();
	        message.put("type", "flex");
	        message.put("altText", "複習單字: " + word);
	        message.put("contents", flexMessage);

	        JSONArray messages = new JSONArray();
	        messages.put(message);

	        JSONObject requestBody = new JSONObject();
	        requestBody.put("replyToken", replyToken);
	        requestBody.put("messages", messages);

	        // 設定 Headers
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setBearerAuth(LINE_SECRET);

	        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

	        // 發送 POST 請求
	        ResponseEntity<String> response = restTemplate.exchange(Reply_Url, HttpMethod.POST, entity, String.class);

	        if (response.getStatusCode() == HttpStatus.OK) {
	            System.out.println("Flex Message 發送成功");
	        } else {
	            System.err.println("Flex Message 發送失敗: " + response.getBody());
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

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
	
	public void ReplyFlexWishListMessage(List<WishList> list,String token,Boolean SelfWish,String wisher)
	{
		Integer order=0;
		String WebWisher = "";
		String WebWisherName = "";
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
		 JSONObject FMBodyContents_Title = new JSONObject();
		 JSONObject FMBodyContents_SubTitle = new JSONObject();
		 JSONObject FMBodyContents_Separator = new JSONObject();
		 JSONObject FMBodyContents_SingleWishData = new JSONObject();
		 JSONObject FMBodyContents_SingleWishDataAData = new JSONObject();
		 JSONArray FMBodyContents_SingleWishDataADataArray = new JSONArray();
		 JSONObject FMBodyContents_SingleWishDataADataA = new JSONObject();
		 JSONObject FMBodyContents_SingleWishDataADataAB = new JSONObject();
		 
		 JSONObject FMFooter = new JSONObject();
		 JSONArray FMFooter_Contents = new JSONArray();
		 JSONObject FMFooter_Contents_Contents1 = new JSONObject();
		 JSONObject FMFooter_Contents_Contents2 = new JSONObject();
		 
		 JSONArray Messages = new JSONArray();
		//處理contents
		FMcontents.put("type", "bubble");
		
		//處理Body下的Contents
		FMBodyContents_Title.put("type","text");
		FMBodyContents_Title.put("text", "願望清單");
		FMBodyContents_Title.put("weight","bold");
		FMBodyContents_Title.put("size","xxl");
		FMBodyContents_Title.put("color","#1DB446");
		FMBodyContents_Title.put("margin", "md");
		
		FMBodyContents_SubTitle.put("type","text");
		FMBodyContents_SubTitle.put("text", "由家庭小助理提供");
		FMBodyContents_SubTitle.put("size","xs");
		FMBodyContents_SubTitle.put("color","#aaaaaa");
		FMBodyContents_SubTitle.put("weight","bold");
		FMBodyContents_SubTitle.put("wrap",true);
		//分割線
		FMBodyContents_Separator.put("type","separator");
		FMBodyContents_Separator.put("margin", "xxl");
		//單一願望資料
		FMBodyContents_SingleWishData.put("type", "box");
		FMBodyContents_SingleWishData.put("layout", "vertical");
		FMBodyContents_SingleWishData.put("margin", "xxl");
		FMBodyContents_SingleWishData.put("spacing", "sm");
		
		FMBodyContents_SingleWishDataAData.put("type", "box");
		FMBodyContents_SingleWishDataAData.put("layout", "horizontal");
		String FinalResult = "";
		//固定的AB
		FMBodyContents_SingleWishDataADataA.put("type", "text");
		FMBodyContents_SingleWishDataADataA.put("size","sm");
		FMBodyContents_SingleWishDataADataA.put("color","#555555");
		FMBodyContents_SingleWishDataADataA.put("flex",0);
		
		FMBodyContents_SingleWishDataADataAB.put("type", "text");
		FMBodyContents_SingleWishDataADataAB.put("size","sm");
		FMBodyContents_SingleWishDataADataAB.put("color","#111111");
		FMBodyContents_SingleWishDataADataAB.put("align","end");
		
		//設置WebWisher名稱
		if(SelfWish) {
			WebWisherName ="自己的願望清單細項";
		}else {
			WebWisherName ="對方的願望清單細項";
		}
		for (WishList e : list) {
			if(e.wisher.equals(wisher) && SelfWish) {
				WebWisher=wisher;

				order = order+1;
				FMBodyContents_SingleWishDataADataA.put("text", order+" "+e.getPersent_name());
				FMBodyContents_SingleWishDataADataAB.put("text", "ID "+e.getWishListID());
				FMBodyContents_SingleWishDataADataArray.put(FMBodyContents_SingleWishDataADataA);
				FMBodyContents_SingleWishDataADataArray.put(FMBodyContents_SingleWishDataADataAB);
				FMBodyContents_SingleWishDataAData.put("contents", FMBodyContents_SingleWishDataADataArray);
				if(FinalResult!= "") {
				  FinalResult = FinalResult +","+FMBodyContents_SingleWishDataAData.toString();
				}else {
				  FinalResult = FMBodyContents_SingleWishDataAData.toString();
				}
				  FMBodyContents_SingleWishDataADataArray.clear();
			 }if(!e.wisher.equals(wisher) && !SelfWish) {
				  //執行非自己的願望清單
				 WebWisher=e.wisher;
				  order = order+1;
				  //組成單一願望Content的Content資料
	              FMBodyContents_SingleWishDataADataA.put("text", order+" "+e.getPersent_name());
				  FMBodyContents_SingleWishDataADataAB.put("text", "ID "+e.getWishListID());
				  FMBodyContents_SingleWishDataADataArray.put(FMBodyContents_SingleWishDataADataA);
				  FMBodyContents_SingleWishDataADataArray.put(FMBodyContents_SingleWishDataADataAB);
				  FMBodyContents_SingleWishDataAData.put("contents", FMBodyContents_SingleWishDataADataArray);
				  if(FinalResult!= "") {
				    FinalResult = FinalResult +","+FMBodyContents_SingleWishDataAData.toString();
				  }else {
					FinalResult = FMBodyContents_SingleWishDataAData.toString();
					}
					FMBodyContents_SingleWishDataADataArray.clear();
			}
		}
		
		FinalResult = "["+FinalResult+"]";
		JSONArray FinalResult2 = new JSONArray(FinalResult); 
		FMBodyContents_SingleWishData.put("contents",FinalResult2);
		
		//處理Footer
		FMFooter.put("type","box"); 
		FMFooter.put("layout","vertical"); 
		FMFooter_Contents_Contents1.put("action",FMFooter_Contents_Contents2);
		FMFooter_Contents_Contents1.put("type","button");
			FMFooter_Contents_Contents2.put("type","uri");
			FMFooter_Contents_Contents2.put("label",WebWisherName);
			FMFooter_Contents_Contents2.put("uri","https://ansathseanbackend.com:8080/MyWishlist/"+WebWisher);

		FMFooter_Contents.put(FMFooter_Contents_Contents1);
		FMFooter.put("contents",FMFooter_Contents); 
		//設置最終Footer
		FMcontents.put("footer", FMFooter);
		//組成單一願望Content
		
		//最終組成所需內容
		FMBody_Contents.put(FMBodyContents_Title);
		FMBody_Contents.put(FMBodyContents_SubTitle);
		FMBody_Contents.put(FMBodyContents_Separator);
		FMBody_Contents.put(FMBodyContents_SingleWishData);
		
		FMBody.put("contents",FMBody_Contents);
		FMBody.put("type", "box");
		FMBody.put("layout", "vertical");
		//設置最終BODY
		FMcontents.put("body", FMBody);
		//處理PayLoadContent 
		PayloadContent.put("replyToken",token); 
		
		//處理所有訊息
		FlexMessage.put("contents", FMcontents);
		FlexMessage.put("footer", FMFooter);
		
		//設定訊息類別
		FlexMessage.put("altText","願望清單"); 
		FlexMessage.put("type","flex"); 
		Messages.put(FlexMessage);
		PayloadContent.put("messages",Messages); 
        //回傳訊息

        HttpEntity<String> entity = new HttpEntity<String>(PayloadContent.toString(), headers);
        restTemplate.exchange(Reply_Url,HttpMethod.POST, entity, String.class);
		
	}
	
	public void ReplyFlexWishListMessageTemplate(String token)
	{
		//建立回傳訊息標頭
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", String.format("%s %s", "Bearer", LINE_SECRET));
		//建立回傳JSON訊息
        //主Jason訊息
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
		 JSONArray Messages = new JSONArray();
		 

		//處理contents
		FMcontents.put("type", "bubble");
		
		//處理Body下的Contents
		FMBody_Contents_Contents1.put("type","text");
		FMBody_Contents_Contents1.put("text", "Hello,");
		FMBody_Contents_Contents1.put("weight","bold");
		
		FMBody_Contents_Contents2.put("type","text");
		FMBody_Contents_Contents2.put("text", "World!,");
		FMBody_Contents_Contents2.put("weight","bold");
		
		FMBody_Contents.put(FMBody_Contents_Contents1);
		FMBody_Contents.put(FMBody_Contents_Contents2);
		
		FMBody.put("contents",FMBody_Contents);
		FMBody.put("type", "box");
		FMBody.put("layout", "vertical");
		//設置最終BODY
		FMcontents.put("body", FMBody);
		//處理PayLoadContent 
		PayloadContent.put("replyToken",token); 
		
		//處理Footer
		FMFooter.put("type","box"); 
		FMFooter.put("layout","vertical"); 
		FMFooter_Contents_Contents1.put("action",FMFooter_Contents_Contents2);
		FMFooter_Contents_Contents1.put("type","button");
			FMFooter_Contents_Contents2.put("type","uri");
			FMFooter_Contents_Contents2.put("label","Shop");
			FMFooter_Contents_Contents2.put("uri","https://www.y-bio.net/");

		
		FMFooter_Contents.put(FMFooter_Contents_Contents1);
		FMFooter.put("contents",FMFooter_Contents); 
		//設置最終Footer
		FMcontents.put("footer", FMFooter);
		//處理所有訊息
		FlexMessage.put("contents", FMcontents);
		//FlexMessage.put("footer", FMFooter);
		
		//設定訊息類別
		FlexMessage.put("altText","This is a Flex Message"); 
		FlexMessage.put("type","flex"); 
		Messages.put(FlexMessage);
		PayloadContent.put("messages",Messages); 
        //回傳訊息
		System.out.println(PayloadContent.toString());
        HttpEntity<String> entity = new HttpEntity<String>(PayloadContent.toString(), headers);
        restTemplate.exchange(Reply_Url,HttpMethod.POST, entity, String.class);
		
	}

	@Override
	public void ReplyConfirmMessageTemplate(String token,String AltText, String TitleText, String YesBackText, String NoBackText) {
		//建立回傳訊息標頭
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", String.format("%s %s", "Bearer", LINE_SECRET));
        //主Jason訊息
		JSONObject PayloadContent = new JSONObject();
		JSONObject Messages = new JSONObject();
		JSONArray MessagesArray = new JSONArray();
		
		JSONObject TemplateContent = new JSONObject();
		JSONObject YActionButton = new JSONObject();
		JSONObject NActionButton = new JSONObject();
		//處理ActionButton層級
		String Actions = "";
		YActionButton.put("type", "message");
		YActionButton.put("label", "確定");
		YActionButton.put("text", YesBackText);
		Actions = Actions + YActionButton.toString();
		NActionButton.put("type", "message");
		NActionButton.put("label", "取消");
		NActionButton.put("text", NoBackText);
		Actions = Actions +","+NActionButton.toString();
		Actions = "["+Actions+"]";
		
		System.out.println(Actions);
		JSONArray ActionArray = new JSONArray(Actions); 
		//處理template
		TemplateContent.put("type", "confirm");
		TemplateContent.put("text", TitleText);
		TemplateContent.put("actions", ActionArray);
		//處理Message層級
		Messages.put("type", "template");
		Messages.put("template", TemplateContent);
		Messages.put("altText",AltText);
		MessagesArray.put(Messages);
		//處理PayloadContent層級
		PayloadContent.put("type","flex");
		//PayloadContent.put("altText",AltText);
		PayloadContent.put("messages",MessagesArray); 
		PayloadContent.put("replyToken",token); 
		
	    //回傳訊息
	    System.out.println(PayloadContent.toString());
	    HttpEntity<String> entity = new HttpEntity<String>(PayloadContent.toString(), headers);
	    restTemplate.exchange(Reply_Url,HttpMethod.POST, entity, String.class);
	}

	@Override
	public void ReplyFlexWebContentMessage(String token, String Message, String wisher,String webfunction) {
		//建立回傳訊息標頭
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", String.format("%s %s", "Bearer", LINE_SECRET));
		//建立回傳JSON訊息
        //主Jason訊息
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
		 JSONArray Messages = new JSONArray();
		 

		//處理contents
		FMcontents.put("type", "bubble");
		
		//處理Body下的Contents
		FMBody_Contents_Contents1.put("type","text");
		FMBody_Contents_Contents1.put("text", "請點選下方連結");
		FMBody_Contents_Contents1.put("weight","bold");
		
		//FMBody_Contents_Contents2.put("type","text");
		//FMBody_Contents_Contents2.put("text", "World!,");
		//FMBody_Contents_Contents2.put("weight","bold");
		
		FMBody_Contents.put(FMBody_Contents_Contents1);
		//FMBody_Contents.put(FMBody_Contents_Contents2);
		
		FMBody.put("contents",FMBody_Contents);
		FMBody.put("type", "box");
		FMBody.put("layout", "vertical");
		//設置最終BODY
		FMcontents.put("body", FMBody);
		//處理PayLoadContent 
		PayloadContent.put("replyToken",token); 
		
		//處理Footer
		FMFooter.put("type","box"); 
		FMFooter.put("layout","vertical"); 
		FMFooter_Contents_Contents1.put("action",FMFooter_Contents_Contents2);
		FMFooter_Contents_Contents1.put("type","button");
			FMFooter_Contents_Contents2.put("type","uri");
			FMFooter_Contents_Contents2.put("label",Message+"功能");
			FMFooter_Contents_Contents2.put("uri","https://ansathseanbackend.com:8080/"+webfunction+"/"+wisher);

		
		FMFooter_Contents.put(FMFooter_Contents_Contents1);
		FMFooter.put("contents",FMFooter_Contents); 
		//設置最終Footer
		FMcontents.put("footer", FMFooter);
		//處理所有訊息
		FlexMessage.put("contents", FMcontents);
		//FlexMessage.put("footer", FMFooter);
		
		//設定訊息類別
		FlexMessage.put("altText","新增功能"); 
		FlexMessage.put("type","flex"); 
		Messages.put(FlexMessage);
		PayloadContent.put("messages",Messages); 
        //回傳訊息
		System.out.println(PayloadContent.toString());
        HttpEntity<String> entity = new HttpEntity<String>(PayloadContent.toString(), headers);
        restTemplate.exchange(Reply_Url,HttpMethod.POST, entity, String.class);
		
	}

	@Override
	public void bindRichMenuToUser(String userId, String richMenuId) {
		  // API URL
	    String apiUrl = "https://api.line.me/v2/bot/user/" + userId + "/richmenu/" + richMenuId;

	    // 建立 RestTemplate
	    RestTemplate restTemplate = new RestTemplate();

	    // 設定 Headers
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setBearerAuth(LINE_SECRET); // 替換成你的 Channel Access Token

	    // 建立請求
	    HttpEntity<String> request = new HttpEntity<>(headers);

	    // 發送請求
	    try {
	        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
	        if (response.getStatusCode() == HttpStatus.OK) {
	            System.out.println("成功綁定圖文選單給使用者: " + userId);
	        } else {
	            System.out.println("綁定失敗: " + response.getStatusCode());
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.out.println("綁定過程中發生錯誤");
	    }
		
	}
}
