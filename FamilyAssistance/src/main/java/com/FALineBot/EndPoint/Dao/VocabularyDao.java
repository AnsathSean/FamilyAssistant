package com.FALineBot.EndPoint.Dao;

import java.util.List;

import com.FALineBot.EndPoint.Model.Vocabulary;

public interface VocabularyDao {
	
	 public Vocabulary getDefinitions(String word, String LineId);
	 public void saveVocabulary(int id);
	 public void deleteToTempVocabulary(int id);
	 public List<Vocabulary> getVocabularyList(String LineId);
}
