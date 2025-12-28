package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StReportPdfBatch;

/**
 * PDF合并批次Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-15
 */
public interface StReportPdfBatchMapper 
{
    /**
     * 查询PDF合并批次
     * 
     * @param id PDF合并批次主键
     * @return PDF合并批次
     */
    public StReportPdfBatch selectStReportPdfBatchById(Long id);

    /**
     * 根据批次ID查询
     * 
     * @param batchId 批次ID（UUID）
     * @return PDF合并批次
     */
    public StReportPdfBatch selectStReportPdfBatchByBatchId(String batchId);

    /**
     * 查询PDF合并批次列表
     * 
     * @param stReportPdfBatch PDF合并批次
     * @return PDF合并批次集合
     */
    public List<StReportPdfBatch> selectStReportPdfBatchList(StReportPdfBatch stReportPdfBatch);

    /**
     * 新增PDF合并批次
     * 
     * @param stReportPdfBatch PDF合并批次
     * @return 结果
     */
    public int insertStReportPdfBatch(StReportPdfBatch stReportPdfBatch);

    /**
     * 修改PDF合并批次
     * 
     * @param stReportPdfBatch PDF合并批次
     * @return 结果
     */
    public int updateStReportPdfBatch(StReportPdfBatch stReportPdfBatch);

    /**
     * 删除PDF合并批次
     * 
     * @param id PDF合并批次主键
     * @return 结果
     */
    public int deleteStReportPdfBatchById(Long id);

    /**
     * 批量删除PDF合并批次
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStReportPdfBatchByIds(Long[] ids);
}




