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
import com.FALineBot.EndPoint.Model.User;
import com.FALineBot.EndPoint.Model.WishList;
import com.FALineBot.EndPoint.Service.ReplyMessageService;
import com.FALineBot.EndPoint.Service.UserManagerService;
import com.FALineBot.EndPoint.Service.WishListService;




@RequestMapping("/robot")
@RestController
public class MainController {
	
	@Autowired
	private WishListService wishListService;
	@Autowired
	private ReplyMessageService replyMessageService;
	@Autowired
	private UserManagerService usermanagerService;

	
	//設置驗證代碼
	private String allValidationCode = "QWERASD123";
	
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
	

	
	//============================================================
	//ＬineBot主功能
	//============================================================
	@SuppressWarnings("rawtypes")
	@PostMapping("/messaging")
	public ResponseEntity messagingAPI(@RequestHeader("X-Line-Signature") String X_Line_Signature,
			@RequestBody String requestBody) {
		//取得Request訊息
		JSONObject object = new JSONObject(requestBody);	
		JSONObject event = new JSONObject();
		boolean SelfWish = true;
		
		//處理Message訊息
		for(int i=0; i<object.getJSONArray("events").length(); i++) {
			//判斷陣列是否正常
			 if(object.getJSONArray("events").getJSONObject(i).getString("type").equals("message")) {
				 
				 String token = object.getJSONArray("events").getJSONObject(0).getString("replyToken").toString();
				 event = object.getJSONArray("events").getJSONObject(i);
				 String Message =event.getJSONObject("message").getString("text").toString();
				 String wisher = event.getJSONObject("source").getString("userId").toString();
				 //===========判斷ID是否為Stranger===================
				 //註冊系統功能
				 User user = usermanagerService.getUserById(wisher);
				 //如果一開始就輸入正確的代碼，直接更新成功
				 if(Message.toString().equals(allValidationCode)) {
					 usermanagerService.setUserInformation(wisher, "","Normal");
					 replyMessageService.ReplyTextMessage("註冊成功",token);
					 return new ResponseEntity<String>("OK", HttpStatus.OK);
				 }
				 //如果一開始沒有註冊，回傳的東西也是錯的
				 if (user == null ) {
					 replyMessageService.ReplyTextMessage("此帳號還沒有註冊，請輸入註冊代碼進行認證",token);
					 usermanagerService.setUserInformation(wisher, "Enroll-Step-01","Stranger");
					 return new ResponseEntity<String>("OK", HttpStatus.OK);
					}
				 //如果之後回錯，然後都是這個
				 if(user.getUserStep() != null && user.getUserStep().equals("Enroll-Step-01")) {
					 if(Message.toString() == allValidationCode) {
						 usermanagerService.updateUserInformation(wisher, "","Normal");
						 replyMessageService.ReplyTextMessage("註冊成功",token);
						 return new ResponseEntity<String>("OK", HttpStatus.OK);
					}else {
						replyMessageService.ReplyTextMessage("註冊代碼輸入錯誤，請輸入正確的代碼",token);
						return new ResponseEntity<String>("OK", HttpStatus.OK);
					}
				 }
				 //如果為Stranger則返回註冊功能
				 //========
				 //一般功能
				 //=====
				 //藉由驗證碼設定另一半
				 if(Message.indexOf("設定驗證碼")!=-1) {
					 usermanagerService.updateUserInformation(wisher, "Validation-Step-01","");
					 replyMessageService.ReplyTextMessage("請輸入對方的驗證碼",token);
					 return new ResponseEntity<String>("OK", HttpStatus.OK);
				 }
				 
				 if(user.getUserStep() != null && user.getUserStep().equals("Validation-Step-01")) {
					 boolean isGetMamber =usermanagerService.getUserbyCombineID(Message);
					 if(isGetMamber) {
						 usermanagerService.updateUserInfo_CombineID(wisher, Message);
						 usermanagerService.updateUserInformation(wisher, "","");
						 replyMessageService.ReplyTextMessage("綁定資料成功",token);
						 return new ResponseEntity<String>("OK", HttpStatus.OK);
					 }else {
						 replyMessageService.ReplyTextMessage("驗證碼無效，請確認驗證碼是否正確。",token);
						 return new ResponseEntity<String>("OK", HttpStatus.OK);
					 }
				 }
				 //取得自己的驗證碼
				 if(Message.indexOf("取得自己的驗證碼")!=-1) {
					 String ValidationCode = usermanagerService.getValidationCode(wisher);
					 replyMessageService.ReplyTextMessage(ValidationCode,token);
					 return new ResponseEntity<String>("OK", HttpStatus.OK);
				 }
				 //顯示所有角色清單，暫時可以不用，現在都先註冊Normal
				 //顯示自己的角色清單，暫時不用
				 //新增角色，暫時不用
				 //===========關鍵字搜尋功能=============================================================
				 //==========
				 //願望查詢功能
				 //============
				 //新增願望清單功能
				 if(Message.indexOf("新增願望")!=-1) {
					 
					    String PeresentName = "";					 
				        String[] newStr = Message.split("\\s+");
				        WishListParam wishListParam = new WishListParam();
				        
				        for (int k = 1; k < newStr.length; k++) {
				        	PeresentName = PeresentName + " " + newStr[k];
				        }
				        wishListParam.setPersent_name(PeresentName.trim());
			
				        wishListParam.setWisher(wisher);
			            wishListService.createProduct(wishListParam);
			            replyMessageService.ReplyTextMessage("新增願望："+wishListParam.getPersent_name(),token);
			            //System.out.println("K1: "+K1);
			            //System.out.println("K2: "+K2);
			            
			            return new ResponseEntity<String>("OK", HttpStatus.OK);
					 
				 }
				 //測試FlexMessage
				 if(Message.indexOf("測試FLEX")!=-1) {
					 replyMessageService.ReplyFlexMessageTemplate(token);
					 return new ResponseEntity<String>("OK", HttpStatus.OK);
				 }
				 //查詢願望清單功能
				 if(Message.indexOf("查詢願望")!=-1) {
					 List<WishList> list = wishListService.findAllWishList();
					 SelfWish = false;
					 System.out.println("是否查自己的"+SelfWish);
					 replyMessageService.ReplyFlexWishListMessage(list,token,SelfWish,wisher);
					 return new ResponseEntity<String>("OK", HttpStatus.OK);
					 
				 }
				 //查詢自己的願望清單功能
				 if(Message.indexOf("查詢自己的願望")!=-1) {
					 List<WishList> list = wishListService.findAllWishList();
					 SelfWish = true;
					 System.out.println("是否查自己的"+SelfWish);
					 replyMessageService.ReplyFlexWishListMessage(list,token,SelfWish,wisher);
					 return new ResponseEntity<String>("OK", HttpStatus.OK);
					 
				 }
				 //刪除願望清單功能
				 if(Message.indexOf("刪除願望")!=-1) {
				 
					 String[] newStr = Message.split("\\s+");
				        Integer ID = 0;
				        for (int k = 1; k < newStr.length; k++) {
				            //System.out.println(newStr[k]);
				            if(k == 1) {
				            	ID=Integer.parseInt(newStr[k]);
				            	//wishListService.deleteWishListByID(ID);
				            	//replyMessageService.ReplyTextMessage("刪除成功",token);
				            	replyMessageService.ReplyCheckDeleteMessage(ID, token);
				            }

				        }
				        return new ResponseEntity<String>("Delete OK", HttpStatus.OK);
					 
				 }
				 if(Message.indexOf("DeleteWish")!=-1) {
					 
					 String[] newStr = Message.split("\\s+");
				        Integer ID = 0;
				        for (int k = 1; k < newStr.length; k++) {
				            //System.out.println(newStr[k]);
				            if(k == 1) {
				            	ID=Integer.parseInt(newStr[k]);
				            	wishListService.deleteWishListByID(ID);
				            	replyMessageService.ReplyTextMessage("刪除成功",token);
				            }

				        }
				        return new ResponseEntity<String>("Delete OK", HttpStatus.OK);
					 
				 }
				 //=========
				 //處理食譜功能
				 //========
				 //新增食譜用網址
				 //查看當前料理(最近紀錄的料理)
				 //查看對方當前的料理
				 //查詢歷史食譜(抓最近十幾筆呈現出來)
				 //查看對方的歷史食譜
				 //修改食譜(也是用網址，要先查詢日期，烈出來之後按設定之後修改)
				 //查詢訊息
				 //System.out.println("完整訊息: "+object.toString());
				 //System.out.println("Message: "+ Message);
				 //System.out.println("ReplyToken: "+token);
				 //System.out.println("i:"+i); 
				 
				 //如果什麼都沒有符合，則回傳相同的訊息
				 replyMessageService.ReplyTextMessage(Message,token);

			 }
		}

		return new ResponseEntity<String>("OK", HttpStatus.OK);
	}
	

	

}