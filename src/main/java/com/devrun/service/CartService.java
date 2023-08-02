package com.devrun.service;


import com.devrun.dto.CartDTO;
import com.devrun.entity.Cart;
import com.devrun.entity.Lecture;
import com.devrun.entity.MemberEntity;
import com.devrun.repository.CartRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private MemberService memberService;

    public CartDTO showCartInfo(String userid){

        List<Cart> Carts = cartRepo.findAllByMemberEntity_id(userid);
        CartDTO result = new CartDTO();
        List<Map<String , String>> lecutreInfo = new ArrayList<Map<String , String>>();

        for (Cart e: Carts) {

            int lecturePrice = e.getLecture().getPrice();
            String mentoName = e.getLecture().getMemberEntity().getName();
            String lectureIntro = e.getLecture().getIntro();
            String lectureTitle = e.getLecture().getTitle();
            int lectureNo = e.getLecture().getLno();
            Map<String , String> singglelectureinfo = new HashMap<String , String>();
            singglelectureinfo.put("lectureNo" , Integer.toString(lectureNo));
            singglelectureinfo.put("lectureTitle" , lectureTitle);
            singglelectureinfo.put("lectrueIntro" , lectureIntro);
            singglelectureinfo.put("mentoName" , mentoName);
            singglelectureinfo.put("lecturePrice" , Integer.toString(lecturePrice));
            lecutreInfo.add(singglelectureinfo);

        }


        result.setLectureInfo(lecutreInfo);

        String userPhonumber = memberService.findById("seokhwan1").getPhonenumber();
        String userEmail = memberService.findById("seokhwan1").getEmail();
        String username = memberService.findById("seokhwan1").getName();

        result.setUserPhonenumber(userPhonumber);
        result.setUserEmail(userEmail);
        result.setUserName(username);

        result.setUserPoint(500);
        result.setAbleCouponCount(1);

        return result;

    }

    public String putInCart(String lectureTitle){
        String resultMsg;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userid = authentication.getName();

        MemberEntity memberEntity = memberService.findById(userid);


        if(memberEntity != null){
            Cart cart = new Cart();
            Lecture lecture = new Lecture();
            lecture.setLno(1);
            cart.setLecture(lecture);
            cart.setMemberEntity(memberEntity);
            //cartRepo.save(cart);
            resultMsg = "저장 성공";

        } else {
            resultMsg = "저장 실패";
        }

        return resultMsg;

    }

    public String deleteInCart(String lectureTitle){

        String resultMsg;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userid = authentication.getName();

        //reulstmsg = cartRepo.deleteInCart(userid , lecturetitle);

        resultMsg = "삭제 완료";

        return resultMsg;

    }

}
