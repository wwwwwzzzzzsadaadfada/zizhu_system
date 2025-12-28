package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StSubsidyPackage;

/**
 * 补助套餐配置Mapper接口
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
public interface StSubsidyPackageMapper 
{
    /**
     * 查询补助套餐配置
     * 
     * @param id 补助套餐配置主键
     * @return 补助套餐配置
     */
    public StSubsidyPackage selectStSubsidyPackageById(Long id);

    /**
     * 查询补助套餐配置列表
     * 
     * @param stSubsidyPackage 补助套餐配置
     * @return 补助套餐配置集合
     */
    public List<StSubsidyPackage> selectStSubsidyPackageList(StSubsidyPackage stSubsidyPackage);

    /**
     * 根据学制查询可用套餐（启用状态）
     * 
     * @param schoolingPlanId 学制ID
     * @return 补助套餐配置集合
     */
    public List<StSubsidyPackage> selectAvailablePackagesBySchoolingPlan(Long schoolingPlanId);

    /**
     * 根据学制和套餐类型查询
     * 
     * @param schoolingPlanId 学制ID
     * @param packageType 套餐类型
     * @return 补助套餐配置
     */
    public List<StSubsidyPackage> selectBySchoolingPlanAndType(Long schoolingPlanId, String packageType);

    /**
     * 新增补助套餐配置
     * 
     * @param stSubsidyPackage 补助套餐配置
     * @return 结果
     */
    public int insertStSubsidyPackage(StSubsidyPackage stSubsidyPackage);

    /**
     * 修改补助套餐配置
     * 
     * @param stSubsidyPackage 补助套餐配置
     * @return 结果
     */
    public int updateStSubsidyPackage(StSubsidyPackage stSubsidyPackage);

    /**
     * 删除补助套餐配置
     * 
     * @param id 补助套餐配置主键
     * @return 结果
     */
    public int deleteStSubsidyPackageById(Long id);

    /**
     * 批量删除补助套餐配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStSubsidyPackageByIds(Long[] ids);
}



