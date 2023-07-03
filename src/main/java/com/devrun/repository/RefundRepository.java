package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devrun.entity.RefundEntity;


@Repository
public interface RefundRepository extends JpaRepository<RefundEntity, Long> {

}
