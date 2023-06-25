package com.devrun.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.ResponseBody;
=======
>>>>>>> SeongMin
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

	@GetMapping("/react1")
	@ResponseBody
	public String home() {

		return "react 환영합니다.";

	}
	
}
