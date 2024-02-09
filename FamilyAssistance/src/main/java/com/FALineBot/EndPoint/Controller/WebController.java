package com.FALineBot.EndPoint.Controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.FALineBot.EndPoint.Model.User;
import com.FALineBot.EndPoint.Model.WishList;
import com.FALineBot.EndPoint.Service.UserManagerService;
import com.FALineBot.EndPoint.Service.WishListService;

@Controller
public class WebController {
	
	@Autowired
	private WishListService wishListService;
	@Autowired
	private UserManagerService userManagerService;
	
	@GetMapping("/MyWishlist/{wisher}")
    public String MyWishList(@PathVariable String wisher,Model model) {
    	List<WishList> list = wishListService.findAllWishListByPersion(wisher);
    	//System.out.println("List: "+list.toString());
    	model.addAttribute("wishList", list);
        return "MyWishList";
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
    
    
	@GetMapping("/Hello")
    public String Hello(Model model) {

    	model.addAttribute("hello", "我在這裡唷");
        return "model";
    }
}