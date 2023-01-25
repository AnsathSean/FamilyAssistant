package com.FALineBot.EndPoint.Controller;



import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.FALineBot.EndPoint.Dto.WishListParam;
import com.FALineBot.EndPoint.Model.WishList;
import com.FALineBot.EndPoint.Service.ReplyMessageService;
import com.FALineBot.EndPoint.Service.WishListService;



@RequestMapping("/robot")
@RestController
public class MainController {
	
	@Autowired
	private WishListService wishListService;
	@Autowired
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
	
	wishListService.createProduct(wishListParam);
	//Product product = WishListService.getProductById(productId);
	
	return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	//ＬineBot主功能
	@SuppressWarnings("rawtypes")
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
				 String wisher = event.getJSONObject("source").getString("userId").toString();
				 //新增願望清單功能
				 if(Message.indexOf("新增願望")!=-1) {
					 
				        String[] newStr = Message.split("\\s+");
				        WishListParam wishListParam = new WishListParam();
				        for (int k = 1; k < newStr.length; k++) {
				            System.out.println(newStr[k]);
				            if(k == 1) {
				            wishListParam.setPersent_name(newStr[k]);
				            }
				            if(k == 2) {
				            wishListParam.setRemark(newStr[k]);
				            }

				        }
			
				        wishListParam.setWisher(wisher);
			            wishListService.createProduct(wishListParam);
			            replyMessageService.ReplyTextMessage("新增願望："+wishListParam.getPersent_name(),token);
			            return new ResponseEntity<String>("OK", HttpStatus.OK);
					 
				 }
				 //查詢願望清單功能
				 if(Message.indexOf("查詢願望")!=-1) {
				   List<WishList> list = wishListService.findAllWishList();
				   String test ="";
					for (WishList e : list) {
						if(e.wisher.replaceAll(" ", "") != wisher.replaceAll(" ", "")) {
						  System.out.println("e.wisher: "+e.wisher);
						  System.out.println("wisher: "+ wisher);
						  test+= e.getWishListID()+" "+e.getPersent_name()+"\n";
						}
					}
			       replyMessageService.ReplyTextMessage("願望清單：\n" + test,token);
			       return new ResponseEntity<String>("OK", HttpStatus.OK);
					 
				 }
				 //刪除願望清單功能
				 if(Message.indexOf("刪除願望")!=-1) {
				 
					 String[] newStr = Message.split("\\s+");
				        Integer ID = 0;
				        for (int k = 1; k < newStr.length; k++) {
				            System.out.println(newStr[k]);
				            if(k == 2) {
				            	ID=Integer.parseInt(newStr[k]);
				            	wishListService.deleteWishListByID(ID);
				            	replyMessageService.ReplyTextMessage("刪除成功",token);
				            }

				        }
					 
				 }
				 //查詢訊息
				 System.out.println("完整訊息: "+object.toString());
				 System.out.println("Message: "+ Message);
				 System.out.println("ReplyToken: "+token);
				 System.out.println("i:"+i); 
				 
				 //回傳訊息
				 replyMessageService.ReplyTextMessage(Message,token);

			 }
		}

		return new ResponseEntity<String>("OK", HttpStatus.OK);
	}
	

	

}