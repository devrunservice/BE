package com.devrun.youtube;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LecutureCategoryService {
    
    private final LecturecategoryRepository categoryRepository;

    @Autowired
    public LecutureCategoryService(LecturecategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<LectureCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public List<LectureCategory> getCategoriesByIds(List<Long> categoryIds) {
        return categoryRepository.findAllById(categoryIds);
    }

	public LectureCategory findcategory(String bigcategory, String midcategory) {
		return categoryRepository.findByLectureBigCategoryAndLectureMidCategory(bigcategory,midcategory);
	}
	
	public List<LectureCategory> findcategory(String bigcategory) {
		return categoryRepository.findByLectureBigCategoryOrLectureMidCategory(bigcategory , bigcategory);
	}
}