package com.ruoyi.system.domain.subsidy.strategy.impl;

import com.ruoyi.system.domain.StSubsidyPackage;
import com.ruoyi.system.domain.subsidy.strategy.PackageSelectionStrategy;
import com.ruoyi.system.mapper.StSubsidyPackageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 初中套餐选择策略
 * 只返回一档补助套餐
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
@Component
public class MiddleSchoolPackageStrategy implements PackageSelectionStrategy {
    
    @Autowired
    private StSubsidyPackageMapper packageMapper;
    
    @Override
    public List<StSubsidyPackage> getAvailablePackages(Long schoolingPlanId, String economyCategory) {
        // 验证学制ID是否为初中（2=初中）
        if (schoolingPlanId == null || !schoolingPlanId.equals(2L)) {
            throw new IllegalArgumentException("此策略只适用于初中（学制ID=2）");
        }
        
        // 查询初中的所有可用套餐
        StSubsidyPackage query = new StSubsidyPackage();
        query.setSchoolingPlanId(schoolingPlanId);
        query.setStatus(1); // 只查询启用状态的套餐
        // 如果提供了经济分类，则根据经济分类过滤
        if (economyCategory != null && !economyCategory.isEmpty()) {
            query.setEconomyCategory(economyCategory);
        }
        
        List<StSubsidyPackage> allPackages = packageMapper.selectStSubsidyPackageList(query);
        
        // 根据业务规则，初中只显示一档套餐（package_type = '1'）
        return allPackages.stream()
            .filter(pkg -> "1".equals(pkg.getPackageType()))
            .sorted((a, b) -> {
                // 按排序字段排序
                int orderA = a.getSortOrder() != null ? a.getSortOrder() : 0;
                int orderB = b.getSortOrder() != null ? b.getSortOrder() : 0;
                return Integer.compare(orderA, orderB);
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public Long getSupportedSchoolingPlanId() {
        return 2L; // 初中
    }
}



