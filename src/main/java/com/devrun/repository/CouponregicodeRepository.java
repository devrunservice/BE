package com.devrun.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devrun.dto.CouponListForMento;
import com.devrun.entity.CouponIssued;
import com.devrun.entity.Couponregicode;

@Repository
public interface CouponregicodeRepository extends JpaRepository<Couponregicode, Long> {

	int countByCouponcode(String code);

	@Procedure("coupon_register_process")
	@Transactional
	String getCouponStatus(String code, String user);

	@Procedure("coupon_remove_recover")
	String removecode(String removecoupon, String able);

	List<Couponregicode> findAllByIssuedno(CouponIssued couponIssued);

	@Query(value = "SELECT lecture.lecture_name AS lecturename, c.couponcode AS couponcode, d.discountrate AS discountrate , d.expirydate AS expirydate, d.issueddate AS issueddate, d.quantity AS quantity , c.state AS state, ROW_NUMBER() OVER() AS issuedno FROM couponregicode c JOIN couponissued d ON d.issuedno = c.issuedno JOIN lecture ON lecture.lectureid = d.lectureid WHERE d.issueduser = :userno", nativeQuery = true)
	Page<CouponListForMento> findCouponsByIssuedUser(@Param("userno") int userNo, Pageable pageable);
	
	@Query(value = "UPDATE couponregicode c SET c.state = \"USED\" WHERE c.couponcode = :code" , nativeQuery = true)
    void couponCodeUsed(@Param("code") String code);

}
