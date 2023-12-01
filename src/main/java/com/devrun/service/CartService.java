package com.devrun.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.devrun.dto.CouponListInCart;
import com.devrun.dto.LectureInfo;
import com.devrun.entity.Cart;
import com.devrun.entity.Cart.removed;
import com.devrun.entity.Contact;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.PointEntity;
import com.devrun.exception.RestApiException;
import com.devrun.repository.CartRepo;
import com.devrun.repository.ContactRepository;
import com.devrun.repository.CouponViewRepository;
import com.devrun.repository.PointRepository;
import com.devrun.youtube.Lecture;
import com.devrun.youtube.LectureService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

	private final CartRepo cartRepo;
	private final ContactRepository contactRepository;
	private final LectureService lectureservice;
	private final MyLectureService myLectureService;
	private final PointRepository pointRepository;
	private final CouponViewRepository couponViewRepository;
	/**
	 * 유저의 장바구니에 담긴 강의들을 보여줍니다.
	 * @param userEntity
	 * @return
	 */
	public List<LectureInfo> showlectureInfo(MemberEntity userEntity) {

		List<Cart> Carts = cartRepo.findAllByMemberEntity(userEntity);
		List<LectureInfo> lecutreInfolist = new ArrayList<LectureInfo>();

		Carts.removeIf(cart -> (cart.getDeleteop().equals(removed.DISABLE)));

		for (Cart e : Carts) {
			LectureInfo lectureInfo = e.getLectureInfo();
			lecutreInfolist.add(lectureInfo);
		}

		return lecutreInfolist;
	}
	
	/**
	 * 장바구니 화면에 보여질 유저의 정보를 보여줍니다.<br>
	 * 유저의 이름 , 이메일 , 연락처 , 보유 포인트, 유저구별코드
	 * @param userEntity
	 * @return
	 */
	public Map<String, Object> showBuyerInfo(MemberEntity userEntity) {

		Map<String, Object> BuyerInfo = new HashMap<String, Object>();

		// BuyerInfo
		Contact c = contactRepository.findByMemberEntity(userEntity);

		String userPhonumber = c.getPhonenumber();
		String userEmail = c.getEmail();
		String username = userEntity.getName();
		int userNo = userEntity.getUserNo();

		BuyerInfo.put("userName", username);
		BuyerInfo.put("userEmail", userEmail);
		BuyerInfo.put("userPhonumber", userPhonumber);
		BuyerInfo.put("userNo", userNo);

		// point
		PointEntity pointEntity = pointRepository.findByMemberEntity_userNo(userEntity.getUserNo());
		int myPoint = pointEntity.getMypoint();
		BuyerInfo.put("userPoint", myPoint);

		return BuyerInfo;

	}
	
	/**
	 * 유저가 가지고 있는 쿠폰에 대한 정보를 보여줍니다.<br>
	 * 강의 고유 아이디 , 강의명 , 쿠폰코드 , 할인율 , 만료일 , 쿠폰상태(사용 가능 , 사용 불가 , 기간 만료 , 사용됨)
	 * @param userEntity
	 * @return
	 */
	public List<CouponListInCart> showUserCoupon(MemberEntity userEntity) {
		List<CouponListInCart> couponListInCart = couponViewRepository.findByUserno(userEntity.getUserNo());
		return couponListInCart;

	}
	/**
	 * 장바구니에 강의를 추가합니다.<br>
	 * 해당 작업 시 예외가 발생하면 해당 처리 후 관련된 메세지를 반환합니다.<br>
	 * 예외 :<br>
	 * 이미 구매한 강의인 경우<br>
	 * 강의를 장바구니에서 삭제했다가 다시 담은 경우<br>
	 * 강의를 장바구니에 이미 담은 경우
	 * @param userEntity
	 * @param lectureId
	 * @return
	 */
	public String putInCart(MemberEntity userEntity, Long lectureId) {
		String resultMsg;
		Lecture lecture = lectureservice.findByLectureID(lectureId);
		try {
			myLectureService.verifyUserHasLecture(userEntity, lecture);
			return resultMsg = "이미 구매한 강의입니다.";

		} catch (RestApiException e) {
			Cart cart = cartRepo.findByMemberEntityAndLecture(userEntity, lecture);
			if (cart != null && cart.getDeleteop().equals(removed.DISABLE)) {// 장바구니에 담겨 있으나 삭제 처리 됐던 경우
				cart.setDeleteop(removed.ENABLE);
				cartRepo.save(cart);
				resultMsg = "장바구니에 다시 담았습니다.";
			} else if (cart != null && cart.getDeleteop().equals(removed.ENABLE)) {// 장바구니에 담겨 있고, 이미 등록 처리 됐을 경우
				resultMsg = "장바구니에 이미 존재합니다.";
			} else {// 처음 장바구니에 등록하는 경우
				Cart newcart = new Cart();
				newcart.setLecture(lecture);
				newcart.setDeleteop(removed.ENABLE);
				newcart.setMemberEntity(userEntity);
				cartRepo.save(newcart);
				resultMsg = "장바구니에 담았습니다.";
			}
			return resultMsg;
		}
	}
	/**
	 * 장바구니에서 보이지 않도록 삭제 처리합니다.
	 * @param userEntity
	 * @param cartId
	 */
	public void deleteInCart(MemberEntity userEntity, List<Long> cartId) {
		List<Cart> cartEntitys = cartRepo.findByMemberEntityAndCartnoIn(userEntity, cartId);
		for (Cart c : cartEntitys) {
			c.setDeleteop(removed.DISABLE);
			cartRepo.save(c);
		}
	}

}
