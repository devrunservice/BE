package com.devrun.controller;

import com.devrun.dto.MypageDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.editmyinfo;
import com.devrun.service.AwsS3ReadService;
import com.devrun.service.AwsS3UploadService;
import com.devrun.service.MemberService;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.TransactionalException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class MyPageController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private AwsS3ReadService awsS3ReadService;

    @Autowired
    private AwsS3UploadService awsS3UploadService;

    @GetMapping("/mypage/{userid}")
    public ResponseEntity<?> mypageopen(@PathVariable String userid) {

        String v = SecurityContextHolder.getContext().getAuthentication().getName();

        if (v.equals(userid)) {
            System.out.println("마이페이지 메인 : 토큰 추출값 : " + v + ", url : " + userid);
            System.out.println("일치");

        } else {
            System.out.println("마이페이지 메인 : 토큰 추출값 : " + v + ", url : " + userid);
            System.out.println("불일치");
        }

        MemberEntity m = memberService.findById(userid);

        MypageDTO mypageDTO = new MypageDTO();
        mypageDTO.setUserid(m.getId());
        mypageDTO.setBirth(m.getBirthday());
        mypageDTO.setUsername(m.getName());
        mypageDTO.setPhonenumber(m.getPhonenumber());
        mypageDTO.setUseremail(m.getEmail());
        String profileimgurl = awsS3ReadService.findUploadKeyUrl(m.getProfileimgsrc());
        mypageDTO.setProfileimgsrc(profileimgurl);


        return ResponseEntity.ok().body(mypageDTO);
    }

    @PostMapping("/mypage/userconfirm")
    public ResponseEntity<?> userConfirm(@RequestBody String password){
        String v = SecurityContextHolder.getContext().getAuthentication().getName();
        if(password.equals(memberService.findById(v).getPassword())){
            return ResponseEntity.ok().body("수정 페이지로 이동");
        } else {
            System.out.println("--------------------------------------");
            System.out.println(v);
            System.out.println(memberService.findById(v).getPassword());
            return ResponseEntity.badRequest().body("비밀번호 불일치");
        }
    }

    @PostMapping("/edit/phone")
    public ResponseEntity<?> editphone(@RequestBody Map<String , String> editdata){
        String v = SecurityContextHolder.getContext().getAuthentication().getName();
        String editphone = editdata.get("phonenumber");
        String verifycode = editdata.get("code");
        if(memberService.checkphone(editphone) >= 1){
            return ResponseEntity.badRequest().body("중복된 번호");
        }

        if(memberService.verifySmsCode(editphone, verifycode)){
            MemberEntity m = memberService.findById(v);
            memberService.removeSmsCode(editphone);
            return ResponseEntity.ok().body("휴대폰 번호 수정 완료");
        } else {
            return ResponseEntity.badRequest().body("인증 실패");
        }
    }

    @PostMapping("/edit/email")
    public ResponseEntity<?> editEmail(@RequestBody Map<String , String> editdata) {

        String v = SecurityContextHolder.getContext().getAuthentication().getName();
        MemberEntity m = memberService.findById(v);
        String editaemail = editdata.get("email");
        if ( !memberService.validateEmail(editaemail) ) {
            return ResponseEntity.badRequest().body("이메일 형식 미 충족");

        } else if (memberService.checkEmail(editaemail) >= 1) {
            return ResponseEntity.badRequest().body("중복된 이메일");
        } else {
            m.setEmail(editaemail);
            memberService.insert(m);
            return ResponseEntity.ok().body("이메일 수정 완료");
        }
    }

    @PostMapping("/edit/password")
    public ResponseEntity<?> editPassword(@RequestBody Map<String , String> editdata) {

        String v = SecurityContextHolder.getContext().getAuthentication().getName();

        MemberEntity m = memberService.findById(v);
        String editpassword = editdata.get("password");
        if (memberService.validatePassword(editpassword)) {
            return ResponseEntity.badRequest().body("비밀번호 형식 미 충족");

        } else {
            m.setPassword(editpassword);
            memberService.insert(m);
            return ResponseEntity.ok().body("비밀번호 수정 완료");
        }
    }

    @PostMapping("/edit/profileimg")
    public ResponseEntity<?> editProfileImg(@RequestPart(required = true) List<MultipartFile> editimg) throws IOException {
        String v = SecurityContextHolder.getContext().getAuthentication().getName();

        MemberEntity m = memberService.findById(v);

        if (editimg != null) {
            if (editimg.size() >= 2) {
                return ResponseEntity.badRequest().body("Only one image is available.");
            } else {
                String uploadpath = awsS3UploadService.putS3(editimg, "profile");
                String newprofileimgsrc = awsS3ReadService.findUploadKeyUrl(uploadpath);
                m.setProfileimgsrc(newprofileimgsrc);
                memberService.insert(m);
                return ResponseEntity.ok("프로필 이미지를 포함한 수정 성공");
            }
        } else {

            return ResponseEntity.ok("이미지가 첨부되지 않았습니다.");



    }


//        memberService.insert(m);
//
//        memberService.removeSmsCode(editdata.getPhonenumber());
//        return ResponseEntity.ok("프로필 이미지를 포함한 수정 성공");

//
//
//        }
    }
}
