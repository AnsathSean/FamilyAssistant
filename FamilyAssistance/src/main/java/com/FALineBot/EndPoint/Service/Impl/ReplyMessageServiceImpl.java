package com.FALineBot.EndPoint.Service.Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.FALineBot.EndPoint.Dao.ReplyMessageDao;
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
	public void ReplyFlexWishListMessage(String token) {
		replyMessageDao.ReplyFlexWishListMessage(token);
	}
	
}
