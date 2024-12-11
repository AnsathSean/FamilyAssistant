package com.FALineBot.EndPoint.Service;

import com.FALineBot.EndPoint.Model.Vocabulary;

public interface VocabularyService {

	
	public Vocabulary getDefinitions(String word, String lineId);
	
}
