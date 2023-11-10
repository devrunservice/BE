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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class MylectureNote {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "note_no")
	private Long noteNo;

	@ManyToOne
	@JoinColumn(name = "mylecture_progress_no")
	@JsonBackReference
	private MyLectureProgress myLectureProgress;

	@LastModifiedDate
    @Temporal(TemporalType.DATE)
	private Date date;
    
	private String noteTitle;
	
	private String subheading;
	
	private int chapter;

	@Column(columnDefinition = "TEXT", nullable = true)
	private String noteContext;
	
	@Column(name = "note_delete", nullable = false)
	private boolean noteDelete = false;

}
