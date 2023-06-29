package com.devrun.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@Api(tags = "Devrun")
public class IndexController {

	@GetMapping("/react1")
	@ResponseBody
	@ApiOperation("Get a greeting message")
	public String home() {

		return "react 환영합니다.";

	}

}