package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.system.service.IReportExcelService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.StReportExcel;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * Excel报表Controller
 * 
 * @author ruoyi
 * @date 2025-01-03
 */
@RestController
@RequestMapping("/system/stReportExcel")
public class StReportExcelController extends BaseController
{
    @Autowired
    private IReportExcelService stReportExcelService;

    /**
     * 查询Excel报表列表
     */
    @PreAuthorize("@ss.hasPermi('system:reportExcel:list')")
    @GetMapping("/list")
    public TableDataInfo list(StReportExcel stReportExcel)
    {
        startPage();
        List<StReportExcel> list = stReportExcelService.selectStReportExcelList(stReportExcel);
        return getDataTable(list);
    }

    /**
     * 导出Excel报表列表
     */
    @PreAuthorize("@ss.hasPermi('system:reportExcel:export')")
    @Log(title = "Excel报表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StReportExcel stReportExcel)
    {
        List<StReportExcel> list = stReportExcelService.selectStReportExcelList(stReportExcel);
        ExcelUtil<StReportExcel> util = new ExcelUtil<StReportExcel>(StReportExcel.class);
        util.exportExcel(response, list, "Excel报表数据");
    }

    /**
     * 获取Excel报表详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:reportExcel:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(stReportExcelService.selectStReportExcelById(id));
    }

    /**
     * 新增Excel报表
     */
    @PreAuthorize("@ss.hasPermi('system:reportExcel:add')")
    @Log(title = "Excel报表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StReportExcel stReportExcel)
    {
        return toAjax(stReportExcelService.insertStReportExcel(stReportExcel));
    }

    /**
     * 修改Excel报表
     */
    @PreAuthorize("@ss.hasPermi('system:reportExcel:edit')")
    @Log(title = "Excel报表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StReportExcel stReportExcel)
    {
        return toAjax(stReportExcelService.updateStReportExcel(stReportExcel));
    }

    /**
     * 删除Excel报表
     */
    @PreAuthorize("@ss.hasPermi('system:reportExcel:remove')")
    @Log(title = "Excel报表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stReportExcelService.deleteStReportExcelByIds(ids));
    }

    /**
     * 生成Excel报表
     */
    @PreAuthorize("@ss.hasPermi('system:reportExcel:generate')")
    @Log(title = "生成Excel报表", businessType = BusinessType.INSERT)
    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody StReportExcelParam param)
    {
        StReportExcel excelRecord = stReportExcelService.generateAndSaveExcel(
            param.getStudentId(), 
            param.getReportId(), 
            param.getYearSemesterId()
        );
        return AjaxResult.success(excelRecord);
    }

    /**
     * 批量生成Excel报表
     */
    @PreAuthorize("@ss.hasPermi('system:reportExcel:batchGenerate')")
    @Log(title = "批量生成Excel报表", businessType = BusinessType.INSERT)
    @PostMapping("/batchGenerate")
    public AjaxResult batchGenerate(@RequestBody StReportExcelBatchParam param)
    {
        stReportExcelService.batchGenerateExcel(
            param.getStudentIds(),
            param.getReportId(),
            param.getYearSemesterId(),
            param.getBatchName(),
            param.getSchoolingPlanId(),
            param.getStudentName(),
            param.getGradeId(),
            param.getClassId()
        );
        return AjaxResult.success();
    }

    /**
     * 下载Excel报表
     */
    @PreAuthorize("@ss.hasPermi('system:reportExcel:download')")
    @GetMapping("/download/{id}")
    public void download(@PathVariable Long id, HttpServletResponse response)
    {
        stReportExcelService.downloadExcel(id, response);
    }

    /**
     * Excel报表参数类
     */
    public static class StReportExcelParam {
        private Long studentId;
        private String reportId;
        private Long yearSemesterId;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getReportId() {
            return reportId;
        }

        public void setReportId(String reportId) {
            this.reportId = reportId;
        }

        public Long getYearSemesterId() {
            return yearSemesterId;
        }

        public void setYearSemesterId(Long yearSemesterId) {
            this.yearSemesterId = yearSemesterId;
        }
    }

    /**
     * Excel报表批量参数类
     */
    public static class StReportExcelBatchParam {
        private List<Long> studentIds;
        private String reportId;
        private Long yearSemesterId;
        private String batchName;
        private Long schoolingPlanId;
        private String studentName;
        private Long gradeId;
        private Long classId;

        public List<Long> getStudentIds() {
            return studentIds;
        }

        public void setStudentIds(List<Long> studentIds) {
            this.studentIds = studentIds;
        }

        public String getReportId() {
            return reportId;
        }

        public void setReportId(String reportId) {
            this.reportId = reportId;
        }

        public Long getYearSemesterId() {
            return yearSemesterId;
        }

        public void setYearSemesterId(Long yearSemesterId) {
            this.yearSemesterId = yearSemesterId;
        }

        public String getBatchName() {
            return batchName;
        }

        public void setBatchName(String batchName) {
            this.batchName = batchName;
        }

        public Long getSchoolingPlanId() {
            return schoolingPlanId;
        }

        public void setSchoolingPlanId(Long schoolingPlanId) {
            this.schoolingPlanId = schoolingPlanId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public Long getGradeId() {
            return gradeId;
        }

        public void setGradeId(Long gradeId) {
            this.gradeId = gradeId;
        }

        public Long getClassId() {
            return classId;
        }

        public void setClassId(Long classId) {
            this.classId = classId;
        }
    }
}