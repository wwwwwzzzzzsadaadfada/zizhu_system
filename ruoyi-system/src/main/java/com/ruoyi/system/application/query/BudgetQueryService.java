package com.ruoyi.system.application.query;

import com.ruoyi.system.domain.StSemesterBudget;
import com.ruoyi.system.domain.StSchoolYearSemester;
import com.ruoyi.system.domain.vo.BudgetVO;
import com.ruoyi.system.mapper.StSemesterBudgetMapper;
import com.ruoyi.system.mapper.StSchoolYearSemesterMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 预算查询服务（CQRS 查询端）
 * 
 * 业务逻辑说明：
 * - 功能分类组合规则：小学（1）和初中（2）可以互相看到对方的预算
 * - 历史结余判断：在Java代码中进行判断，而不是SQL
 *
 * @author ruoyi
 */
@Service
public class BudgetQueryService {

    @Autowired
    private StSemesterBudgetMapper semesterBudgetMapper;
    
    @Autowired
    private StSchoolYearSemesterMapper schoolYearSemesterMapper;

    /**
     * 查询当前学期可用预算（增强版）
     * 
     * 业务逻辑：
     * 1. 参数验证（安全性）
     * 2. 确定目标功能分类列表（业务规则：义教阶段预算组合）
     * 3. 调用Mapper查询
     * 4. 转换为VO并返回
     * 
     * @param currentSemesterId 当前学期ID（必填）
     * @param economyCategory 经济分类（可选，从套餐获取）
     * @param functionCategory 功能分类（可选，从学生学制获取）
     * @return 预算列表（包含指标信息）
     * @throws IllegalArgumentException 如果参数无效
     */
    public List<BudgetVO> getAvailableBudgets(Long currentSemesterId, String economyCategory, String functionCategory) {
        // 1. 参数验证（安全性检查）
        validateBudgetQueryParams(currentSemesterId, economyCategory, functionCategory);
        
        // 2. 查询当前学期的学年和学期信息（用于判断历史结余）
        StSchoolYearSemester currentSemester = schoolYearSemesterMapper.selectStSchoolYearSemesterById(currentSemesterId);
        if (currentSemester == null) {
            throw new IllegalArgumentException("当前学期ID不存在: " + currentSemesterId);
        }
        String currentSchoolYear = currentSemester.getSchoolYear();
        Integer currentSemesterValue = currentSemester.getSemester();
        if (currentSchoolYear == null || currentSemesterValue == null) {
            throw new IllegalStateException("当前学期的学年或学期信息不完整: ID=" + currentSemesterId);
        }
        
        // 3. 确定目标功能分类列表（业务规则：义教阶段预算组合）
        List<String> targetFunctionCategories = FunctionCategoryUtils.determineTargetCategories(functionCategory);
        
        // 4. 调用Mapper查询（传递列表参数，而不是单个值）
        // 查询所有当前学期的预算（包括结余资金）
        // 注意：不需要过滤quotaSourceType，因为结余资金也需要显示，只是需要标识
        List<StSemesterBudget> allBudgets = semesterBudgetMapper.selectAvailableBudgets(
            currentSemesterId, 
            economyCategory, 
            targetFunctionCategories.isEmpty() ? null : targetFunctionCategories
        );
        
        // 5. 转换为VO并返回（在toVO中根据sourceDetailId判断是否为结余资金）
        return toVOList(allBudgets, false);
    }

