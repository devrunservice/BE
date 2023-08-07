package com.devrun.entity;

import com.devrun.dto.CartDTO;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Cartno;


    @ManyToOne(fetch = FetchType.LAZY) //한명의 회원은 여러개의 장바구니를 가질 수 있기 때문에 장바구니는 회원과 다대일 관계
    @JoinColumn(name = "user_no")   //장바구니 테이블에서 회원 테이블로 접근하여 회원의 정보를 얻을 수 있으나, 그 역은 불가능하다.
    private MemberEntity memberEntity;


    @ManyToOne(fetch = FetchType.LAZY) //강의 하나는 여러개의 장바구니에 담길 수 있기 때문에 장바구니와 강의는 다대일 관계
    @JoinColumn(name = "lno") //장바구니 테이블에서 강의 테이블로 접근하여 강의 정보를 얻을 수 있으나, 그 역은 불가능하다.
    private Lecture lecture;


    private boolean deleteop; //flag , 1 이면 삭제처리로 숨기기


}
