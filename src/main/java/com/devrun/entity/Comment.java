package com.devrun.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.devrun.dto.CommentDTO;

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
    @JoinColumn(name = "noticeNo")
    @org.hibernate.annotations.Comment("공지사항과의 관계 설정")
    private Notice notice;
    
    @ManyToOne
    @JoinColumn(name = "parentCommentNo", nullable = true)
    @org.hibernate.annotations.Comment("부모 댓글과의 관계 설정")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    @org.hibernate.annotations.Comment("자식 댓글과의 관계 설정")
    private List<Comment> childComments = new ArrayList<>();

    @Column(name = "content", nullable = false , columnDefinition = "TEXT")
    @org.hibernate.annotations.Comment("댓글 내용")
    private String content;

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
    
    public CommentDTO toDTO(){
    	// parentComment가 null인 경우, parentCommentNo를 0으로 설정
        int parentCommentNo = (this.parentComment != null) ? this.parentComment.getCommentNo() : 0;

        return new CommentDTO(
            this.commentNo,
            this.notice.getNoticeNo(),
            parentCommentNo,
            this.content,
            this.createdDate,
            this.modifiedDate
        );
    }
}
