package com.devrun.youtube;

import lombok.Data;

@Data
public class LecturecategoryDto {
    private String lectureBigCategory;
    private String lectureMidCategory;

    public LecturecategoryDto() {
    }

    public LecturecategoryDto(String lectureBigCategory, String lectureMidCategory) {
        this.lectureBigCategory = lectureBigCategory;
        this.lectureMidCategory = lectureMidCategory;
    }
}
