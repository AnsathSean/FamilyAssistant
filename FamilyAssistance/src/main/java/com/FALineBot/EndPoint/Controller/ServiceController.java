package com.FALineBot.EndPoint.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.FALineBot.EndPoint.Model.Bento;
import com.FALineBot.EndPoint.Model.Cook;
import com.FALineBot.EndPoint.Service.CookingService;
import com.FALineBot.EndPoint.Service.SmokeService;


@RequestMapping("/service")
@RestController
public class ServiceController {

	@Autowired
	private CookingService cookingService;
	
	//@Autowired
	//private SmokeService smokeservice;
	
	@GetMapping("/ShowCookingList/{wisher}")
    public List<Cook> ShowCookingList(@PathVariable String wisher,Model model) {
		System.out.println("wisher: "+wisher);
        List<Cook> cookingList = cookingService.getCookingList(wisher);

        return cookingList; // 返回HTML模板的名稱
    }
	
	@GetMapping("/BentoInfo/{wisher}/{dateString}")
	public ResponseEntity<?> ShowBentoInfo(@PathVariable String wisher, @PathVariable String dateString, Model model) {
	    try {
	        Bento bento = cookingService.getBentoInfo(dateString, wisher);
	        if (bento == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Bento found for the provided wisher and dateString.");
	        }
	        return ResponseEntity.ok(bento);
	    } catch (Exception e) {
	        // Log the error
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request: " + e.getMessage());
	    }
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
