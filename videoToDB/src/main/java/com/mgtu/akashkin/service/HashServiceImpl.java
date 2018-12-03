package com.mgtu.akashkin.service;

import com.mgtu.akashkin.entity.Hash;
import com.mgtu.akashkin.model.HashInfo;
import com.mgtu.akashkin.repository.HashRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HashServiceImpl
        implements HashService {

    @Autowired
    private HashRepos userRepos;



    @Nullable
    @Override
    public void registrationUser(@Nonnull HashInfo userInfo) {



            userRepos.saveAndFlush(createUser(userInfo));

    }


    @Nonnull
    @Override
    public List<HashInfo> findAllUsers() {
        return userRepos.findAll()
                .stream()
                .map(this::createUserInfo)
                .collect(Collectors.toList());
    }



    @Nonnull
    private HashInfo createUserInfo(@Nonnull Hash hash) {

        HashInfo hashInfo = new HashInfo();
        hashInfo.setHash(hash.getHash());

        return hashInfo;
    }

    @Nonnull
    private Hash createUser(@Nonnull HashInfo hashInfo) {

        Hash hash = new Hash();
        hash.setHash(hashInfo.getHash());

        return hash;
    }

}
