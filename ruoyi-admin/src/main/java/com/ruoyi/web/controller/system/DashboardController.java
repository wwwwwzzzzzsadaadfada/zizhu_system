package com.ruoyi.web.controller.system;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IDashboardService;

/**
 * 控制舱数据Controller
 * 
 * @author ruoyi
 * @date 2024-12-29
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController
{
    @Autowired
    private IDashboardService dashboardService;

    /**
     * 获取控制舱所有统计数据
     * @param segment 学段筛选: all-全部, high-高中, compulsory-义教(小学+初中)
     */
    @PreAuthorize("@ss.hasPermi('dashboard:data:query')")
    @GetMapping("/data")
    public AjaxResult getData(@RequestParam(value = "segment", required = false, defaultValue = "all") String segment)
    {
        Map<String, Object> result = new HashMap<>();
        
        // 获取顶部统计数据
        result.put("topStats", dashboardService.getTopStats(segment));
        
        // 获取困难类型分布
        result.put("difficultyTypeDistribution", dashboardService.getDifficultyTypeDistribution(segment));
        
        // 获取各学段受助人数
        result.put("schoolLevelStats", dashboardService.getSchoolLevelStats(segment));
        
        // 获取指标下达表统计
        result.put("quotaStats", dashboardService.getQuotaStats(segment));
        
        // 获取学期预算表统计
        result.put("budgetStats", dashboardService.getBudgetStats(segment));
        
        // 获取地图数据(各县区受助学生)
        result.put("mapData", dashboardService.getMapData(segment));
        
        // 获取学段受助金额统计
        result.put("amountBySchoolLevel", dashboardService.getAmountBySchoolLevel(segment));
        
        // 获取资助项目列表
        result.put("projectList", dashboardService.getProjectList(segment));
        
        // 获取用户注册统计
        result.put("userRegistrations", dashboardService.getUserRegistrations());
        
        // 获取本学期受助人数趋势
        result.put("subsidyTrend", dashboardService.getSubsidyTrend(segment));
        
        return success(result);
    }

    /**
     * 获取顶部统计数据
     * @param segment 学段筛选: all-全部, high-高中, compulsory-义教(小学+初中)
     */
    @PreAuthorize("@ss.hasPermi('dashboard:topStats:query')")
    @GetMapping("/topStats")
    public AjaxResult getTopStats(@RequestParam(value = "segment", required = false, defaultValue = "all") String segment)
    {
        return success(dashboardService.getTopStats(segment));
    }

    /**
     * 获取困难类型分布
     * @param segment 学段筛选
     */
    @PreAuthorize("@ss.hasPermi('dashboard:difficultyType:query')")
    @GetMapping("/difficultyTypeDistribution")
    public AjaxResult getDifficultyTypeDistribution(@RequestParam(value = "segment", required = false, defaultValue = "all") String segment)
    {
        return success(dashboardService.getDifficultyTypeDistribution(segment));
    }

    /**
     * 获取各学段受助人数
     * @param segment 学段筛选
     */
    @PreAuthorize("@ss.hasPermi('dashboard:schoolLevel:query')")
    @GetMapping("/schoolLevelStats")
    public AjaxResult getSchoolLevelStats(@RequestParam(value = "segment", required = false, defaultValue = "all") String segment)
    {
        return success(dashboardService.getSchoolLevelStats(segment));
    }

    /**
     * 获取指标下达表统计
     * @param segment 学段筛选
     */
    @PreAuthorize("@ss.hasPermi('dashboard:quota:query')")
    @GetMapping("/quotaStats")
    public AjaxResult getQuotaStats(@RequestParam(value = "segment", required = false, defaultValue = "all") String segment)
    {
        return success(dashboardService.getQuotaStats(segment));
    }

    /**
     * 获取学期预算表统计
     * @param segment 学段筛选
     */
    @PreAuthorize("@ss.hasPermi('dashboard:budget:query')")
    @GetMapping("/budgetStats")
    public AjaxResult getBudgetStats(@RequestParam(value = "segment", required = false, defaultValue = "all") String segment)
    {
        return success(dashboardService.getBudgetStats(segment));
    }

    /**
     * 获取地图数据
     * @param segment 学段筛选
     */
    @PreAuthorize("@ss.hasPermi('dashboard:map:query')")
    @GetMapping("/mapData")
    public AjaxResult getMapData(@RequestParam(value = "segment", required = false, defaultValue = "all") String segment)
    {
        return success(dashboardService.getMapData(segment));
    }

    /**
     * 获取学段受助金额统计
     * @param segment 学段筛选
     */
    @PreAuthorize("@ss.hasPermi('dashboard:amount:query')")
    @GetMapping("/amountBySchoolLevel")
    public AjaxResult getAmountBySchoolLevel(@RequestParam(value = "segment", required = false, defaultValue = "all") String segment)
    {
        return success(dashboardService.getAmountBySchoolLevel(segment));
    }

    /**
     * 获取资助项目列表
     * @param segment 学段筛选
     */
    @PreAuthorize("@ss.hasPermi('dashboard:project:query')")
    @GetMapping("/projectList")
    public AjaxResult getProjectList(@RequestParam(value = "segment", required = false, defaultValue = "all") String segment)
    {
        return success(dashboardService.getProjectList(segment));
    }

    /**
     * 获取用户注册统计
     */
    @PreAuthorize("@ss.hasPermi('dashboard:user:query')")
    @GetMapping("/userRegistrations")
    public AjaxResult getUserRegistrations()
    {
        return success(dashboardService.getUserRegistrations());
    }

    /**
     * 获取本学期受助人数趋势
     * @param segment 学段筛选
     */
    @PreAuthorize("@ss.hasPermi('dashboard:trend:query')")
    @GetMapping("/subsidyTrend")
    public AjaxResult getSubsidyTrend(@RequestParam(value = "segment", required = false, defaultValue = "all") String segment)
    {
        return success(dashboardService.getSubsidyTrend(segment));
    }
}
