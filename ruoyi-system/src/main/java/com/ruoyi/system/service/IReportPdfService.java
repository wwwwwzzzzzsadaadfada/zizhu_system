package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StReportPdf;
import com.ruoyi.system.domain.StReportPdfBatch;

/**
 * 报表PDF服务接口
 * 
 * @author ruoyi
 * @date 2025-12-15
 */
public interface IReportPdfService 
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
     * 查询文件包列表（按reportId分组）
     * 
     * @param stReportPdf 查询条件
     * @return 文件包列表
     */
    public List<java.util.Map<String, Object>> selectPackageList(StReportPdf stReportPdf);

    /**
     * 查询可用报表列表（用于合并PDF）
     * 
     * @param stReportPdf 查询条件
     * @return 可用报表列表
     */
    public List<java.util.Map<String, Object>> selectAvailableReports(StReportPdf stReportPdf);

    /**
     * 生成并保存单个学生报表PDF
     * 
     * @param studentId 学生ID
     * @param reportId 报表ID
     * @param yearSemesterId 学年学期ID
     * @return PDF记录
     */
    public StReportPdf generateAndSavePdf(Long studentId, String reportId, Long yearSemesterId);

    /**
     * 批量生成并保存学生报表PDF
     * 
     * @param studentIds 学生ID列表
     * @param reportId 报表ID
     * @param yearSemesterId 学年学期ID
     * @param batchName 批次名称
     * @return 批次信息
     */
    public StReportPdfBatch batchGenerateAndSavePdf(List<Long> studentIds,
                                                   String reportId,
                                                   Long yearSemesterId,
                                                   String batchName,
                                                   Long schoolingPlanId,
                                                   String studentName,
                                                   Long gradeId,
                                                   Long classId);

    /**
     * 合并PDF文件
     * 
     * @param pdfIds PDF记录ID列表
     * @param mergedFileName 合并后的文件名
     * @return 合并批次信息
     */
    public StReportPdfBatch mergePdfs(List<Long> pdfIds, String mergedFileName);

    /**
     * 按条件批量合并PDF
     * 
     * @param reportId 报表ID
     * @param yearSemesterId 学年学期ID
     * @param schoolingPlanId 学制ID（可选）
     * @param gradeId 年级ID（可选）
     * @param classId 班级ID（可选）
     * @param mergedFileName 合并后的文件名
     * @return 合并批次信息
     */
    public StReportPdfBatch mergePdfsByCondition(String reportId, Long yearSemesterId, 
                                                 Long schoolingPlanId, Long gradeId, Long classId, 
                                                 String mergedFileName);

    /**
     * 通过ID集合批量查询PDF记录
     */
    public List<StReportPdf> selectStReportPdfByIds(List<Long> ids);

    /**
     * 更新报表PDF记录
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
     * 查询PDF合并批次列表
     * 
     * @param stReportPdfBatch PDF合并批次
     * @return PDF合并批次集合
     */
    public List<StReportPdfBatch> selectStReportPdfBatchList(StReportPdfBatch stReportPdfBatch);

    /**
     * 查询PDF合并批次
     * 
     * @param id PDF合并批次主键
     * @return PDF合并批次
     */
    public StReportPdfBatch selectStReportPdfBatchById(Long id);

    /**
     * 按报表ID删除文件包（删除该报表的所有PDF归档记录及物理文件）
     * 
     * @param reportId 报表ID
     * @return 删除的记录数
     */
    public int deletePackageByReportId(String reportId);
}



