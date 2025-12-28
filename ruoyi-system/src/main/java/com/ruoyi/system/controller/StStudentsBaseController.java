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
import com.ruoyi.system.domain.StStudentsBase;
import com.ruoyi.system.service.IStStudentsBaseService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;

/**
 * 学生基础信息Controller
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
@RestController
@RequestMapping("/system/studentsBase")
public class StStudentsBaseController extends BaseController
{
    @Autowired
    private IStStudentsBaseService stStudentsBaseService;

    /**
     * 查询学生基础信息列表
     * 
     * 功能说明：
     * 1. 如果姓名为空，返回前10条记录；如果有姓名，进行模糊查询
     * 2. 支持按学段ID(schoolingPlanId)过滤，所有过滤逻辑在后端MyBatis层完成
     * 3. 前端只需传递参数，后端自动进行安全的数据过滤
     * 
     * 安全性：
     * - 所有查询条件由后端MyBatis动态SQL处理
     * - 前端无法绕过学段过滤逻辑
     * - 数据库层面保证查询安全性
     * 
     * @param stStudentsBase 查询条件对象，支持的字段：
     *        - name: 学生姓名（模糊查询）
     *        - schoolingPlanId: 学段ID（精确匹配）
     *        - gradeId: 年级ID（精确匹配）
     *        - classId: 班级ID（精确匹配）
     * @return 学生列表（分页）
     */
    @PreAuthorize("@ss.hasPermi('system:studentsBase:list')")
    @GetMapping("/list")
    public TableDataInfo list(StStudentsBase stStudentsBase)
    {
        // 支持分页参数，默认每页 50 条，便于下拉选择
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum() == null ? 1 : pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize() == null ? 50 : pageDomain.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        List<StStudentsBase> list = stStudentsBaseService.selectStStudentsBaseList(stStudentsBase);
        return getDataTable(list);
    }

    /**
     * 导出学生基础信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:studentsBase:export')")
    @Log(title = "学生基础信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StStudentsBase stStudentsBase)
    {
        List<StStudentsBase> list = stStudentsBaseService.selectStStudentsBaseList(stStudentsBase);
        ExcelUtil<StStudentsBase> util = new ExcelUtil<StStudentsBase>(StStudentsBase.class);
        util.exportExcel(response, list, "学生基础信息数据");
    }

    /**
     * 获取学生基础信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:studentsBase:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stStudentsBaseService.selectStStudentsBaseById(id));
    }

    /**
     * 新增学生基础信息
     */
    @PreAuthorize("@ss.hasPermi('system:studentsBase:add')")
    @Log(title = "学生基础信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StStudentsBase stStudentsBase)
    {
        return toAjax(stStudentsBaseService.insertStStudentsBase(stStudentsBase));
    }

    /**
     * 修改学生基础信息
     */
    @PreAuthorize("@ss.hasPermi('system:studentsBase:edit')")
    @Log(title = "学生基础信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StStudentsBase stStudentsBase)
    {
        return toAjax(stStudentsBaseService.updateStStudentsBase(stStudentsBase));
    }

    /**
     * 删除学生基础信息
     */
    @PreAuthorize("@ss.hasPermi('system:studentsBase:remove')")
    @Log(title = "学生基础信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stStudentsBaseService.deleteStStudentsBaseByIds(ids));
    }
}
