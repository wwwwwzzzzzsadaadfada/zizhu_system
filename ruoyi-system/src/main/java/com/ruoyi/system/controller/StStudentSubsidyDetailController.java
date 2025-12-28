package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.system.domain.StStudentSubsidyDetail;
import com.ruoyi.system.service.IStStudentSubsidyDetailService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 学生受助明细Controller
 * 
 * @author ruoyi
 * @date 2024-11-22
 */
@RestController
@RequestMapping("/system/subsidyDetail")
public class StStudentSubsidyDetailController extends BaseController
{
    @Autowired
    private IStStudentSubsidyDetailService stStudentSubsidyDetailService;

    /**
     * 查询学生受助明细列表
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyDetail:list')")
    @GetMapping("/list")
    public TableDataInfo list(StStudentSubsidyDetail stStudentSubsidyDetail)
    {
        startPage();
        List<StStudentSubsidyDetail> list = stStudentSubsidyDetailService.selectStStudentSubsidyDetailList(stStudentSubsidyDetail);
        return getDataTable(list);
    }

    /**
     * 导出学生受助明细列表
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyDetail:export')")
    @Log(title = "学生受助明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StStudentSubsidyDetail stStudentSubsidyDetail)
    {
        List<StStudentSubsidyDetail> list = stStudentSubsidyDetailService.selectStStudentSubsidyDetailList(stStudentSubsidyDetail);
        ExcelUtil<StStudentSubsidyDetail> util = new ExcelUtil<StStudentSubsidyDetail>(StStudentSubsidyDetail.class);
        util.exportExcel(response, list, "学生受助明细数据");
    }

    /**
     * 获取学生受助明细详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyDetail:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stStudentSubsidyDetailService.selectStStudentSubsidyDetailById(id));
    }

    /**
     * 新增学生受助明细
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyDetail:add')")
    @Log(title = "学生受助明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StStudentSubsidyDetail stStudentSubsidyDetail)
    {
        return toAjax(stStudentSubsidyDetailService.insertStStudentSubsidyDetail(stStudentSubsidyDetail));
    }

    /**
     * 修改学生受助明细
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyDetail:edit')")
    @Log(title = "学生受助明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StStudentSubsidyDetail stStudentSubsidyDetail)
    {
        return toAjax(stStudentSubsidyDetailService.updateStStudentSubsidyDetail(stStudentSubsidyDetail));
    }

    /**
     * 删除学生受助明细
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyDetail:remove')")
    @Log(title = "学生受助明细", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stStudentSubsidyDetailService.deleteStStudentSubsidyDetailByIds(ids));
    }
}