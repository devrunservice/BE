package com.devrun.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.devrun.dto.CommentDTO;
import com.devrun.dto.CommentDTO.Status;

import lombok.Data;

@Data
@Entity
@Table(name = "Comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentNo")
    @org.hibernate.annotations.Comment("댓글 번호")
    private int commentNo;

    @ManyToOne
    @JoinColumn(name = "noticeNo", referencedColumnName = "noticeNo",nullable = false)
    @org.hibernate.annotations.Comment("댓글이 달린 공지사항의 번호")
    private Notice notice;
    
    @ManyToOne
    @JoinColumn(name = "parentCommentNo", nullable = true)
    @org.hibernate.annotations.Comment("댓글이 달린 댓글의 번호")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment"
//    		, cascade = CascadeType.ALL								// 부모 댓글이 삭제되면 같이 삭제되도록 만드는 속성인데 현재 status로 관리하는 방식으로 변경되었기 때문에 필요없어졌다.
    		)
    @org.hibernate.annotations.Comment("자식 댓글과의 관계 설정")
    private List<Comment> childComments = new ArrayList<>();

    @Column(name = "content", nullable = false , columnDefinition = "TEXT")
    @org.hibernate.annotations.Comment("댓글 내용")
    private String content;
    
    @ManyToOne
    @JoinColumns({
    	@JoinColumn(name = "userNo", referencedColumnName = "userNo", nullable = false),
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
        int parentCommentNo = (this.parentComment != null) ? this.parentComment.getCommentNo() : 0;

        return new CommentDTO(
            this.commentNo,
            this.notice.getNoticeNo(),
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
