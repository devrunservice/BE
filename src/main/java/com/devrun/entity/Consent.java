package com.devrun.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import lombok.Data;

@Data
@Entity
@Table(name = "consent")
public class Consent {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consentNo")
    @Comment("동의 번호")
    private int consentNo;

    @OneToOne
    @JoinColumn(name = "userNo", nullable = false)
//    @NotNull(message = "information cannot be null or empty")
    @Comment("유저 구분 번호")
    private MemberEntity memberEntity;

    @Column(name = "ageConsent", nullable = false, columnDefinition = "TINYINT(1)")
    @Comment("나이 동의")
    @AssertTrue(message = "User has not agreed to the terms")
    private boolean ageConsent;

    @Column(name = "serviceConsent", nullable = false, columnDefinition = "TINYINT(1)")
    @Comment("서비스 동의")
    @AssertTrue(message = "User has not agreed to the terms")
    private boolean termsOfService;

    @Column(name = "privacyConsent", nullable = false, columnDefinition = "TINYINT(1)")
    @Comment("개인정보 동의")
    @AssertTrue(message = "User has not agreed to the terms")
    private boolean privacyConsent;

    @Column(name = "marketingConsent", nullable = false, columnDefinition = "TINYINT(1)")
    @Comment("광고, 마케팅 동의")
    private boolean marketConsent;
}
