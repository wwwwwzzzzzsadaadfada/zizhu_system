package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;

/**
 * 基础配置Mapper接口
 * 
 * @author ruoyi
 * @date 2025-11-20
 */
public interface BaseConfigMapper 
{
    // ==================== 学制管理 ====================
    
    /**
     * 查询学制列表
     */
    public List<Map<String, Object>> selectSchoolPlanList();

    /**
     * 查询学制详细
     */
    public Map<String, Object> selectSchoolPlanById(Long id);

    /**
     * 新增学制
     */
    public int insertSchoolPlan(Map<String, Object> params);

    /**
     * 修改学制
     */
    public int updateSchoolPlan(Map<String, Object> params);

    /**
     * 删除学制
     */
    public int deleteSchoolPlanById(Long id);

    // ==================== 年级管理 ====================
    
    /**
     * 查询年级列表
     * @param schoolingPlanId 学制ID，为null时查询所有年级
     */
    public List<Map<String, Object>> selectGradeList(Long schoolingPlanId);

    /**
     * 查询年级详细
     */
    public Map<String, Object> selectGradeById(Long id);

    /**
     * 新增年级
     */
    public int insertGrade(Map<String, Object> params);

    /**
     * 修改年级
     */
    public int updateGrade(Map<String, Object> params);

    /**
     * 删除年级
     */
    public int deleteGradeById(Long id);

    // ==================== 班级管理 ====================
    
    /**
     * 查询班级列表
     * @param gradeId 年级ID，为null时查询所有班级
     */
    public List<Map<String, Object>> selectClassList(Long gradeId);

    /**
     * 查询班级详细
     */
    public Map<String, Object> selectClassById(Long classId);

    /**
     * 新增班级
     */
    public int insertClass(Map<String, Object> params);

    /**
     * 修改班级
     */
    public int updateClass(Map<String, Object> params);

    /**
     * 删除班级
     */
    public int deleteClassById(Long classId);
}
