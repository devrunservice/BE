package com.devrun.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import java.sql.Date;

@Data
@Entity
public class UserCoupon {

    @Id
    private Long couponcodeno;
    private String couponcode; // 쿠폰 등록 코드 - 외래키  
    
    @ManyToOne
    @JoinColumn(name="userno")
    private MemberEntity userno; // 쿠폰 등록자 = 유저 - null 가능
    
    private boolean isenabled; // 쿠폰 사용 여부 - null 가능
    private Date registrationdate; // 쿠폰 등록일 - null 가능
    private Date usagedate; // 쿠폰 사용일 - null 가능
}
