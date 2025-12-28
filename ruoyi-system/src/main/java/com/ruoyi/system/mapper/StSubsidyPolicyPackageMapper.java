package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.StSubsidyPolicyPackage;

/**
 * 政策与套餐关联Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
public interface StSubsidyPolicyPackageMapper 
{
    /**
     * 查询政策与套餐关联
     * 
     * @param id 政策与套餐关联主键
     * @return 政策与套餐关联
     */
    public StSubsidyPolicyPackage selectStSubsidyPolicyPackageById(Long id);

    /**
     * 根据政策ID查询关联套餐列表
     * 
     * @param policyId 政策ID
     * @return 政策与套餐关联集合
     */
    public List<StSubsidyPolicyPackage> selectStSubsidyPolicyPackageByPolicyId(Long policyId);

    /**
     * 查询政策与套餐关联列表
     * 
     * @param stSubsidyPolicyPackage 政策与套餐关联
     * @return 政策与套餐关联集合
     */
    public List<StSubsidyPolicyPackage> selectStSubsidyPolicyPackageList(StSubsidyPolicyPackage stSubsidyPolicyPackage);

    /**
     * 新增政策与套餐关联
     * 
     * @param stSubsidyPolicyPackage 政策与套餐关联
     * @return 结果
     */
    public int insertStSubsidyPolicyPackage(StSubsidyPolicyPackage stSubsidyPolicyPackage);

    /**
     * 批量新增政策与套餐关联
     * 
     * @param packageList 套餐列表
     * @return 结果
     */
    public int batchInsertStSubsidyPolicyPackage(List<StSubsidyPolicyPackage> packageList);

    /**
     * 修改政策与套餐关联
     * 
     * @param stSubsidyPolicyPackage 政策与套餐关联
     * @return 结果
     */
    public int updateStSubsidyPolicyPackage(StSubsidyPolicyPackage stSubsidyPolicyPackage);

    /**
     * 删除政策与套餐关联
     * 
     * @param id 政策与套餐关联主键
     * @return 结果
     */
    public int deleteStSubsidyPolicyPackageById(Long id);

    /**
     * 根据政策ID删除关联套餐
     * 
     * @param policyId 政策ID
     * @return 结果
     */
    public int deleteStSubsidyPolicyPackageByPolicyId(Long policyId);

    /**
     * 批量删除政策与套餐关联
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStSubsidyPolicyPackageByIds(Long[] ids);
}


