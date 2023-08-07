package com.devrun.dto;


import lombok.Data;

import java.util.Date;

@Data
public class MypageDTO {

    private String name , id , email, phonenumber , profileimgsrc;
    private Date birthday;
}
