package com.FALineBot.EndPoint.Dao.Impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.FALineBot.EndPoint.Model.Page;
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
                    .buildAndExpand(word.trim())
                    .toString();
            
            //System.out.println("word:"+word);
            //System.out.println("Constructed URL: " + definitionUrl);
            // 使用 RestTemplate 發送 GET definition 的請求
            List<Map<String, Object>> definitionResponse;
            try {
             definitionResponse = restTemplate.getForObject(definitionUrl, List.class);
            } catch (HttpClientErrorException e) {
                // 打印錯誤狀態碼和錯誤訊息
                System.err.println("HTTP 錯誤狀態碼: " + e.getStatusCode());
                System.err.println("HTTP 錯誤回應內容: " + e.getResponseBodyAsString());
                System.err.println("完整的錯誤訊息: " + e);

                // 回傳自定義錯誤訊息或處理邏輯
                return null;
            } catch (Exception e) {
                // 捕捉其他未預期的異常
                System.err.println("其他錯誤發生: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
            System.out.println("get the definition");
            // 處理 Definitions
            List<String> definitions = definitionResponse.stream()
                    .map(entry -> (String) entry.get("text")) // 取得 "text" 作為定義
                    .filter(Objects::nonNull) // 避免空值
                    .map(text -> text.replaceAll("<[^>]+>", "")) // 去除所有 HTML 標籤
                    .limit(3) // 取最多3個定義
                    .collect(Collectors.toList());
            //System.out.println("handle the definition");
            // 處理 partOfSpeech
            List<String> partOfSpeeches = definitionResponse.stream()
                    .map(entry -> (String) entry.get("partOfSpeech")) // 取得 "partOfSpeech"
                    .filter(Objects::nonNull) // 避免空值
                    .map(text -> text.replaceAll("<[^>]+>", "")) // 去除所有 HTML 標籤
                    .distinct() // 確保不重複
                    .limit(3) // 取最多3個定義
                    .collect(Collectors.toList());
            //System.out.println("handle the partOfSpeech");
            // 構建 Examples API URL
            String exampleUrl = UriComponentsBuilder.fromUriString(EXAMPLE_URL)
                    .queryParam("limit", 5) // 限制返回的例子數量
                    .queryParam("includeDuplicates", false)
                    .queryParam("useCanonical", false)
                    .queryParam("api_key", API_KEY)
                    .build(word)
                    .toString();

            List<String> exampleSentences = List.of();
            try {
                // 使用 RestTemplate 發送 GET examples 的請求
                Map<String, Object> exampleResponse = restTemplate.getForObject(exampleUrl, Map.class);

                // 處理 Examples
                         exampleSentences = ((List<Map<String, Object>>) exampleResponse.get("examples")).stream()
                        .map(entry -> (String) entry.get("text")) // 取得 "text" 作為例子
                        .filter(Objects::nonNull) // 避免空值
                        .map(text -> text.replaceAll("<[^>]+>", "")) // 去除所有 HTML 標籤
                        .limit(3) // 取最多3個例子
                        .collect(Collectors.toList());

            } catch (Exception e) {
                // 捕捉異常，設置例子為空
                System.err.println("發生錯誤，無法獲取例子: " + e.getMessage());
                
            }

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

    public void saveVocabulary(int id) {
        // 從 TempVocabulary 獲取資料
        String selectSql = "SELECT line_id, word, definition, example_sentence, part_of_speech FROM TempVocabulary WHERE id = ?";
        Map<String, Object> tempData = jdbcTemplate.queryForMap(selectSql, id);

        // 檢查 Vocabulary 中是否已存在該單字
        String checkSql = "SELECT COUNT(*) FROM Vocabulary WHERE word = ? AND line_id = ?";
        @SuppressWarnings("deprecation")
		int count = jdbcTemplate.queryForObject(checkSql, new Object[]{tempData.get("word"), tempData.get("line_id")}, Integer.class);

        if (count > 0) {
            // 如果該單字已存在，跳過儲存
            System.out.println("Word already exists in Vocabulary. Skipping save.");
            return;
        }
        
        // 將 TempVocabulary 的資料存入 Vocabulary
        String insertSql = "INSERT INTO Vocabulary (line_id, word, definition, example_sentence, repetitions, ease_factor, next_review_date, last_review_date, status, created_at, updated_at, part_of_speech) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(insertSql,
                tempData.get("line_id"),
                tempData.get("word"),
                tempData.get("definition"),
                tempData.get("example_sentence"),
                0, // repetitions
                2.5, // ease_factor
                null, // next_review_date
                null, // last_review_date
                "Mid", // status
                new java.sql.Timestamp(System.currentTimeMillis()), // created_at
                new java.sql.Timestamp(System.currentTimeMillis()), // updated_at
                tempData.get("part_of_speech")
        );
    }
    
    private int saveToTempVocabulary(Vocabulary voc, String LineId) {
        String definitions = voc.getDefinition().stream()
                .map(s -> s.replace("@", ""))
                .collect(Collectors.joining("@"));
String examples = voc.getExampleSentence().stream()
              .map(s -> s.replace("@", ""))
              .collect(Collectors.joining("@"));
String partOfSpeeches = voc.getPartOfSpeech().stream()
                    .map(s -> s.replace("@", ""))
                    .collect(Collectors.joining("@"));

        String sql = "INSERT INTO TempVocabulary (line_id, word, definition, example_sentence, part_of_speech) " +
                     "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, LineId, voc.getWord(), definitions, examples, partOfSpeeches);

        // 取得自動生成的 ID
        String idQuery = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(idQuery, Integer.class);
    }

    public void deleteToTempVocabulary(int id) {
        String deleteSql = "DELETE FROM TempVocabulary WHERE id = ?";
        jdbcTemplate.update(deleteSql, id);
    }
    
    public void deleteVocabulary(int id) {
        String deleteSql = "DELETE FROM Vocabulary WHERE id = ?";
        jdbcTemplate.update(deleteSql, id);    	
    }

    @SuppressWarnings("deprecation")
	@Override
    public List<Vocabulary> getVocabularyList(String LineId) {
        String query = "SELECT id, line_id, word, definition, example_sentence, repetitions, ease_factor, " +
                       "next_review_date, last_review_date, status, created_at, updated_at, part_of_speech " +
                       "FROM Vocabulary WHERE line_id = ?";

        return jdbcTemplate.query(query, new Object[]{LineId}, (rs, rowNum) -> {
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setId(rs.getInt("id"));
            vocabulary.setLineId(rs.getString("line_id"));
            vocabulary.setWord(rs.getString("word"));

            // 將以 "@" 分隔的字串轉換成 List<String>
            String definitionStr = rs.getString("definition");
            vocabulary.setDefinition(definitionStr != null ? List.of(definitionStr.split("@")) : null);

            String exampleSentenceStr = rs.getString("example_sentence");
            vocabulary.setExampleSentence(exampleSentenceStr != null ? List.of(exampleSentenceStr.split("@")) : null);

            String partOfSpeechStr = rs.getString("part_of_speech");
            vocabulary.setPartOfSpeech(partOfSpeechStr != null ? List.of(partOfSpeechStr.split("@")) : null);

            vocabulary.setRepetitions(rs.getInt("repetitions"));
            vocabulary.setEaseFactor(rs.getFloat("ease_factor"));
            vocabulary.setNextReviewDate(rs.getDate("next_review_date") != null 
                    ? rs.getDate("next_review_date").toLocalDate() : null);
            vocabulary.setLastReviewDate(rs.getDate("last_review_date") != null 
                    ? rs.getDate("last_review_date").toLocalDate() : null);
            vocabulary.setStatus(rs.getString("status"));

            vocabulary.setCreatedAt(rs.getDate("created_at") != null 
                    ? rs.getDate("created_at").toLocalDate() : null);
            vocabulary.setUpdatedAt(rs.getDate("updated_at") != null 
                    ? rs.getDate("updated_at").toLocalDate() : null);

            return vocabulary;
        });
    }

    @SuppressWarnings("deprecation")
	@Override
    public List<Vocabulary> getVocListPage(String lineId, int page, int pageSize) {
        // 計算 OFFSET，根據頁數計算需要跳過的資料筆數
        int offset = (page - 1) * pageSize;

        // 查詢 SQL，使用 LIMIT 和 OFFSET 進行分頁
        String query = "SELECT id, line_id, word, definition, example_sentence, repetitions, ease_factor, " +
                       "next_review_date, last_review_date, status, created_at, updated_at, part_of_speech " +
                       "FROM Vocabulary WHERE line_id = ? ORDER BY id ASC LIMIT ? OFFSET ?";

        // 使用 JdbcTemplate 查詢並轉換為 List<Vocabulary>
        return jdbcTemplate.query(query, new Object[]{lineId, pageSize, offset}, (rs, rowNum) -> {
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setId(rs.getInt("id"));
            vocabulary.setLineId(rs.getString("line_id"));
            vocabulary.setWord(rs.getString("word"));

            // 將以 "@" 分隔的字串轉換為 List<String>
            String definitionStr = rs.getString("definition");
            vocabulary.setDefinition(definitionStr != null ? List.of(definitionStr.split("@")) : null);

            String exampleSentenceStr = rs.getString("example_sentence");
            vocabulary.setExampleSentence(exampleSentenceStr != null ? List.of(exampleSentenceStr.split("@")) : null);

            String partOfSpeechStr = rs.getString("part_of_speech");
            vocabulary.setPartOfSpeech(partOfSpeechStr != null ? List.of(partOfSpeechStr.split("@")) : null);

            vocabulary.setRepetitions(rs.getInt("repetitions"));
            vocabulary.setEaseFactor(rs.getFloat("ease_factor"));
            vocabulary.setNextReviewDate(rs.getDate("next_review_date") != null 
                    ? rs.getDate("next_review_date").toLocalDate() : null);
            vocabulary.setLastReviewDate(rs.getDate("last_review_date") != null 
                    ? rs.getDate("last_review_date").toLocalDate() : null);
            vocabulary.setStatus(rs.getString("status"));

            vocabulary.setCreatedAt(rs.getDate("created_at") != null 
                    ? rs.getDate("created_at").toLocalDate() : null);
            vocabulary.setUpdatedAt(rs.getDate("updated_at") != null 
                    ? rs.getDate("updated_at").toLocalDate() : null);
            
            //System.out.println("id:"+rs.getInt("id")+";int:"+page+";pageSize:"+pageSize);
            
            return vocabulary;
        });
    }

    public Page getPageProperties(String lineId, int pageSize, int currentPage) {
        // 查詢單字總數
        String countQuery = "SELECT COUNT(*) FROM Vocabulary WHERE line_id = ?";
        @SuppressWarnings("deprecation")
		int totalCount = jdbcTemplate.queryForObject(countQuery, new Object[]{lineId}, Integer.class);

        // 計算總頁數，若有小數點則向上取整
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        // 判斷是否有下一頁和上一頁
        boolean hasNext = currentPage < totalPages; // 當前頁小於總頁數時有下一頁
        boolean hasBefore = currentPage > 1;       // 當前頁大於 1 時有上一頁

        // 創建 Page 對象並設置屬性
        Page page = new Page();
        page.setCurrentPage(currentPage);
        page.setTotalPages(totalPages);
        page.setHasNext(hasNext);
        page.setHasBeofre(hasBefore); // 注意拼寫錯誤，應改為 setHasBefore

        return page;
    }

    public List<Vocabulary> getVocabularyListbySearch(String lineId, String keyWords) {
        String query = "SELECT id, line_id, word, definition, example_sentence, repetitions, ease_factor, " +
                       "next_review_date, last_review_date, status, created_at, updated_at, part_of_speech " +
                       "FROM Vocabulary WHERE line_id = ? AND word LIKE ?";

        // 使用 '%' 包裹關鍵字以進行模糊比對
        @SuppressWarnings("deprecation")
		List<Vocabulary> result = jdbcTemplate.query(query, new Object[]{lineId, "%" + keyWords + "%"}, (rs, rowNum) -> {
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setId(rs.getInt("id"));
            vocabulary.setLineId(rs.getString("line_id"));
            vocabulary.setWord(rs.getString("word"));

            // 將以 "@" 分隔的字串轉換為 List<String>
            String definitionStr = rs.getString("definition");
            vocabulary.setDefinition(definitionStr != null ? List.of(definitionStr.split("@")) : null);

            String exampleSentenceStr = rs.getString("example_sentence");
            vocabulary.setExampleSentence(exampleSentenceStr != null ? List.of(exampleSentenceStr.split("@")) : null);

            String partOfSpeechStr = rs.getString("part_of_speech");
            vocabulary.setPartOfSpeech(partOfSpeechStr != null ? List.of(partOfSpeechStr.split("@")) : null);

            vocabulary.setRepetitions(rs.getInt("repetitions"));
            vocabulary.setEaseFactor(rs.getFloat("ease_factor"));
            vocabulary.setNextReviewDate(rs.getDate("next_review_date") != null 
                    ? rs.getDate("next_review_date").toLocalDate() : null);
            vocabulary.setLastReviewDate(rs.getDate("last_review_date") != null 
                    ? rs.getDate("last_review_date").toLocalDate() : null);
            vocabulary.setStatus(rs.getString("status"));

            vocabulary.setCreatedAt(rs.getDate("created_at") != null 
                    ? rs.getDate("created_at").toLocalDate() : null);
            vocabulary.setUpdatedAt(rs.getDate("updated_at") != null 
                    ? rs.getDate("updated_at").toLocalDate() : null);

            return vocabulary;
        });

        // 若結果為空則回傳 null
        return result.isEmpty() ? null : result;
    }

    @SuppressWarnings("deprecation")
	public Vocabulary getVocabularybyId(String id) {
        String query = "SELECT id, line_id, word, definition, example_sentence, repetitions, ease_factor, " +
                       "next_review_date, last_review_date, status, created_at, updated_at, part_of_speech " +
                       "FROM Vocabulary WHERE id = ?";

        return jdbcTemplate.queryForObject(query, new Object[]{id}, (rs, rowNum) -> {
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setId(rs.getInt("id"));
            vocabulary.setLineId(rs.getString("line_id"));
            vocabulary.setWord(rs.getString("word"));

            // 將以 "@" 分隔的字串轉換為 List<String>
            String definitionStr = rs.getString("definition");
            vocabulary.setDefinition(definitionStr != null ? List.of(definitionStr.split("@")) : null);

            String exampleSentenceStr = rs.getString("example_sentence");
            vocabulary.setExampleSentence(exampleSentenceStr != null ? List.of(exampleSentenceStr.split("@")) : null);

            String partOfSpeechStr = rs.getString("part_of_speech");
            vocabulary.setPartOfSpeech(partOfSpeechStr != null ? List.of(partOfSpeechStr.split("@")) : null);

            vocabulary.setRepetitions(rs.getInt("repetitions"));
            vocabulary.setEaseFactor(rs.getFloat("ease_factor"));
            vocabulary.setNextReviewDate(rs.getDate("next_review_date") != null 
                    ? rs.getDate("next_review_date").toLocalDate() : null);
            vocabulary.setLastReviewDate(rs.getDate("last_review_date") != null 
                    ? rs.getDate("last_review_date").toLocalDate() : null);
            vocabulary.setStatus(rs.getString("status"));

            vocabulary.setCreatedAt(rs.getDate("created_at") != null 
                    ? rs.getDate("created_at").toLocalDate() : null);
            vocabulary.setUpdatedAt(rs.getDate("updated_at") != null 
                    ? rs.getDate("updated_at").toLocalDate() : null);

            return vocabulary;
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public void updateVocabulary(String id, Vocabulary vocabulary) {
        // 建構動態 SQL 查詢
        StringBuilder queryBuilder = new StringBuilder("UPDATE Vocabulary SET ");
        List<Object> params = new ArrayList<>();

        if (vocabulary.getWord() != null) {
            queryBuilder.append("word = ?, ");
            params.add(vocabulary.getWord());
        }

        if (vocabulary.getDefinition() != null) {
            String definitionStr = String.join("@", vocabulary.getDefinition());
            queryBuilder.append("definition = ?, ");
            params.add(definitionStr);
        }

        if (vocabulary.getExampleSentence() != null) {
            String exampleSentenceStr = String.join("@", vocabulary.getExampleSentence());
            queryBuilder.append("example_sentence = ?, ");
            params.add(exampleSentenceStr);
        }

        if (vocabulary.getRepetitions() != null) {
            queryBuilder.append("repetitions = ?, ");
            params.add(vocabulary.getRepetitions());
        }

        if (vocabulary.getEaseFactor() != null) {
            queryBuilder.append("ease_factor = ?, ");
            params.add(vocabulary.getEaseFactor());
        }

        if (vocabulary.getNextReviewDate() != null) {
            queryBuilder.append("next_review_date = ?, ");
            params.add(LocalDate.parse(vocabulary.getNextReviewDate().toString()));
        }

        if (vocabulary.getLastReviewDate() != null) {
            queryBuilder.append("last_review_date = ?, ");
            params.add(LocalDate.parse(vocabulary.getLastReviewDate().toString()));
        }

        if (vocabulary.getStatus() != null) {
            queryBuilder.append("status = ?, ");
            params.add(vocabulary.getStatus());
        }

        // 永遠更新 updated_at
        queryBuilder.append("updated_at = ?, ");
        params.add(LocalDateTime.now());

        if (vocabulary.getPartOfSpeech() != null) {
            String partOfSpeechStr = String.join("@", vocabulary.getPartOfSpeech());
            queryBuilder.append("part_of_speech = ?, ");
            params.add(partOfSpeechStr);
        }

        // 移除最後的逗號和空格，並加上 WHERE 子句
        queryBuilder.setLength(queryBuilder.length() - 2); // 移除最後的 ", "
        queryBuilder.append(" WHERE id = ?");
        params.add(id);

        // 執行更新
        jdbcTemplate.update(queryBuilder.toString(), params.toArray());
    }

    @SuppressWarnings("deprecation")
	@Override
    public Vocabulary getVocabularybyDate(String lineId) {
        String query = "SELECT id, line_id, word, definition, example_sentence, repetitions, ease_factor, " +
                       "next_review_date, last_review_date, status, created_at, updated_at, part_of_speech " +
                       "FROM Vocabulary WHERE line_id = ? AND (next_review_date IS NULL OR next_review_date <= CURRENT_DATE()) " +
                       "ORDER BY next_review_date ASC LIMIT 1";

        try {
            return jdbcTemplate.queryForObject(query, new Object[]{lineId}, (rs, rowNum) -> {
                Vocabulary vocabulary = new Vocabulary();
                vocabulary.setId(rs.getInt("id"));
                vocabulary.setLineId(rs.getString("line_id"));
                vocabulary.setWord(rs.getString("word"));

                // 處理定義、例句、詞性，將"@"分隔的字串轉為List<String>
                String definitionStr = rs.getString("definition");
                vocabulary.setDefinition(definitionStr != null ? List.of(definitionStr.split("@")) : null);

                String exampleSentenceStr = rs.getString("example_sentence");
                vocabulary.setExampleSentence(exampleSentenceStr != null ? List.of(exampleSentenceStr.split("@")) : null);

                String partOfSpeechStr = rs.getString("part_of_speech");
                vocabulary.setPartOfSpeech(partOfSpeechStr != null ? List.of(partOfSpeechStr.split("@")) : null);

                vocabulary.setRepetitions(rs.getInt("repetitions"));
                vocabulary.setEaseFactor(rs.getFloat("ease_factor"));
                vocabulary.setNextReviewDate(rs.getDate("next_review_date") != null
                        ? rs.getDate("next_review_date").toLocalDate() : null);
                vocabulary.setLastReviewDate(rs.getDate("last_review_date") != null
                        ? rs.getDate("last_review_date").toLocalDate() : null);
                vocabulary.setStatus(rs.getString("status"));

                vocabulary.setCreatedAt(rs.getDate("created_at") != null
                        ? rs.getDate("created_at").toLocalDate() : null);
                vocabulary.setUpdatedAt(rs.getDate("updated_at") != null
                        ? rs.getDate("updated_at").toLocalDate() : null);

                return vocabulary;
            });
        } catch (Exception e) {
            // 如果找不到符合條件的資料，回傳 null
            return null;
        }
    }







}
