package com.FALineBot.EndPoint.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.FALineBot.EndPoint.Dao.VocabularyDao;
import com.FALineBot.EndPoint.Service.VocabularyService;

@Component
public class VocabularyServiceImpl implements VocabularyService{

	@Autowired
	private VocabularyDao vocabularyDao;
}