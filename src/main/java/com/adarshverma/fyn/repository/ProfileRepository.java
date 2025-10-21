package com.adarshverma.fyn.repository;

import com.adarshverma.fyn.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    //    Select * From tbl_profile where email = ;
    Optional<ProfileEntity> findByEmail(String email);

    // SELECT activationToken from tbl_profile
    Optional<ProfileEntity> findByActivationToken(String activationToken);

}
