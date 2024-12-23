package com.FALineBot.EndPoint.Dao;

import java.util.List;

import com.FALineBot.EndPoint.Model.Page;
import com.FALineBot.EndPoint.Model.Vocabulary;

public interface VocabularyDao {
	
	 public Vocabulary getDefinitions(String word, String LineId);
	 public void saveVocabulary(int id);
	 public void deleteToTempVocabulary(int id);
	 public void deleteVocabulary(int id);
	 public List<Vocabulary> getVocabularyList(String LineId);
	 public List<Vocabulary> getVocListPage(String lineId, int page, int pageSize);
	 public Page getPageProperties(String lineId, int pageSize, int currentPage);
	 public List<Vocabulary> getVocabularyListbySearch(String lineId, String keyWords);
}
