package com.devrun.dto;

import java.util.Date;
import lombok.Data;

@Data
public class MylectureReviewDTO {
	private String profileimgsrc;
	private String userId;
	private String reviewContent;
	private Date reviewDate;
	private double reviewRating;
}
