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
		 JSONArray FMBodyContents_SingleWishDataArray = new JSONArray();
		 JSONObject FMBodyContents_SingleWishDataAData = new JSONObject();
		 JSONArray FMBodyContents_SingleWishDataADataArray = new JSONArray();
		 JSONObject FMBodyContents_SingleWishDataADataA = new JSONObject();
		 JSONObject FMBodyContents_SingleWishDataADataAB = new JSONObject();
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
		
		for (WishList e : list) {

			//FMBodyContents_SingleWishDataAData.clear();
			
			if(e.wisher.equals(wisher)) {
				//FMBodyContents_SingleWishDataADataA.clear();
				//FMBodyContents_SingleWishDataADataAB.clear();
				//FMBodyContents_SingleWishDataAData.clear();
				order = order+1;
				String Test =e.getPersent_name();
				System.out.println("Oder順序"+order.toString());
				if(order<4) {
					System.out.println("有在執行塞資料的動作");
					System.out.println("資料名稱"+Test.toString());
					//單一願望Content資料
							FMBodyContents_SingleWishDataAData.put("type", "box");
							FMBodyContents_SingleWishDataAData.put("layout", "horizontal");
					//組成單一願望Content的Content資料
							FMBodyContents_SingleWishDataADataA.put("type", "text");
							FMBodyContents_SingleWishDataADataA.put("text", order+" "+e.getPersent_name());
							FMBodyContents_SingleWishDataADataA.put("size","sm");
							FMBodyContents_SingleWishDataADataA.put("color","#555555");
							FMBodyContents_SingleWishDataADataA.put("flex",0);
					
							FMBodyContents_SingleWishDataADataAB.put("type", "text");
							FMBodyContents_SingleWishDataADataAB.put("text", "ID "+e.getWishListID());
							FMBodyContents_SingleWishDataADataAB.put("size","sm");
							FMBodyContents_SingleWishDataADataAB.put("color","#111111");
							FMBodyContents_SingleWishDataADataAB.put("align","end");
					
							FMBodyContents_SingleWishDataADataArray.put(FMBodyContents_SingleWishDataADataA);
							FMBodyContents_SingleWishDataADataArray.put(FMBodyContents_SingleWishDataADataAB);
							FMBodyContents_SingleWishDataAData.put("contents", FMBodyContents_SingleWishDataADataArray);
							FMBodyContents_SingleWishDataArray.put(FMBodyContents_SingleWishDataAData);
							System.out.println("塞完資料");
							System.out.println("資料名稱"+FMBodyContents_SingleWishDataArray.toString());
				}else {

				}
			}
		}

		//System.out.println("Oder順序"+order.toString());
		FMBodyContents_SingleWishData.put("contents",FMBodyContents_SingleWishDataArray);
		
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
		
		//處理Footer
		//FMFooter.put("type","box"); 
		//FMFooter.put("layout","vertical"); 

		//	FMFooter_Contents_Contents2.put("type","uri");
		//	FMFooter_Contents_Contents2.put("label","Shop");
		//	FMFooter_Contents_Contents2.put("uri","https://www.y-bio.net/");
		//FMFooter_Contents_Contents1.put("action",FMFooter_Contents_Contents2);
		//FMFooter_Contents_Contents1.put("type","button");
		
		//FMFooter_Contents.put(FMFooter_Contents_Contents1);
		//FMFooter.put("contents",FMFooter_Contents); 
		//設置最終Footer
		//FMcontents.put("footer", FMFooter);
		//處理所有訊息
		FlexMessage.put("contents", FMcontents);
		//FlexMessage.put("footer", FMFooter);
		
		//設定訊息類別
		FlexMessage.put("altText","願望清單"); 
		FlexMessage.put("type","flex"); 
		Messages.put(FlexMessage);
		PayloadContent.put("messages",Messages); 
        //回傳訊息
		System.out.println(PayloadContent.toString());
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

			FMFooter_Contents_Contents2.put("type","uri");
			FMFooter_Contents_Contents2.put("label","Shop");
			FMFooter_Contents_Contents2.put("uri","https://www.y-bio.net/");
		FMFooter_Contents_Contents1.put("action",FMFooter_Contents_Contents2);
		FMFooter_Contents_Contents1.put("type","button");
		
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
}
