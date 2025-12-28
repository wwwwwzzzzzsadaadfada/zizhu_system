package com.ruoyi.system.application.query;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.system.domain.StSubsidyQuota;
import com.ruoyi.system.domain.StSubsidyQuotaDetail;
import com.ruoyi.system.domain.StSchoolYearSemester;
import com.ruoyi.common.utils.SemesterUtils;

/**
 * 指标业务规则工具类
 * 将业务逻辑从SQL移到Java代码，确保安全性、逻辑性和可维护性
 * 
 * @author ruoyi
 */
public class QuotaBusinessRule {
    
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    
    /**
     * 设置指标的计算字段（所有业务逻辑判断在Java代码中实现）
     * 
     * @param quota 指标对象（需要包含schoolYear和semester字段）
     * @param currentSemester 当前学期（用于判断是否为历史学期）
     * @param detailList 指标明细列表（用于汇总计算，如果为null则使用主表字段）
     */
    public static void attachComputedFields(StSubsidyQuota quota, StSchoolYearSemester currentSemester, 
                                            List<StSubsidyQuotaDetail> detailList) {
        if (quota == null) {
            return;
        }
        
        // 1. 设置学期标签（使用SemesterUtils工具类）
        String semesterLabel = SemesterUtils.getSemesterLabel(quota.getSemester());
        quota.setSemesterLabel(semesterLabel);
        
        // 2. 设置来源指标文号（如果存在sourceQuotaId）
        // 注意：这个需要在Service层通过Mapper查询，此处不处理
        
        // 3. 计算总指标和已分配金额（优先从明细表汇总）
        BigDecimal totalQuota;
        BigDecimal allocatedAmount;
        if (detailList != null && !detailList.isEmpty()) {
            // 从明细表汇总
            totalQuota = detailList.stream()
                .filter(d -> d != null && d.getTotalAmount() != null)
                .map(StSubsidyQuotaDetail::getTotalAmount)
                .reduce(ZERO, BigDecimal::add);
            
            allocatedAmount = detailList.stream()
                .filter(d -> d != null && d.getAllocatedAmount() != null)
                .map(StSubsidyQuotaDetail::getAllocatedAmount)
                .reduce(ZERO, BigDecimal::add);
        } else {
            // 使用主表字段
            totalQuota = quota.getTotalQuota() != null ? quota.getTotalQuota() : ZERO;
            allocatedAmount = quota.getAllocatedAmount() != null ? quota.getAllocatedAmount() : ZERO;
        }
        
        // 4. 计算可用指标金额
        BigDecimal availableQuota = totalQuota.subtract(allocatedAmount);
        quota.setAvailableQuota(availableQuota);
        
        // 5. 判断是否为历史学期（使用SemesterComparisonUtils，基于学年和学期比较）
        boolean isHistoricalSemester = false;
        BigDecimal carryOverAmount = ZERO;
        if (currentSemester != null && quota.getSchoolYear() != null && quota.getSemester() != null) {
            isHistoricalSemester = SemesterComparisonUtils.isBefore(
                quota.getSchoolYear(), quota.getSemester(),
                currentSemester.getSchoolYear(), currentSemester.getSemester()
            );
            
            // 6. 计算上期结余金额
            if (isHistoricalSemester) {
                // 历史学期：显示剩余额度
                carryOverAmount = availableQuota;
            } else {
                // 当前学期或未来学期：只显示从历史学期结转过来的金额
                // 通过明细表的source_detail_id字段判断是否为结转资金
                if (detailList != null && !detailList.isEmpty()) {
                    carryOverAmount = detailList.stream()
                        .filter(d -> d != null && d.getSourceDetailId() != null && d.getTotalAmount() != null)
                        .map(StSubsidyQuotaDetail::getTotalAmount)
                        .reduce(ZERO, BigDecimal::add);
                }
            }
        }
        quota.setIsHistoricalSemester(isHistoricalSemester);
        quota.setCarryOverAmount(carryOverAmount);
        
        // 7. 判断是否允许结转
        // 规则：历史学期 + 有剩余额度 + 未锁定（status != 4）
        boolean canCarryOver = false;
        if (isHistoricalSemester) {
            // 已锁定状态不允许结转
            if (quota.getStatus() == null || quota.getStatus() != 4) {
                // 检查是否有剩余额度
                if (availableQuota.compareTo(ZERO) > 0) {
                    canCarryOver = true;
                }
            }
        }
        quota.setCanCarryOver(canCarryOver);
        
        // 8. 判断是否已结转
        // 规则：历史学期 + 已分配金额 > 0
        boolean hasCarriedOver = isHistoricalSemester && allocatedAmount.compareTo(ZERO) > 0;
        quota.setHasCarriedOver(hasCarriedOver);
    }
    
    /**
     * 判断指标明细是否可编辑
     * 
     * 规则：
     * - 已分配金额 > 0：不可编辑
     * - source_detail_id != NULL（结转资金）：不可编辑
     * - 其他情况：可编辑
     * 
     * @param detail 指标明细
     * @return true=可编辑，false=不可编辑
     */
    public static boolean isEditable(StSubsidyQuotaDetail detail) {
        if (detail == null) {
            return false;
        }
        
        // 已分配金额 > 0 时不可编辑
        if (detail.getAllocatedAmount() != null && detail.getAllocatedAmount().compareTo(ZERO) > 0) {
            return false;
        }
        
        // 结转资金（source_detail_id != NULL）不可编辑
        if (detail.getSourceDetailId() != null) {
            return false;
        }
        
        return true;
    }
}





