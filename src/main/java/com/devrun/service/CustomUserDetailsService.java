package com.devrun.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
        // Status 정보를 GrantedAuthority로 변환
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(memberEntity.getRole().name()));

//        return new CustomUserDetails(memberEntity.getId(), memberEntity.getPassword(), memberEntity.getEmail(), new ArrayList<>());
//        return new org.springframework.security.core.userdetails.User(memberEntity.getId(), memberEntity.getPassword(), new ArrayList<>());
        return new org.springframework.security.core.userdetails.User(memberEntity.getId(), memberEntity.getPassword(), authorities);
    }

}