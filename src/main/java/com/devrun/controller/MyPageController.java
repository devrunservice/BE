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
    public ResponseEntity<?> mypageopen(@PathVariable String userid){

        String v = SecurityContextHolder.getContext().getAuthentication().getName();

        if(v.equals(userid)){
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

    @PostMapping("/edit/{userid}")
    public ResponseEntity<?> myinfoedit(@PathVariable String userid , @RequestPart @Validated(editmyinfo.class) MemberEntity editdata, @RequestPart(name = "file" , required = false) List<MultipartFile> files) throws IOException {

        String v = SecurityContextHolder.getContext().getAuthentication().getName();
        if(v.equals(userid)){
            System.out.println("마이페이지 수정 : 토큰 추출값 : " + v + ", url : " + userid);
            System.out.println("일치");

        } else {
            System.out.println("마이페이지 수정 : 토큰 추출값 : " + v + ", url : " + userid);
            System.out.println("불일치");
        }

            MemberEntity m = memberService.findById(userid);
            m.setEmail(editdata.getEmail());
            m.setPhonenumber(editdata.getPhonenumber());
            m.setPassword(editdata.getPassword());

            if(files != null) {
                if(files.size() >= 2){
                    return ResponseEntity.badRequest().body("Only one image is available.");
                } else {
                        try{
                            String uploadpath = awsS3UploadService.putS3(files, "profile");
                            String newprofileimgsrc = awsS3ReadService.findUploadKeyUrl(uploadpath);
                            m.setProfileimgsrc(newprofileimgsrc);
                        } catch (StringIndexOutOfBoundsException e) {

                        }
                        finally {
                            try{
                                memberService.insert(m);
                                return ResponseEntity.ok("프로필 이미지를 포함한 수정 성공");}
                            catch (ConstraintViolationException e){
                                System.out.println("실패!");
                                return ResponseEntity.badRequest().body(e.getMessage());
                            }

                        }
                    }
            }
            else {
                try{
                    memberService.insert(m);
                    return ResponseEntity.ok("프로필 이미지를 제외한 수정 성공");}
                catch (ConstraintViolationException e){
                    System.out.println("실패!");
                    return ResponseEntity.badRequest().body(e.getMessage());
                }
            }
    }
}
