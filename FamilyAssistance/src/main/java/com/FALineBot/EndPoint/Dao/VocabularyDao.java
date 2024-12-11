package com.FALineBot.EndPoint.Dao;

import com.FALineBot.EndPoint.Model.Vocabulary;

public interface VocabularyDao {
	
	 public Vocabulary getDefinitions(String word, String LineId);
}
