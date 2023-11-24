package com.devrun.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.devrun.dto.CommentDTO;
import com.devrun.dto.CommentDTO.Status;

import lombok.Data;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class MylectureQaAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qaAnswerNo")
    @org.hibernate.annotations.Comment("댓글 번호")
    private int qaAnswerNo;

    @ManyToOne
    @JoinColumn(name = "qaNo")
    @org.hibernate.annotations.Comment("댓글이 달린 공지사항의 번호")
    private MylectureQa qaNo;
    
    @ManyToOne
    @JoinColumn(name = "parentCommentNo", nullable = true)
    @org.hibernate.annotations.Comment("댓글이 달린 댓글의 번호")
    private MylectureQaAnswer parentComment;

    @OneToMany(mappedBy = "parentComment")
    @org.hibernate.annotations.Comment("자식 댓글과의 관계 설정")
    private List<MylectureQaAnswer> childComments = new ArrayList<>();

    @Column(name = "content", nullable = false , columnDefinition = "TEXT")
    @org.hibernate.annotations.Comment("댓글 내용")
    private String content;
    
    @ManyToOne
    @JoinColumns({
    	@JoinColumn(name = "userNo", referencedColumnName =  "userNo", nullable = false),
    	@JoinColumn(name = "id", referencedColumnName = "id", nullable = false),
    	@JoinColumn(name = "profileimgsrc", referencedColumnName = "profileimgsrc", nullable = false)})
	@NotNull(message = "information cannot be null or empty")
	private MemberEntity memberEntity;
    
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdDate")
    @org.hibernate.annotations.Comment("댓글 작성일")
    private Date createdDate;
    
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modifiedDate", insertable = false)
    @org.hibernate.annotations.Comment("댓글 수정일")
    private Date modifiedDate;
    
    @Column(name = "status", nullable = false, length = 9)
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.Comment("댓글의 상태")
    private Status status = Status.ACTIVE;
    
    public CommentDTO toDTO(){
    	// parentComment가 null인 경우, parentCommentNo를 0으로 설정
        int parentCommentNo = (this.parentComment != null) ? this.parentComment.getQaAnswerNo() : 0;

        return new CommentDTO(
            this.qaAnswerNo,
            this.qaNo.getLectureQaNo().intValue(),
            parentCommentNo,
            this.memberEntity.getUserNo(),
            this.content,
            this.memberEntity.getId(),
            this.memberEntity.getProfileimgsrc(),
            this.createdDate,
            this.modifiedDate,
            this.status
        );
    }
}
