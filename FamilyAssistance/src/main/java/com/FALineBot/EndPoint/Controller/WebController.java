package com.FALineBot.EndPoint.Controller;


import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.FALineBot.EndPoint.Model.Bento;
import com.FALineBot.EndPoint.Model.Cook;
import com.FALineBot.EndPoint.Model.CookInfo;
import com.FALineBot.EndPoint.Model.Page;
import com.FALineBot.EndPoint.Model.User;
import com.FALineBot.EndPoint.Model.Vocabulary;
import com.FALineBot.EndPoint.Model.WishList;
import com.FALineBot.EndPoint.Service.CookingService;
import com.FALineBot.EndPoint.Service.UserManagerService;
import com.FALineBot.EndPoint.Service.VocabularyService;
import com.FALineBot.EndPoint.Service.WishListService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class WebController {
	
	@Autowired
	private WishListService wishListService;
	@Autowired
	private UserManagerService userManagerService;
	@Autowired
	private CookingService cookingService;
	@Autowired
	private VocabularyService vocabularyService;
	
	@GetMapping("/MyWishlist/{wisher}")
    public String MyWishList(@PathVariable String wisher,Model model) {
    	List<WishList> list = wishListService.findAllWishListByPersion(wisher);
    	//System.out.println("List: "+list.toString());
    	model.addAttribute("wishList", list);
        return "MyWishList";
    }
	
	@GetMapping("/AddCooking/{wisher}")
    public String AddCooking(@PathVariable String wisher,Model model) {
	    List<Cook> cooks = new ArrayList<>(); // 創建一個空的 Cook 列表，用於存放從表單提交的資料

	    // 在此處添加其他需要的數據，例如早餐、午餐、晚餐的選項
	    List<String> mealOptions = Arrays.asList("主菜", "配菜", "小菜");

	    model.addAttribute("cooks", cooks); // 將 Cook 列表添加到模型中，以便在HTML中使用
	    model.addAttribute("wisher", wisher); // 將 wisher 添加到模型中，以便在HTML中使用
	    return "TestAddCook"; // 返回HTML模板的名稱
    }
	
	@GetMapping("/ShowCooking/{wisher}")
    public String ShowCooking(@PathVariable String wisher,Model model) {
        String title = "Me";
        model.addAttribute("wisher", wisher);
        model.addAttribute("title",title);
        return "ShowCookingList"; // 返回HTML模板的名稱
    }
	
	@GetMapping("/ShowVocabulary/{user}")
	public String ShowVocabulary(@PathVariable String user, Model model) {
	    String title = "Vocabulary List";
	  //System.out.println("User: "+user);
	    List<Vocabulary> vocList = vocabularyService.getVocabularyList(user);
	    // 計算 Hard, Mid, Easy 的數量
	    long hardCount = vocList.stream().filter(v -> "Hard".equalsIgnoreCase(v.getStatus())).count();
	    long midCount = vocList.stream().filter(v -> "Mid".equalsIgnoreCase(v.getStatus())).count();
	    long easyCount = vocList.stream().filter(v -> "Easy".equalsIgnoreCase(v.getStatus())).count();
	    long totalCount = vocList.size();

	    // 傳遞資料到前端
	    model.addAttribute("user", user);
	    model.addAttribute("title", title);
	    model.addAttribute("hardCount", hardCount);
	    model.addAttribute("midCount", midCount);
	    model.addAttribute("easyCount", easyCount);
	    model.addAttribute("totalCount", totalCount);
	    return "VocabularyTotal"; // 返回對應的 HTML 模板名稱
	}
	
	@GetMapping("/VocabularyList/{user}/{page}")
	public String VocabularyList(@PathVariable String user,@PathVariable int page, Model model) throws JsonProcessingException {
	   //String title = "Vocabulary List";
	  //System.out.println("User: "+user);
	    List<Vocabulary> vocList = vocabularyService.getVocListPage(user, page, 10);
	    Page currentPageProp = vocabularyService.getPageProperties(user, 10, page);
	    // 將資料封裝為 Map
	    Map<Integer, String> vocDictionary = vocList.stream()
	            .collect(Collectors.toMap(Vocabulary::getId, Vocabulary::getWord));

	    // 將封裝的字典傳遞到前端
	    model.addAttribute("user", user);
	    model.addAttribute("page",page);
	   // model.addAttribute(currentPageProp.getTotalPages());
	    model.addAttribute("HasSearch",false);
	    model.addAttribute("HasBefore",currentPageProp.isHasBeofre());
	    model.addAttribute("HasNext",currentPageProp.isHasNext());
	    model.addAttribute("vocDictionary", vocDictionary);
	
	    
	    return "VoacbularyList"; // 返回對應的 HTML 模板名稱
	}	
	
	@GetMapping("/VocabularySearch/{user}")
	public String VocabularySearch(@PathVariable String user,Model model) {
		
		model.addAttribute("user", user);
		model.addAttribute("page",1);
		model.addAttribute("HasSearch",true);
	    model.addAttribute("HasBefore",false);
	    model.addAttribute("DoSearch", false);
	    model.addAttribute("HasNext",false);
	    model.addAttribute("vocDictionary", null);
		return "VoacbularyList"; // 返回對應的 HTML 模板名稱
	}
	
	@GetMapping("/DoVocabularySearch/{lineId}")
	public String VocabularySearch(@PathVariable String lineId, @RequestParam String keyWords, Model model) {
	    // 搜尋單字資料
	    List<Vocabulary> vocList = vocabularyService.getVocabularyListbySearch(lineId, keyWords);
	    //System.out.println("我有執行搜尋");
	    System.out.println("KeyWord:"+keyWords);
	    // 印出 vocList 的資料內容
	    //if (vocList != null && !vocList.isEmpty()) {
	    //    System.out.println("搜尋結果:");
	    //    vocList.forEach(voc -> {
	    //        System.out.println("ID: " + voc.getId());
	    //        System.out.println("Word: " + voc.getWord());
	    //        System.out.println("Definitions: " + voc.getDefinition());
	    //        System.out.println("Examples: " + voc.getExampleSentence());
	    //        System.out.println("Part of Speech: " + voc.getPartOfSpeech());
	    //        System.out.println("-------------");
	    //    });
	    //} else {
	    //    System.out.println("搜尋結果為空");
	    //}
	    
	    
	    // 判斷是否有搜尋結果
	    boolean hasResult = vocList != null && !vocList.isEmpty();

	    // 將搜尋結果封裝為 Map
	    Map<Integer, String> vocDictionary = hasResult
	            ? vocList.stream().collect(Collectors.toMap(Vocabulary::getId, Vocabulary::getWord))
	            : null;

	    // 設定模型屬性
	    model.addAttribute("user", lineId);
	    model.addAttribute("page", 1); // 假設當前頁數固定為 1
	    model.addAttribute("HasSearch", true);
	    model.addAttribute("DoSearch", true);
	    model.addAttribute("HasBefore", false); // 搜尋結果不需要上一頁邏輯
	    model.addAttribute("HasNext", hasResult && vocList.size() > 10); // 如果清單數量大於10則設置為 true
	    model.addAttribute("vocDictionary", vocDictionary); // 將 Map 傳遞到前端

	    return "VoacbularyList"; // 返回對應的 HTML 模板名稱
	}


	
	@GetMapping("/ShowOPCooking/{wisher}")
    public String ShowOPCooking(@PathVariable String wisher,Model model) {
		//System.out.println("wisher: "+wisher);
        String title = "OP";
        model.addAttribute("wisher", wisher);
        model.addAttribute("title",title);
        return "ShowCookingList"; // 返回HTML模板的名稱
    }
	
	@GetMapping("/RatingCook/{wisher}/{dateString}")
    public String RatingCook(@PathVariable String wisher,@PathVariable String dateString,Model model) {
		//System.out.println("wisher: "+wisher);
        String title = "OP";
        model.addAttribute("wisher", wisher);
        model.addAttribute("dateString",dateString);
        model.addAttribute("title",title);
        return "CookRating"; // 返回HTML模板的名稱
    }
	
	@GetMapping("/CheckBento/{wisher}/{dateString}")
    public String CheckBento(@PathVariable String wisher,@PathVariable String dateString,Model model) {
		//System.out.println("wisher: "+wisher);
        String title = "OP";
        model.addAttribute("wisher", wisher);
        model.addAttribute("dateString",dateString);
        model.addAttribute("title",title);
        return "CheckBento"; // 返回HTML模板的名稱
    }
	
	@GetMapping("/UploadCook/{wisher}/{dateString}")
    public String UpdateCook(@PathVariable String wisher,@PathVariable String dateString,Model model) {
        model.addAttribute("wisher", wisher);
        model.addAttribute("dateString",dateString);

        return "UploadCook"; // 返回HTML模板的名稱
    }
	
	
	@PostMapping("/updateCook/{cookID}")
	public String updateCook(@PathVariable String cookID, @RequestParam(name = "CookName", required = false, defaultValue = "") String CookName,
            @RequestParam(name = "CookDate", required = false, defaultValue = "1990/12/14") Date cookDate,
            @RequestParam(name = "CookTime", required = false, defaultValue = "12:00:00") Time cookTime,
            @RequestParam(name = "lineID", required = false, defaultValue = "") String LineID,
            @RequestParam(name = "Type", required = false, defaultValue = "") String type) throws ParseException {
		//System.out.println("CookID為："+cookID);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date defaultDate = formatter.parse("1990/12/14");
		
		SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm:ss");
		Time defaultTime = new Time(formatter1.parse("12:00:00").getTime());
		
		//System.out.println("LineID為："+LineID+"," + "Type為："+ type);
	    Cook cook = new Cook();
	    cook.setUUID(cookID);
	    cook.setLineID(LineID);
	    if (!CookName.equals("")) {
	        cook.setCookName(CookName);
	    }
	    if(!cookDate.equals(defaultDate)) {
	    cook.setCookDate(cookDate);
	    }
	    if(!cookTime.equals(defaultTime)) {
	    cook.setCookTime(cookTime);
	    }
	    if (!type.equals("")) {
	        cook.setType(type);
	    }

	    cookingService.updateCook(cook);
	    return "redirect:/success";
	}
	@PostMapping("/deleteCook/{uuid}")
	public String deleteCook(@PathVariable String uuid) {
		//System.out.println("UUID:"+uuid);
		cookingService.deleteCook(uuid);
		return "redirect:/success";
	}
	
	
	@PostMapping("/submitCooking/{wisher}")
	public String submitCooking(@PathVariable String wisher,@RequestParam("cookNames") String[] cookNames, @RequestParam("types") String[] types, @RequestParam("lineID") String lineID) {
	    List<Cook> cooks = new ArrayList<>();
	    LocalDate currentDate = LocalDate.now();
	    LocalTime currentTime = LocalTime.now();
	    // 遍历cookNames和types数组，根据索引将每个菜名和类型创建为Cook对象，并添加到cooks列表中
	    for (int i = 0; i < cookNames.length; i++) {
	        Cook cook = new Cook();
	        cook.setCookName(cookNames[i]);
	        cook.setType(types[i]);
	        // 将LocalDate转换为java.util.Date，并设置到Cook对象的CookDate字段中
	        Date date = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	        cook.setCookDate(date);
	        // 将LocalTime转换为java.sql.Time，并设置到Cook对象的CookTime字段中
	        Time time = Time.valueOf(currentTime);
	        cook.setCookTime(time);
	        cooks.add(cook);
	    }
	    //System.out.println("this is submit function,wisher:"+wisher+" LineID:"+lineID);
	    // 将Cook对象列表传递给cookingService进行处理
	    cookingService.addCookList(cooks, wisher);
	    return "redirect:/success"; // 重定向到成功頁面
	}
	
	@PostMapping("/AddBento/{wisher}")
	public String AddBento(@RequestParam("lineID") String wisher,
			@RequestParam("bentoID") String bentoID,
			@RequestParam("bentoComment") String bentoComment,
			@RequestParam("bentoRate") String bentoRate,
			@RequestParam("cookInfo") String cookInfo) {
		//System.out.println("獲得的資料"+bentoString);
		//System.out.println("LineID: "+wisher+" bentoID: "+ bentoID);
		//System.out.println("bentoComment: "+bentoComment+" bentoRate: "+ bentoRate);
		//System.out.println("cookInfo: "+cookInfo);
		try {
            // 创建 ObjectMapper 实例
            ObjectMapper objectMapper = new ObjectMapper();

            // 将 cookInfo 字符串转换为简单的 CookInfo 对象数组
            CookInfo[] cookInfos = objectMapper.readValue(cookInfo, CookInfo[].class);

            // 创建 Bento 对象并设置属性
            Bento bento = new Bento();
            bento.setWisher(wisher);
            bento.setBentoID(bentoID);
            bento.setComment(bentoComment);
            bento.setBentoRate(Integer.parseInt(bentoRate)); // 确保 bentoRate 是整数类型

            // 重组 cooks 列表
            List<Cook> cooks = new ArrayList<>();
            for (CookInfo info : cookInfos) {
                Cook cook = new Cook();
                cook.setUUID(info.getUuid());
                cook.setRate(Integer.parseInt(info.getRate())); // 确保 rate 是整数类型
                cook.setLineID(wisher); // 使用 wisher 作为 lineID
                cook.setType("都可");
                cook.setCookName("我不想知道");
                // 其他字段可以设置为默认值或null
                cook.setCookDate(null);
                cook.setCookTime(null);
                cooks.add(cook);
            }

            bento.setCooks(cooks);

            // 打印转换后的 Bento 对象
            //String bentoJson = objectMapper.writeValueAsString(bento);
            //System.out.println("bento: " + bentoJson);

            // 使用服务处理 Bento 对象
            cookingService.addBento(bento);

        } catch (Exception e) {
            // 记录错误
            e.printStackTrace();
            return "redirect:/error"; // 重定向到错误页面
        }

        return "redirect:/success"; // 重定向到成功页面
    }

	
	

	@GetMapping("/CookModify/{UUID}/{wisher}")
	public String CookModify(@PathVariable String UUID,@PathVariable String wisher,Model model) {
		//System.out.println("UUID:"+UUID);
		//System.out.println("我有先執行這裡");
		Cook cook =cookingService.getCookByUUID(UUID);
		//System.out.println("讀出來的Cook名稱:"+cook.getCookName());
	    Cook cooks = new Cook();
	    model.addAttribute("cook", cooks);
		model.addAttribute("items", cook);
		return "ModifyCook";
	}
	
    @GetMapping("/Commodity/{id}")
    public String wishlist(@PathVariable Integer id,Model model) {
    	List<WishList> items = wishListService.findWishListByID(id);
        model.addAttribute("items", items);
        return "Commodity";
    }
    
    @GetMapping("UserManager/{LineID}")
    public String getUserInformation(@PathVariable String LineID,Model model) {
    	User items = userManagerService.getUserById(LineID);
    	model.addAttribute("items", items);
    	return "UserInformation";
    }
    
    @GetMapping("/success")
    public String showSuccessPage() {
        return "success"; // 返回 success.html 文件的名稱，Spring 將根據視圖解析器查找對應的視圖文件
    }
	@GetMapping("/Hello")
    public String Hello(Model model) {

    	model.addAttribute("hello", "我在這裡唷");
        return "model";
    }
}