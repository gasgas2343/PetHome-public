package com.system.petmap.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.system.petmap.dto.PlaceSubmissionCreateRequest;
import com.system.petmap.dto.SubmissionDetailResponse;
import com.system.petmap.dto.SubmissionStatisticsResponse;
import com.system.petmap.entity.Place;
import com.system.petmap.entity.PlaceSubmission;
import com.system.petmap.entity.PlaceTag;
import com.system.petmap.entity.PlaceTagId;
import com.system.petmap.entity.SubmissionTag;
import com.system.petmap.entity.SubmissionTagId;
import com.system.petmap.entity.Tag;
import com.system.member.exception.BusinessException;
import com.system.petmap.model.PlaceType;
import com.system.petmap.repository.PlaceRepository;
import com.system.petmap.repository.PlaceSubmissionRepository;
import com.system.petmap.repository.PlaceTagRepository;
import com.system.petmap.repository.SubmissionTagRepository;
import com.system.petmap.repository.PetMapTagRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceSubmissionService {

        private final PlaceSubmissionRepository placeSubmissionRepository;

        private final PlaceRepository placeRepository;

        private final SubmissionTagRepository submissionTagRepository;

        private final PlaceTagRepository placeTagRepository;

        private final PetMapTagRepository tagRepository;

        public PlaceSubmission create(
                        PlaceSubmissionCreateRequest request) {

                PlaceSubmission submission = new PlaceSubmission();

                submission.setUserId(
                                request.getUserId());

                submission.setName(
                                request.getName());

                submission.setPlaceType(
                                request.getPlaceType());

                submission.setAddress(
                                request.getAddress());

                submission.setLatitude(
                                request.getLatitude());

                submission.setLongitude(
                                request.getLongitude());

                submission.setPhone(
                                request.getPhone());

                submission.setDescription(
                                request.getDescription());

                submission.setStatus("PENDING");

                submission.setCreatedAt(
                                LocalDateTime.now());

                PlaceSubmission savedSubmission = placeSubmissionRepository
                                .save(submission);

                if (request.getTagIds() != null) {

                        for (Integer tagId : request.getTagIds()) {

                                SubmissionTagId id = new SubmissionTagId();

                                id.setSubmissionId(
                                                savedSubmission.getSubmissionId());

                                id.setTagId(tagId);

                                SubmissionTag submissionTag = new SubmissionTag();

                                submissionTag.setId(id);

                                submissionTagRepository
                                                .save(submissionTag);
                        }
                }

                return savedSubmission;
        }

        public List<PlaceSubmission> findAll() {
                return placeSubmissionRepository.findAll();
        }

        public PlaceSubmission findById(
                        Integer submissionId) {

                return findSubmission(submissionId);
        }

        public PlaceSubmission approve(Integer submissionId) {

                PlaceSubmission submission = findSubmission(submissionId);

                if (!"PENDING".equals(submission.getStatus())) {
                        throw new BusinessException(
                                        "SUBMISSION_ALREADY_REVIEWED",
                                        "此投稿已處理");
                }

                Place place = new Place();

                place.setName(
                                submission.getName());

                place.setPlaceType(

                                PlaceType.valueOf(
                                                submission.getPlaceType())

                );

                place.setAddress(
                                submission.getAddress());

                place.setLatitude(
                                submission.getLatitude());

                place.setLongitude(
                                submission.getLongitude());

                place.setPhone(
                                submission.getPhone());

                LocalDateTime now = LocalDateTime.now();

                place.setStatus("ACTIVE");

                place.setReviewCount(0);

                place.setCreatedAt(now);

                place.setUpdatedAt(now);

                submission.setReviewedAt(now);

                Place savedPlace = placeRepository.save(place);

                List<SubmissionTag> submissionTags = submissionTagRepository
                                .findByIdSubmissionId(
                                                submissionId);

                for (SubmissionTag submissionTag : submissionTags) {

                        Integer tagId = submissionTag.getId().getTagId();

                        PlaceTagId placeTagId = new PlaceTagId();

                        placeTagId.setPlaceId(
                                        savedPlace.getPlaceId());

                        placeTagId.setTagId(tagId);

                        PlaceTag placeTag = new PlaceTag();

                        placeTag.setId(placeTagId);

                        placeTagRepository.save(placeTag);
                }

                submission.setStatus("APPROVED");

                submission.setReviewedAt(now);

                return placeSubmissionRepository
                                .save(submission);
        }

        public PlaceSubmission reject(
                        Integer submissionId,
                        String adminNote) {

                PlaceSubmission submission = findSubmission(submissionId);

                if (!"PENDING".equals(submission.getStatus())) {
                        throw new BusinessException(
                                        "SUBMISSION_ALREADY_REVIEWED",
                                        "此投稿已處理");
                }

                submission.setStatus(
                                "REJECTED");

                submission.setAdminNote(
                                adminNote);

                submission.setReviewedAt(
                                LocalDateTime.now());

                return placeSubmissionRepository
                                .save(submission);
        }

        public PlaceSubmission requestRevision(
                        Integer submissionId,
                        String adminNote) {

                PlaceSubmission submission = findSubmission(submissionId);

                if (!"PENDING".equals(submission.getStatus())) {
                        throw new BusinessException(
                                        "SUBMISSION_ALREADY_REVIEWED",
                                        "此投稿已處理");
                }

                submission.setStatus(
                                "NEED_REVISION");

                submission.setAdminNote(
                                adminNote);

                submission.setReviewedAt(
                                LocalDateTime.now());

                return placeSubmissionRepository
                                .save(submission);
        }

        public SubmissionDetailResponse getSubmissionDetail(
                        Integer submissionId) {

                PlaceSubmission submission = findSubmission(submissionId);

                SubmissionDetailResponse response = new SubmissionDetailResponse();

                response.setSubmissionId(
                                submission.getSubmissionId());

                response.setName(
                                submission.getName());

                response.setPlaceType(
                                submission.getPlaceType());

                response.setAddress(
                                submission.getAddress());

                response.setStatus(
                                submission.getStatus());

                response.setPhone(
                                submission.getPhone());

                response.setDescription(
                                submission.getDescription());

                response.setAdminNote(
                                submission.getAdminNote());

                response.setCreatedAt(
                                submission.getCreatedAt());

                response.setReviewedAt(
                                submission.getReviewedAt());

                response.setLatitude(
                                submission.getLatitude());

                response.setLongitude(
                                submission.getLongitude());

                List<SubmissionTag> submissionTags = submissionTagRepository
                                .findByIdSubmissionId(
                                                submissionId);

                List<String> tagNames = new ArrayList<>();
                List<Integer> tagIds = new ArrayList<>();

                for (SubmissionTag submissionTag : submissionTags) {

                        Integer tagId = submissionTag.getId().getTagId();

                        tagIds.add(tagId);

                        Tag tag = tagRepository
                                        .findById(tagId)
                                        .orElseThrow(() -> new BusinessException(
                                                        "TAG_NOT_FOUND",
                                                        "找不到 Tag"));

                        tagNames.add(tag.getName());
                }

                response.setTagIds(tagIds);

                response.setTags(tagNames);

                return response;
        }

        public List<PlaceSubmission> findByUserId(Long userId) {

                return placeSubmissionRepository
                                .findByUserId(userId);
        }

        public List<PlaceSubmission> findByStatus(
                        String status) {

                return placeSubmissionRepository
                                .findByStatus(status);
        }

        public SubmissionStatisticsResponse getStatistics() {

                return new SubmissionStatisticsResponse(

                                placeSubmissionRepository
                                                .countByStatus(
                                                                "PENDING"),

                                placeSubmissionRepository
                                                .countByStatus(
                                                                "APPROVED"),

                                placeSubmissionRepository
                                                .countByStatus(
                                                                "REJECTED"),

                                placeSubmissionRepository
                                                .countByStatus(
                                                                "NEED_REVISION")

                );
        }

        @Transactional
        public void resubmit(
                        Integer submissionId) {

                PlaceSubmission submission = findSubmission(submissionId);

                submission.setStatus(
                                "PENDING");

                submission.setAdminNote(
                                null);

                submission.setReviewedAt(
                                null);
        }

        @Transactional
        public PlaceSubmission update(
                        Integer submissionId,
                        PlaceSubmissionCreateRequest request) {

                PlaceSubmission submission = findSubmission(submissionId);

                submission.setName(
                                request.getName());

                submission.setPlaceType(
                                request.getPlaceType());

                submission.setAddress(
                                request.getAddress());

                submission.setPhone(
                                request.getPhone());

                submission.setDescription(
                                request.getDescription());

                submission.setLatitude(
                                request.getLatitude());

                submission.setLongitude(
                                request.getLongitude());

                // ===== 更新 Tag =====

                submissionTagRepository.deleteByIdSubmissionId(
                                submissionId);

                if (request.getTagIds() != null) {

                        for (Integer tagId : request.getTagIds()) {

                                SubmissionTag tag = new SubmissionTag();

                                SubmissionTagId id = new SubmissionTagId();

                                id.setSubmissionId(
                                                submissionId);

                                id.setTagId(
                                                tagId);

                                tag.setId(id);

                                submissionTagRepository.save(
                                                tag);
                        }
                }

                return placeSubmissionRepository.save(
                                submission);
        }

        private PlaceSubmission findSubmission(Integer submissionId) {

                return placeSubmissionRepository
                                .findById(submissionId)
                                .orElseThrow(() -> new BusinessException(
                                                "SUBMISSION_NOT_FOUND",
                                                "找不到投稿"));

        }
}