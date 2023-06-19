package com.devrun.dto.member;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignUpDTO {
	
	private String name, email, id, password, phonenumber;
	private int userNo;
	private Date birthday, signup;
	private Status status;
	private Role role;
	
	@Getter
	@AllArgsConstructor
	public enum Status{
		
		ACTIVE("활동"),
		INACTIVE("휴면"),
		WITHDRAWN("탈퇴");
		
		private final String description;	//status.ACTIVE.description로 "활동"이라는 설명을 불러올 수 있다
	}
	
	@Getter
	@AllArgsConstructor
	public enum Role{
		
		STUDENT("학생"),
		MENTO("멘토"),
		ADMIN("관리자");
		
		private final String description;
	}
	
//	데이터 입력 & 조회 예제
//	
//	// save a user
//	User user = new User();
//	user.setName("John");
//	user.setRole(Role.ADMIN);
//	userRepository.save(user);
//
//	// find users with a specific role
//	List<User> admins = userRepository.findByRole(Role.ADMIN);
//	for (User admin : admins) {
//	    System.out.println(admin.getName());
//	}
}
