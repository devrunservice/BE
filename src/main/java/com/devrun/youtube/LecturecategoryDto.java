package com.devrun.youtube;

import lombok.Data;

@Data
public class LecturecategoryDto {
	private Long categoryNo;
    private String lectureBigCategory;
    private String lectureMidCategory;

    public LecturecategoryDto() {
    }

    public LecturecategoryDto(Long categoryNo, String lectureBigCategory, String lectureMidCategory) {
        this.categoryNo = categoryNo;
        this.lectureBigCategory = lectureBigCategory;
        this.lectureMidCategory = lectureMidCategory;
    }
}
