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
import com.FALineBot.EndPoint.Model.Cook;
import com.FALineBot.EndPoint.Model.User;
import com.FALineBot.EndPoint.Model.Vocabulary;
import com.FALineBot.EndPoint.Model.WishList;
import com.FALineBot.EndPoint.Service.CookingService;
import com.FALineBot.EndPoint.Service.ReplyMessageService;
import com.FALineBot.EndPoint.Service.SmokeService;
import com.FALineBot.EndPoint.Service.UserManagerService;
import com.FALineBot.EndPoint.Service.VocabularyService;
import com.FALineBot.EndPoint.Service.WishListService;

import java.time.LocalDate;
import java.util.ArrayList;





@RequestMapping("/robot")
@RestController
public class MainController {
	
	@Autowired
	private WishListService wishListService;
	@Autowired
	private ReplyMessageService replyMessageService;
	@Autowired
	private UserManagerService usermanagerService;
    @Autowired
    private CookingService cookingService;
    @Autowired
    private SmokeService smokeService;
    @Autowired
    private VocabularyService vocabularyService;
	
	//設置驗證代碼
	private String allValidationCode = "QWERASD123";
	private String SetValidationCodeName = "設定驗證碼"; //呼叫設定驗證碼的關鍵自
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
		//-----------------------
		// 處理 Postback 訊息
		//-------------------------
		for (int i = 0; i < object.getJSONArray("events").length(); i++) {
		    JSONObject currentEvent = object.getJSONArray("events").getJSONObject(i);

		    if (currentEvent.getString("type").equals("postback")) {
		        String token = currentEvent.getString("replyToken");
		        JSONObject postbackObject = currentEvent.getJSONObject("postback");

		        // 取得 post back 的 data 資料
		        String postbackData = postbackObject.getString("data");

                // 解析難度評分的 Postback 資料
                if (postbackData.startsWith("action=difficulty&level=")) {
                    try {
                        // 解析 level 和 id
                        String[] parts = postbackData.split("&");
                        String level = parts[1].split("=")[1];
                        String idStr = parts[2].split("=")[1];
                        int id = Integer.parseInt(idStr);

                        // 使用 WordId 取得單字
                        Vocabulary vocabulary = vocabularyService.getVocabularybyId(Integer.toString(id));

                        if (vocabulary != null) {
                            // 將 level 轉換為質量評分
                            int quality;
                            String status;
                            switch (level) {
                                case "hard":
                                    quality = 2;
                                    status = "Hard";
                                    break;
                                case "medium":
                                    quality = 4;
                                    status = "Mid";
                                    break;
                                case "easy":
                                    quality = 5;
                                    status = "Easy";
                                    break;
                                default:
                                    quality = 3;
                                    status = "Unknown";
                                    break;
                            }

                            //取得接下來日期、新增複習次數並直接存起來
                            vocabularyService.updateNextReviewVocbyQuality(vocabulary, quality);
                            // 取得下一個單字並回傳 replyRecapMessage
                            Vocabulary nextVocabulary = vocabularyService.getVocabularybyDate(vocabulary.getLineId());
                            if (nextVocabulary != null) {
                                replyMessageService.ReplyRecapVocFlexMessage(token, nextVocabulary.getWord(), nextVocabulary.getId().toString(), nextVocabulary.getDefinition());
                            } else {
                                replyMessageService.ReplyTextMessage("所有單字已複習完成！", token);
                            }
                        } else {
                            replyMessageService.ReplyTextMessage("無法找到對應的單字！", token);
                        }
                    } catch (NumberFormatException e) {
                        replyMessageService.ReplyTextMessage("ID 格式錯誤！", token);
                    } catch (Exception e) {
                        replyMessageService.ReplyTextMessage("處理失敗：" + e.getMessage(), token);
                    }

                    return new ResponseEntity<String>("OK", HttpStatus.OK);
                }
		        
		        // 解析 data，確認是 query_reading
		        if ("action=query_reading".equals(postbackData)) {
		            // 取得使用者 ID
		            String userId = currentEvent.getJSONObject("source").getString("userId");

		            // 將圖文選單綁定給該用戶
		            String richMenuId = "richmenu-2be56f583ed9fa0f7c3e55f8d6e6b75a";
		            replyMessageService.bindRichMenuToUser(userId, richMenuId);

		            // 回應訊息給用戶
		            //replyMessageService.ReplyTextMessage("已切換至閱讀模式圖文選單", token);

		            return new ResponseEntity<String>("OK", HttpStatus.OK);
		        }
		        
		        
		        
		        // 解析 data，確認是 query_reading
		        if ("action=query_wish".equals(postbackData)) {
		            // 取得使用者 ID
		            String userId = currentEvent.getJSONObject("source").getString("userId");

		            // 將圖文選單綁定給該用戶
		            String richMenuId = "richmenu-0501fca8e01ac0fa9c77c14e5039ae72";
		            replyMessageService.bindRichMenuToUser(userId, richMenuId);

		            // 回應訊息給用戶
		            //replyMessageService.ReplyTextMessage("已切換至處菜與願望選單", token);

		            return new ResponseEntity<String>("OK", HttpStatus.OK);
		        }
		        
		        // 判斷是否是 action=save&id=*
		        if (postbackData.startsWith("action=save&id=")) {
		            try {
		                // 取得 id
		                String idStr = postbackData.substring("action=save&id=".length());
		                int id = Integer.parseInt(idStr);

		                // 執行 saveVocabulary
		                boolean success = vocabularyService.saveVocabulary(id);

		                // 回應成功或失敗訊息
		                if (success) {
		                	vocabularyService.deleteToTempVocabulary(id);
		                    replyMessageService.ReplyTextMessage("儲存成功！", token);
		                } else {
		                    replyMessageService.ReplyTextMessage("儲存失敗，請稍後再試！", token);
		                }
		            } catch (NumberFormatException e) {
		                replyMessageService.ReplyTextMessage("ID 格式錯誤！", token);
		            } catch (Exception e) {
		                replyMessageService.ReplyTextMessage("處理失敗：" + e.getMessage(), token);
		            }

		            return new ResponseEntity<String>("OK", HttpStatus.OK);
		        }
		    
		        
		    }
		}

		

		
		
