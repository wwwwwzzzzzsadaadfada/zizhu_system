package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 控制舱数据Mapper接口
 * 
 * @author ruoyi
 * @date 2024-12-29
 */
public interface DashboardMapper 
{
    /**
     * 统计在校学生总数
     * 
     * @param segment 学段筛选: all-全部, high-高中, compulsory-义教(小学+初中)
     * @return 在校学生总数
     */
    int countTotalStudents(@Param("segment") String segment);

    /**
     * 统计困难学生总数(从学生基础信息表统计所有有困难类型的学生)
     * 
     * @param segment 学段筛选
     * @return 困难学生总数
     */
    int countDifficultyStudents(@Param("segment") String segment);

    /**
     * 统计受助人次(当前学期,所有学段)
     * 
     * @param yearSemesterId 学年学期ID
     * @param segment 学段筛选
     * @return 受助人次
     */
    int countSubsidyTimes(@Param("yearSemesterId") Long yearSemesterId, @Param("segment") String segment);

    /**
     * 统计资助档案数(当前学期,所有学段)
     * 
     * @param yearSemesterId 学年学期ID
     * @param segment 学段筛选
     * @return 资助档案数
     */
    int countSubsidyRecords(@Param("yearSemesterId") Long yearSemesterId, @Param("segment") String segment);

    /**
     * 统计资助项目数
     * 
     * @param segment 学段筛选
     * @return 资助项目数
     */
    int countSubsidyProjects(@Param("segment") String segment);

    /**
     * 获取困难类型分布(当前学期,所有学段)
     * 
     * @param yearSemesterId 学年学期ID
     * @param segment 学段筛选
     * @return 困难类型分布数据
     */
    List<Map<String, Object>> getDifficultyTypeDistribution(@Param("yearSemesterId") Long yearSemesterId, @Param("segment") String segment);

    /**
     * 获取各学段受助人数(当前学期,统计所有学段)
     * 
     * @param yearSemesterId 学年学期ID
     * @param segment 学段筛选
     * @return 学段受助人数数据
     */
    List<Map<String, Object>> getSchoolLevelStats(@Param("yearSemesterId") Long yearSemesterId, @Param("segment") String segment);

    /**
     * 获取指标下达表统计(当前学期)
     * 统计st_subsidy_quota表的数据
     * 
     * @param yearSemesterId 学年学期ID
     * @param segment 学段筛选
     * @return 指标下达表统计数据
     */
    Map<String, Object> getQuotaStats(@Param("yearSemesterId") Long yearSemesterId, @Param("segment") String segment);

    /**
     * 获取学期预算表统计(当前学期)
     * 统计st_semester_budget表的数据
     * 
     * @param yearSemesterId 学年学期ID
     * @param segment 学段筛选
     * @return 学期预算表统计数据
     */
    Map<String, Object> getBudgetStats(@Param("yearSemesterId") Long yearSemesterId, @Param("segment") String segment);

    /**
     * 获取地图数据(按县区统计,当前学期,所有学段)
     * 
     * @param yearSemesterId 学年学期ID
     * @param segment 学段筛选
     * @return 地图数据
     */
    List<Map<String, Object>> getMapData(@Param("yearSemesterId") Long yearSemesterId, @Param("segment") String segment);

    /**
     * 获取学段受助金额统计(当前学期,所有学段)
     * 
     * @param yearSemesterId 学年学期ID
     * @param segment 学段筛选
     * @return 学段受助金额统计数据
     */
    List<Map<String, Object>> getAmountBySchoolLevel(@Param("yearSemesterId") Long yearSemesterId, @Param("segment") String segment);

    /**
     * 获取资助项目列表(最近的项目,所有学段)
     * 
     * @param limit 限制条数
     * @param segment 学段筛选
     * @return 资助项目列表
     */
    List<Map<String, Object>> getProjectList(@Param("limit") int limit, @Param("segment") String segment);

    /**
     * 获取用户注册统计(最近几个月)
     * 
     * @param limit 限制条数
     * @return 用户注册统计数据
     */
    List<Map<String, Object>> getUserRegistrations(@Param("limit") int limit);

    /**
     * 获取本学期受助人数趋势(按月份,所有学段)
     * 
     * @param yearSemesterId 学年学期ID
     * @param segment 学段筛选
     * @return 受助人数趋势数据
     */
    List<Map<String, Object>> getSubsidyTrend(@Param("yearSemesterId") Long yearSemesterId, @Param("segment") String segment);
}
