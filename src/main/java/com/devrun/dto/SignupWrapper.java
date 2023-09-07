package com.devrun.dto;

import javax.validation.Valid;

import com.devrun.entity.Consent;
import com.devrun.entity.Contact;
import com.devrun.entity.LoginInfo;
import com.devrun.entity.MemberEntity;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor			// 모든 필드를 인수로 받는 생성자만 생성
@NoArgsConstructor			// 기본 생성자가 생성
public class SignupWrapper {
	
    @Valid
    @JsonUnwrapped			// 하나의 {}json으로 변경
    private MemberEntity memberEntity;

    @Valid
    @JsonUnwrapped
    private Contact contact;

    @Valid
    @JsonUnwrapped
    private Consent consent;

    @Valid
    @JsonUnwrapped
    private LoginInfo loginInfo;
    
    private String code;
    
}