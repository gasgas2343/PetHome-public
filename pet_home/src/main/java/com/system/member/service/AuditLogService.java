package com.system.member.service;

import com.system.member.dto.response.AuditLogResponse;
import com.system.member.entity.AuditLogBean;
import com.system.member.entity.UserBean;
import com.system.member.exception.BusinessException;
import com.system.member.repository.AuditLogRepository;
import com.system.member.repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    AuditLogRepository auditLogRepository;
    @Autowired
    private UsersRepository usersRepository;

    public AuditLogResponse toAuditLogResponse(AuditLogBean auditLog){
        UserBean actor = auditLog.getActorUser();

        return new AuditLogResponse(
                auditLog.getId(),
                actor == null ? null : actor.getId(),
                actor == null ? null : actor.getEmail(),
                auditLog.getAction(),
                auditLog.getModuleCode(),
                auditLog.getTargetType(),
                auditLog.getTargetId(),
                auditLog.getMetadata(),
                auditLog.getIpAddress(),
                auditLog.getUserAgent(),
                auditLog.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogs(){
        List<AuditLogBean> logs = auditLogRepository.findAllByOrderByCreatedAtDesc();

        return logs.stream().map(this::toAuditLogResponse).toList();
    }

    @Transactional
    public void createAuditLog(Long actorUserId, String action, String moduleCode, String targetType,
                               Long targetId, String metadata, HttpServletRequest request
    ) {
        UserBean actor = usersRepository.findById(actorUserId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "找不到操作者"));

        AuditLogBean auditLog = new AuditLogBean();
        auditLog.setActorUser(actor);
        auditLog.setAction(action);
        auditLog.setModuleCode(moduleCode);
        auditLog.setTargetType(targetType);
        auditLog.setTargetId(targetId);
        auditLog.setMetadata(metadata);
        auditLog.setIpAddress(request.getRemoteAddr());
        auditLog.setUserAgent(request.getHeader("User-Agent"));

        auditLogRepository.save(auditLog);
    }
}

