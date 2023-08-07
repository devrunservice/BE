package com.devrun.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CartDTO {
    //강의 관련
    private List<Map<String , String>> lectureInfo;

    //구매자 정보
    private String userName , userEmail , userPhonenumber;

    //할인 정보
    private int userPoint , ableCouponCount;//사용가능한 쿠폰 갯수





}
