package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StReportPdf;

/**
 * 报表PDF记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-15
 */
public interface StReportPdfMapper 
{
    /**
     * 查询报表PDF记录
     * 
     * @param id 报表PDF记录主键
     * @return 报表PDF记录
     */
    public StReportPdf selectStReportPdfById(Long id);

    /**
     * 查询报表PDF记录列表
     * 
     * @param stReportPdf 报表PDF记录
     * @return 报表PDF记录集合
     */
    public List<StReportPdf> selectStReportPdfList(StReportPdf stReportPdf);

    /**
     * 根据ID集合查询报表PDF记录
     *
     * @param ids ID集合
     * @return 报表PDF记录集合
     */
    public List<StReportPdf> selectStReportPdfByIds(List<Long> ids);

    /**
     * 新增报表PDF记录
     * 
     * @param stReportPdf 报表PDF记录
     * @return 结果
     */
    public int insertStReportPdf(StReportPdf stReportPdf);

    /**
     * 修改报表PDF记录
     * 
     * @param stReportPdf 报表PDF记录
     * @return 结果
     */
    public int updateStReportPdf(StReportPdf stReportPdf);

    /**
     * 删除报表PDF记录
     * 
     * @param id 报表PDF记录主键
     * @return 结果
     */
    public int deleteStReportPdfById(Long id);

    /**
     * 批量删除报表PDF记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStReportPdfByIds(Long[] ids);

    /**
     * 根据批次ID查询PDF列表
     * 
     * @param batchId 批次ID
     * @return PDF列表
     */
    public List<StReportPdf> selectStReportPdfListByBatchId(String batchId);
}




