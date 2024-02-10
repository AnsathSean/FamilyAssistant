package com.FALineBot.EndPoint.Dao.Impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
		FMBody_Contents_Contents1.put("text", "請點選以下功能新增菜譜");
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
			FMFooter_Contents_Contents2.put("label","新增菜譜功能");
			FMFooter_Contents_Contents2.put("uri","https://ansathseanbackend.com:8080/"+webfunction+"/"+wisher);

		
		FMFooter_Contents.put(FMFooter_Contents_Contents1);
		FMFooter.put("contents",FMFooter_Contents); 
		//設置最終Footer
		FMcontents.put("footer", FMFooter);
		//處理所有訊息
		FlexMessage.put("contents", FMcontents);
		//FlexMessage.put("footer", FMFooter);
		
		//設定訊息類別
		FlexMessage.put("altText","新增菜餚功能"); 
		FlexMessage.put("type","flex"); 
		Messages.put(FlexMessage);
		PayloadContent.put("messages",Messages); 
        //回傳訊息
		System.out.println(PayloadContent.toString());
        HttpEntity<String> entity = new HttpEntity<String>(PayloadContent.toString(), headers);
        restTemplate.exchange(Reply_Url,HttpMethod.POST, entity, String.class);
		
	}
}
