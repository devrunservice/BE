package com.devrun.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import com.devrun.entity.Cart;
import com.devrun.entity.MemberEntity;
import com.devrun.youtube.Lecture;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {

    @Procedure("cart_delete")
    @Transactional
    String deleteInCart(String userid, String lecturetitle);

    List<Cart> findAllByMemberEntity_id(String id);

	List<Cart> findAllByMemberEntity(MemberEntity userEntity);

	Long countByMemberEntityAndLecture(MemberEntity userEntity, Lecture lecture);

	Cart findByMemberEntityAndLecture(MemberEntity userEntity, Lecture lecture);
}
