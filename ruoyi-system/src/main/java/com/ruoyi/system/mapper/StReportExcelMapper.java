package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StReportExcel;

/**
 * Excel报表Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-03
 */
public interface StReportExcelMapper 
{
    /**
     * 查询Excel报表
     * 
     * @param id Excel报表主键
     * @return Excel报表
     */
    public StReportExcel selectStReportExcelById(Long id);

    /**
     * 查询Excel报表列表
     * 
     * @param stReportExcel Excel报表
     * @return Excel报表集合
     */
    public List<StReportExcel> selectStReportExcelList(StReportExcel stReportExcel);

    /**
     * 新增Excel报表
     * 
     * @param stReportExcel Excel报表
     * @return 结果
     */
    public int insertStReportExcel(StReportExcel stReportExcel);

    /**
     * 修改Excel报表
     * 
     * @param stReportExcel Excel报表
     * @return 结果
     */
    public int updateStReportExcel(StReportExcel stReportExcel);

    /**
     * 删除Excel报表
     * 
     * @param id Excel报表主键
     * @return 结果
     */
    public int deleteStReportExcelById(Long id);

    /**
     * 批量删除Excel报表
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStReportExcelByIds(Long[] ids);

    /**
     * 批量查询Excel报表通过ID
     * 
     * @param ids ID列表
     * @return Excel报表列表
     */
    public List<StReportExcel> selectStReportExcelByIds(List<Long> ids);
}