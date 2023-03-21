package com.FALineBot.EndPoint.Service.Impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.FALineBot.EndPoint.Dao.ReplyMessageDao;
import com.FALineBot.EndPoint.Model.WishList;
import com.FALineBot.EndPoint.Service.ReplyMessageService;

@Component
public class ReplyMessageServiceImpl implements ReplyMessageService{

	@Autowired
	private ReplyMessageDao replyMessageDao;
	
	@Override
	public void ReplyTextMessage(String Message,String token) {
		replyMessageDao.ReplyTextMessage(Message, token);
	}
	
	@Override
	public void ReplyFlexWishListMessage(List<WishList> list,String token,Boolean SelfWish) {
		replyMessageDao.ReplyFlexWishListMessage(list,token,SelfWish);
	}
	
}
