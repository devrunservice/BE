package com.devrun.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.devrun.entity.PointHistoryEntity;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistoryEntity, Long> {

	@Query(value = "SELECT p.explanation AS explanation, p.pointupdown AS pointupdown, "
	        + "p.updatetime AS updatetime, p.productname AS productname, "
	        + "ROW_NUMBER() OVER(ORDER BY p.updatetime DESC) AS pointno "
	        + "FROM pointhistory p "
	        + "WHERE p.user_no = :usrno", nativeQuery = true)
	Page<PointHis> findAllbyPointHistoryEntity(@Param("usrno") int usrno, PageRequest pageRequest);





   


}
