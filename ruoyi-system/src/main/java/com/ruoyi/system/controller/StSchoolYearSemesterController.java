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
import com.ruoyi.system.domain.StSchoolYearSemester;
import com.ruoyi.system.service.IStSchoolYearSemesterService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 学年学期Controller
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
@RestController
@RequestMapping("/system/yearSemester")
public class StSchoolYearSemesterController extends BaseController
{
    @Autowired
    private IStSchoolYearSemesterService stSchoolYearSemesterService;

    /**
     * 查询学年学期列表
     */
    @PreAuthorize("@ss.hasPermi('system:yearSemester:list')")
    @GetMapping("/list")
    public TableDataInfo list(StSchoolYearSemester stSchoolYearSemester)
    {
        startPage();
        List<StSchoolYearSemester> list = stSchoolYearSemesterService.selectStSchoolYearSemesterList(stSchoolYearSemester);
        return getDataTable(list);
    }

    /**
     * 导出学年学期列表
     */
    @PreAuthorize("@ss.hasPermi('system:yearSemester:export')")
    @Log(title = "学年学期", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StSchoolYearSemester stSchoolYearSemester)
    {
        List<StSchoolYearSemester> list = stSchoolYearSemesterService.selectStSchoolYearSemesterList(stSchoolYearSemester);
        ExcelUtil<StSchoolYearSemester> util = new ExcelUtil<StSchoolYearSemester>(StSchoolYearSemester.class);
        util.exportExcel(response, list, "学年学期数据");
    }

    /**
     * 获取学年学期详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:yearSemester:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stSchoolYearSemesterService.selectStSchoolYearSemesterById(id));
    }

    /**
     * 新增学年学期
     */
    @PreAuthorize("@ss.hasPermi('system:yearSemester:add')")
    @Log(title = "学年学期", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StSchoolYearSemester stSchoolYearSemester)
    {
        return toAjax(stSchoolYearSemesterService.insertStSchoolYearSemester(stSchoolYearSemester));
    }

    /**
     * 修改学年学期
     */
    @PreAuthorize("@ss.hasPermi('system:yearSemester:edit')")
    @Log(title = "学年学期", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StSchoolYearSemester stSchoolYearSemester)
    {
        return toAjax(stSchoolYearSemesterService.updateStSchoolYearSemester(stSchoolYearSemester));
    }

    /**
     * 删除学年学期
     */
    @PreAuthorize("@ss.hasPermi('system:yearSemester:remove')")
    @Log(title = "学年学期", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stSchoolYearSemesterService.deleteStSchoolYearSemesterByIds(ids));
    }

    /**
     * 设置当前学期
     */
    @PreAuthorize("@ss.hasPermi('system:yearSemester:setCurrent')")
    @Log(title = "设置当前学期", businessType = BusinessType.UPDATE)
    @PutMapping("/setCurrent/{id}")
    public AjaxResult setCurrent(@PathVariable("id") Long id)
    {
        return toAjax(stSchoolYearSemesterService.setCurrentSemester(id));
    }

    /**
     * 获取当前学期
     */
    @GetMapping("/current")
    public AjaxResult getCurrent()
    {
        return success(stSchoolYearSemesterService.selectCurrentSemester());
    }
}
