package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StReportPackage;

/**
 * 报表打包记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public interface StReportPackageMapper 
{
    /**
     * 查询报表打包记录
     * 
     * @param id 报表打包记录主键
     * @return 报表打包记录
     */
    public StReportPackage selectStReportPackageById(Long id);

    /**
     * 查询报表打包记录列表
     * 
     * @param stReportPackage 报表打包记录
     * @return 报表打包记录集合
     */
    public List<StReportPackage> selectStReportPackageList(StReportPackage stReportPackage);

    /**
     * 新增报表打包记录
     * 
     * @param stReportPackage 报表打包记录
     * @return 结果
     */
    public int insertStReportPackage(StReportPackage stReportPackage);

    /**
     * 修改报表打包记录
     * 
     * @param stReportPackage 报表打包记录
     * @return 结果
     */
    public int updateStReportPackage(StReportPackage stReportPackage);

    /**
     * 删除报表打包记录
     * 
     * @param id 报表打包记录主键
     * @return 结果
     */
    public int deleteStReportPackageById(Long id);

    /**
     * 批量删除报表打包记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStReportPackageByIds(Long[] ids);
}

