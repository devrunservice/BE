package com.devrun.dto;

import com.devrun.entity.MemberEntity;
import com.devrun.youtube.Lecture;

import lombok.Data;

@Data
public class TotalProgress {
	
	public MemberEntity user_no;
	public Lecture lecture_no;
	public int totalprogress;

}
