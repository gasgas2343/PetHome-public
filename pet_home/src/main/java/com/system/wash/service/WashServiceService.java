package com.system.wash.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.system.wash.entity.WashServiceBean;
import com.system.wash.repository.WashServiceRepository;

@Service
@Transactional
public class WashServiceService {

    @Autowired
    private WashServiceRepository washServiceRepository;

    public List<WashServiceBean> findAll() {
        return washServiceRepository.findAll();
    }

    public WashServiceBean findById(Integer id) {
        Optional<WashServiceBean> opt = washServiceRepository.findById(id);
        return opt.orElse(null);
    }

    public WashServiceBean insert(WashServiceBean bean) {
        if (bean != null && bean.getServiceId() == null) {
            return washServiceRepository.save(bean);
        }
        return null;
    }

    public WashServiceBean update(WashServiceBean bean) {
        if (bean != null && bean.getServiceId() != null) {
            if (washServiceRepository.existsById(bean.getServiceId())) {
                return washServiceRepository.save(bean);
            }
        }
        return null;
    }

    @Autowired
    private com.system.wash.repository.WashPackageServiceItemRepository washPackageServiceItemRepository;

    public boolean delete(Integer id) {
        if (id != null && washServiceRepository.existsById(id)) {
            washPackageServiceItemRepository.deleteByServiceServiceId(id);
            washServiceRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByServiceName(String serviceName) {
        return washServiceRepository.existsByServiceName(serviceName);
    }

    public boolean existsByServiceNameAndServiceIdNot(String serviceName, Integer serviceId) {
        return washServiceRepository.existsByServiceNameAndServiceIdNot(serviceName, serviceId);
    }
}
