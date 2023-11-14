package com.devrun.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devrun.dto.CouponListForStudent;
import com.devrun.dto.CouponListInCart;
import com.devrun.entity.CouponViewEntity;

@Repository
public interface CouponViewRepository extends JpaRepository<CouponViewEntity, Long> {

	CouponViewEntity findByCouponcode(String couponcode);

	List<CouponViewEntity> findAllByUserno(int userNo);
	
	@Query(value = "SELECT d.target AS lecturename, d.couponcode AS couponcode, d.discountrate AS discountrate , d.expirydate AS expirydate, d.issueddate AS issueddate, d.state AS state, ROW_NUMBER() OVER() AS issuedno FROM coupon_manage d WHERE d.userno = :userno ORDER BY target ASC, issueddate DESC", nativeQuery = true)
	List<CouponListForStudent> findAllByUserno2(@Param("userno") int userNo);
	
	@Query(value = "SELECT"	
			+ " cm.target AS lecturename,\r\n"
			+ "	cm.targetid AS lectureid,\r\n"
			+ "	cm.discountrate AS discountrate,\r\n"
			+ "	cm.expirydate AS expirydate,\r\n"
			+ "	cm.state AS state,\r\n"
			+ "	cm.couponcode AS couponcode\r\n"
			+ "FROM\r\n"
			+ "(SELECT ca.lectureid , lt.lecture_name AS lecture_name, lt.lecture_thumbnail AS lecture_thumbnail, lt.lecture_intro AS lecture_intro, lt.lecture_price AS lecture_price, ca.user_no AS user_no\r\n"
			+ "FROM lecture AS lt JOIN cart AS ca ON lt.lectureid = ca.lectureid WHERE ca.user_no= :userno) AS join_lt_ca RIGHT JOIN coupon_manage AS cm ON join_lt_ca.lecture_name = cm.target WHERE cm.userno = :userno", nativeQuery = true)
	List<CouponListInCart> showUserCouponByUserno(@Param("userno") int userNo);



}
