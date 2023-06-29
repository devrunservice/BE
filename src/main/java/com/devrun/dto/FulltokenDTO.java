package com.devrun.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


	@ToString
	@Getter
	@NoArgsConstructor
	public class FulltokenDTO {
		private int code;
		private ResponseData response;
		private String message;
		
		@Getter
		@NoArgsConstructor
		@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
		public class ResponseData{
			private String access_token;
			private int now;
			private int expire_at;
		}

}
