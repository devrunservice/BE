package com.devrun.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devrun.dto.CouponIssuanceRequestDTO;
import com.devrun.dto.CouponListForMento;
import com.devrun.dto.CouponListForStudent;
import com.devrun.entity.CouponIssued;
import com.devrun.entity.Couponregicode;
import com.devrun.entity.MemberEntity;
import com.devrun.exception.CommonErrorCode;
import com.devrun.exception.RestApiException;
import com.devrun.exception.UserErrorCode;
import com.devrun.repository.CouponIssuedRepository;
import com.devrun.repository.CouponViewRepository;
import com.devrun.repository.CouponregicodeRepository;
import com.devrun.util.CouponCodeGenerator;
import com.devrun.youtube.Lecture;
import com.devrun.youtube.LectureRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponSerivce {

	private final CouponIssuedRepository couponIssuedRepository;
	private final CouponregicodeRepository couponregicodeRepository;
	private final CouponViewRepository couponviewRepositroy;
	private final LectureRepository lectureRepository;

	/**
	 * 요청된 정보를 바탕으로 쿠폰 데이터를 생성하고 데이터베이스에 저장합니다.
	 * 
	 * @param couponIssuanceRequestDTO
	 * @param mentoEntity
	 * @return
	 */
	public CouponIssuanceRequestDTO saveCouponDetail(CouponIssuanceRequestDTO couponIssuanceRequestDTO,
			MemberEntity mentoEntity) {
		Optional<Lecture> lectureEntity = lectureRepository.findById(couponIssuanceRequestDTO.getLectureId());
		if (lectureEntity.isPresent()) {
			if (lectureEntity.get().getMentoId().equals(mentoEntity)) {
			} else {
				throw new RestApiException(UserErrorCode.USERHASNOTLECTURE);
			}
		} else {
			throw new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
		}

		CouponIssued couponeIssued = new CouponIssued();
		couponeIssued.setIssueduser(mentoEntity);
		couponeIssued.setLectureid(lectureEntity.get());
		// couponeIssued.setCoupontype(couponIssuanceRequestDTO.getCoupontype());
		couponeIssued.setDiscountrate(couponIssuanceRequestDTO.getDiscountrate());
		couponeIssued.setExpirydate(couponIssuanceRequestDTO.getExpirydate());
		couponeIssued.setQuantity(couponIssuanceRequestDTO.getQuantity());

		couponIssuedRepository.save(couponeIssued);
		saveCouponCode(couponeIssued);
		return couponIssuanceRequestDTO;
	}

	/**
	 * 발행 수 만큼 랜덤한 쿠폰 코드를 생성하여 데이터베이스에 저장합니다.<br>
	 * 생성 규칙(총17자) : 앞5글자는 숫자 - 뒤12글자는 숫자, 알파벳 대문자, 알파벳 소문자 조합
	 * 
	 * @param couponeIssued
	 */
	public void saveCouponCode(CouponIssued couponeIssued) {

		List<Couponregicode> codelist = new ArrayList<Couponregicode>();

		int i = 1;
		while (i <= couponeIssued.getQuantity()) {
			Couponregicode couponregicode = new Couponregicode();
			String code = new CouponCodeGenerator().toString();
			couponregicode.setCouponcode(code);
			couponregicode.setIssuedno(couponeIssued);
			codelist.add(couponregicode);
			i++;
		}
		couponregicodeRepository.saveAll(codelist);
	}

	/**
	 * 쿠폰 코드가 코드 생성 규칙을 위배하는 지를 확인합니다.<br>
	 * 생성 규칙(총17자) : 앞5글자는 숫자 - 뒤12글자는 숫자, 알파벳 대문자, 알파벳 소문자 조합
	 * 
	 * @param code
	 * @return
	 */
	public boolean validate(String code) {
		Pattern pattern = Pattern.compile("\\d{5}-[A-Za-z0-9]{12}");
		Matcher matcher = pattern.matcher(code);

		return matcher.find();
	}

	/**
	 * 해당 쿠폰 코드가 존재하는 지 확인합니다.
	 * 
	 * @param code
	 * @return
	 */
	public int isequal(String code) {
		return couponregicodeRepository.countByCouponcode(code);
	}

	/**
	 * 해당 쿠폰을 등록하는 DB 프로시저를 실행하고 그 결과 메세지를 반환합니다.
	 * 
	 * @param code
	 * @param user
	 * @return
	 */
	public String checkcoupon(String code, String user) {
		return couponregicodeRepository.getCouponStatus(code, user);
	}

	/**
	 * 해당 쿠폰이 사용자가 발행한 쿠폰인지 검증하고, 삭제하거나, 복구합니다.
	 * 
	 * @param userEntity
	 * @param TargetCouponCode
	 * @return
	 */
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

	/**
	 * 학생 사용자가 가진 쿠폰을 보여줍니다.
	 * 
	 * @param userEntity
	 * @return
	 */
	public List<CouponListForStudent> readmycoupon(MemberEntity userEntity) {
		return couponviewRepositroy.findByUserno2(userEntity.getUserNo());
	}

	/**
	 * 강사 사용자가 발행한 쿠폰을 보여줍니다.
	 * 
	 * @param userEntity
	 * @param pageable
	 * @return
	 */
	public Page<CouponListForMento> readCouponMadeByMento(MemberEntity userEntity, Pageable pageable) {
		return couponregicodeRepository.findCouponsByIssuedUser(userEntity.getUserNo(), pageable);
	}

	/**
	 * 강사 사용자가 등록한 강의 목록을 보여줍니다. 
	 * @param userEntity
	 */
	public List<Map<String, String>> findMentoLecture(MemberEntity userEntity) {
		List<Lecture> lecturelist = lectureRepository.findByMentoId(userEntity);

		List<Map<String, String>> dto = new ArrayList<Map<String, String>>();

		int count = 0;
		do {
			Map<String, String> dtoElement = new HashMap<String, String>();
			Lecture lecture = lecturelist.get(count);
			dtoElement.put("lectureId", lecture.getLectureid().toString());
			dtoElement.put("lectureName", lecture.getLectureName());
			dto.add(dtoElement);
			count++;
		} while (count < lecturelist.size());
		return dto;
	}
}
