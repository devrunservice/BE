package com.devrun.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devrun.dto.CouponListForMento;
import com.devrun.entity.CouponIssued;
import com.devrun.entity.CouponViewEntity;
import com.devrun.entity.Couponregicode;
import com.devrun.entity.MemberEntity;
import com.devrun.repository.CouponIssuedRepository;
import com.devrun.repository.CouponViewRepository;
import com.devrun.repository.CouponregicodeRepository;
import com.devrun.util.CouponCodeGenerator;

@Service
public class CouponSerivce {

	@Autowired
	private CouponIssuedRepository couponIssuedRepository;

	@Autowired
	private CouponregicodeRepository couponregicodeRepository;

	@Autowired
	private CouponViewRepository couponviewRepositroy;

	public CouponIssued saveCouponDetail(CouponIssued couponBlueprint) {
		CouponIssued result = couponIssuedRepository.save(couponBlueprint);
		int quantity = couponBlueprint.getQuantity();

		saveCouponCode(quantity, couponBlueprint);

		return result;
	}

	public void saveCouponCode(int q, CouponIssued couponBlueprint) {

		List<Couponregicode> codelist = new ArrayList<Couponregicode>();

		int i = 1;
		while (i <= q) {
			Couponregicode couponregicode = new Couponregicode();
			String code = new CouponCodeGenerator().toString();
			couponregicode.setCouponcode(code);
			couponregicode.setIssuedno(couponBlueprint);
			codelist.add(couponregicode);
			i++;
		}
		couponregicodeRepository.saveAll(codelist);
	}

	public boolean validate(String code) {
		Pattern pattern = Pattern.compile("\\d{5}-[A-Za-z0-9]{12}");
		Matcher matcher = pattern.matcher(code);

		return matcher.find();
	}

	public int isequal(String code) {
		int i = couponregicodeRepository.countByCouponcode(code);
		return i;
	}

	public String checkcoupon(String code, String user) {
		String res = couponregicodeRepository.getCouponStatus(code, user);
		return res;
	}

	public String removecode(MemberEntity userEntity, List<String> TargetCouponCodeList) {

		List<CouponIssued> couponIssuedlist = couponIssuedRepository.findAllByIssueduser(userEntity);
		List<Couponregicode> couponcodes = new ArrayList<Couponregicode>();
		for (CouponIssued couponIssued : couponIssuedlist) {
			List<Couponregicode> couponcodelist = couponregicodeRepository.findAllByIssuedno(couponIssued);
			for (Couponregicode coupon : couponcodelist) {
				couponcodes.add(coupon);
			}
		}

		String rsl = "해당 멘토가 발행한 쿠폰이 아닙니다.";
		for (Couponregicode couponregicode : couponcodes) {
			for (String TargetCouponCode : TargetCouponCodeList) {
				if (couponregicode.getCouponcode().equals(TargetCouponCode)) {
					System.out.println("해당 멘토가 발행한 쿠폰으로 확인 되었으므로, 삭제");
					rsl = couponregicodeRepository.removecode(TargetCouponCode, "REMOVED");

				}
			}
		}

		return rsl;
	}

	public Page<CouponViewEntity> readmycoupon(MemberEntity userEntity, Pageable pageable) {
		Page<CouponViewEntity> couponlist = couponviewRepositroy.findAllByUserno2(userEntity.getUserNo(), pageable);
		for (CouponViewEntity couponViewEntity : couponlist.getContent()) {
			if (couponViewEntity.getExpirydate().after(Date.valueOf(LocalDate.now()))) {
				System.out.println("유효기간이 만료된 쿠폰입니다. 쿠폰 코드 : " + couponViewEntity.getCouponcode());
				couponViewEntity.setState("EXPIRY");
			}
		}
		return couponlist;
	}

	public Page<CouponListForMento> readCouponMadeByMento(MemberEntity userEntity, Pageable pageable) {
		Page<CouponListForMento> couponcodes = couponregicodeRepository.findCouponsByIssuedUser(userEntity.getUserNo(),
				pageable);
		return couponcodes;

	}
}
