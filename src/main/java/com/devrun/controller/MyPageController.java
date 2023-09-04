package com.devrun.controller;

import com.devrun.dto.MypageDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.editmyinfo;
import com.devrun.service.AwsS3ReadService;
import com.devrun.service.AwsS3UploadService;
import com.devrun.service.MemberService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

import javax.servlet.http.HttpServletResponse;
import javax.transaction.TransactionalException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
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
    @ApiOperation(value = "프로필 불러오기", notes = "헤더에 삽입된 액세스 토큰으로부터 id를 조회하여, 해당 유저의 프로필을 불러옵니다.")
    @ApiImplicitParam(name = "userid" , value = "주소창에 표시할 유저 id" , required = true)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 처리되었습니다"),
            @ApiResponse(code = 400, message = "액세스 토큰이 입력되지 않았거나, 존재하지 않는 유저입니다.")
    })
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
        mypageDTO.setId(m.getId());
        mypageDTO.setBirthday(m.getBirthday());
        mypageDTO.setName(m.getName());
        mypageDTO.setPhonenumber(m.getPhonenumber());
        mypageDTO.setEmail(m.getEmail());
        String profileimgurl = awsS3ReadService.findUploadKeyUrl(m.getProfileimgsrc());
        mypageDTO.setProfileimgsrc(profileimgurl);


        return ResponseEntity.ok().body(mypageDTO);
    }

    @PostMapping("/mypage/userconfirm")
    public ResponseEntity<?> userConfirm(@RequestBody Map<String, String> editdata) {
        String v = SecurityContextHolder.getContext().getAuthentication().getName();
        if (editdata.get("password").equals(memberService.findById(v).getPassword())) {
            return ResponseEntity.ok().body("수정 페이지로 이동");
        } else {
            System.out.println("--------------------------------------");
            System.out.println(v);
            System.out.println(memberService.findById(v).getPassword());
            return ResponseEntity.status(409).body("비밀번호 불일치");
        }
    }

    @PostMapping("/edit/phone")
    @ApiOperation(value = "연락처 수정", notes = "헤더에 삽입된 액세스 토큰으로부터 id를 조회하여, 해당 유저의 연락처를 수정합니다.")
    @ApiImplicitParam(name = "editdata" , value = "연락처와 인증번호" , required = true)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 처리되었습니다"),
            @ApiResponse(code = 400, message = "액세스 토큰과 키 값을 확인해주세요(phonenumber , code)"),
            @ApiResponse(code = 409, message = "이미 등록된 번호"),
            @ApiResponse(code = 409, message = "인증번호 불일치"),
    })
    public ResponseEntity<?> editphone(@RequestBody Map<String, String> editdata) {
        String v = SecurityContextHolder.getContext().getAuthentication().getName();
        String editphone = editdata.get("phonenumber");
        String verifycode = editdata.get("code");
        Map<String, String> result = new HashMap<String, String>();
        if (memberService.checkphone(editphone) >= 1) {

            result.put("message", "This number is duplicated");

            return ResponseEntity.status(409).body(result);
        }

        if (memberService.verifyCode(editphone, verifycode)) {
            MemberEntity m = memberService.findById(v);
            m.setPhonenumber(editphone);
            memberService.insert(m);
            memberService.removeVerifyCode(editphone);

            result.put("message", "Number edited successfully.");
            result.put("phonenumber" , editphone);
            return ResponseEntity.ok().body(result);
        } else {

            result.put("message", "Failure to Authenticate");
            return ResponseEntity.status(409).body(result);
        }
    }

    @PostMapping(value = "/edit/email")
    @ApiOperation(value = "이메일 수정", notes = "헤더에 삽입된 액세스 토큰으로부터 id를 조회하여, 해당 유저의 이메일주소를 수정합니다.")
    @ApiImplicitParam(name = "editdata" , value = "이메일주소" , required = true)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 처리되었습니다"),
            @ApiResponse(code = 400, message = "액세스 토큰과 키 값을 확인해주세요(email)"),
            @ApiResponse(code = 409, message = "이미 등록된 이메일 주소"),
            @ApiResponse(code = 409, message = "형식이 유효하지 않은 이메일 주소"),
    })
    public ResponseEntity<?> editEmail(@RequestBody Map<String, String> editdata, HttpServletResponse response) throws IOException {

        String v = SecurityContextHolder.getContext().getAuthentication().getName();
        MemberEntity m = memberService.findById(v);
        String editaemail = editdata.get("email");

        Map<String, String> result = new HashMap<String, String>();
        if (!memberService.validateEmail(editaemail)) {
            response.sendError(409, "Invalid email address.");
            response.flushBuffer();
            result.put("message", "Invalid email address.");
            return ResponseEntity.status(409).body(result);

        } else if (memberService.checkEmail(editaemail) >= 1) {

            response.sendError(409, "This email is duplicated");
            response.flushBuffer();
            result.put("message", "This email is duplicated");
            return ResponseEntity.status(409).body(result);

        } else {
            m.setEmail(editaemail);
            memberService.insert(m);

            result.put("message", "Email edited successfully.");
            result.put("email" , editaemail);
            return ResponseEntity.ok().body(result);
        }
    }


//    @PostMapping("/edit/password")
//    public ResponseEntity<?> editPassword(@RequestBody Map<String , String> editdata) {
//
//        String v = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        MemberEntity m = memberService.findById(v);
//        String editpassword = editdata.get("password");
//        if ( !memberService.validatePassword(editpassword) ) {
//            return ResponseEntity.badRequest().body("Invalid password.");
//
//        } else {
//            m.setPassword(editpassword);
//            memberService.insert(m);
//            return ResponseEntity.ok().body("password edited successfully.");
//        }
//    }

    @PostMapping("/edit/profileimg")
    @ApiOperation(value = "프로필 이미지 수정", notes = "헤더에 삽입된 액세스 토큰으로부터 id를 조회하여, 해당 유저의 프로필 이미지를 수정합니다.")
    //@ApiImplicitParam(value = "프로필 이미지 파일") 왠지 모르겠는 데 nullpointer error 뜸
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 처리되었습니다"),
            @ApiResponse(code = 400, message = "액세스 토큰과 키 값을 확인해주세요(phonenumber , code"),
            @ApiResponse(code = 409, message = "이미지가 첨부되지 않았거나, 2장 이상 첨부되었습니다."),
    })
    public ResponseEntity<?> editProfileImg(@RequestPart(required = true) List<MultipartFile> editimg) throws IOException {
        String v = SecurityContextHolder.getContext().getAuthentication().getName();

        MemberEntity m = memberService.findById(v);
        Map<String, String> result = new HashMap<String, String>();
        if (editimg != null) {
            if (editimg.size() >= 2) {

                result.put("message", "Only one image is available.");

                return ResponseEntity.status(409).body(result);
            } else {

                String uploadpath = awsS3UploadService.putS3(editimg, "profile");
                String newprofileimgsrc = awsS3ReadService.findUploadKeyUrl(uploadpath);
                m.setProfileimgsrc(uploadpath);
                memberService.insert(m);

                result.put("message", "profile image edited successfully.");
                result.put("profileimg" , newprofileimgsrc);
                return ResponseEntity.ok(result);
            }
        } else {

            result.put("message", "The profile image is not attached.");
            return ResponseEntity.status(409).body(result);


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
