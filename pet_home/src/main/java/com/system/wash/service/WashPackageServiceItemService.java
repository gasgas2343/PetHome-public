package com.system.wash.service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.system.wash.entity.WashPackageServiceItemBean;
import com.system.wash.repository.WashPackageServiceItemRepository;

@Service
@Transactional
public class WashPackageServiceItemService {

    @Autowired
    private WashPackageServiceItemRepository washPackageServiceItemRepository;

    public List<WashPackageServiceItemBean> findAll() {
        return washPackageServiceItemRepository.findAll();
    }

    public WashPackageServiceItemBean findById(Integer id) {
        Optional<WashPackageServiceItemBean> opt = washPackageServiceItemRepository.findById(id);
        return opt.orElse(null);
    }

    public WashPackageServiceItemBean insert(WashPackageServiceItemBean bean) {
        if (bean != null && bean.getPackageServiceItemId() == null) {
            if (bean.getCreatedAt() == null) {
                bean.setCreatedAt(LocalDateTime.now());
            }
            return washPackageServiceItemRepository.save(bean);
        }
        return null;
    }

    public List<WashPackageServiceItemBean> insertBatch(List<WashPackageServiceItemBean> beans) {
        if (beans != null && !beans.isEmpty()) {
            for (WashPackageServiceItemBean bean : beans) {
                if (bean.getCreatedAt() == null) {
                    bean.setCreatedAt(LocalDateTime.now());
                }
            }
            return washPackageServiceItemRepository.saveAll(beans);
        }
        return null;
    }


    public WashPackageServiceItemBean update(WashPackageServiceItemBean bean) {
        if (bean != null && bean.getPackageServiceItemId() != null) {
            if (washPackageServiceItemRepository.existsById(bean.getPackageServiceItemId())) {
                return washPackageServiceItemRepository.save(bean);
            }
        }
        return null;
    }

    public boolean delete(Integer id) {
        if (id != null && washPackageServiceItemRepository.existsById(id)) {
            washPackageServiceItemRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
