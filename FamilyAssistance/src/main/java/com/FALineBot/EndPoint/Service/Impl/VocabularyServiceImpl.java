package com.FALineBot.EndPoint.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.FALineBot.EndPoint.Dao.VocabularyDao;
import com.FALineBot.EndPoint.Model.Page;
import com.FALineBot.EndPoint.Model.Vocabulary;
import com.FALineBot.EndPoint.Service.VocabularyService;

@Component
public class VocabularyServiceImpl implements VocabularyService{

	@Autowired
	private VocabularyDao vocabularyDao;

	@Override
	public Vocabulary getDefinitions(String word, String LindId) {
		Vocabulary getVoc =  vocabularyDao.getDefinitions(word,LindId);
		return getVoc;
	}
	
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
}
