package com.FALineBot.EndPoint.Dao.Impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.FALineBot.EndPoint.Dao.VocabularyDao;
import com.FALineBot.EndPoint.Model.Vocabulary;

@Component
public class VocabularyDaoImpl implements VocabularyDao{
	private RestTemplate restTemplate = new RestTemplate();
	private String DEFINITION_URL = "https://api.wordnik.com/v4/word.json/{word}/definitions";
	private String EXAMPLE_URL = "https://api.wordnik.com/v4/word.json/{word}/examples";
    private String API_KEY = "jqijpq8io3sqdk7c7lt5t0hv3dp4tywufxd36upsgy19odx44"; 

    public Vocabulary getDefinitions(String word) {
        // 構建 API URL
        String url = UriComponentsBuilder.fromUriString(DEFINITION_URL)
                .queryParam("limit", 10) // 限制返回的定義數量
                .queryParam("api_key", API_KEY)
                .build(word)
                .toString();    
        
        
        // 使用 RestTemplate 發送 GET definition的請求
        List<Map<String, Object>> response  = restTemplate.getForObject(url, List.class);
        
        if (response == null || response.isEmpty()) {
            // 如果沒有回應，返回空結果
            return new Vocabulary();
        }
        
        // 提取詞性與定義
        List<String> partOfSpeech = response.stream()
                .map(entry -> (String) entry.get("partOfSpeech")) // 取得 "partOfSpeech"
                .filter(Objects::nonNull) // 避免空值
                .distinct() // 去重
                .collect(Collectors.toList());

        List<String> definitions = response.stream()
                .map(entry -> (String) entry.get("text")) // 取得 "text" 作為定義
                .filter(Objects::nonNull) // 避免空值
                .limit(3) // 取最多3個定義
                .collect(Collectors.toList());
        
        Vocabulary voc = new Vocabulary();
        voc.setWord(word);
        voc.setPartOfSpeech(partOfSpeech);
        voc.setDefinition(definitions);
        
        
    	return voc;
    }
}
