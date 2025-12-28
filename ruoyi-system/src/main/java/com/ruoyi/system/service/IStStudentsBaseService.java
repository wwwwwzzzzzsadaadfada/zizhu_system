package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StStudentsBase;

/**
 * 学生基础信息Service接口
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
public interface IStStudentsBaseService 
{
    /**
     * 查询学生基础信息
     * 
     * @param id 学生基础信息主键
     * @return 学生基础信息
     */
    public StStudentsBase selectStStudentsBaseById(Long id);

    /**
     * 查询学生基础信息列表
     * 
     * @param stStudentsBase 学生基础信息
     * @return 学生基础信息集合
     */
    public List<StStudentsBase> selectStStudentsBaseList(StStudentsBase stStudentsBase);

    /**
     * 新增学生基础信息
     * 
     * @param stStudentsBase 学生基础信息
     * @return 结果
     */
    public int insertStStudentsBase(StStudentsBase stStudentsBase);

    /**
     * 修改学生基础信息
     * 
     * @param stStudentsBase 学生基础信息
     * @return 结果
     */
    public int updateStStudentsBase(StStudentsBase stStudentsBase);

    /**
     * 批量删除学生基础信息
     * 
     * @param ids 需要删除的学生基础信息主键集合
     * @return 结果
     */
    public int deleteStStudentsBaseByIds(Long[] ids);

    /**
     * 删除学生基础信息信息
     * 
     * @param id 学生基础信息主键
     * @return 结果
     */
    public int deleteStStudentsBaseById(Long id);

    /**
     * 根据身份证号查询学生基础信息
     * 
     * @param idCardNo 身份证号
     * @return 学生基础信息
     */
    public StStudentsBase selectStStudentsBaseByIdCardNo(String idCardNo);

    /**
     * 根据学籍号查询学生基础信息
     * 
     * @param studentNo 学籍号
     * @return 学生基础信息
     */
    public StStudentsBase selectStStudentsBaseByStudentNo(String studentNo);

    /**
     * 根据学年（例如2025-2026）自动升年级
     * 
     * @param schoolYear 学年
     * @return 升级人数
     */
    public int promoteStudentsForSchoolYear(String schoolYear);

    /**
     * 批量更新所有在读学生的当前学期ID
     * 
     * @param yearSemesterId 学年学期ID
     * @return 更新的记录数
     */
    public int updateAllStudentsCurrentSemester(Long yearSemesterId);
}
