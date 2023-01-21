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
import com.FALineBot.EndPoint.Service.ReplyMessageService;
import com.FALineBot.EndPoint.Service.WishListService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@RequestMapping("/robot")
@RestController
public class MainController {
	
	@Autowired
	private WishListService wishListService;
	private ReplyMessageService replyMessageService;


	//測試用，回傳Hello Java
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping("/test")
	public ResponseEntity test(){
		return new ResponseEntity("Hello Java", HttpStatus.OK);
	}
	//這個是測試SQL用的動作
	@PostMapping("/products/")
	public ResponseEntity<WishList> createProduct(@RequestBody WishListParam wishListParam){
	
	int ID = wishListService.createProduct(wishListParam);
	//Product product = WishListService.getProductById(productId);
	
	return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	//ＬineBot主功能
	@PostMapping("/messaging")
	public ResponseEntity messagingAPI(@RequestHeader("X-Line-Signature") String X_Line_Signature,
			@RequestBody String requestBody) {
		//取得Request訊息
		JSONObject object = new JSONObject(requestBody);	
		JSONObject event = new JSONObject();
		
		for(int i=0; i<object.getJSONArray("events").length(); i++) {
			//判斷陣列裡是否有續鐔
			 if(object.getJSONArray("events").getJSONObject(i).getString("type").equals("message")) {
				 
				 String token = object.getJSONArray("events").getJSONObject(0).getString("replyToken").toString();
				 event = object.getJSONArray("events").getJSONObject(i);
				 String Message =event.getJSONObject("message").getString("text").toString();
				 //判斷文字是否為願望清單
				 //if(Message.indexOf("新增願望")!=-1) {
					 
				 //       String[] newStr = Message.split("\\s+");
				 //       WishList wishList = new WishList();
				 //       for (int k = 1; k < newStr.length; k++) {
				 //           System.out.println(newStr[k]);
				 //           wishList.setPersent_name(newStr[k]);
				 //       }
					 
				 //}
				 //查詢訊息
				 System.out.println("完整訊息: "+object.toString());
				 System.out.println("i:"+i); 
				 
				 //回傳訊息
				 replyMessageService.ReplyTextMessage(Message, token);

			 }
		}

		return new ResponseEntity<String>("OK", HttpStatus.OK);
	}
	

	

}