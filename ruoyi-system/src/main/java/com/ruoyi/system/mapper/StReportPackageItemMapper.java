package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StReportPackageItem;

/**
 * 报表打包明细Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public interface StReportPackageItemMapper 
{
    /**
     * 查询报表打包明细
     * 
     * @param id 报表打包明细主键
     * @return 报表打包明细
     */
    public StReportPackageItem selectStReportPackageItemById(Long id);

    /**
     * 查询报表打包明细列表
     * 
     * @param stReportPackageItem 报表打包明细
     * @return 报表打包明细集合
     */
    public List<StReportPackageItem> selectStReportPackageItemList(StReportPackageItem stReportPackageItem);

    /**
     * 根据打包记录ID查询明细列表
     * 
     * @param packageId 打包记录ID
     * @return 报表打包明细集合
     */
    public List<StReportPackageItem> selectStReportPackageItemListByPackageId(Long packageId);

    /**
     * 新增报表打包明细
     * 
     * @param stReportPackageItem 报表打包明细
     * @return 结果
     */
    public int insertStReportPackageItem(StReportPackageItem stReportPackageItem);

    /**
     * 批量新增报表打包明细
     * 
     * @param itemList 报表打包明细列表
     * @return 结果
     */
    public int batchInsertStReportPackageItem(List<StReportPackageItem> itemList);

    /**
     * 修改报表打包明细
     * 
     * @param stReportPackageItem 报表打包明细
     * @return 结果
     */
    public int updateStReportPackageItem(StReportPackageItem stReportPackageItem);

    /**
     * 删除报表打包明细
     * 
     * @param id 报表打包明细主键
     * @return 结果
     */
    public int deleteStReportPackageItemById(Long id);

    /**
     * 根据打包记录ID删除明细
     * 
     * @param packageId 打包记录ID
     * @return 结果
     */
    public int deleteStReportPackageItemByPackageId(Long packageId);

    /**
     * 批量删除报表打包明细
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStReportPackageItemByIds(Long[] ids);
}

