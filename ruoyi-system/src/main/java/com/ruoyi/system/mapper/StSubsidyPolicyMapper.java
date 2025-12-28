package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Date;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.StSubsidyPolicy;

/**
 * 资助政策Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
public interface StSubsidyPolicyMapper 
{
    /**
     * 查询资助政策
     * 
     * @param id 资助政策主键
     * @return 资助政策
     */
    public StSubsidyPolicy selectStSubsidyPolicyById(Long id);

    /**
     * 根据政策编号查询资助政策
     * 
     * @param policyCode 政策编号
     * @return 资助政策
     */
    public StSubsidyPolicy selectStSubsidyPolicyByCode(String policyCode);

    /**
     * 查询资助政策列表
     * 
     * @param stSubsidyPolicy 资助政策
     * @return 资助政策集合
     */
    public List<StSubsidyPolicy> selectStSubsidyPolicyList(StSubsidyPolicy stSubsidyPolicy);

    /**
     * 查询有效政策列表（已发布且当前日期在生效日期和失效日期之间）
     * 
     * @param schoolingPlanId 学制ID（可选）
     * @param yearSemesterId 学年学期ID（可选）
     * @param currentDate 当前日期
     * @return 资助政策集合
     */
    public List<StSubsidyPolicy> selectEffectivePolicies(
        @Param("schoolingPlanId") Long schoolingPlanId,
        @Param("yearSemesterId") Long yearSemesterId,
        @Param("currentDate") Date currentDate
    );

    /**
     * 检查政策编号是否存在
     * 
     * @param policyCode 政策编号
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 数量
     */
    public int countByPolicyCode(@Param("policyCode") String policyCode, @Param("excludeId") Long excludeId);

    /**
     * 检查是否有冲突的有效政策（同类型、同适用范围、时间重叠）
     * 
     * @param policyId 政策ID（排除自己）
     * @param policyType 政策类型
     * @param schoolingPlanId 学制ID
     * @param effectiveDate 生效日期
     * @param expiryDate 失效日期
     * @return 数量
     */
    public int countConflictingPolicies(
        @Param("policyId") Long policyId,
        @Param("policyType") String policyType,
        @Param("schoolingPlanId") Long schoolingPlanId,
        @Param("effectiveDate") Date effectiveDate,
        @Param("expiryDate") Date expiryDate
    );

    /**
     * 新增资助政策
     * 
     * @param stSubsidyPolicy 资助政策
     * @return 结果
     */
    public int insertStSubsidyPolicy(StSubsidyPolicy stSubsidyPolicy);

    /**
     * 修改资助政策
     * 
     * @param stSubsidyPolicy 资助政策
     * @return 结果
     */
    public int updateStSubsidyPolicy(StSubsidyPolicy stSubsidyPolicy);

    /**
     * 发布政策
     * 
     * @param id 政策ID
     * @param publishTime 发布时间
     * @param publisher 发布人
     * @return 结果
     */
    public int publishPolicy(@Param("id") Long id, @Param("publishTime") Date publishTime, @Param("publisher") String publisher);

    /**
     * 废止政策
     * 
     * @param id 政策ID
     * @param abolishTime 废止时间
     * @param abolishReason 废止原因
     * @return 结果
     */
    public int abolishPolicy(@Param("id") Long id, @Param("abolishTime") Date abolishTime, @Param("abolishReason") String abolishReason);

    /**
     * 删除资助政策
     * 
     * @param id 资助政策主键
     * @return 结果
     */
    public int deleteStSubsidyPolicyById(Long id);

    /**
     * 批量删除资助政策
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStSubsidyPolicyByIds(Long[] ids);
}


