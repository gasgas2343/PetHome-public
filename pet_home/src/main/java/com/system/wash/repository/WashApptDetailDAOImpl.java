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
import com.system.wash.entity.WashApptDetailBean;

@Repository
public class WashApptDetailDAOImpl implements WashApptDetailDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public long count(JSONObject obj) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<WashApptDetailBean> table = criteriaQuery.from(WashApptDetailBean.class);

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
    public List<WashApptDetailBean> find(JSONObject obj) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<WashApptDetailBean> criteriaQuery = criteriaBuilder.createQuery(WashApptDetailBean.class);
        Root<WashApptDetailBean> table = criteriaQuery.from(WashApptDetailBean.class);

        List<Predicate> predicates = buildPredicates(obj, criteriaBuilder, table);
        if (!predicates.isEmpty()) {
            criteriaQuery = criteriaQuery.where(predicates.toArray(new Predicate[0]));
        }

        int start = obj.isNull("start") ? 0 : obj.getInt("start");
        int rows = obj.isNull("rows") ? 10 : obj.getInt("rows");
        String order = obj.isNull("order") ? "apptdetailId" : obj.getString("order");
        boolean dir = obj.isNull("dir") ? false : obj.getBoolean("dir");

        if (dir) {
            criteriaQuery = criteriaQuery.orderBy(criteriaBuilder.desc(table.get(order)));
        } else {
            criteriaQuery = criteriaQuery.orderBy(criteriaBuilder.asc(table.get(order)));
        }

        TypedQuery<WashApptDetailBean> typedQuery = entityManager.createQuery(criteriaQuery)
                .setFirstResult(start)
                .setMaxResults(rows);

        return typedQuery.getResultList();
    }

    private List<Predicate> buildPredicates(JSONObject obj, CriteriaBuilder cb, Root<WashApptDetailBean> table) {
        List<Predicate> predicates = new ArrayList<>();
        
        if (!obj.isNull("apptdetailId")) {
            predicates.add(cb.equal(table.get("apptdetailId"), obj.getInt("apptdetailId")));
        }
        if (!obj.isNull("apptOrderId")) {
            // 支援使用 apptOrderId 查詢該預約單底下的所有明細
            predicates.add(cb.equal(table.get("apptOrder").get("apptOrderId"), obj.getInt("apptOrderId")));
        }
        if (!obj.isNull("serviceId")) {
            predicates.add(cb.equal(table.get("service").get("serviceId"), obj.getInt("serviceId")));
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
                    predicates.add(cb.between(table.get("apptOrder").get("apptDate"), startOfDay, endOfDay));
                } catch (Exception e) {
                    System.out.println("DAO apptDate 解析失敗: " + apptDateStr);
                    e.printStackTrace();
                }
            }
        }
        
        return predicates;
    }
}
