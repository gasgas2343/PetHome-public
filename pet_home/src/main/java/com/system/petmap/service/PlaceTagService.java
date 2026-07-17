package com.system.petmap.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.system.member.exception.BusinessException;
import com.system.petmap.dto.PlaceTagCreateRequest;
import com.system.petmap.entity.Place;
import com.system.petmap.entity.PlaceTag;
import com.system.petmap.entity.PlaceTagId;
import com.system.petmap.entity.Tag;
import com.system.petmap.repository.PlaceRepository;
import com.system.petmap.repository.PlaceTagRepository;
import com.system.petmap.repository.PetMapTagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceTagService {

    private final PlaceTagRepository placeTagRepository;

    private final PetMapTagRepository tagRepository;

    private final PlaceRepository placeRepository;

    public PlaceTag create(
            PlaceTagCreateRequest request) {

        placeRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new BusinessException(
                        "PLACE_NOT_FOUND",
                        "找不到景點"));

        tagRepository.findById(request.getTagId())
                .orElseThrow(() -> new BusinessException(
                        "TAG_NOT_FOUND",
                        "找不到 Tag"));

        PlaceTagId id = new PlaceTagId(
                request.getPlaceId(),
                request.getTagId());

        PlaceTag placeTag = new PlaceTag();

        placeTag.setId(id);

        return placeTagRepository.save(placeTag);
    }

    public List<Tag> findTagsByPlaceId(Integer placeId) {

        List<PlaceTag> placeTags = placeTagRepository.findByIdPlaceId(placeId);

        List<Tag> tags = new ArrayList<>();

        for (PlaceTag placeTag : placeTags) {

            Integer tagId = placeTag.getId().getTagId();

            tagRepository.findById(tagId)
                    .ifPresent(tags::add);
        }

        return tags;
    }

    public List<Place> findPlacesByTagId(Integer tagId) {

        List<PlaceTag> placeTags = placeTagRepository.findByIdTagId(tagId);

        List<Place> places = new ArrayList<>();

        for (PlaceTag placeTag : placeTags) {

            Integer placeId = placeTag.getId().getPlaceId();

            placeRepository.findById(placeId)
                    .ifPresent(places::add);
        }

        return places;
    }

    public void delete(
            Integer placeId,
            Integer tagId) {

        PlaceTagId id = new PlaceTagId(placeId, tagId);

        PlaceTag placeTag = placeTagRepository
                .findById(id)
                .orElseThrow(() -> new BusinessException(
                        "PLACE_TAG_NOT_FOUND",
                        "找不到景點標籤"));

        placeTagRepository.delete(placeTag);
    }

    public List<String> findTagNamesByPlaceId(Integer placeId) {

        List<Tag> tags = findTagsByPlaceId(placeId);

        return tags.stream()
                .map(Tag::getName)
                .toList();
    }
}
