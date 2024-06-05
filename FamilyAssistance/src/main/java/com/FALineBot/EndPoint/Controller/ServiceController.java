package com.FALineBot.EndPoint.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.FALineBot.EndPoint.Model.Cook;
import com.FALineBot.EndPoint.Service.CookingService;
import com.FALineBot.EndPoint.Service.SmokeService;


@RequestMapping("/service")
@RestController
public class ServiceController {

	@Autowired
	private CookingService cookingService;
	
	@Autowired
	private SmokeService smokeservice;
	
	@GetMapping("/ShowCookingList/{wisher}")
    public List<Cook> ShowCookingList(@PathVariable String wisher,Model model) {
		System.out.println("wisher: "+wisher);
        List<Cook> cookingList = cookingService.getCookingList(wisher);

        return cookingList; // 返回HTML模板的名稱
    }
	
	//@GetMapping("/RecordSmoke")
	//public void RecordSmoke() {
	//	smokeservice.RecordSmokeTime("123");
	//}
	
	//@GetMapping("/ReplySmoke")
	//public void ReplySmoke() {
	//	smokeservice.ReplySmokeTime("123");
	//}
}