    /**
     * 查询历史结余预算（增强版）
     * 
     * 业务逻辑：
     * 1. 参数验证（安全性）
     * 2. 确定目标功能分类列表（业务规则：义教阶段预算组合）
     * 3. 调用Mapper查询所有可能的预算（不过滤历史结余）
     * 4. 在Java代码中使用HistoricalSurplusUtils判断是否为历史结余
     * 5. 转换为VO并返回
     * 
     * @param currentSemesterId 当前学期ID（必填）
     * @param economyCategory 经济分类（可选，从套餐获取）
     * @param functionCategory 功能分类（可选，从学生学制获取）
     * @return 预算列表（包含指标信息）
     * @throws IllegalArgumentException 如果参数无效
     */
    public List<BudgetVO> getHistoricalSurplus(Long currentSemesterId, String economyCategory, String functionCategory) {
        // 1. 参数验证（安全性检查）
        validateBudgetQueryParams(currentSemesterId, economyCategory, functionCategory);
        
        // 2. 查询当前学期的学年和学期信息（用于判断历史结余）
        StSchoolYearSemester currentSemester = schoolYearSemesterMapper.selectStSchoolYearSemesterById(currentSemesterId);
        if (currentSemester == null) {
            throw new IllegalArgumentException("当前学期ID不存在: " + currentSemesterId);
        }
        String currentSchoolYear = currentSemester.getSchoolYear();
        Integer currentSemesterValue = currentSemester.getSemester();
        if (currentSchoolYear == null || currentSemesterValue == null) {
            throw new IllegalStateException("当前学期的学年或学期信息不完整: ID=" + currentSemesterId);
        }
        
        // 3. 确定目标功能分类列表（业务规则：义教阶段预算组合）
        List<String> targetFunctionCategories = FunctionCategoryUtils.determineTargetCategories(functionCategory);
        
        // 4. 调用Mapper查询所有可能的预算（不过滤历史结余）
        List<StSemesterBudget> allBudgets = semesterBudgetMapper.selectAllPossibleBudgets(
            currentSemesterId, 
            economyCategory, 
            targetFunctionCategories.isEmpty() ? null : targetFunctionCategories
        );
        
        // 5. 在Java代码中使用HistoricalSurplusUtils判断是否为历史结余（业务逻辑）
        // 使用学年和学期进行比较，而不是ID
        List<StSemesterBudget> historicalBudgets = HistoricalSurplusUtils.filterHistoricalSurplus(
            allBudgets, currentSchoolYear, currentSemesterValue);
        
        // 6. 转换为VO并返回
        return toVOList(historicalBudgets, true); // true 表示是历史结余
    }
    
    /**
     * 验证预算查询参数
     * 
     * @param currentSemesterId 当前学期ID
     * @param economyCategory 经济分类（可选）
     * @param functionCategory 功能分类（可选）
     * @throws IllegalArgumentException 如果参数无效
     */
    private void validateBudgetQueryParams(Long currentSemesterId, String economyCategory, String functionCategory) {
        if (currentSemesterId == null || currentSemesterId <= 0) {
            throw new IllegalArgumentException("当前学期ID不能为空且必须大于0");
        }
        
        if (economyCategory != null && !economyCategory.trim().isEmpty()) {
            if (!EconomyCategoryUtils.isValid(economyCategory)) {
                throw new IllegalArgumentException("无效的经济分类代码: " + economyCategory);
            }
        }
        
        if (functionCategory != null && !functionCategory.trim().isEmpty()) {
            if (!FunctionCategoryUtils.isValid(functionCategory)) {
                throw new IllegalArgumentException("无效的功能分类代码: " + functionCategory);
            }
        }
    }

