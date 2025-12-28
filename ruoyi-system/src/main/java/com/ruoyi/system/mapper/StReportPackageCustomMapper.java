package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StReportPackageCustom;

/**
 * 自定义档案包Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
public interface StReportPackageCustomMapper 
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
     * 删除自定义档案包
     * 
     * @param id 自定义档案包主键
     * @return 结果
     */
    public int deleteStReportPackageCustomById(Long id);

    /**
     * 批量删除自定义档案包
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStReportPackageCustomByIds(Long[] ids);

    /**
     * 根据档案包编码删除
     * 
     * @param packageCode 档案包编码
     * @return 结果
     */
    public int deleteStReportPackageCustomByCode(String packageCode);
}
