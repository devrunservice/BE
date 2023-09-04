package com.devrun.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Comment;

import lombok.Data;

@Data
@Entity
@Table(name = "contact")
public class Contact {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contactNo")
    @Comment("연락처 번호")
    private int contactNo;

    @OneToOne
    @JoinColumn(name = "userNo")
    @NotBlank(message = "information cannot be null or empty")
    @Comment("유저 구분 번호")
    private MemberEntity memberEntity;

    @Column(name = "email", nullable = false, length = 50)
	@Comment("유저 이메일")
	@NotBlank(message = "information cannot be null or empty")
	private String email;

    @Column(name = "phonenumber", nullable = false, length = 11)
	@Comment("유저 연락처")
	@NotBlank(message = "information cannot be null or empty")
	private String phonenumber;
}
