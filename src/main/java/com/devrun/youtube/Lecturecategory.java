package com.devrun.youtube;

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

import lombok.Data;

@Data
@Entity
public class Lecturecategory {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categoryNo;

    @Column(nullable = false)
    private String lectureBigCategory;
    
    @Column(nullable = false)
    private String LectureMidCategory;

   

}