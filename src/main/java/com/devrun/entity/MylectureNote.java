package com.devrun.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class MylectureNote {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "note_no")
	private Integer noteNo;

	@ManyToOne
	@JoinColumn(name = "mylecture_no")
	private MyLecture myLecture;

	@CreatedDate
    @Temporal(TemporalType.DATE)
	private Date createDate;
	
    @Temporal(TemporalType.DATE)
	private Date modiDate;

	@Column(columnDefinition = "TEXT", nullable = true)
	private String noteContext;
	
	@Column(name = "note_delete", nullable = false)
	private boolean noteDelete = false;

}
