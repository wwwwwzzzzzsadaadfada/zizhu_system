package com.ruoyi.system.domain.subsidy.strategy.impl;

import com.ruoyi.system.domain.StSubsidyPackage;
import com.ruoyi.system.domain.subsidy.strategy.PackageSelectionStrategy;
import com.ruoyi.system.mapper.StSubsidyPackageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 高中套餐选择策略
 * 返回所有套餐（一档、二档、三档助学金 + 免学杂费）
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
@Component
public class HighSchoolPackageStrategy implements PackageSelectionStrategy {
    
    @Autowired
    private StSubsidyPackageMapper packageMapper;
    
    @Override
    public List<StSubsidyPackage> getAvailablePackages(Long schoolingPlanId, String economyCategory) {
        // 验证学制ID是否为高中（3=高中）
        if (schoolingPlanId == null || !schoolingPlanId.equals(3L)) {
            throw new IllegalArgumentException("此策略只适用于高中（学制ID=3）");
        }
        
        // 查询高中的所有可用套餐
        StSubsidyPackage query = new StSubsidyPackage();
        query.setSchoolingPlanId(schoolingPlanId);
        query.setStatus(1); // 只查询启用状态的套餐
        // 如果提供了经济分类，则根据经济分类过滤
        if (economyCategory != null && !economyCategory.isEmpty()) {
            query.setEconomyCategory(economyCategory);
        }
        
        List<StSubsidyPackage> allPackages = packageMapper.selectStSubsidyPackageList(query);
        
        // 高中显示所有套餐（已根据经济分类过滤），按排序字段排序
        return allPackages.stream()
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
        return 3L; // 高中
    }
}



