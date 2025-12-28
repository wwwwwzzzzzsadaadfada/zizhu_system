package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.BaseConfigMapper;
import com.ruoyi.system.service.IBaseConfigService;

/**
 * 基础配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-11-20
 */
@Service
public class BaseConfigServiceImpl implements IBaseConfigService 
{
    @Autowired
    private BaseConfigMapper baseConfigMapper;

    // ==================== 学制管理 ====================
    
    @Override
    public List<Map<String, Object>> selectSchoolPlanList()
    {
        return baseConfigMapper.selectSchoolPlanList();
    }

    @Override
    public Map<String, Object> selectSchoolPlanById(Long id)
    {
        return baseConfigMapper.selectSchoolPlanById(id);
    }

    @Override
    public int insertSchoolPlan(Map<String, Object> params)
    {
        return baseConfigMapper.insertSchoolPlan(params);
    }

    @Override
    public int updateSchoolPlan(Map<String, Object> params)
    {
        return baseConfigMapper.updateSchoolPlan(params);
    }

    @Override
    public int deleteSchoolPlanById(Long id)
    {
        return baseConfigMapper.deleteSchoolPlanById(id);
    }

    // ==================== 年级管理 ====================
    
    @Override
    public List<Map<String, Object>> selectGradeList(Long schoolingPlanId)
    {
        return baseConfigMapper.selectGradeList(schoolingPlanId);
    }

    @Override
    public Map<String, Object> selectGradeById(Long id)
    {
        return baseConfigMapper.selectGradeById(id);
    }

    @Override
    public int insertGrade(Map<String, Object> params)
    {
        return baseConfigMapper.insertGrade(params);
    }

    @Override
    public int updateGrade(Map<String, Object> params)
    {
        return baseConfigMapper.updateGrade(params);
    }

    @Override
    public int deleteGradeById(Long id)
    {
        return baseConfigMapper.deleteGradeById(id);
    }

    // ==================== 班级管理 ====================
    
    @Override
    public List<Map<String, Object>> selectClassList(Long gradeId)
    {
        return baseConfigMapper.selectClassList(gradeId);
    }

    @Override
    public Map<String, Object> selectClassById(Long classId)
    {
        return baseConfigMapper.selectClassById(classId);
    }

    @Override
    public int insertClass(Map<String, Object> params)
    {
        return baseConfigMapper.insertClass(params);
    }

    @Override
    public int updateClass(Map<String, Object> params)
    {
        return baseConfigMapper.updateClass(params);
    }

    @Override
    public int deleteClassById(Long classId)
    {
        return baseConfigMapper.deleteClassById(classId);
    }
}
