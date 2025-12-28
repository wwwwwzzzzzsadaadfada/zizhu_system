package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StSubsidyPolicy;

/**
 * 资助政策Service接口
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
public interface IStSubsidyPolicyService 
{
    /**
     * 查询资助政策
     * 
     * @param id 资助政策主键
     * @return 资助政策
     */
    public StSubsidyPolicy selectStSubsidyPolicyById(Long id);

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
     * @return 资助政策集合
     */
    public List<StSubsidyPolicy> selectEffectivePolicies(Long schoolingPlanId, Long yearSemesterId);

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
     * 批量删除资助政策
     * 
     * @param ids 需要删除的资助政策主键
     * @return 结果
     */
    public int deleteStSubsidyPolicyByIds(Long[] ids);

    /**
     * 删除资助政策信息
     * 
     * @param id 资助政策主键
     * @return 结果
     */
    public int deleteStSubsidyPolicyById(Long id);

    /**
     * 发布政策
     * 
     * @param id 政策ID
     * @param publisher 发布人
     * @return 结果
     */
    public int publishPolicy(Long id, String publisher);

    /**
     * 废止政策
     * 
     * @param id 政策ID
     * @param abolishReason 废止原因
     * @return 结果
     */
    public int abolishPolicy(Long id, String abolishReason);
}


