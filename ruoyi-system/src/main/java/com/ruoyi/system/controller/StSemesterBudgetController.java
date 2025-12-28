package com.ruoyi.system.controller;

import java.util.List;
import java.util.stream.Collectors;
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
import com.ruoyi.system.domain.StSemesterBudget;
import com.ruoyi.system.service.IStSemesterBudgetService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.EconomyCategoryOption;

/**
 * 学期预算支出Controller
 * 
 * @author ruoyi
 * @date 2024-11-21
 */
@RestController
@RequestMapping("/system/semesterBudget")
public class StSemesterBudgetController extends BaseController
{
    @Autowired
    private IStSemesterBudgetService stSemesterBudgetService;

    /**
     * 查询学期预算支出列表
     */
    @PreAuthorize("@ss.hasPermi('system:semesterBudget:list')")
    @GetMapping("/list")
    public TableDataInfo list(StSemesterBudget stSemesterBudget)
    {
        // 如果经济分类是中文名称，转换为代码
        if (stSemesterBudget.getEconomyCategory() != null && !stSemesterBudget.getEconomyCategory().isEmpty()) {
            String economyCategory = stSemesterBudget.getEconomyCategory();
            if (!economyCategory.matches("^[1-4]$")) {  // 如果不是代码1-4
                economyCategory = convertEconomyCategoryNameToCode(economyCategory);
                stSemesterBudget.setEconomyCategory(economyCategory);
            }
        }
        
        startPage();
        List<StSemesterBudget> list = stSemesterBudgetService.selectStSemesterBudgetList(stSemesterBudget);
        return getDataTable(list);
    }

    /**
     * 查询预算项目列表（去重）
     */
    @PreAuthorize("@ss.hasPermi('system:semesterBudget:list')")
    @GetMapping("/budgetProjects")
    public AjaxResult listBudgetProjects(StSemesterBudget stSemesterBudget)
    {
        // 如果传入了经济分类参数，则根据经济分类过滤预算项目
        if (stSemesterBudget.getEconomyCategory() != null && !stSemesterBudget.getEconomyCategory().isEmpty()) {
            // 如果经济分类是中文名称，转换为代码
            String economyCategory = stSemesterBudget.getEconomyCategory();
            if (!economyCategory.matches("^[1-4]$")) {  // 如果不是代码1-4
                economyCategory = convertEconomyCategoryNameToCode(economyCategory);
                stSemesterBudget.setEconomyCategory(economyCategory);
            }
            
            // 根据经济分类查询预算项目
            List<StSemesterBudget> list = stSemesterBudgetService.selectStSemesterBudgetList(stSemesterBudget);
            // 按预算项目名称去重
            List<String> budgetProjects = list.stream()
                .filter(budget -> budget.getBudgetProjectName() != null && !budget.getBudgetProjectName().isEmpty())
                .map(StSemesterBudget::getBudgetProjectName)
                .distinct()
                .collect(Collectors.toList());
            return AjaxResult.success(budgetProjects);
        } else {
            // 否则查询所有预算项目名称
            List<String> budgetProjects;
            if (stSemesterBudget.getYearSemesterId() != null) {
                budgetProjects = stSemesterBudgetService.selectAllBudgetProjectNames(stSemesterBudget.getYearSemesterId());
            } else {
                budgetProjects = stSemesterBudgetService.selectAllBudgetProjectNames();
            }
            return AjaxResult.success(budgetProjects);
        }
    }

    /**
     * 查询经济分类列表（去重）
     */
    @PreAuthorize("@ss.hasPermi('system:semesterBudget:list')")
    @GetMapping("/economyCategories")
    public AjaxResult listEconomyCategories(StSemesterBudget stSemesterBudget)
    {
        List<StSemesterBudget> list = stSemesterBudgetService.selectStSemesterBudgetList(stSemesterBudget);
        // 按经济分类去重
        List<EconomyCategoryOption> economyCategories = list.stream()
            .filter(budget -> budget.getEconomyCategory() != null && !budget.getEconomyCategory().isEmpty())
            .map(StSemesterBudget::getEconomyCategory)
            .distinct()
            .map(code -> new EconomyCategoryOption(code, convertEconomyCategoryCodeToName(code)))  // 将代码和中文名称一起返回
            .collect(Collectors.toList());
        return AjaxResult.success(economyCategories);
    }
    
