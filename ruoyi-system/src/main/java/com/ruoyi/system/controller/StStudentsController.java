package com.ruoyi.system.controller;

import java.util.List;
import java.util.Map;
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
import com.ruoyi.system.domain.StStudents;
import com.ruoyi.system.service.IStStudentsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 困难学生基础信息Controller
 * 
 * @author ruoyi
 * @date 2025-11-19
 */
@RestController
@RequestMapping("/system/students")
public class StStudentsController extends BaseController
{
    @Autowired
    private IStStudentsService stStudentsService;

    /**
     * 查询困难学生基础信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:students:list')")
    @GetMapping("/list")
    public TableDataInfo list(StStudents stStudents)
    {
        startPage();
        List<StStudents> list = stStudentsService.selectStStudentsList(stStudents);
        return getDataTable(list);
    }

    /**
     * 导出困难学生基础信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:students:export')")
    @Log(title = "困难学生基础信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StStudents stStudents)
    {
        List<StStudents> list = stStudentsService.selectStStudentsList(stStudents);
        ExcelUtil<StStudents> util = new ExcelUtil<StStudents>(StStudents.class);
        util.exportExcel(response, list, "困难学生基础信息数据");
    }

    /**
     * 获取困难学生基础信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:students:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(stStudentsService.selectStStudentsById(id));
    }

    /**
     * 新增困难学生基础信息
     */
    @PreAuthorize("@ss.hasPermi('system:students:add')")
    @Log(title = "困难学生基础信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StStudents stStudents)
    {
        return toAjax(stStudentsService.insertStStudents(stStudents));
    }

    /**
     * 修改困难学生基础信息
     */
    @PreAuthorize("@ss.hasPermi('system:students:edit')")
    @Log(title = "困难学生基础信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StStudents stStudents)
    {
        return toAjax(stStudentsService.updateStStudents(stStudents));
    }

    /**
     * 删除困难学生基础信息
     */
    @PreAuthorize("@ss.hasPermi('system:students:remove')")
    @Log(title = "困难学生基础信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(stStudentsService.deleteStStudentsByIds(ids));
    }

    /**
     * 获取学制列表
     */
    @GetMapping("/schoolPlans")
    public AjaxResult getSchoolPlanList()
    {
        List<Map<String, Object>> list = stStudentsService.selectSchoolPlanList();
        return success(list);
    }

    /**
     * 根据学制ID获取年级列表
     */
    @GetMapping("/grades/{schoolingPlanId}")
    public AjaxResult getGradeList(@PathVariable("schoolingPlanId") String schoolingPlanId)
    {
        List<Map<String, Object>> list = stStudentsService.selectGradeListByPlanId(schoolingPlanId);
        return success(list);
    }

    /**
     * 根据年级ID获取班级列表
     */
    @GetMapping("/classes/{gradeId}")
    public AjaxResult getClassList(@PathVariable("gradeId") String gradeId)
    {
        List<Map<String, Object>> list = stStudentsService.selectClassListByGradeId(gradeId);
        return success(list);
    }

    /**
     * 批量更新困难类型和等级
     */
    @PreAuthorize("@ss.hasPermi('system:students:edit')")
    @Log(title = "困难学生基础信息", businessType = BusinessType.UPDATE)
    @PutMapping("/batchUpdateDifficulty")
    @SuppressWarnings("unchecked")
    public AjaxResult batchUpdateDifficulty(@RequestBody Map<String, Object> params)
    {
        List<String> idsList = (List<String>) params.get("ids");
        String[] ids = idsList.toArray(new String[0]);
        String difficultyTypeId = (String) params.get("difficultyTypeId");
        String difficultyLevelId = (String) params.get("difficultyLevelId");
        String isPovertyReliefFamily = (String) params.get("isPovertyReliefFamily");
        Integer povertyReliefYear = null;
        if (params.get("povertyReliefYear") != null)
        {
            Object yearObj = params.get("povertyReliefYear");
            if (yearObj instanceof Integer)
            {
                povertyReliefYear = (Integer) yearObj;
            }
            else
            {
                povertyReliefYear = Integer.valueOf(yearObj.toString());
            }
        }
        
        return toAjax(stStudentsService.batchUpdateDifficultyInfo(ids, difficultyTypeId, difficultyLevelId, isPovertyReliefFamily, povertyReliefYear));
    }
}
