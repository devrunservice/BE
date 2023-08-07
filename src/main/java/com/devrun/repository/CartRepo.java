package com.devrun.repository;

import com.devrun.dto.CartDTO;
import com.devrun.entity.Cart;
import com.devrun.entity.PointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {

    @Procedure("cart_delete")
    @Transactional
    String deleteInCart(String userid, String lecturetitle);

    List<Cart> findAllByMemberEntity_id(String id);
}
