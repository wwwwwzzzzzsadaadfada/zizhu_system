package com.ruoyi.system.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.ruoyi.system.mapper.StSubsidyQuotaMapper;
import com.ruoyi.system.service.IStSemesterBudgetService;

/**
 * 数据修复任务
 * 系统启动时自动执行一次数据修复，确保数据一致性
 */
@Component
public class DataFixTask implements CommandLineRunner {
    
    @Autowired
    private StSubsidyQuotaMapper stSubsidyQuotaMapper;
    
    @Autowired
    private IStSemesterBudgetService stSemesterBudgetService;
    
    @Override
    public void run(String... args) throws Exception {
        try {
            // 修复指标表中的已分配金额
            int fixedCount = stSubsidyQuotaMapper.fixAllocatedAmount();
            System.out.println("系统启动时成功修复 " + fixedCount + " 条指标数据的已分配金额");
            
            // 校准预算表中的已使用金额
            stSemesterBudgetService.calibrateBudgetConsistency();
            System.out.println("系统启动时成功校准预算表中的已使用金额");
        } catch (Exception e) {
            System.err.println("系统启动时执行数据修复任务时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}