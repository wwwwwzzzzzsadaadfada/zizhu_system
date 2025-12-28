package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StSchoolYearSemester;

/**
 * 学年学期Service接口
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
public interface IStSchoolYearSemesterService 
{
    /**
     * 查询学年学期
     * 
     * @param id 学年学期主键
     * @return 学年学期
     */
    public StSchoolYearSemester selectStSchoolYearSemesterById(Long id);

    /**
     * 查询学年学期列表
     * 
     * @param stSchoolYearSemester 学年学期
     * @return 学年学期集合
     */
    public List<StSchoolYearSemester> selectStSchoolYearSemesterList(StSchoolYearSemester stSchoolYearSemester);

    /**
     * 新增学年学期
     * 
     * @param stSchoolYearSemester 学年学期
     * @return 结果
     */
    public int insertStSchoolYearSemester(StSchoolYearSemester stSchoolYearSemester);

    /**
     * 修改学年学期
     * 
     * @param stSchoolYearSemester 学年学期
     * @return 结果
     */
    public int updateStSchoolYearSemester(StSchoolYearSemester stSchoolYearSemester);

    /**
     * 批量删除学年学期
     * 
     * @param ids 需要删除的学年学期主键集合
     * @return 结果
     */
    public int deleteStSchoolYearSemesterByIds(Long[] ids);

    /**
     * 删除学年学期信息
     * 
     * @param id 学年学期主键
     * @return 结果
     */
    public int deleteStSchoolYearSemesterById(Long id);

    /**
     * 设置当前学期
     * 
     * @param id 学年学期主键
     * @return 结果
     */
    public int setCurrentSemester(Long id);

    /**
     * 获取当前学期
     * 
     * @return 当前学期
     */
    public StSchoolYearSemester selectCurrentSemester();
}
