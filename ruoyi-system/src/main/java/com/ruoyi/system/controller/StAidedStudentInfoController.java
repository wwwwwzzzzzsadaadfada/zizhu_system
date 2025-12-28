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
import com.ruoyi.system.domain.StAidedStudentInfo;
import com.ruoyi.system.service.IStAidedStudentInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 受助学生信息Controller
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
@RestController
@RequestMapping("/system/aidedStudentInfo")
public class StAidedStudentInfoController extends BaseController
{
    @Autowired
    private IStAidedStudentInfoService stAidedStudentInfoService;

    /**
     * 查询受助学生信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:aidedStudentInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(StAidedStudentInfo stAidedStudentInfo)
    {
        startPage();
        List<StAidedStudentInfo> list = stAidedStudentInfoService.selectStAidedStudentInfoList(stAidedStudentInfo);
        return getDataTable(list);
    }

    /**
     * 导出受助学生信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:aidedStudentInfo:export')")
    @Log(title = "受助学生信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StAidedStudentInfo stAidedStudentInfo)
    {
        List<StAidedStudentInfo> list = stAidedStudentInfoService.selectStAidedStudentInfoList(stAidedStudentInfo);
        ExcelUtil<StAidedStudentInfo> util = new ExcelUtil<StAidedStudentInfo>(StAidedStudentInfo.class);
        util.exportExcel(response, list, "受助学生信息数据");
    }

    /**
     * 获取受助学生信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:aidedStudentInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stAidedStudentInfoService.selectStAidedStudentInfoById(id));
    }

    /**
     * 新增受助学生信息
     */
    @PreAuthorize("@ss.hasPermi('system:aidedStudentInfo:add')")
    @Log(title = "受助学生信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StAidedStudentInfo stAidedStudentInfo)
    {
        return toAjax(stAidedStudentInfoService.insertStAidedStudentInfo(stAidedStudentInfo));
    }

    /**
     * 修改受助学生信息
     */
    @PreAuthorize("@ss.hasPermi('system:aidedStudentInfo:edit')")
    @Log(title = "受助学生信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StAidedStudentInfo stAidedStudentInfo)
    {
        return toAjax(stAidedStudentInfoService.updateStAidedStudentInfo(stAidedStudentInfo));
    }

    /**
     * 删除受助学生信息
     */
    @PreAuthorize("@ss.hasPermi('system:aidedStudentInfo:remove')")
    @Log(title = "受助学生信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stAidedStudentInfoService.deleteStAidedStudentInfoByIds(ids));
    }
}