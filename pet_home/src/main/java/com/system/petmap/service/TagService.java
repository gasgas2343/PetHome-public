package com.system.petmap.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.system.member.exception.BusinessException;
import com.system.petmap.dto.TagCreateRequest;
import com.system.petmap.dto.TagUpdateRequest;
import com.system.petmap.entity.Tag;
import com.system.petmap.repository.PetMapTagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {

    private final PetMapTagRepository tagRepository;

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public Tag findById(Integer id) {

        return tagRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "TAG_NOT_FOUND",
                        "找不到 Tag"));

    }

    public Tag create(TagCreateRequest request) {

        Tag tag = new Tag();

        tag.setName(request.getName());
        tag.setCategory(request.getCategory());

        return tagRepository.save(tag);
    }

    public Tag update(
            Integer id,
            TagUpdateRequest request) {

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "TAG_NOT_FOUND",
                        "找不到 Tag"));

        tag.setName(request.getName());
        tag.setCategory(request.getCategory());

        return tagRepository.save(tag);
    }

    public void delete(Integer id) {

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "TAG_NOT_FOUND",
                        "找不到 Tag"));

        tagRepository.delete(tag);

    }
}