    /**
     * 根据预算项目名称查询经济分类列表（去重）
     */
    @PreAuthorize("@ss.hasPermi('system:semesterBudget:list')")
    @GetMapping("/economyCategoriesByProject")
    public AjaxResult listEconomyCategoriesByProject(StSemesterBudget stSemesterBudget)
    {
        // 添加调试日志
        System.out.println("查询经济分类参数: budgetProjectName=" + stSemesterBudget.getBudgetProjectName());
        
        // 根据预算项目名称查询经济分类
        List<StSemesterBudget> list = stSemesterBudgetService.selectStSemesterBudgetList(stSemesterBudget);
        
        // 添加调试日志
        System.out.println("查询结果数量: " + list.size());
        
        // 按经济分类去重
        List<EconomyCategoryOption> economyCategories = list.stream()
            .filter(budget -> budget.getEconomyCategory() != null && !budget.getEconomyCategory().isEmpty())
            .map(StSemesterBudget::getEconomyCategory)
            .distinct()
            .map(code -> new EconomyCategoryOption(code, convertEconomyCategoryCodeToName(code)))  // 将代码和中文名称一起返回
            .collect(Collectors.toList());
            
        // 添加调试日志
        System.out.println("去重后经济分类数量: " + economyCategories.size());
        
        return AjaxResult.success(economyCategories);
    }
    
    /**
     * 将经济分类代码转换为中文名称
     * 
     * @param code 经济分类代码
     * @return 中文名称
     */
    private String convertEconomyCategoryCodeToName(String code) {
        return com.ruoyi.system.application.query.EconomyCategoryUtils.codeToName(code);
    }
    
    /**
     * 将经济分类中文名称转换为代码
     * 
     * @param name 经济分类中文名称
     * @return 代码
     */
    private String convertEconomyCategoryNameToCode(String name) {
        switch (name) {
            case "助学金":
                return "1";
            case "免学杂费":
                return "2";
            case "免学费":
                return "3";
            case "营养改善计划":
                return "4";
            default:
                return name;
        }
    }

    /**
     * 导出学期预算支出列表
     */
    @PreAuthorize("@ss.hasPermi('system:semesterBudget:export')")
    @Log(title = "学期预算支出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StSemesterBudget stSemesterBudget)
    {
        List<StSemesterBudget> list = stSemesterBudgetService.selectStSemesterBudgetList(stSemesterBudget);
        ExcelUtil<StSemesterBudget> util = new ExcelUtil<StSemesterBudget>(StSemesterBudget.class);
        util.exportExcel(response, list, "学期预算支出数据");
    }

    /**
     * 获取学期预算支出详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:semesterBudget:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stSemesterBudgetService.selectStSemesterBudgetById(id));
    }

    /**
     * 根据预算ID查询经济分类
     */
    @PreAuthorize("@ss.hasPermi('system:semesterBudget:query')")
    @GetMapping(value = "/economyCategory/{id}")
    public AjaxResult getEconomyCategory(@PathVariable("id") Long id)
    {
        String economyCategoryCode = stSemesterBudgetService.selectEconomyCategoryByBudgetId(id);
        String economyCategoryName = convertEconomyCategoryCodeToName(economyCategoryCode);
        return success(economyCategoryName);
    }

    /**
     * 新增学期预算支出
     */
    @PreAuthorize("@ss.hasPermi('system:semesterBudget:add')")
    @Log(title = "学期预算支出", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StSemesterBudget stSemesterBudget)
    {
        return toAjax(stSemesterBudgetService.insertStSemesterBudget(stSemesterBudget));
    }

    /**
     * 修改学期预算支出
     */
    @PreAuthorize("@ss.hasPermi('system:semesterBudget:edit')")
    @Log(title = "学期预算支出", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StSemesterBudget stSemesterBudget)
    {
        return toAjax(stSemesterBudgetService.updateStSemesterBudget(stSemesterBudget));
    }

    /**
     * 删除学期预算支出
     */
    @PreAuthorize("@ss.hasPermi('system:semesterBudget:remove')")
    @Log(title = "学期预算支出", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stSemesterBudgetService.deleteStSemesterBudgetByIds(ids));
    }

    /**
     * 修改预算状态
     */
    @PreAuthorize("@ss.hasPermi('system:semesterBudget:edit')")
    @PutMapping("/status/{id}")
    public AjaxResult changeStatus(@PathVariable Long id, @RequestBody StSemesterBudget budget)
    {
        return toAjax(stSemesterBudgetService.updateBudgetStatus(id, budget.getStatus()));
    }

    /**
     * 查询预算统计
     */
    @PreAuthorize("@ss.hasPermi('system:semesterBudget:list')")
    @GetMapping("/statistics")
    public AjaxResult statistics(StSemesterBudget stSemesterBudget)
    {
        return success(stSemesterBudgetService.statistics(stSemesterBudget));
    }
}