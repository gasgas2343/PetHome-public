// package com.system.member.shop.repository;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;
// import com.system.member.shop.entity.Brand;

// @Repository
// public interface BrandRepository extends JpaRepository<Brand, Integer> {
// }

package com.system.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.shop.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
}