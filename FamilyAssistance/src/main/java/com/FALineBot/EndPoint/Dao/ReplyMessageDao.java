package com.FALineBot.EndPoint.Dao;

import java.util.List;


import com.FALineBot.EndPoint.Model.WishList;

public interface ReplyMessageDao {

	public void ReplyTextMessage(String Message,String token);
	public void ReplyFlexWishListMessage(List<WishList> list,String token,Boolean SelfWish,String wisher);
	public void ReplyFlexWishListMessageTemplate(String token);
	public void ReplyConfirmMessageTemplate(String token,String AltText,String TitleText, String YesBackText,String NoBackText);
	public void ReplyFlexWebContentMessage(String token, String Message,String wisher,String webfunction);
}
