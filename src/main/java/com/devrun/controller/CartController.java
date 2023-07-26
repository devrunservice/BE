package com.devrun.controller;

import com.devrun.entity.Cart;
import com.devrun.entity.Lecture;
import com.devrun.entity.MemberEntity;
import com.devrun.repository.CartRepo;
import com.devrun.service.SignupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(tags = "Devrun.Cart")
public class CartController {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private SignupService signupService;

    @GetMapping("/api/cart")
    @ResponseBody
    @ApiOperation("장바구니에 강의를 추가합니다.")
    @ApiImplicitParam(name = "lno"
            , value = "강의 일련번호"
    )
    public String putCart(@RequestParam(name = "lno") int lno) {
        String msg;
        String userid = "bbb222";
        MemberEntity memberEntity = signupService.findById(userid);
        if(memberEntity.getId() != null){
            Cart cart = new Cart();
            Lecture lecture = new Lecture();
            lecture.setLno(1);
            cart.setLecture(lecture);
            cart.setMemberEntity(memberEntity);
            cartRepo.save(cart);
            msg = "저장 성공";

        } else {
            msg = "저장 실패";
        }

        return msg;
    }

    @GetMapping("/api/cartdelete")
    @ResponseBody
    public String deleteInCart(@RequestParam(name = "lecturetitle") String lecturetitle){

        String userid = "bbb222";
        cartRepo.deleteInCart(userid , lecturetitle);

        return "삭제 성공";
    }
}
