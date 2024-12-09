package com.FALineBot.EndPoint.Service;

import java.util.List;

import com.FALineBot.EndPoint.Model.WishList;

public interface ReplyMessageService {

	
	void ReplyTextMessage(String Message,String token);
	void ReplyFlexWishListMessage(List<WishList> list,String token,Boolean SelfWish,String wisher);
	void ReplyFlexMessageTemplate(String token);
	void ReplyCheckDeleteMessage(Integer id,String token);
	void ReplyWebClickTemplate(String Message,String token,String webfunction,String wisher);
	void bindRichMenuToUser(String userId, String richMenuId);
	void ReplyVocFlexMessage(String replyToken, String word, List<String> definition, List<String> example, List<String> partOfSpeechList);
}
