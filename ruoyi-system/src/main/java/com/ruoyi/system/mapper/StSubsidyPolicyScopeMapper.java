package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.StSubsidyPolicyScope;

/**
 * 政策适用范围Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
public interface StSubsidyPolicyScopeMapper 
{
    /**
     * 查询政策适用范围
     * 
     * @param id 政策适用范围主键
     * @return 政策适用范围
     */
    public StSubsidyPolicyScope selectStSubsidyPolicyScopeById(Long id);

    /**
     * 根据政策ID查询适用范围列表
     * 
     * @param policyId 政策ID
     * @return 政策适用范围集合
     */
    public List<StSubsidyPolicyScope> selectStSubsidyPolicyScopeByPolicyId(Long policyId);

    /**
     * 查询政策适用范围列表
     * 
     * @param stSubsidyPolicyScope 政策适用范围
     * @return 政策适用范围集合
     */
    public List<StSubsidyPolicyScope> selectStSubsidyPolicyScopeList(StSubsidyPolicyScope stSubsidyPolicyScope);

    /**
     * 新增政策适用范围
     * 
     * @param stSubsidyPolicyScope 政策适用范围
     * @return 结果
     */
    public int insertStSubsidyPolicyScope(StSubsidyPolicyScope stSubsidyPolicyScope);

    /**
     * 批量新增政策适用范围
     * 
     * @param scopeList 适用范围列表
     * @return 结果
     */
    public int batchInsertStSubsidyPolicyScope(List<StSubsidyPolicyScope> scopeList);

    /**
     * 修改政策适用范围
     * 
     * @param stSubsidyPolicyScope 政策适用范围
     * @return 结果
     */
    public int updateStSubsidyPolicyScope(StSubsidyPolicyScope stSubsidyPolicyScope);

    /**
     * 删除政策适用范围
     * 
     * @param id 政策适用范围主键
     * @return 结果
     */
    public int deleteStSubsidyPolicyScopeById(Long id);

    /**
     * 根据政策ID删除适用范围
     * 
     * @param policyId 政策ID
     * @return 结果
     */
    public int deleteStSubsidyPolicyScopeByPolicyId(Long policyId);

    /**
     * 批量删除政策适用范围
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStSubsidyPolicyScopeByIds(Long[] ids);
}


