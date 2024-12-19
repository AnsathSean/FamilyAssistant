package com.FALineBot.EndPoint.Service;

import java.util.List;

import com.FALineBot.EndPoint.Model.Vocabulary;

public interface VocabularyService {

	
	public Vocabulary getDefinitions(String word, String lineId);
	public boolean saveVocabulary(int id);
	public boolean deleteToTempVocabulary(int id);
	public List<Vocabulary> getVocabularyList(String lineId);
	public List<Vocabulary> getVocListPage(String lineId, int page, int pageSize); 
}
