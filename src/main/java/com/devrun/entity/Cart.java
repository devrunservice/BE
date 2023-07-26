package com.devrun.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Cartno;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_no")
    private MemberEntity memberEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lno")
    private Lecture lecture;


    private boolean deleteop;


}
