package com.FALineBot.EndPoint.Service.Impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.FALineBot.EndPoint.Dao.VocabularyDao;
import com.FALineBot.EndPoint.Model.Page;
import com.FALineBot.EndPoint.Model.Vocabulary;
import com.FALineBot.EndPoint.Service.VocabularyService;
import java.time.temporal.ChronoUnit;

@Component
public class VocabularyServiceImpl implements VocabularyService{

	@Autowired
	private VocabularyDao vocabularyDao;

	@Override
	public Vocabulary getDefinitions(String word, String LindId) {
		Vocabulary getVoc =  vocabularyDao.getDefinitions(word,LindId);
		return getVoc;
	}
	
	//這個是把temp資料庫的單字資料存到VOC SQL裡面
    public boolean saveVocabulary(int id) {
        try {
            vocabularyDao.saveVocabulary(id);
            return true;
        } catch (Exception e) {
            // 可以選擇記錄錯誤日誌
            System.err.println("Error saving vocabulary: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteToTempVocabulary(int id) {
        try {
            vocabularyDao.deleteToTempVocabulary(id);
            return true;
        } catch (Exception e) {
            // 可以選擇記錄錯誤日誌
            System.err.println("Error deleting temp vocabulary: " + e.getMessage());
            return false;
        }
    }
    
    public void deleteVocabulary(int id) {
    	vocabularyDao.deleteVocabulary(id);
    }

	@Override
	public List<Vocabulary> getVocabularyList(String lineId) {
		List<Vocabulary> vocList = vocabularyDao.getVocabularyList(lineId);
		return vocList;
	}

	@Override
	public List<Vocabulary> getVocListPage(String lineId, int page, int pageSize) {
		List<Vocabulary> vocList = vocabularyDao.getVocListPage(lineId, page, pageSize);
		return vocList;
	}

	@Override
	public Page getPageProperties(String lineId, int pageSize, int currentPage) {
		Page page = vocabularyDao.getPageProperties(lineId, pageSize, currentPage);
		return page;
	}
	
	public List<Vocabulary> getVocabularyListbySearch(String lineId, String keyWords){
	  List<Vocabulary> voc = vocabularyDao.getVocabularyListbySearch(lineId, keyWords);
	  return voc;
	}

	public Vocabulary getVocabularybyId(String id) {
		Vocabulary voc = vocabularyDao.getVocabularybyId(id);
		return voc;
	}

	public void updateVocabulary(String id, Vocabulary vocabulary) {
		vocabularyDao.updateVocabulary(id, vocabulary);
	}
	
	public Vocabulary getVocabularybyDate(String lineId) {
		Vocabulary voc = vocabularyDao.getVocabularybyDate(lineId);
		return voc;
	}
	
    public void updateNextReviewVocbyQuality(Vocabulary card, int quality) {
        // 根據使用者的評分更新卡片的間隔和 ease_factor
    	
        LocalDate today = LocalDate.now();
        int interval = 1; // 默認間隔

        if(card.getLastReviewDate() !=null && card.getNextReviewDate()!=null) {
        	interval = (int) java.time.temporal.ChronoUnit.DAYS.between(card.getLastReviewDate(),card.getNextReviewDate());
        }
        
        if (quality < 3) { // 比較困難
            interval = 1;
            card.setRepetitions(0);
        } else { // 比較簡單
            card.setRepetitions(card.getRepetitions() + 1);

            if (card.getRepetitions() == 1) { // 第一次覺得簡單隔天再複習
                interval = 1;
            } else if (card.getRepetitions() == 2) { // 第二次覺得簡單六天後再複習
                interval = 6;
            } else { // 超過兩次以上簡單，根據 ease_factor 調整間隔
                interval = (int) (interval * card.getEaseFactor());
            }
        }

        // 更新 ease_factor 根據評分
        double newEaseFactor = card.getEaseFactor() + 0.1 - (5 - quality) * 0.08;
        if (newEaseFactor < 1.3) {
            newEaseFactor = 1.3; // 最低限制
        }
        card.setEaseFactor((float) newEaseFactor);
        
        // 設定最後複習日期為今天
        card.setLastReviewDate(today);
        
        // 計算下一次複習日期
        LocalDate nextReviewDate = today.plus(interval, ChronoUnit.DAYS);
        card.setNextReviewDate(nextReviewDate);

        // 直接更新到資料庫
        vocabularyDao.updateVocabulary(Integer.toString(card.getId()), card);
    }
}
