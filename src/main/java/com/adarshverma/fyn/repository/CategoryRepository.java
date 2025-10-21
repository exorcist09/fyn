package com.adarshverma.fyn.repository;

import com.adarshverma.fyn.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    //    fetches all the categories for a given profile id from the database
    List<CategoryEntity> findByProfileId(Long profileId);

    //    sele--ct * from tbl_category where id = ? and profile_id = ?; // ? means what you will enter there
    Optional<CategoryEntity> findByIdAndProfileId(Long categoryId, Long profileId);

//    Select * from tbl_category where type = ? and profile_id = ?;
    List<CategoryEntity> findByTypeAndProfileId(String type, Long profileId);

//    as one user cannot create multiple categories with same name as we cannot use unique constraint here as profile_id will be different for different users and if you make name unique then no two users can have same category name which is not correct
    Boolean existsByNameAndProfileId(String name, Long profileId);
}