    private List<BudgetVO> toVOList(List<StSemesterBudget> budgets, boolean isHistorical) {
        if (budgets == null || budgets.isEmpty()) {
            return Collections.emptyList();
        }
        List<BudgetVO> results = new ArrayList<>(budgets.size());
        for (StSemesterBudget budget : budgets) {
            if (budget != null) {
                // 动态计算实际的锁定金额（从未审批记录中汇总）
                BigDecimal actualLocked = semesterBudgetMapper.calculateActualLockedAmount(budget.getId());
                budget.setLockedAmount(actualLocked);
                // 重新计算可用金额
                BigDecimal available = budget.getBudgetAmount()
                    .subtract(budget.getUsedAmount() != null ? budget.getUsedAmount() : BigDecimal.ZERO)
                    .subtract(actualLocked != null ? actualLocked : BigDecimal.ZERO);
                budget.setAvailableAmount(available);
                // 使用工具类确保学期标签的一致性（即使SQL已计算，也通过工具类校验和设置）
                String semesterLabel = com.ruoyi.common.utils.SemesterUtils.getSemesterLabel(budget.getSemester());
                budget.setSemesterLabel(semesterLabel);
                
                // 计算结转标志（业务逻辑：从SQL移到Java代码）
                if (budget.getCarryOverFlag() == null && budget.getQuotaSourceType() != null) {
                    budget.setCarryOverFlag(QuotaSourceUtils.isCarryOver(budget.getQuotaSourceType()));
                }
                
                // 计算结转标志：优先使用isCarryover字段，其次使用quotaSourceType（兼容旧数据）
                if ("1".equals(budget.getIsCarryover())) {
                    // 明确标记为结转资金
                    budget.setCarryOverFlag(true);
                } else if (budget.getQuotaSourceType() != null && QuotaSourceUtils.isCarryOver(budget.getQuotaSourceType())) {
                    // 兼容旧数据：根据quotaSourceType判断
                    budget.setCarryOverFlag(true);
                } else {
                    // 非结转资金
                    budget.setCarryOverFlag(false);
                }
                
                results.add(toVO(budget, isHistorical));
            }
        }
        return results;
    }

    /**
     * 将StSemesterBudget转换为BudgetVO
     * 
     * 注意：isHistorical参数已不再使用，实际判断逻辑在方法内部根据sourceDetailId实现
     * 保留此参数是为了保持方法签名的兼容性
     */
    private BudgetVO toVO(@NonNull StSemesterBudget budget, @SuppressWarnings("unused") boolean isHistorical) {
        BudgetVO vo = new BudgetVO();
        BeanUtils.copyProperties(budget, vo);
        
        // 判断是否为结余资金：如果指标明细的sourceDetailId不为NULL，就是结转资金
        // sourceDetailId不为NULL表示这个明细是从其他明细结转来的
        boolean isSurplus = budget.getSourceDetailId() != null;
        
        vo.setIsHistorical(isSurplus);
        
        // 获取功能分类名称（小学/初中/高中）
        // 如果code无效，codeToName会返回原code，此时functionCategoryName.equals(functionCategory)为true
        String functionCategory = budget.getFunctionCategory();
        String functionCategoryName = FunctionCategoryUtils.codeToName(functionCategory);
        // 判断是否成功转换为名称（名称不等于原code，说明转换成功）
        boolean hasValidFunctionCategory = functionCategory != null 
            && functionCategoryName != null 
            && !functionCategoryName.equals(functionCategory);
        
        // 1. 在预算项目名称后添加（小学/初中/高中）标识
        String budgetProjectName = budget.getBudgetProjectName();
        if (budgetProjectName != null && !budgetProjectName.trim().isEmpty()) {
            String newName = budgetProjectName;
            
            // 检查是否已经包含功能分类标识，避免重复添加
            boolean hasFunctionCategorySuffix = newName.endsWith("（小学）") 
                || newName.endsWith("（初中）") 
                || newName.endsWith("（高中）")
                || newName.endsWith("(小学)")
                || newName.endsWith("(初中)")
                || newName.endsWith("(高中)");
            
            // 如果功能分类有效且预算项目名称中没有包含，则添加
            if (hasValidFunctionCategory && !hasFunctionCategorySuffix) {
                newName = newName + "（" + functionCategoryName + "）";
            }
            
            vo.setBudgetProjectName(newName);
        }
        
        // 2. 设置预算来源类型标签（所有判断逻辑在后端实现）
        // 格式：学期下达 或 结余
        // 注意：结余资金不再添加（小学/初中）标识，因为预算项目名称已经包含功能分类标识
        String sourceTypeLabel;
        if (isSurplus) {
            // 结余资金：只显示"结余"（功能分类已在预算项目名称中体现）
            sourceTypeLabel = "结余";
        } else {
            // 学期下达
            sourceTypeLabel = "学期下达";
        }
        vo.setSourceTypeLabel(sourceTypeLabel);
        
        return vo;
    }
}

