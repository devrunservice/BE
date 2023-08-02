package com.devrun.controller;

import com.devrun.dto.CartDTO;
import com.devrun.entity.Cart;
import com.devrun.entity.Lecture;
import com.devrun.entity.MemberEntity;
import com.devrun.exception.CommonErrorCode;
import com.devrun.exception.RestApiException;
import com.devrun.exception.UserErrorCode;
import com.devrun.repository.CartRepo;
import com.devrun.service.CartService;
import com.devrun.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.*;

@Controller
@Api(tags = "Devrun.Cart")
public class CartController {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CartService cartService;

    @PostMapping("/api/cartinsert")
    @ResponseBody
    @ApiOperation("장바구니에 강의를 추가합니다.")
    @ApiImplicitParam(name = "lno"
            , value = "강의 일련번호"
    )
    public String putCart(@RequestParam(name = "lno") int lno) {

    String msg = cartService.putInCart("title");

        return  msg;
    }

    @PostMapping("/api/cartdelete")
    @ResponseBody
    public String deleteInCart(@RequestParam(name = "lecturetitle") String lecturetitle){

        lecturetitle = "1";
        String msg = cartService.deleteInCart(lecturetitle);

        return msg;
    }

    @GetMapping("/cart")
    @ResponseBody
    public ResponseEntity<?> cartopen(HttpServletRequest request){
         String token = request.getHeader("Access_token");
         if(token == null || token.isEmpty()){
             throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
         }


        CartDTO result = cartService.showCartInfo("seokhwan1");

        return ResponseEntity.ok().body(result);
    }

//    @GetMapping("/users/test")
//    public String getUser() {
//        throw new RestApiException(UserErrorCode.INACTIVE_USER);
//    }

}
