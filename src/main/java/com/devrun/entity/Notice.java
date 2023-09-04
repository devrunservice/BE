package com.devrun.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.devrun.dto.NoticeDTO.Status;

import lombok.Data;

@Data
@Entity
@Table(name = "Notice")
public class Notice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "noticeNo", length = 5)
	@Comment("공지사항 번호")
	private int noticeNo;
	
	@OneToOne
	@JoinColumn(name = "userNo", nullable = false)
	@Comment("작성자")
	@NotBlank(message = "information cannot be null or empty")
	private MemberEntity memberEntity;
	
	@Column(name = "title", nullable = false, length = 25)
	@Comment("공지사항 타이틀")
    @NotBlank(message = "information cannot be null or empty")
    private String title;

    @Column(name = "content", nullable = false , columnDefinition = "TEXT")
    @Comment("공지사항 내용")
    @NotBlank(message = "information cannot be null or empty")
    private String content;

    @Column(name = "viewCount", nullable = false)
    @Comment("조회수")
    private int viewCount = 0;

    @CreationTimestamp								// 엔티티가 처음으로 데이터베이스에 저장될 때 현재 시간을 자동으로 설정
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdDate", nullable = false)
    @Comment("작성날짜")
    private Date createdDate;

    @UpdateTimestamp								// 엔티티가 수정될 때마다 현재 시간으로 자동으로 업데이트
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modifiedDate")
    @Comment("수정날짜")
    private Date modifiedDate;

    @Column(name = "status", nullable = false, length = 9)
    @Enumerated(EnumType.STRING)
    @Comment("공지사항의 상태")
    private Status status = Status.ACTIVE;
	
}
