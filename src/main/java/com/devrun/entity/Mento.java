package com.devrun.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Data
@Entity
public class Mento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mentono;

    private int userNo;
    private String link;
    private Date mentoaskday;
    private Date approvedday;
    private Boolean approved;
}
