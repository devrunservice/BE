package com.devrun.dto;


import lombok.Data;

import java.util.Date;

@Data
public class MypageDTO {

    private String username , userid , useremail, phonenumber , profileimgsrc;
    private Date birth;
}
