package com.system.wash.repository;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalDate;

import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import com.system.wash.entity.WashAppointmentBean;

@Repository
public class WashAppointmentDAOImpl implements WashAppointmentDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public long count(JSONObject obj) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<WashAppointmentBean> table = criteriaQuery.from(WashAppointmentBean.class);

        List<Predicate> predicates = buildPredicates(obj, criteriaBuilder, table);
        if (!predicates.isEmpty()) {
            criteriaQuery = criteriaQuery.where(predicates.toArray(new Predicate[0]));
        }

        criteriaQuery = criteriaQuery.select(criteriaBuilder.count(table));
        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
        Long result = typedQuery.getSingleResult();
        return result != null ? result.longValue() : 0L;
    }

    @Override
    public List<WashAppointmentBean> find(JSONObject obj) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<WashAppointmentBean> criteriaQuery = criteriaBuilder.createQuery(WashAppointmentBean.class);
        Root<WashAppointmentBean> table = criteriaQuery.from(WashAppointmentBean.class);

        List<Predicate> predicates = buildPredicates(obj, criteriaBuilder, table);
        if (!predicates.isEmpty()) {
            criteriaQuery = criteriaQuery.where(predicates.toArray(new Predicate[0]));
        }

        int start = obj.isNull("start") ? 0 : obj.getInt("start");
        int rows = obj.isNull("rows") ? 1000 : obj.getInt("rows");
        String order = obj.isNull("order") ? "apptOrderId" : obj.getString("order");
        boolean dir = obj.isNull("dir") ? true : obj.getBoolean("dir");

        if (dir) {
            criteriaQuery = criteriaQuery.orderBy(criteriaBuilder.desc(table.get(order)));
        } else {
            criteriaQuery = criteriaQuery.orderBy(criteriaBuilder.asc(table.get(order)));
        }

        TypedQuery<WashAppointmentBean> typedQuery = entityManager.createQuery(criteriaQuery)
                .setFirstResult(start)
                .setMaxResults(rows);

        return typedQuery.getResultList();
    }

    private List<Predicate> buildPredicates(JSONObject obj, CriteriaBuilder cb, Root<WashAppointmentBean> table) {
        List<Predicate> predicates = new ArrayList<>();
        
        if (!obj.isNull("apptOrderId")) {
            predicates.add(cb.equal(table.get("apptOrderId"), obj.getInt("apptOrderId")));
        }
        if (!obj.isNull("apptNo")) {
            predicates.add(cb.like(table.get("apptNo"), "%" + obj.getString("apptNo") + "%"));
        }
        if (!obj.isNull("apptStatus")) {
            predicates.add(cb.equal(table.get("apptStatus"), (byte) obj.getInt("apptStatus")));
        }
        if (!obj.isNull("memberId")) {
            predicates.add(cb.equal(table.get("memberId"), obj.getLong("memberId")));
        }
        if (!obj.isNull("apptDate")) {
            String apptDateStr = obj.getString("apptDate");
            if (apptDateStr != null && !apptDateStr.trim().isEmpty()) {
                try {
                    String cleanDate = apptDateStr;
                    if (cleanDate.contains("T")) {
                        cleanDate = cleanDate.split("T")[0];
                    }
                    LocalDate localDate = LocalDate.parse(cleanDate);
                    LocalDateTime startOfDay = localDate.atStartOfDay();
                    LocalDateTime endOfDay = localDate.plusDays(1).atStartOfDay().minusNanos(1);
                    predicates.add(cb.between(table.get("apptDate"), startOfDay, endOfDay));
                } catch (Exception e) {
                    System.out.println("DAO apptDate 解析失敗: " + apptDateStr);
                    e.printStackTrace();
                }
            }
        }
        
        return predicates;
    }
}
