package com.ruoyi.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import com.ruoyi.system.domain.StSchoolYearSemester;
import com.ruoyi.system.mapper.DashboardMapper;
import com.ruoyi.system.mapper.StSchoolYearSemesterMapper;
import com.ruoyi.system.service.IDashboardService;
import com.ruoyi.common.utils.EncryptionUtil;

/**
 * 控制舱数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-12-29
 */
@Service
public class DashboardServiceImpl implements IDashboardService 
{
    private static final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);

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
     * 注意：domicile字段是加密的，需要解密后再按县区分组统计
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
        
        // 查询原始数据（包含加密的domicile字段）
        List<Map<String, Object>> rawData = dashboardMapper.getMapData(yearSemesterId, segment);
        
        // 按县区分组统计（解密domicile后匹配）
        Map<String, Set<Long>> countyStudentMap = new HashMap<>();
        
        for (Map<String, Object> row : rawData)
        {
            Object studentIdObj = row.get("studentId");
            Object domicileObj = row.get("domicile");
            
            if (studentIdObj == null || domicileObj == null)
            {
                continue;
            }
            
            Long studentId = null;
            try
            {
                if (studentIdObj instanceof Number)
                {
                    studentId = ((Number) studentIdObj).longValue();
                }
                else if (studentIdObj instanceof String)
                {
                    studentId = Long.parseLong(studentIdObj.toString());
                }
            }
            catch (Exception e)
            {
                log.warn("解析studentId失败: {}", studentIdObj, e);
                continue;
            }
            
            String domicile = domicileObj.toString();
            if (StringUtils.isBlank(domicile))
            {
                continue;
            }
            
            // 解密domicile字段
            String decryptedDomicile = null;
            try
            {
                decryptedDomicile = EncryptionUtil.decrypt(domicile);
            }
            catch (Exception e)
            {
                log.debug("解密domicile失败，可能不是加密数据: {}", e.getMessage());
                decryptedDomicile = domicile; // 解密失败，使用原值
            }
            
            if (StringUtils.isBlank(decryptedDomicile))
            {
                continue;
            }
            
            // 匹配县区
            String countyName = matchCountyName(decryptedDomicile);
            if (countyName != null)
            {
                countyStudentMap.computeIfAbsent(countyName, k -> new HashSet<>()).add(studentId);
            }
        }
        
        // 转换为返回格式
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Map.Entry<String, Set<Long>> entry : countyStudentMap.entrySet())
        {
            Map<String, Object> countyData = new HashMap<>();
            countyData.put("name", entry.getKey());
            countyData.put("value", entry.getValue().size());
            result.add(countyData);
        }
        
        // 按value降序排序
        result.sort((a, b) -> {
            Integer valueA = (Integer) a.get("value");
            Integer valueB = (Integer) b.get("value");
            return valueB.compareTo(valueA);
        });
        
        log.debug("地图数据统计完成，共{}个县区，总学生数: {}", result.size(), 
                  countyStudentMap.values().stream().mapToInt(Set::size).sum());
        
        return result;
    }
    
    /**
     * 根据户籍地址匹配县区名称
     * 
     * @param domicile 户籍地址（已解密）
     * @return 县区名称，如果无法匹配则返回null
     */
    private String matchCountyName(String domicile)
    {
        if (StringUtils.isBlank(domicile))
        {
            return null;
        }
        
        if (domicile.contains("江州"))
        {
            return "江州区";
        }
        else if (domicile.contains("扶绥"))
        {
            return "扶绥县";
        }
        else if (domicile.contains("宁明"))
        {
            return "宁明县";
        }
        else if (domicile.contains("龙州"))
        {
            return "龙州县";
        }
        else if (domicile.contains("大新"))
        {
            return "大新县";
        }
        else if (domicile.contains("天等"))
        {
            return "天等县";
        }
        else if (domicile.contains("凭祥"))
        {
            return "凭祥市";
        }
        
        return null;
    }

    /**
     * 获取年级受助金额统计
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
