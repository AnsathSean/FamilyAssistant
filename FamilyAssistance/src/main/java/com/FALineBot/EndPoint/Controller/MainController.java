package com.FALineBot.EndPoint.Controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.FALineBot.EndPoint.Service.WishListService;
import com.fasterxml.jackson.databind.util.JSONPObject;


@RequestMapping("/robot")
@RestController
public class MainController {
	
	
	@Autowired
	private WishListService wishListService;
	
	@Value("${line.user.secret}")
	 private String LINE_SECRET;
	
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
	//處理Line BOT訊息
	public boolean checkFromLine(String requestBody, String X_Line_Signature) {
		 SecretKeySpec key = new SecretKeySpec(LINE_SECRET.getBytes(), "HmacSHA256");
		 Mac mac;
		 try {
		  mac = Mac.getInstance("HmacSHA256");
		  mac.init(key);
		  byte[] source = requestBody.getBytes("UTF-8");
		  String signature = Base64.encodeBase64String(mac.doFinal(source));
		  if(signature.equals(X_Line_Signature)) {
		   return true;
		  }
		 } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
		 }
		 
		 return false;
		}
	
	@SuppressWarnings("rawtypes")
	@PostMapping("/messaging")
	public ResponseEntity messagingAPI(@RequestHeader("X-Line-Signature") String X_Line_Signature, @RequestBody String requestBody) throws UnsupportedEncodingException, IOException{
	 if(checkFromLine(requestBody, X_Line_Signature)) {
	  System.out.println("驗證通過");
	  return new ResponseEntity<String>("OK", HttpStatus.OK);
	 }
	 System.out.println("驗證不通過");
	 return new ResponseEntity<String>("Not line platform", HttpStatus.BAD_GATEWAY);
	}

}