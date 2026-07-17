package com.system.wash.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import com.system.wash.entity.WashScheduleBean;

@Repository
public class WashScheduleDAOImpl implements WashScheduleDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public long count(JSONObject obj) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<WashScheduleBean> table = criteriaQuery.from(WashScheduleBean.class);

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
    public List<WashScheduleBean> find(JSONObject obj) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<WashScheduleBean> criteriaQuery = criteriaBuilder.createQuery(WashScheduleBean.class);
        Root<WashScheduleBean> table = criteriaQuery.from(WashScheduleBean.class);

        List<Predicate> predicates = buildPredicates(obj, criteriaBuilder, table);
        if (!predicates.isEmpty()) {
            criteriaQuery = criteriaQuery.where(predicates.toArray(new Predicate[0]));
        }

        int start = obj.isNull("start") ? 0 : obj.getInt("start");
        int rows = obj.isNull("rows") ? 10 : obj.getInt("rows");
        String order = obj.isNull("order") ? "scheduleId" : obj.getString("order");
        boolean dir = obj.isNull("dir") ? false : obj.getBoolean("dir");

        if (dir) {
            criteriaQuery = criteriaQuery.orderBy(criteriaBuilder.desc(table.get(order)));
        } else {
            criteriaQuery = criteriaQuery.orderBy(criteriaBuilder.asc(table.get(order)));
        }

        TypedQuery<WashScheduleBean> typedQuery = entityManager.createQuery(criteriaQuery)
                .setFirstResult(start)
                .setMaxResults(rows);

        return typedQuery.getResultList();
    }

    private List<Predicate> buildPredicates(JSONObject obj, CriteriaBuilder cb, Root<WashScheduleBean> table) {
        List<Predicate> predicates = new ArrayList<>();
        
        if (!obj.isNull("scheduleId")) {
            predicates.add(cb.equal(table.get("scheduleId"), obj.getInt("scheduleId")));
        }
        // 您可以依照需求擴增其他 JSONObject 的參數判斷
        return predicates;
    }
}
