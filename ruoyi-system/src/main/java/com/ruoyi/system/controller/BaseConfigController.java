package com.ruoyi.system.controller;

import java.util.List;
import java.util.Map;
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
import com.ruoyi.system.service.IBaseConfigService;

/**
 * 基础配置Controller
 * 
 * @author ruoyi
 * @date 2025-11-20
 */
@RestController
@RequestMapping("/system/baseconfig")
public class BaseConfigController extends BaseController
{
    @Autowired
    private IBaseConfigService baseConfigService;

    // ==================== 学制管理 ====================
    
    /**
     * 查询学制列表
     */
    @PreAuthorize("@ss.hasPermi('system:baseconfig:list')")
    @GetMapping("/schoolPlans")
    public AjaxResult listSchoolPlans()
    {
        List<Map<String, Object>> list = baseConfigService.selectSchoolPlanList();
        return success(list);
    }

    /**
     * 获取学制详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:baseconfig:query')")
    @GetMapping(value = "/schoolPlan/{id}")
    public AjaxResult getSchoolPlan(@PathVariable("id") Long id)
    {
        return success(baseConfigService.selectSchoolPlanById(id));
    }

    /**
     * 新增学制
     */
    @PreAuthorize("@ss.hasPermi('system:baseconfig:add')")
    @Log(title = "学制管理", businessType = BusinessType.INSERT)
    @PostMapping("/schoolPlan")
    public AjaxResult addSchoolPlan(@RequestBody Map<String, Object> params)
    {
        return toAjax(baseConfigService.insertSchoolPlan(params));
    }

    /**
     * 修改学制
     */
    @PreAuthorize("@ss.hasPermi('system:baseconfig:edit')")
    @Log(title = "学制管理", businessType = BusinessType.UPDATE)
    @PutMapping("/schoolPlan")
    public AjaxResult editSchoolPlan(@RequestBody Map<String, Object> params)
    {
        return toAjax(baseConfigService.updateSchoolPlan(params));
    }

    /**
     * 删除学制
     */
    @PreAuthorize("@ss.hasPermi('system:baseconfig:remove')")
    @Log(title = "学制管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/schoolPlan/{id}")
    public AjaxResult removeSchoolPlan(@PathVariable Long id)
    {
        return toAjax(baseConfigService.deleteSchoolPlanById(id));
    }

    // ==================== 年级管理 ====================
    
    /**
     * 查询年级列表
     */
    @PreAuthorize("@ss.hasPermi('system:baseconfig:list')")
    @GetMapping("/grades")
    public AjaxResult listGrades(Long schoolingPlanId)
    {
        List<Map<String, Object>> list = baseConfigService.selectGradeList(schoolingPlanId);
        return success(list);
    }

    /**
     * 获取年级详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:baseconfig:query')")
    @GetMapping(value = "/grade/{id}")
    public AjaxResult getGrade(@PathVariable("id") Long id)
    {
        return success(baseConfigService.selectGradeById(id));
    }

    /**
     * 新增年级
     */
    @PreAuthorize("@ss.hasPermi('system:baseconfig:add')")
    @Log(title = "年级管理", businessType = BusinessType.INSERT)
    @PostMapping("/grade")
    public AjaxResult addGrade(@RequestBody Map<String, Object> params)
    {
        return toAjax(baseConfigService.insertGrade(params));
    }

    /**
     * 修改年级
     */
    @PreAuthorize("@ss.hasPermi('system:baseconfig:edit')")
    @Log(title = "年级管理", businessType = BusinessType.UPDATE)
    @PutMapping("/grade")
    public AjaxResult editGrade(@RequestBody Map<String, Object> params)
    {
        return toAjax(baseConfigService.updateGrade(params));
    }

    /**
     * 删除年级
     */
    @PreAuthorize("@ss.hasPermi('system:baseconfig:remove')")
    @Log(title = "年级管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/grade/{id}")
    public AjaxResult removeGrade(@PathVariable Long id)
    {
        return toAjax(baseConfigService.deleteGradeById(id));
    }

    // ==================== 班级管理 ====================
    
    /**
     * 查询班级列表
     */
    @PreAuthorize("@ss.hasPermi('system:baseconfig:list')")
    @GetMapping("/classes")
    public AjaxResult listClasses(Long gradeId)
    {
        List<Map<String, Object>> list = baseConfigService.selectClassList(gradeId);
        return success(list);
    }

    /**
     * 获取班级详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:baseconfig:query')")
    @GetMapping(value = "/class/{classId}")
    public AjaxResult getClass(@PathVariable("classId") Long classId)
    {
        return success(baseConfigService.selectClassById(classId));
    }

    /**
     * 新增班级
     */
    @PreAuthorize("@ss.hasPermi('system:baseconfig:add')")
    @Log(title = "班级管理", businessType = BusinessType.INSERT)
    @PostMapping("/class")
    public AjaxResult addClass(@RequestBody Map<String, Object> params)
    {
        return toAjax(baseConfigService.insertClass(params));
    }

    /**
     * 修改班级
     */
    @PreAuthorize("@ss.hasPermi('system:baseconfig:edit')")
    @Log(title = "班级管理", businessType = BusinessType.UPDATE)
    @PutMapping("/class")
    public AjaxResult editClass(@RequestBody Map<String, Object> params)
    {
        return toAjax(baseConfigService.updateClass(params));
    }

    /**
     * 删除班级
     */
    @PreAuthorize("@ss.hasPermi('system:baseconfig:remove')")
    @Log(title = "班级管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/class/{classId}")
    public AjaxResult removeClass(@PathVariable Long classId)
    {
        return toAjax(baseConfigService.deleteClassById(classId));
    }
}
