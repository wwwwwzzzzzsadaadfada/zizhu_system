package com.ruoyi.system.application.service;

import com.ruoyi.system.application.command.ApproveSubsidyCommand;
import com.ruoyi.system.application.command.ApproveSubsidyCommandHandler;
import com.ruoyi.system.application.command.CreateSubsidyCommand;
import com.ruoyi.system.application.command.CreateSubsidyCommandHandler;
import com.ruoyi.system.domain.StSubsidyPackage;
import com.ruoyi.system.domain.subsidy.factory.PackageStrategyFactory;
import com.ruoyi.system.domain.subsidy.strategy.PackageSelectionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 补助录入应用服务
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
@Service
public class SubsidyApplicationService {
    
    @Autowired
    private CreateSubsidyCommandHandler createSubsidyCommandHandler;
    
    @Autowired
    private ApproveSubsidyCommandHandler approveSubsidyCommandHandler;
    
    @Autowired
    private PackageStrategyFactory packageStrategyFactory;
    
    /**
     * 录入补助
     * 
     * @param command 补助录入命令
     * @return 创建的补助记录ID列表
     */
    public List<Long> createSubsidy(CreateSubsidyCommand command) {
        return createSubsidyCommandHandler.handle(command);
    }
    
    /**
     * 审批补助
     * 
     * @param command 审批命令
     */
    public void approveSubsidy(ApproveSubsidyCommand command) {
        approveSubsidyCommandHandler.handle(command);
    }
    
    /**
     * 查询可用套餐（查询服务，CQRS分离）
     * 
     * @param schoolingPlanId 学制ID
     * @param economyCategory 经济分类（可选），如果提供则只返回该经济分类的套餐
     * @return 套餐列表
     */
    public List<StSubsidyPackage> getAvailablePackages(Long schoolingPlanId, String economyCategory) {
        PackageSelectionStrategy strategy = packageStrategyFactory.getStrategy(schoolingPlanId);
        return strategy.getAvailablePackages(schoolingPlanId, economyCategory);
    }
}



