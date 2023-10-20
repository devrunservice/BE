package com.devrun.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devrun.dto.CouponIssuanceRequestDTO;
import com.devrun.dto.CouponListForMento;
import com.devrun.dto.CouponListForStudent;
import com.devrun.entity.CouponIssued;
import com.devrun.entity.Couponregicode;
import com.devrun.entity.MemberEntity;
import com.devrun.repository.CouponIssuedRepository;
import com.devrun.repository.CouponViewRepository;
import com.devrun.repository.CouponregicodeRepository;
import com.devrun.util.CouponCodeGenerator;
import com.devrun.youtube.Lecture;
import com.devrun.youtube.LectureRepository;

@Service
public class CouponSerivce {

	@Autowired
	private CouponIssuedRepository couponIssuedRepository;

	@Autowired
	private CouponregicodeRepository couponregicodeRepository;

	@Autowired
	private CouponViewRepository couponviewRepositroy;

	@Autowired
	private LectureRepository lectureRepository;

	public CouponIssuanceRequestDTO saveCouponDetail(CouponIssuanceRequestDTO couponIssuanceRequestDTO,
			MemberEntity mentoEntity) {
		Lecture lectureEntity = lectureRepository.findByLectureNameAndMentoId(couponIssuanceRequestDTO.getLectureName(),
				mentoEntity);
		if (lectureEntity == null) {
			throw new NullPointerException("Lecture not found");
		}
		;

		CouponIssued couponeIssued = new CouponIssued();
		couponeIssued.setIssueduser(mentoEntity);
		couponeIssued.setLectureid(lectureEntity);
		couponeIssued.setCoupontype(couponIssuanceRequestDTO.getCoupontype());
		couponeIssued.setDiscountrate(couponIssuanceRequestDTO.getDiscountrate());
		couponeIssued.setExpirydate(couponIssuanceRequestDTO.getExpirydate());
		couponeIssued.setQuantity(couponIssuanceRequestDTO.getQuantity());

		couponIssuedRepository.save(couponeIssued);

		return couponIssuanceRequestDTO;
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

	public String removecode(MemberEntity userEntity, String TargetCouponCode) {

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
			if (couponregicode.getCouponcode().equals(TargetCouponCode)) {
				System.out.println("해당 멘토가 발행한 쿠폰으로 확인 되었음");
				if (couponregicode.getState().toString().equals("REMOVED")) {
					couponregicodeRepository.removecode(TargetCouponCode, "ACTIVE");
					rsl = "복구 처리 되었습니다.";
				}
				if (couponregicode.getState().toString().equals("ACTIVE")) {
					couponregicodeRepository.removecode(TargetCouponCode, "REMOVED");
					rsl = "정지 처리 되었습니다.";
				}
			}
		}
		return rsl;
	}

	public List<CouponListForStudent> readmycoupon(MemberEntity userEntity) {
		List<CouponListForStudent> couponlist = couponviewRepositroy.findAllByUserno2(userEntity.getUserNo());
		return couponlist;
	}

	public Page<CouponListForMento> readCouponMadeByMento(MemberEntity userEntity, Pageable pageable) {
		Page<CouponListForMento> couponcodes = couponregicodeRepository.findCouponsByIssuedUser(userEntity.getUserNo(),
				pageable);
		return couponcodes;

	}
}
