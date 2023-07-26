package com.devrun.entity;


import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
@Table(name = "lecture")
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lno")
    private Integer lno;

    @Column(name = "category")
    private Integer category;

    @Column(name = "intro", length = 300)
    private String intro;

    @Column(name = "price")
    private Integer price;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "launch", nullable = false)
    private Date launch;

    @Column(name = "edit")
    private Date edit;

    @Column(name = "discount")
    private boolean discount;

    @Column(name = "discountrate", nullable = false)
    private Float discountrate;

    @Column(name = "discountstart")
    private Date discountstart;

    @Column(name = "discountend")
    private Date discountend;

    @Column(name = "status")
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "uno")
    private MemberEntity memberEntity;
}
