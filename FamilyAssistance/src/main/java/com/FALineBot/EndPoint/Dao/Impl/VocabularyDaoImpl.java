package com.FALineBot.EndPoint.Dao.Impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.FALineBot.EndPoint.Dao.VocabularyDao;
import com.FALineBot.EndPoint.Model.Vocabulary;

@Component
public class VocabularyDaoImpl implements VocabularyDao {
    private RestTemplate restTemplate = new RestTemplate();
    private String DEFINITION_URL = "https://api.wordnik.com/v4/word.json/{word}/definitions";
    private String EXAMPLE_URL = "https://api.wordnik.com/v4/word.json/{word}/examples";
    private String API_KEY = "jqijpq8io3sqdk7c7lt5t0hv3dp4tywufxd36upsgy19odx44";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Vocabulary getDefinitions(String word,String LineId) {
        Vocabulary voc = new Vocabulary();
        voc.setWord(word);

        try {
            // 構建 Definitions API URL
            String definitionUrl = UriComponentsBuilder.fromUriString(DEFINITION_URL)
                    .queryParam("limit", 10) // 限制返回的定義數量
                    .queryParam("api_key", API_KEY)
                    .build(word)
                    .toString();

            // 使用 RestTemplate 發送 GET definition 的請求
            List<Map<String, Object>> definitionResponse = restTemplate.getForObject(definitionUrl, List.class);

            // 處理 Definitions
            List<String> definitions = definitionResponse.stream()
                    .map(entry -> (String) entry.get("text")) // 取得 "text" 作為定義
                    .filter(Objects::nonNull) // 避免空值
                    .map(text -> text.replaceAll("<[^>]+>", "")) // 去除所有 HTML 標籤
                    .limit(3) // 取最多3個定義
                    .collect(Collectors.toList());

            // 處理 partOfSpeech
            List<String> partOfSpeeches = definitionResponse.stream()
                    .map(entry -> (String) entry.get("partOfSpeech")) // 取得 "partOfSpeech"
                    .filter(Objects::nonNull) // 避免空值
                    .map(text -> text.replaceAll("<[^>]+>", "")) // 去除所有 HTML 標籤
                    .distinct() // 確保不重複
                    .limit(3) // 取最多3個定義
                    .collect(Collectors.toList());

            // 構建 Examples API URL
            String exampleUrl = UriComponentsBuilder.fromUriString(EXAMPLE_URL)
                    .queryParam("limit", 5) // 限制返回的例子數量
                    .queryParam("includeDuplicates", false)
                    .queryParam("useCanonical", false)
                    .queryParam("api_key", API_KEY)
                    .build(word)
                    .toString();

            // 使用 RestTemplate 發送 GET examples 的請求
            Map<String, Object> exampleResponse = restTemplate.getForObject(exampleUrl, Map.class);

            // 處理 Examples
            List<String> exampleSentences = ((List<Map<String, Object>>) exampleResponse.get("examples")).stream()
                    .map(entry -> (String) entry.get("text")) // 取得 "text" 作為例子
                    .filter(Objects::nonNull) // 避免空值
                    .map(text -> text.replaceAll("<[^>]+>", "")) // 去除所有 HTML 標籤
                    .limit(3) // 取最多3個例子
                    .collect(Collectors.toList());

            // 設置 Vocabulary 屬性
            voc.setDefinition(definitions);
            voc.setExampleSentence(exampleSentences);
            voc.setPartOfSpeech(partOfSpeeches);
            voc.setMinLimit(false);
            voc.setHourLimit(false);

            // 儲存到 TempVocabulary 並取得生成的 ID
            int generatedId = saveToTempVocabulary(voc,LineId);
            voc.setId(generatedId);

        } catch (HttpClientErrorException e) {
            // 捕捉限流錯誤
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                org.springframework.http.HttpHeaders headers = e.getResponseHeaders();
                if (headers != null) {
                    String remainingMinute = headers.getFirst("X-RateLimit-Remaining-Minute");
                    String remainingHour = headers.getFirst("X-RateLimit-Remaining-Hour");

                    if ("0".equals(remainingMinute)) {
                        voc.setMinLimit(true);
                    } else {
                        voc.setMinLimit(false);
                    }

                    if ("0".equals(remainingHour)) {
                        voc.setHourLimit(true);
                    } else {
                        voc.setHourLimit(false);
                    }
                }
            } else {
                throw e; // 如果不是限流錯誤，繼續丟出異常
            }
        }

        return voc;
    }

    private int saveToTempVocabulary(Vocabulary voc, String LineId) {
        String definitions = String.join(",", voc.getDefinition());
        String examples = String.join(",", voc.getExampleSentence());
        String partOfSpeeches = String.join(",", voc.getPartOfSpeech());

        String sql = "INSERT INTO TempVocabulary (line_id, word, definition, example_sentence, part_of_speech) " +
                     "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, LineId, voc.getWord(), definitions, examples, partOfSpeeches);

        // 取得自動生成的 ID
        String idQuery = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(idQuery, Integer.class);
    }
}
