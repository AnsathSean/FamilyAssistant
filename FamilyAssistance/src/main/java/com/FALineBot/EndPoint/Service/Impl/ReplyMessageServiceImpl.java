package com.FALineBot.EndPoint.Service.Impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.FALineBot.EndPoint.Dao.ReplyMessageDao;
import com.FALineBot.EndPoint.Dao.WishListDao;
import com.FALineBot.EndPoint.Model.WishList;
import com.FALineBot.EndPoint.Service.ReplyMessageService;

@Component
public class ReplyMessageServiceImpl implements ReplyMessageService {

	@Autowired
	private ReplyMessageDao replyMessageDao;
	@Autowired
	private WishListDao wishListDao;
	
	@Override
	public void ReplyTextMessage(String Message,String token) {
		replyMessageDao.ReplyTextMessage(Message, token);
	}
	
	@Override
	public void ReplyFlexWishListMessage(List<WishList> list,String token,Boolean SelfWish,String wisher) {
		replyMessageDao.ReplyFlexWishListMessage(list,token,SelfWish,wisher);
	}

	@Override
	public void ReplyFlexMessageTemplate(String token) {
		replyMessageDao.ReplyFlexWishListMessageTemplate(token);
		
	}

	@Override
	public void ReplyCheckDeleteMessage(Integer id, String token) {
		List<WishList> ItemList = wishListDao.findWishListByID(id);
		String ItemName = "";
		for (WishList e : ItemList) {
			ItemName = e.getPersent_name();
		}
		
		replyMessageDao.ReplyConfirmMessageTemplate(token,"是否刪除願望","刪除 "+ItemName+"?","DeleteWish "+id,"取消刪除");
		
	}

	@Override
	public void ReplyWebClickTemplate(String Message, String token, String webfunction,String wisher) {
		replyMessageDao.ReplyFlexWebContentMessage(token, Message,wisher, webfunction);
	}

	@Override
	public void bindRichMenuToUser(String userId, String richMenuId) {
		replyMessageDao.bindRichMenuToUser(userId, richMenuId);
		
	}

	@Override
	public void ReplyVocFlexMessage(String replyToken, String word, List<String> definition, List<String> example, List<String> partOfSpeechList,String tempId) {
		replyMessageDao.ReplyVocFlexMessage(replyToken, word, definition, example, partOfSpeechList,tempId);
		
	}
	
	public void ReplyRecapVocFlexMessage(String replyToken, String word, String WordId,List<String>  definition) {
		replyMessageDao.ReplyRecapVocFlexMessage(replyToken, word, WordId,definition);
	}
}
