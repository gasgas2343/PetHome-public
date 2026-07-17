package com.system.petmap.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petmap.entity.Tag;


public interface PetMapTagRepository
        extends JpaRepository<Tag, Integer> {

}
