package com.ruoyi.system.service;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import com.ruoyi.system.domain.StReportExcel;

/**
 * Excel报表Service接口
 * 
 * @author ruoyi
 * @date 2025-01-03
 */
public interface IReportExcelService 
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
     * 批量删除Excel报表
     * 
     * @param ids 需要删除的Excel报表主键集合
     * @return 结果
     */
    public int deleteStReportExcelByIds(Long[] ids);

    /**
     * 删除Excel报表信息
     * 
     * @param id Excel报表主键
     * @return 结果
     */
    public int deleteStReportExcelById(Long id);
    
    /**
     * 生成并保存Excel报表
     * 
     * @param studentId 学生ID
     * @param reportId 报表ID
     * @param yearSemesterId 学年学期ID
     * @return Excel报表记录
     */
    public StReportExcel generateAndSaveExcel(Long studentId, String reportId, Long yearSemesterId);

    /**
     * 批量生成Excel报表
     */
    public void batchGenerateExcel(List<Long> studentIds, String reportId, Long yearSemesterId, 
                                 String batchName, Long schoolingPlanId, String studentName, 
                                 Long gradeId, Long classId);

    /**
     * 下载Excel报表
     */
    public void downloadExcel(Long id, HttpServletResponse response);
}