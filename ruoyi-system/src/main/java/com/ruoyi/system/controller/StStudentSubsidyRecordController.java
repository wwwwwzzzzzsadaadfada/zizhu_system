package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.StStudentSubsidyRecord;
import com.ruoyi.system.domain.dto.BatchPaymentRequest;
import com.ruoyi.system.domain.dto.PaymentImportDTO;
import com.ruoyi.system.domain.vo.BatchPaymentResultVO;
import com.ruoyi.system.domain.vo.PaymentImportResultVO;
import com.ruoyi.system.service.IStStudentSubsidyRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 学生补助发放记录Controller
 * 
 * @author ruoyi
 * @date 2024-11-21
 */
@RestController
@RequestMapping("/system/subsidyRecord")
public class StStudentSubsidyRecordController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(StStudentSubsidyRecordController.class);
    
    @Autowired
    private IStStudentSubsidyRecordService stStudentSubsidyRecordService;

    /**
     * 查询学生补助发放记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(StStudentSubsidyRecord stStudentSubsidyRecord)
    {
        startPage();
        List<StStudentSubsidyRecord> list = stStudentSubsidyRecordService.selectStStudentSubsidyRecordList(stStudentSubsidyRecord);
        return getDataTable(list);
    }

    /**
     * 导出学生补助发放记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyRecord:export')")
    @Log(title = "学生补助发放记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StStudentSubsidyRecord stStudentSubsidyRecord)
    {
        List<StStudentSubsidyRecord> list = stStudentSubsidyRecordService.selectStStudentSubsidyRecordList(stStudentSubsidyRecord);
        ExcelUtil<StStudentSubsidyRecord> util = new ExcelUtil<StStudentSubsidyRecord>(StStudentSubsidyRecord.class);
        util.exportExcel(response, list, "学生补助发放记录数据");
    }

    /**
     * 获取学生补助发放记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyRecord:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stStudentSubsidyRecordService.selectStStudentSubsidyRecordById(id));
    }

    /**
     * 新增学生补助发放记录
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyRecord:add')")
    @Log(title = "学生补助发放记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StStudentSubsidyRecord stStudentSubsidyRecord)
    {
        return toAjax(stStudentSubsidyRecordService.insertStStudentSubsidyRecord(stStudentSubsidyRecord));
    }

    /**
     * 修改学生补助发放记录
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyRecord:edit')")
    @Log(title = "学生补助发放记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StStudentSubsidyRecord stStudentSubsidyRecord)
    {
        return toAjax(stStudentSubsidyRecordService.updateStStudentSubsidyRecord(stStudentSubsidyRecord));
    }

    /**
     * 删除学生补助发放记录
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyRecord:remove')")
    @Log(title = "学生补助发放记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stStudentSubsidyRecordService.deleteStStudentSubsidyRecordByIds(ids));
    }

    /**
     * 审批补助
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyRecord:approve')")
    @Log(title = "补助审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approve/{id}")
    public AjaxResult approve(
        @PathVariable Long id,
        @RequestParam Integer approvalStatus,
        @RequestParam(required = false) String approvalMemo)
    {
        return toAjax(stStudentSubsidyRecordService.approveSubsidy(id, approvalStatus, approvalMemo));
    }
    
    /**
     * 退回补助（已审批通过的记录退回）
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyRecord:return')")
    @Log(title = "补助退回", businessType = BusinessType.UPDATE)
    @PutMapping("/return/{id}")
    public AjaxResult returnSubsidy(
        @PathVariable Long id,
        @RequestParam(required = false) String returnMemo)
    {
        return toAjax(stStudentSubsidyRecordService.returnSubsidy(id, returnMemo));
    }
    
    /**
     * 批量发放补助（支持直接录入金额）
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyRecord:payment')")
    @Log(title = "批量发放补助", businessType = BusinessType.UPDATE)
    @PostMapping("/batchPayment")
    public AjaxResult batchPayment(@RequestBody BatchPaymentRequest request)
    {
        BatchPaymentResultVO result = stStudentSubsidyRecordService.batchPaymentWithStudents(request);
        return success(result);
    }
    
    /**
     * Excel导入批量发放
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyRecord:import')")
    @Log(title = "导入批量发放", businessType = BusinessType.IMPORT)
    @PostMapping("/importPayment")
    public AjaxResult importPayment(MultipartFile file) throws Exception
    {
        if (file == null || file.isEmpty()) {
            log.error("Excel导入请求失败：文件为空");
            return error("导入文件不能为空");
        }
        
        log.info("收到Excel导入请求，文件名：{}，文件大小：{}字节", 
            file.getOriginalFilename(), file.getSize());
        
        try {
            ExcelUtil<PaymentImportDTO> util = new ExcelUtil<PaymentImportDTO>(PaymentImportDTO.class);
            List<PaymentImportDTO> importList = util.importExcel(file.getInputStream());
            log.info("Excel解析完成，解析出{}条数据", importList != null ? importList.size() : 0);
            
            PaymentImportResultVO result = stStudentSubsidyRecordService.importPaymentFromExcel(importList);
            log.info("Excel导入处理完成，成功：{}条，失败：{}条，总计：{}条", 
                result.getSuccessCount(), result.getFailureCount(), result.getTotalCount());
            
            return success(result);
        } catch (Exception e) {
            log.error("Excel导入处理异常", e);
            throw e;
        }
    }
    
    /**
     * 下载批量发放导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<PaymentImportDTO> util = new ExcelUtil<PaymentImportDTO>(PaymentImportDTO.class);
        util.importTemplateExcel(response, "补助发放导入模板");
    }
}
