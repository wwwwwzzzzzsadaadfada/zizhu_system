package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;

/**
 * 控制舱数据Service接口
 * 
 * @author ruoyi
 * @date 2024-12-29
 */
public interface IDashboardService 
{
    /**
     * 获取顶部统计数据
     * 返回：在校学生、困难学生、受助人次、资助档案、资助项目
     * 
     * @param segment 学段筛选: all-全部, high-高中, compulsory-义教(小学+初中)
     * @return 顶部统计数据
     */
    Map<String, Object> getTopStats(String segment);

    /**
     * 获取困难类型分布
     * 按困难类型统计人数
     * 
     * @param segment 学段筛选
     * @return 困难类型分布数据
     */
    List<Map<String, Object>> getDifficultyTypeDistribution(String segment);

    /**
     * 获取各学段受助人数
     * 按学制(小学/初中/高中)统计受助人数
     * 
     * @param segment 学段筛选
     * @return 学段受助人数数据
     */
    List<Map<String, Object>> getSchoolLevelStats(String segment);

    /**
     * 获取指标下达表统计
     * 统计st_subsidy_quota表的数据
     * 返回：总指标、已分配、可用指标、分配率
     * 
     * @param segment 学段筛选
     * @return 指标下达表统计数据
     */
    Map<String, Object> getQuotaStats(String segment);

    /**
     * 获取学期预算表统计
     * 统计st_semester_budget表的数据
     * 返回：总预算、已使用、锁定金额、可用预算、使用率
     * 
     * @param segment 学段筛选
     * @return 学期预算表统计数据
     */
    Map<String, Object> getBudgetStats(String segment);

    /**
     * 获取地图数据
     * 按县区统计受助学生人数
     * 
     * @param segment 学段筛选
     * @return 地图数据
     */
    List<Map<String, Object>> getMapData(String segment);

    /**
     * 获取年级受助金额统计
     * 按年级统计受助人数和金额，最后一行显示总计
     * 
     * @param segment 学段筛选
     * @return 年级受助金额统计数据
     */
    List<Map<String, Object>> getAmountBySchoolLevel(String segment);

    /**
     * 获取资助项目列表
     * 最近的资助项目列表
     * 
     * @param segment 学段筛选
     * @return 资助项目列表
     */
    List<Map<String, Object>> getProjectList(String segment);

    /**
     * 获取用户注册统计
     * 最近注册的用户统计
     * 
     * @return 用户注册统计数据
     */
    List<Map<String, Object>> getUserRegistrations();

    /**
     * 获取本学期受助人数趋势
     * 按月份统计受助人数趋势
     * 
     * @param segment 学段筛选
     * @return 受助人数趋势数据
     */
    List<Map<String, Object>> getSubsidyTrend(String segment);
}
