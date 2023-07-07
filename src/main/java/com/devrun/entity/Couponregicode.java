package com.devrun.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;


@Data
@Entity
public class Couponregicode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponcodeno;

    private String couponcode; // 쿠폰 등록 코드 - 사전 생성
    private Long issuedno; //쿠폰발행번호 - 외래키
}
