package com.ruoyi.system.domain.subsidy.strategy;

import com.ruoyi.system.domain.StSubsidyPackage;

import java.util.List;

/**
 * 套餐选择策略接口
 * 根据学制提供不同的套餐选择策略
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
public interface PackageSelectionStrategy {
    
    /**
     * 获取可用套餐列表
     * 
     * @param schoolingPlanId 学制ID（1=小学，2=初中，3=高中）
     * @param economyCategory 经济分类（可选），如果提供则只返回该经济分类的套餐
     * @return 套餐列表
     */
    List<StSubsidyPackage> getAvailablePackages(Long schoolingPlanId, String economyCategory);
    
    /**
     * 获取策略支持的学制ID
     * 
     * @return 学制ID
     */
    Long getSupportedSchoolingPlanId();
}



