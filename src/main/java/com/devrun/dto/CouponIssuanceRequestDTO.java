package com.devrun.dto;

import java.util.Date;

import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.devrun.entity.CouponIssued.coupontype;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "쿠폰 발행 정보", description = "발행할 쿠폰에 대한 세부 내역")
public class CouponIssuanceRequestDTO {

	@Enumerated(EnumType.STRING)
	@ApiModelProperty(value = "쿠폰 종류", required = true, example = "all", allowableValues = "all, category, lecture, mento , testname")
	private coupontype coupontype; // 쿠폰 종류 (카테고리 할인, 특정 강의 할인 , 특정 강사 할인, 모든 강의 할인)

	@Max(value = 99)
	@Min(value = 1)
	@ApiModelProperty(value = "쿠폰 할인율", required = true, example = "20", allowableValues = "1~99")
	private int discountrate; // 쿠폰 할인율

	@ApiModelProperty(value = "쿠폰 만료일", required = true, example = "2023-01-01")
	private Date expirydate; // 쿠폰 일괄 만료일

	@Max(value = 99)
	@Min(value = 1)
	@ApiModelProperty(value = "쿠폰 발행 수량", required = true, example = "20", allowableValues = "1~99")
	private int quantity; // 쿠폰 발행 수량
	
	@ApiModelProperty(value = "쿠폰을 적용할 강의 이름", required = true, example = "자바가제일쉬웠어요")
	private String lectureName;

}
