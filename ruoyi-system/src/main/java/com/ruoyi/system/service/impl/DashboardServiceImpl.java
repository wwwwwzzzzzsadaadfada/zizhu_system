package com.ruoyi.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.StSchoolYearSemester;
import com.ruoyi.system.mapper.DashboardMapper;
import com.ruoyi.system.mapper.StSchoolYearSemesterMapper;
import com.ruoyi.system.service.IDashboardService;

/**
 * 控制舱数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-12-29
 */
@Service
public class DashboardServiceImpl implements IDashboardService 
{
    @Autowired
    private DashboardMapper dashboardMapper;

    @Autowired
    private StSchoolYearSemesterMapper stSchoolYearSemesterMapper;

    /**
     * 获取当前学期
     */
    private StSchoolYearSemester getCurrentSemester()
    {
        return stSchoolYearSemesterMapper.selectCurrentSemester(true);
    }

    /**
     * 获取顶部统计数据
     * 
     * @param segment 学段筛选: all-全部, high-高中, compulsory-义教(小学+初中)
     */
    @Override
    public Map<String, Object> getTopStats(String segment)
    {
        Map<String, Object> result = new HashMap<>();
        
        StSchoolYearSemester currentSemester = getCurrentSemester();
        Long yearSemesterId = null;
        
        if (currentSemester != null) {
            yearSemesterId = currentSemester.getId();
        }
        
        // 在校学生总数
        int totalStudents = dashboardMapper.countTotalStudents(segment);
        result.put("totalStudents", totalStudents);
        
        // 困难学生总数(从学生基础表统计所有有困难类型的学生)
        int difficultyStudents = dashboardMapper.countDifficultyStudents(segment);
        result.put("difficultyStudents", difficultyStudents);
        
        // 受助人次(当前学期)
        int subsidyTimes = dashboardMapper.countSubsidyTimes(yearSemesterId, segment);
        result.put("subsidyTimes", subsidyTimes);
        
        // 资助档案数(当前学期)
        int subsidyRecords = dashboardMapper.countSubsidyRecords(yearSemesterId, segment);
        result.put("subsidyRecords", subsidyRecords);
        
        // 资助项目数
        int subsidyProjects = dashboardMapper.countSubsidyProjects(segment);
        result.put("subsidyProjects", subsidyProjects);
        
        return result;
    }

    /**
     * 获取困难类型分布
     * 
     * @param segment 学段筛选
     */
    @Override
    public List<Map<String, Object>> getDifficultyTypeDistribution(String segment)
    {
        StSchoolYearSemester currentSemester = getCurrentSemester();
        Long yearSemesterId = null;
        
        if (currentSemester != null) {
            yearSemesterId = currentSemester.getId();
        }
        
        return dashboardMapper.getDifficultyTypeDistribution(yearSemesterId, segment);
    }

    /**
     * 获取各学段受助人数
     * 
     * @param segment 学段筛选
     */
    @Override
    public List<Map<String, Object>> getSchoolLevelStats(String segment)
    {
        StSchoolYearSemester currentSemester = getCurrentSemester();
        Long yearSemesterId = null;
        
        if (currentSemester != null) {
            yearSemesterId = currentSemester.getId();
        }
        
        return dashboardMapper.getSchoolLevelStats(yearSemesterId, segment);
    }

    /**
     * 获取指标下达表统计
     * 
     * @param segment 学段筛选
     */
    @Override
    public Map<String, Object> getQuotaStats(String segment)
    {
        StSchoolYearSemester currentSemester = getCurrentSemester();
        Long yearSemesterId = null;
        
        if (currentSemester != null) {
            yearSemesterId = currentSemester.getId();
        }
        
        return dashboardMapper.getQuotaStats(yearSemesterId, segment);
    }

    /**
     * 获取学期预算表统计
     * 
     * @param segment 学段筛选
     */
    @Override
    public Map<String, Object> getBudgetStats(String segment)
    {
        StSchoolYearSemester currentSemester = getCurrentSemester();
        Long yearSemesterId = null;
        
        if (currentSemester != null) {
            yearSemesterId = currentSemester.getId();
        }
        
        return dashboardMapper.getBudgetStats(yearSemesterId, segment);
    }

    /**
     * 获取地图数据
     * 
     * @param segment 学段筛选
     */
    @Override
    public List<Map<String, Object>> getMapData(String segment)
    {
        StSchoolYearSemester currentSemester = getCurrentSemester();
        Long yearSemesterId = null;
        
        if (currentSemester != null) {
            yearSemesterId = currentSemester.getId();
        }
        
        return dashboardMapper.getMapData(yearSemesterId, segment);
    }

    /**
     * 获取学段受助金额统计
     * 
     * @param segment 学段筛选
     */
    @Override
    public List<Map<String, Object>> getAmountBySchoolLevel(String segment)
    {
        StSchoolYearSemester currentSemester = getCurrentSemester();
        Long yearSemesterId = null;
        
        if (currentSemester != null) {
            yearSemesterId = currentSemester.getId();
        }
        
        return dashboardMapper.getAmountBySchoolLevel(yearSemesterId, segment);
    }

    /**
     * 获取资助项目列表
     * 
     * @param segment 学段筛选
     */
    @Override
    public List<Map<String, Object>> getProjectList(String segment)
    {
        // 获取最近3条项目记录
        return dashboardMapper.getProjectList(3, segment);
    }

    /**
     * 获取用户注册统计
     */
    @Override
    public List<Map<String, Object>> getUserRegistrations()
    {
        // 获取最近3条用户注册记录
        return dashboardMapper.getUserRegistrations(3);
    }

    /**
     * 获取本学期受助人数趋势
     * 
     * @param segment 学段筛选
     */
    @Override
    public List<Map<String, Object>> getSubsidyTrend(String segment)
    {
        StSchoolYearSemester currentSemester = getCurrentSemester();
        Long yearSemesterId = null;
        
        if (currentSemester != null) {
            yearSemesterId = currentSemester.getId();
        }
        
        return dashboardMapper.getSubsidyTrend(yearSemesterId, segment);
    }
}
