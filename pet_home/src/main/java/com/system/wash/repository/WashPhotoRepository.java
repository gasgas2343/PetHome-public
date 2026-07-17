package com.system.wash.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.system.wash.entity.WashPhotoBean;

public interface WashPhotoRepository extends JpaRepository<WashPhotoBean, Integer> {
}
