package com.FALineBot.EndPoint.Service.Impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.FALineBot.EndPoint.Dao.ReplyMessageDao;
import com.FALineBot.EndPoint.Dao.SmokeDao;
import com.FALineBot.EndPoint.Service.SmokeService;

@Component
public class SmokeServiceImpl implements SmokeService{

	@Autowired
	private ReplyMessageDao replyMessageDao;
	@Autowired
	private SmokeDao smokeDao;
	
	@Override
	public void ReplySmokeTime(String token) {
        Date lastSmokeTime = smokeDao.GetLastSmokeTime();
        Date currentTime = new Date();

        long diffInMillies = Math.abs(currentTime.getTime() - lastSmokeTime.getTime());
        long diffInMinutes = diffInMillies / (60 * 1000);

        if (diffInMinutes >= 120) {
            replyMessageDao.ReplyTextMessage("可以抽菸", token);
        } else {
            long remainingMinutes = 120 - diffInMinutes;
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
            String formattedLastSmokeTime = sdf.format(lastSmokeTime);
            replyMessageDao.ReplyTextMessage("上次抽菸時間：" + formattedLastSmokeTime + "\n剩餘" + remainingMinutes + "分鐘", token);
            //System.out.println("上次抽烟时间：" + formattedLastSmokeTime + "，剩餘" + remainingMinutes + "分鐘");
        }
    }
	

	@Override
	public void RecordSmokeTime(String token) {
		smokeDao.RecordSmokeTime();
		//System.out.println("紀錄抽菸時間");
		replyMessageDao.ReplyTextMessage("紀錄抽菸時間", token);
	}

}
