package com.ruoyi.system.domain.subsidy.factory;

import com.ruoyi.system.domain.subsidy.strategy.PackageSelectionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 套餐策略工厂
 * 根据学制ID获取对应的套餐选择策略
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
@Component
public class PackageStrategyFactory {
    
    @Autowired
    private List<PackageSelectionStrategy> strategies;
    
    private Map<Long, PackageSelectionStrategy> strategyMap;
    
    /**
     * 初始化策略映射
     */
    @PostConstruct
    public void init() {
        strategyMap = new HashMap<>();
        if (strategies != null) {
            for (PackageSelectionStrategy strategy : strategies) {
                strategyMap.put(strategy.getSupportedSchoolingPlanId(), strategy);
            }
        }
    }
    
    /**
     * 根据学制ID获取策略
     * 
     * @param schoolingPlanId 学制ID（1=小学，2=初中，3=高中）
     * @return 套餐选择策略
     */
    public PackageSelectionStrategy getStrategy(Long schoolingPlanId) {
        if (schoolingPlanId == null) {
            throw new IllegalArgumentException("学制ID不能为空");
        }
        
        PackageSelectionStrategy strategy = strategyMap.get(schoolingPlanId);
        if (strategy == null) {
            throw new IllegalArgumentException("未找到学制ID为 " + schoolingPlanId + " 的套餐选择策略");
        }
        
        return strategy;
    }
    
    /**
     * 检查是否支持指定的学制
     * 
     * @param schoolingPlanId 学制ID
     * @return 是否支持
     */
    public boolean isSupported(Long schoolingPlanId) {
        return schoolingPlanId != null && strategyMap.containsKey(schoolingPlanId);
    }
}



