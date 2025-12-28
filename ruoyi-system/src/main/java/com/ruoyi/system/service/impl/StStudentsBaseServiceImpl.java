package com.ruoyi.system.service.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StStudentsBaseMapper;
import com.ruoyi.system.domain.StStudentsBase;
import com.ruoyi.system.service.IStStudentsBaseService;

/**
 * 学生基础信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
@Service
public class StStudentsBaseServiceImpl implements IStStudentsBaseService 
{
    @Autowired
    private StStudentsBaseMapper stStudentsBaseMapper;

    /**
     * 查询学生基础信息
     * 
     * @param id 学生基础信息主键
     * @return 学生基础信息
     */
    @Override
    public StStudentsBase selectStStudentsBaseById(Long id)
    {
        return stStudentsBaseMapper.selectStStudentsBaseById(id);
    }

    /**
     * 查询学生基础信息列表
     * 
     * @param stStudentsBase 学生基础信息
     * @return 学生基础信息
     */
    @Override
    public List<StStudentsBase> selectStStudentsBaseList(StStudentsBase stStudentsBase)
    {
        return stStudentsBaseMapper.selectStStudentsBaseList(stStudentsBase);
    }

    /**
     * 新增学生基础信息
     * 
     * @param stStudentsBase 学生基础信息
     * @return 结果
     */
    @Override
    public int insertStStudentsBase(StStudentsBase stStudentsBase)
    {
        return stStudentsBaseMapper.insertStStudentsBase(stStudentsBase);
    }

    /**
     * 修改学生基础信息
     * 
     * @param stStudentsBase 学生基础信息
     * @return 结果
     */
    @Override
    public int updateStStudentsBase(StStudentsBase stStudentsBase)
    {
        return stStudentsBaseMapper.updateStStudentsBase(stStudentsBase);
    }

    /**
     * 批量删除学生基础信息
     * 
     * @param ids 需要删除的学生基础信息主键
     * @return 结果
     */
    @Override
    public int deleteStStudentsBaseByIds(Long[] ids)
    {
        return stStudentsBaseMapper.deleteStStudentsBaseByIds(ids);
    }

    /**
     * 删除学生基础信息信息
     * 
     * @param id 学生基础信息主键
     * @return 结果
     */
    @Override
    public int deleteStStudentsBaseById(Long id)
    {
        return stStudentsBaseMapper.deleteStStudentsBaseById(id);
    }

    /**
     * 根据身份证号查询学生基础信息
     * 
     * @param idCardNo 身份证号
     * @return 学生基础信息
     */
    @Override
    public StStudentsBase selectStStudentsBaseByIdCardNo(String idCardNo)
    {
        return stStudentsBaseMapper.selectStStudentsBaseByIdCardNo(idCardNo);
    }

    /**
     * 根据学籍号查询学生基础信息
     * 
     * @param studentNo 学籍号
     * @return 学生基础信息
     */
    @Override
    public StStudentsBase selectStStudentsBaseByStudentNo(String studentNo)
    {
        return stStudentsBaseMapper.selectStStudentsBaseByStudentNo(studentNo);
    }

    /**
     * 自动升年级
     */
    @Override
    public int promoteStudentsForSchoolYear(String schoolYear)
    {
        if (schoolYear == null || schoolYear.length() < 4)
        {
            return 0;
        }
        int startYear = Integer.parseInt(schoolYear.substring(0, 4));
        LocalDate promotionLocalDate = LocalDate.of(startYear, 9, 1);
        Date promotionDate = Date.valueOf(promotionLocalDate);

        List<Map<String, Object>> grades = stStudentsBaseMapper.selectAllGradesForPromotion();
        if (grades.isEmpty())
        {
            return 0;
        }

        Map<Long, Long> nextGradeMap = buildNextGradeMap(grades);
        List<StStudentsBase> candidates = stStudentsBaseMapper.selectStudentsForPromotion(promotionDate);
        if (candidates.isEmpty())
        {
            return 0;
        }

        int promoted = 0;
        for (StStudentsBase student : candidates)
        {
            Long currentGradeId = student.getGradeId();
            if (currentGradeId == null)
            {
                continue;
            }
            Long nextGradeId = nextGradeMap.get(currentGradeId);
            Long targetGradeId = nextGradeId != null ? nextGradeId : currentGradeId;
            int rows = stStudentsBaseMapper.updateStudentGradeForPromotion(student.getId(), targetGradeId, promotionDate);
            if (rows > 0 && nextGradeId != null)
            {
                promoted++;
            }
        }
        return promoted;
    }

    private Map<Long, Long> buildNextGradeMap(List<Map<String, Object>> grades)
    {
        Map<Long, Long> nextGradeMap = new HashMap<>();
        Map<Long, Long> lastGradePerPlan = new HashMap<>();
        for (Map<String, Object> row : grades)
        {
            Long gradeId = toLong(row.get("id"));
            Long planId = toLong(row.get("schoolingPlanId"));
            if (gradeId == null || planId == null)
            {
                continue;
            }
            Long previousGrade = lastGradePerPlan.put(planId, gradeId);
            if (previousGrade != null)
            {
                nextGradeMap.put(previousGrade, gradeId);
            }
        }
        return nextGradeMap;
    }

    private Long toLong(Object value)
    {
        if (value instanceof Number)
        {
            return ((Number) value).longValue();
        }
        if (value != null)
        {
            return Long.parseLong(value.toString());
        }
        return null;
    }

    /**
     * 批量更新所有在读学生的当前学期ID
     */
    @Override
    public int updateAllStudentsCurrentSemester(Long yearSemesterId)
    {
        if (yearSemesterId == null)
        {
            return 0;
        }
        return stStudentsBaseMapper.updateAllStudentsCurrentSemester(yearSemesterId);
    }
}
