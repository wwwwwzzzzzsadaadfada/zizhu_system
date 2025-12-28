package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StReportPackageCustom;

/**
 * 自定义档案包Service接口
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
public interface IReportPackageCustomService 
{
    /**
     * 查询自定义档案包
     * 
     * @param id 自定义档案包主键
     * @return 自定义档案包
     */
    public StReportPackageCustom selectStReportPackageCustomById(Long id);

    /**
     * 根据档案包编码查询
     * 
     * @param packageCode 档案包编码
     * @return 自定义档案包
     */
    public StReportPackageCustom selectStReportPackageCustomByCode(String packageCode);

    /**
     * 查询自定义档案包列表
     * 
     * @param stReportPackageCustom 自定义档案包
     * @return 自定义档案包集合
     */
    public List<StReportPackageCustom> selectStReportPackageCustomList(StReportPackageCustom stReportPackageCustom);

    /**
     * 创建自定义档案包
     * 
     * @param packageName 档案包名称
     * @param description 描述
     * @return 自定义档案包
     */
    public StReportPackageCustom createCustomPackage(String packageName, String description);

    /**
     * 新增自定义档案包
     * 
     * @param stReportPackageCustom 自定义档案包
     * @return 结果
     */
    public int insertStReportPackageCustom(StReportPackageCustom stReportPackageCustom);

    /**
     * 修改自定义档案包
     * 
     * @param stReportPackageCustom 自定义档案包
     * @return 结果
     */
    public int updateStReportPackageCustom(StReportPackageCustom stReportPackageCustom);

    /**
     * 批量删除自定义档案包
     * 
     * @param ids 需要删除的自定义档案包主键集合
     * @return 结果
     */
    public int deleteStReportPackageCustomByIds(Long[] ids);

    /**
     * 删除自定义档案包信息
     * 
     * @param id 自定义档案包主键
     * @return 结果
     */
    public int deleteStReportPackageCustomById(Long id);

    /**
     * 更新档案包文件统计信息
     * 
     * @param packageCode 档案包编码
     * @return 结果
     */
    public int updateFileStatistics(String packageCode);
}
