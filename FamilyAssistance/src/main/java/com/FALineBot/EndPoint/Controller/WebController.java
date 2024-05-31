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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.FALineBot.EndPoint.Model.Cook;
import com.FALineBot.EndPoint.Model.User;
import com.FALineBot.EndPoint.Model.WishList;
import com.FALineBot.EndPoint.Service.CookingService;
import com.FALineBot.EndPoint.Service.UserManagerService;
import com.FALineBot.EndPoint.Service.WishListService;

@Controller
public class WebController {
	
	@Autowired
	private WishListService wishListService;
	@Autowired
	private UserManagerService userManagerService;
	@Autowired
	private CookingService cookingService;
	
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
		//System.out.println("wisher: "+wisher);
        List<Cook> cookingList = cookingService.getCookingList(wisher);

        model.addAttribute("cookingList", cookingList);
        model.addAttribute("wisher", wisher);
        return "ShowCookingList"; // 返回HTML模板的名稱
    }
	
	@GetMapping("/ShowOPCooking/{wisher}")
    public String ShowOPCooking(@PathVariable String wisher,Model model) {
		System.out.println("wisher: "+wisher);
        List<Cook> cookingList = cookingService.getCookingList(wisher);

        model.addAttribute("cookingList", cookingList);
        model.addAttribute("wisher", wisher);
        return "ShowOppsoitCookingList"; // 返回HTML模板的名稱
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
		
		System.out.println("LineID為："+LineID+"," + "Type為："+ type);
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
		System.out.println("UUID:"+uuid);
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

	@GetMapping("/CookModify/{UUID}/{wisher}")
	public String CookModify(@PathVariable String UUID,@PathVariable String wisher,Model model) {
		//System.out.println("UUID:"+UUID);
		//System.out.println("我有先執行這裡");
		Cook cook =cookingService.getCookByUUID(UUID);
		//System.out.println("讀出來的Cook名稱:"+cook.getCookName());
	    Cook cooks = new Cook();
	    //List<Cook> cooks = new ArrayList<>(); // 創建一個空的 Cook 列表，用於存放從表單提交的資料
	    // 將 Cook 對象添加到模型中，名稱為 "cook"
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