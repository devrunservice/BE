package com.devrun.controller;

import com.devrun.dto.CartDTO;
import com.devrun.entity.Cart;
import com.devrun.youtube.Lecture;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.*;
//
//@RestController
//@Api(tags = "Devrun.Cart")
//public class CartController {
//    @Autowired
//    private CartService cartService;
//
//
//    @PostMapping("/api/cartinsert")
//    @ApiOperation("장바구니에 강의를 추가합니다. 현재 미완성")
//    @ApiImplicitParam(name = "lno"
//            , value = "강의 일련번호"
//    )
//    public String putCart(@RequestParam(name = "lno") int lno) {
//
//    String msg = cartService.putInCart("title");
//    //강의 기능이 마무리 되고 관련 JPA 코드가 생기면 그때 수정하기
//        return  msg;
//    }
//
//    @PostMapping("/api/cartdelete")
//    @ApiOperation("장바구니에서 강의를 삭제합니다. 현재 미완성")
//    @ApiImplicitParam(name = "lecturetitle"
//            , value = "강의명"
//    )
//    public String deleteInCart(@RequestParam(name = "lecturetitle") String lecturetitle){
//
//        lecturetitle = "1";
//        String msg = cartService.deleteInCart(lecturetitle);
//    //강의 기능이 마무리 되고 관련 JPA 코드가 생기면 그때 수정하기
//        return msg;
//    }
//
//    @GetMapping("/cart")
//    @ApiOperation("장바구니 화면에 출력할 데이터를 전달합니다.")
//    public ResponseEntity<?> cartopen(){
//
//        String userid = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        CartDTO result = cartService.showCartInfo(userid);
//        return ResponseEntity.ok().body(result);
//
//    }
//
////    @GetMapping("/users/test")
////    public String getUser() {
////        throw new RestApiException(UserErrorCode.INACTIVE_USER);
////    }
//
//}
