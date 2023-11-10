package com.devrun.dto;

import java.time.LocalDate;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "수료증 객체")
public class CertificateDto {
	@ApiModelProperty(value = "수료자 이름")
	private String userName;
	@ApiModelProperty(value = "수료자 생년월일")
	private LocalDate birthday;
	@ApiModelProperty(value = "수료 강의 이름")
	private String lectureName;
	@ApiModelProperty(value = "학습 시작일")
	private Date start;
	@ApiModelProperty(value = "학습 종료일")
	private Date end;
}
