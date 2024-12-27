package com.FALineBot.EndPoint.Service;

import java.time.LocalDate;
import java.util.List;

import com.FALineBot.EndPoint.Model.Page;
import com.FALineBot.EndPoint.Model.Vocabulary;

public interface VocabularyService {

	
	public Vocabulary getDefinitions(String word, String lineId);
	public boolean saveVocabulary(int id);
	public boolean deleteToTempVocabulary(int id);
	public void deleteVocabulary(int id);
	public List<Vocabulary> getVocabularyList(String lineId);
	public List<Vocabulary> getVocListPage(String lineId, int page, int pageSize); 
	public Page getPageProperties(String lineId, int pageSize, int currentPage);
	public List<Vocabulary> getVocabularyListbySearch(String lineId, String keyWords);
	public Vocabulary getVocabularybyId(String id);
	public Vocabulary getVocabularybyDate(String lineId);
	public void updateVocabulary(String id, Vocabulary vocabulary);
	public void calNextReviewVoc(Vocabulary card, int quality);
}
