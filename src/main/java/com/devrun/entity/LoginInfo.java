package com.devrun.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import lombok.Data;

@Data
@Entity
@Table(name = "login_info")
public class LoginInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loginInfoNo")
    @Comment("로그인 정보 번호")
    private int loginInfoNo;

    @OneToOne
    @JoinColumn(name = "userNo", nullable = false)
//    @NotNull(message = "information cannot be null or empty")
    @Comment("유저 구분 번호")
    private MemberEntity memberEntity;

    @Column(name = "lastlogin", nullable = true)
	@Comment("마지막 로그인 날짜")
	private Date lastlogin;

    @Column(name = "logintry", nullable = false, length = 2)
    @Comment("로그인 시도 횟수")
    private int logintry = 0;
}
