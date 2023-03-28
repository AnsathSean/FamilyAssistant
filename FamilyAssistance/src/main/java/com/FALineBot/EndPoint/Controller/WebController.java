package com.FALineBot.EndPoint.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.FALineBot.EndPoint.Model.WishList;
import com.FALineBot.EndPoint.Service.ReplyMessageService;
import com.FALineBot.EndPoint.Service.WishListService;

@Controller
public class WebController {
	
	@Autowired
	private WishListService wishListService;
	
	@GetMapping("/MyWishlist/{wisher}")
    public String MyWishList(@PathVariable String wisher,Model model) {
    	List<WishList> list = wishListService.findAllWishListByPersion(wisher);
    	System.out.println("List: "+list.toString());
    	//List<WishList> MyWishList = new ArrayList<WishList>();
    	//Integer order = 0;
    	//for (WishList e : list) {
    		
    		//if(e.wisher.equals(wisher)) {
				//order = order+1;
				//e.order = order;
				//MyWishList.add(order,e);
    	//}
    		
    	//}
    	model.addAttribute("wishList", list);
        return "MyWishList";
    }
	
	@GetMapping("/Hello")
    public String Hello(Model model) {

    	model.addAttribute("hello", "我在這裡唷");
        return "model";
    }
}