package com.devrun.youtube;

import java.util.List;

import lombok.Data;

@Data
public class LectureSectionDto {
    private int SectionNumber; //1번
    private String SectionTitle; //1번 제목
    List<VideoDto> videoDtos; //4
}




/*
{"전체섹션및동영상":
["LectureSectionDto1" : {
		"SectionNumber" : "SectionNumber" 1
		"SectionTitle" : "SectionTitle" 1 
		"videoDtos" : [{"title" : "title" , "file" : "(binary)"} ...] 4
},

"LectureSectionDto2" : {
		"SectionNumber" : "SectionNumber" 2
		"SectionTitle" : "SectionTitle" 2 
		"videoDtos" : [{"title" : "title" , "file" : "(binary)"} ...] 5
}]
}

JSON
	- JSONLIST 
		- JSONLIST


1ㅁ 섹션 : 제목 id - first
	 v 제목, 파일
	 v
	 v
2ㅁ
	v
	v
3ㅁ
	v
4ㅁ
 	v

*/