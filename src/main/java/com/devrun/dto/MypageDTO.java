package com.devrun.dto;


import java.time.LocalDate;

import lombok.Data;

@Data
public class MypageDTO {

    private String name , id , email, phonenumber , profileimgsrc;
    private LocalDate birthday;
}
