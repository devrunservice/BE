package com.devrun.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Long issuedno = couponBlueprint.getIssuedno();

        saveCouponCode(quantity , issuedno);
        return result;
    }

    public void saveCouponCode(int q , Long aLong){

        List<Couponregicode> codelist = new ArrayList<Couponregicode>();

        int i = 1;
        while (i <= q){
            Couponregicode couponregicode = new Couponregicode();
            String code = new CouponCodeGenerator().toString();
            couponregicode.setCouponcode(code);
            couponregicode.setIssuedno(aLong);
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
        String res = couponregicodeRepository.getCouponStatus(code , user);
        return res;
    }

    public String removecode(String removecoupon , int able) {
        String res = couponregicodeRepository.removecode(removecoupon, able);
        return res;
    }

	public  List<CouponViewEntity> readmycoupon(MemberEntity userEntity) {
		List<CouponViewEntity> couponlist = couponviewRepositroy.findAllByUserno(userEntity.getUserNo());
		//List<CouponViewEntity> couponlist = couponviewRepositroy.activatequery(userEntity.getUserNo());
		return couponlist;
	}
}
