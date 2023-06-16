package com.devrun.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JoinController {
	
	@GetMapping("/signup")
	public String signup() {
		
		return "signup";
	}

}
