package com.FALineBot.EndPoint.Dao;

import com.FALineBot.EndPoint.Model.Vocabulary;

public interface VocabularyDao {
	
	 public Vocabulary getDefinitions(String word, String LineId);
	 public void saveVocabulary(int id);
	 public void deleteToTempVocabulary(int id);
}