		//處理一般Message訊息
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

				 //如果一開始沒有註冊
				 if (user == null ) {
					 //如果一開始就輸入正確的代碼，直接更新成功
					 if(Message.toString().equals(allValidationCode)) {
						 usermanagerService.setUserInformation(wisher, "Enroll-Step-01","Stranger");
						 usermanagerService.updateUserInformation(wisher, "","Normal");
						 replyMessageService.ReplyTextMessage("註冊成功",token);
						 return new ResponseEntity<String>("OK", HttpStatus.OK);
					 }
					 //若沒有回答正確的資料，就回答還沒有註冊
					 replyMessageService.ReplyTextMessage("此帳號還沒有註冊，請輸入註冊代碼進行認證",token);
					 usermanagerService.setUserInformation(wisher, "Enroll-Step-01","Stranger");
					 return new ResponseEntity<String>("OK", HttpStatus.OK);
					}
				 //如果之後回錯，然後都是這個
				 if(user.getUserStep() != null && user.getUserStep().equals("Enroll-Step-01")) {
					 if(Message.toString().equals(allValidationCode)) {
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
				 //===========================================
				 //===========關鍵字搜尋功能=====================
				 //===========================================
				 
				 
				//------------------------------------------
				// 查詢單字功能
				//---------------------------------------------
				if (Message.trim().matches("^[a-zA-Z- ]+$")) {
				    // 將 Message 去除左右空白並轉換為小寫
				    String trimmedMessage = Message.trim();
				    String lowerCaseMessage = trimmedMessage.toLowerCase();

				    // 檢查是否為單個單字或兩組單字
				    if (lowerCaseMessage.matches("^[a-zA-Z]+( [a-zA-Z]+)?$")) {
				        // 呼叫 VocabularyService 使用處理後的單字查詢
				        Vocabulary vocabulary = vocabularyService.getDefinitions(lowerCaseMessage, wisher);

				        // 如果小寫查詢失敗，嘗試將首字母大寫
				        if (vocabulary == null || vocabulary.getDefinition() == null || vocabulary.getDefinition().isEmpty()) {
				            // 將每個單字的首字母大寫
				            String[] words = lowerCaseMessage.split(" ");
				            StringBuilder capitalizedMessage = new StringBuilder();
				            for (String word : words) {
				                capitalizedMessage.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
				            }
				            String formattedMessage = capitalizedMessage.toString().trim();
				            vocabulary = vocabularyService.getDefinitions(formattedMessage, wisher);
				        }

				        // 檢查是否超過查詢次數限制
				        if (vocabulary.isMinLimit()) {
				            replyMessageService.ReplyTextMessage("已達到每分鐘查詢次數上限，請稍後再試。", token);
				            return new ResponseEntity<>("OK", HttpStatus.OK);
				        } else if (vocabulary.isHourLimit()) {
				            replyMessageService.ReplyTextMessage("已達到每小時查詢次數上限，請於下個小時再進行查詢。", token);
				            return new ResponseEntity<>("OK", HttpStatus.OK);
				        }

				        // 檢查是否查詢到結果
				        if (vocabulary != null && vocabulary.getDefinition() != null && !vocabulary.getDefinition().isEmpty()) {
				            // 獲取詞性，若為空則設置為預設值 "none"
				            List<String> partOfSpeechList = vocabulary.getPartOfSpeech() != null && !vocabulary.getPartOfSpeech().isEmpty()
				                    ? vocabulary.getPartOfSpeech()
				                    : List.of("none");

				            // 使用 ReplyVocFlexMessage 發送 Flex Message
				            replyMessageService.ReplyVocFlexMessage(
				                    token,                       // replyToken
				                    vocabulary.getWord(),        // 單字
				                    vocabulary.getDefinition(),  // 定義
				                    vocabulary.getExampleSentence() != null ? vocabulary.getExampleSentence() : List.of(), // 例句，若無則使用空列表
				                    partOfSpeechList,            // 詞性列表
				                    vocabulary.getId().toString()
				            );
				        } else {
				            // 如果仍然找不到定義，回傳提示訊息
				            replyMessageService.ReplyTextMessage("抱歉，未能找到該單字的定義。", token);
				        }

				        return new ResponseEntity<>("OK", HttpStatus.OK);
				    } else {
				        // 如果輸入不符合格式，回傳提示訊息
				        replyMessageService.ReplyTextMessage("請輸入正確的單字或單字組 (例如: 'doll up')。", token);
				        return new ResponseEntity<>("OK", HttpStatus.OK);
				    }
				}
				
				//顯示單字清單
				 if(Message.indexOf("檢視單字")!=-1) {
					 if(usermanagerService.checkUserPermission(user.getUUID(),"Cooking_02_Show") ) {
						 replyMessageService.ReplyWebClickTemplate("檢視單字",token, "ShowVocabulary", wisher);
						 return new ResponseEntity<String>("Delete OK", HttpStatus.OK);
					 }else {
						 replyMessageService.ReplyTextMessage("無法顯示，請確認權限",token);
					     return new ResponseEntity<String>("OK", HttpStatus.OK);
					 }
					 
				 }
				 
				 //複習單字功能
				 if(Message.indexOf("複習單字")!=-1) {
					 if(usermanagerService.checkUserPermission(user.getUUID(),"Cooking_02_Show") ) {
						 Vocabulary voc = vocabularyService.getVocabularybyDate(wisher);
				         if (voc != null) {
				             replyMessageService.ReplyRecapVocFlexMessage(token, voc.getWord(), voc.getId().toString(),voc.getDefinition());
				             return new ResponseEntity<String>("OK", HttpStatus.OK);
				         } else {
				             replyMessageService.ReplyTextMessage("已經都複習過了!", token);
				             return new ResponseEntity<String>("OK", HttpStatus.OK);
				         }
					 }else {
						 replyMessageService.ReplyTextMessage("無法顯示，請確認權限",token);
					     return new ResponseEntity<String>("OK", HttpStatus.OK);
					 }
					 
				 }

				 
				 //=========
				 //願望查詢功能
				 //============
				 //新增願望清單功能
				 if(Message.indexOf("新增願望")!=-1 ) {
					 if(usermanagerService.checkUserPermission(user.getUUID(),"WishList_01_Add")) {
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
					 }else {
						 replyMessageService.ReplyTextMessage("權限不足無法新增願望",token);
						 return new ResponseEntity<String>("OK", HttpStatus.OK);
					 }
				 }
				 //測試FlexMessage
				 if(Message.indexOf("測試FLEX")!=-1) {
					 replyMessageService.ReplyFlexMessageTemplate(token);
					 return new ResponseEntity<String>("OK", HttpStatus.OK);
				 }
				 //查詢願望清單功能
				 if(Message.indexOf("查詢願望")!=-1) {
					 String combineID = user.getCombineID();
					 if(usermanagerService.checkUserPermission(user.getUUID(),"WishList_02_CheckOther") && combineID != null) {
					 List<WishList> list = wishListService.findAllWishList();
					 List<WishList> filteredList = new ArrayList<>();
					 
				     for (WishList wish : list) {
				          // 检查每个 WishList 对象的 wisher 是否与 combineID 相匹配
				          if (wish.getWisher() != null && wish.getWisher().equals(combineID)) {
				              filteredList.add(wish);
				          }
		             }
				        
					 SelfWish = false;
					 System.out.println("是否查自己的"+SelfWish);
					 replyMessageService.ReplyFlexWishListMessage(filteredList,token,SelfWish,wisher);
					 return new ResponseEntity<String>("OK", HttpStatus.OK);
					 }else {
						 replyMessageService.ReplyTextMessage("無法查詢願望，請確認權限或綁定對象",token);
						 return new ResponseEntity<String>("OK", HttpStatus.OK);
						 
					 }
				 }
				 //查詢自己的願望清單功能
				 if(Message.indexOf("查詢自己的願望")!=-1) {
					 if(usermanagerService.checkUserPermission(user.getUUID(),"WishList_03_CheckMyself")) {
					 List<WishList> list = wishListService.findAllWishList();
					 SelfWish = true;
				        if (list.isEmpty()) {
				            // 例如发送一条消息告知用户愿望清单为空
				            replyMessageService.ReplyTextMessage("您的願望清单為空，請新增願望，可從功能介紹選單查詢新增願望方法。", token);
				            return new ResponseEntity<String>("OK", HttpStatus.OK);
				        }
					 System.out.println("是否查自己的"+SelfWish);
					 replyMessageService.ReplyFlexWishListMessage(list,token,SelfWish,wisher);
					 return new ResponseEntity<String>("OK", HttpStatus.OK);
					 }else {
						 replyMessageService.ReplyTextMessage("無法查詢自己的願望，請確認權限",token);
						 return new ResponseEntity<String>("OK", HttpStatus.OK);
						 
					 }
				 }
				 //刪除願望清單功能
				 if(Message.indexOf("刪除願望")!=-1) {
					 if(usermanagerService.checkUserPermission(user.getUUID(),"WishList_04_Deletewish")) {
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
					 }else {						 
						 replyMessageService.ReplyTextMessage("無法啟動刪除願望，請確認權限",token);
					     return new ResponseEntity<String>("OK", HttpStatus.OK);
					 }
				 }
				 if(Message.indexOf("DeleteWish")!=-1) {
					 if(usermanagerService.checkUserPermission(user.getUUID(),"WishList_04_Deletewish")) {
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
					 }else {
						 replyMessageService.ReplyTextMessage("無法刪除願望，請確認權限",token);
					     return new ResponseEntity<String>("OK", HttpStatus.OK);
					 }
				 }
				 //=========
				 //處理抽菸紀錄功能
				 //========
				 if(Message.indexOf("抽菸")!=-1 && wisher.indexOf("U7a3d0a754eb02a8c7786e4ffd1f33b8b")!=-1) {
					 smokeService.RecordSmokeTime(token);
					 return new ResponseEntity<String>("OK", HttpStatus.OK);
				 }
				 if(Message.indexOf("剩餘時間")!=-1 && wisher.indexOf("U7a3d0a754eb02a8c7786e4ffd1f33b8b")!=-1) {
					 smokeService.ReplySmokeTime(token);
					 return new ResponseEntity<String>("OK", HttpStatus.OK);
				 }
				 if(Message.indexOf("我的LineID")!=-1 && wisher.indexOf("U7a3d0a754eb02a8c7786e4ffd1f33b8b")!=-1) {
					 replyMessageService.ReplyTextMessage("Line ID: "+wisher,token);
					 return new ResponseEntity<String>("OK", HttpStatus.OK);
				 }
				 
				 //=========
				 //處理食譜功能
				 //========
				 //新增食譜用網址
				 if(Message.indexOf("新增菜餚")!=-1) {
					 if(usermanagerService.checkUserPermission(user.getUUID(),"Cooking_01_Add")) {
						 replyMessageService.ReplyWebClickTemplate("新增菜餚",token, "AddCooking", wisher);
						 return new ResponseEntity<String>("Delete OK", HttpStatus.OK);
					 }else {
						 replyMessageService.ReplyTextMessage("無法新增，請確認權限",token);
					     return new ResponseEntity<String>("OK", HttpStatus.OK);
					 }
					 
				 }
				 if(Message.indexOf("顯示歷史菜餚")!=-1) {
					 if(usermanagerService.checkUserPermission(user.getUUID(),"Cooking_02_Show") ) {
						 replyMessageService.ReplyWebClickTemplate("顯示菜餚",token, "ShowCooking", wisher);
						 return new ResponseEntity<String>("Delete OK", HttpStatus.OK);
					 }else {
						 replyMessageService.ReplyTextMessage("無法顯示，請確認權限",token);
					     return new ResponseEntity<String>("OK", HttpStatus.OK);
					 }
					 
				 }
				 
				 if(Message.indexOf("顯示對方的歷史菜餚")!=-1) {
					 String combineID = user.getCombineID();
					 if(usermanagerService.checkUserPermission(user.getUUID(),"Cooking_02_Show")&& combineID != null) {
						 replyMessageService.ReplyWebClickTemplate("顯示對方菜餚",token, "ShowOPCooking",user.getCombineID());
						 return new ResponseEntity<String>("Delete OK", HttpStatus.OK);
					 }else {
						 replyMessageService.ReplyTextMessage("無法顯示，請確認權限或綁定對方ID",token);
					     return new ResponseEntity<String>("OK", HttpStatus.OK);
					 }
					 
				 }
				 
				 if(Message.indexOf("隨機菜餚")!=-1) {
					String message;
					 if(usermanagerService.checkUserPermission(user.getUUID(),"Cooking_02_Show") ) {
						 List<Cook> cookList = cookingService.getRandomCookList(wisher);
				            if (cookList.isEmpty()) {
				            	message = "數據不足，無法提供隨機菜餚。";
				            	replyMessageService.ReplyTextMessage(message,token);
				            	return new ResponseEntity<String>("OK", HttpStatus.OK);
				            } else {
				                StringBuilder stringBuilder = new StringBuilder();
				                stringBuilder.append("隨機菜餚：\n");
				                for (Cook cook : cookList) {
				                    stringBuilder.append(cook.getCookName()).append("\n");
				                }
				                message = stringBuilder.toString();
				            	replyMessageService.ReplyTextMessage(message,token);
				            	return new ResponseEntity<String>("OK", HttpStatus.OK);
				            }
					 }else {
						 replyMessageService.ReplyTextMessage("無法顯示，請確認權限或綁定對方ID",token);
					     return new ResponseEntity<String>("OK", HttpStatus.OK);
					 }
					 
				 }
				 if(Message.indexOf("操作方法")!=-1) {
					 String OperateMessage;
					 StringBuilder stringBuilder = new StringBuilder();
					 stringBuilder.append("讓另一半綁定你ID的方法：\n");
					 stringBuilder.append("1.先取得對方的驗證碼：\n");
					 stringBuilder.append("輸入：取得自己的驗證碼\n");
					 stringBuilder.append("2.讓另一半輸入：設定驗證碼，然後輸入自己的驗證碼，即可綁定\n");
					 stringBuilder.append("新增願望方法：\n");
					 stringBuilder.append("輸入範例:新增願望 吹風機\n");
					 stringBuilder.append("刪除願望方法\n");
					 stringBuilder.append("輸入範例:刪除願望 4（為顯示願望清單後的ID數值）\n");
					 stringBuilder.append("隨機菜餚方法\n");
					 stringBuilder.append("輸入範例:隨機菜餚\n");
					 stringBuilder.append("後續功能更新中\n");
					 stringBuilder.append("更新日期：2024/02/17\n");
					 OperateMessage =stringBuilder.toString();
					 replyMessageService.ReplyTextMessage(OperateMessage,token);
				     return new ResponseEntity<String>("OK", HttpStatus.OK);
				 }
				 
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