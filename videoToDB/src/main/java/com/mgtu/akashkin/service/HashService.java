package com.mgtu.akashkin.service;

import com.mgtu.akashkin.model.HashInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface HashService {


    @Nonnull
    List<HashInfo> findAllUsers();


    @Nullable
    void registrationUser(@Nonnull HashInfo userinfo);

}
