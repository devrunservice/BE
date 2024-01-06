package com.devrun.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.devrun.dto.MentoMoneyDTO;
import com.devrun.repository.MentoMoney;
import com.devrun.repository.PaymentRepository;
import com.devrun.youtube.LectureRepository;

@Service
public class MentoMoneyService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private LectureRepository lectureRepository;

    public PageImpl<MentoMoneyDTO> searchMoney(int usrno, Pageable pageable) {
        System.err.println(usrno);
        List<Long> lectureIds = lectureRepository.findLectureIdsByUserNo(usrno);
        System.err.println(lectureIds);
        
        //native쿼리 사용하기위해 인터페이스로 받기
        Page<MentoMoney> payments = paymentRepository.findPaymentsByLectureIds(lectureIds, pageable);

        // 강사 실제 총 수익 초기화
        int[] totalAmount = {0};
        // DTO로 변환
        List<MentoMoneyDTO> dtos = payments.map(mentoMoney -> {
            // 세후 금액 계산
            int afterAmount = (int) (mentoMoney.getPaidAmount() * 0.9);

            // DTO에 값 설정
            MentoMoneyDTO mentoMoneyDTO = new MentoMoneyDTO();
            mentoMoneyDTO.setMoneyNo(mentoMoney.getMoneyNo());
            mentoMoneyDTO.setName(mentoMoney.getName());
            mentoMoneyDTO.setPaidAmount(mentoMoney.getPaidAmount());
            mentoMoneyDTO.setPaymentDate(mentoMoney.getPaymentDate());
            mentoMoneyDTO.setAfterAmount(afterAmount);
            return mentoMoneyDTO;
        }).getContent();

        // 총 수익 계산
        totalAmount[0] = dtos.stream().mapToInt(MentoMoneyDTO::getAfterAmount).sum();
        
        // 총 수익 설정
        dtos.forEach(dto -> dto.setTotalAmount(totalAmount[0]));

        System.err.println(dtos);
        return new PageImpl<>(dtos, pageable, payments.getTotalElements());
    }
}


