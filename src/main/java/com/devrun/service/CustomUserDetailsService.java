package com.devrun.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.devrun.entity.MemberEntity;
import com.devrun.repository.MemberEntityRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberEntityRepository memberEntityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	MemberEntity memberEntity = memberEntityRepository.findById(username);
        if (memberEntity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(memberEntity.getId(), memberEntity.getPassword(), new ArrayList<>());
    }
}