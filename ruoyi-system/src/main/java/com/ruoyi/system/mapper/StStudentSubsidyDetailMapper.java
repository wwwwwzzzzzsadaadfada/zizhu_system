package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StStudentSubsidyDetail;

/**
 * 学生受助明细Mapper接口
 * 
 * @author ruoyi
 * @date 2024-11-22
 */
public interface StStudentSubsidyDetailMapper 
{
    /**
     * 查询学生受助明细
     * 
     * @param id 学生受助明细主键
     * @return 学生受助明细
     */
    public StStudentSubsidyDetail selectStStudentSubsidyDetailById(Long id);

    /**
     * 查询学生受助明细列表
     * 
     * @param stStudentSubsidyDetail 学生受助明细
     * @return 学生受助明细集合
     */
    public List<StStudentSubsidyDetail> selectStStudentSubsidyDetailList(StStudentSubsidyDetail stStudentSubsidyDetail);

    /**
     * 新增学生受助明细
     * 
     * @param stStudentSubsidyDetail 学生受助明细
     * @return 结果
     */
    public int insertStStudentSubsidyDetail(StStudentSubsidyDetail stStudentSubsidyDetail);

    /**
     * 修改学生受助明细
     * 
     * @param stStudentSubsidyDetail 学生受助明细
     * @return 结果
     */
    public int updateStStudentSubsidyDetail(StStudentSubsidyDetail stStudentSubsidyDetail);

    /**
     * 删除学生受助明细
     * 
     * @param id 学生受助明细主键
     * @return 结果
     */
    public int deleteStStudentSubsidyDetailById(Long id);

    /**
     * 批量删除学生受助明细
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStStudentSubsidyDetailByIds(Long[] ids);
    
    /**
     * 根据学生学期记录ID查询受助明细列表
     * 
     * @param studentRecordId 学生学期记录ID
     * @return 学生受助明细集合
     */
    public List<StStudentSubsidyDetail> selectStStudentSubsidyDetailByStudentRecordId(Long studentRecordId);
    
    /**
     * 根据预算ID查询受助明细列表
     * 
     * @param budgetId 预算ID
     * @return 学生受助明细集合
     */
    public List<StStudentSubsidyDetail> selectStStudentSubsidyDetailListByBudgetId(Long budgetId);
    
    /**
     * 根据学生学期记录ID删除受助明细
     * 
     * @param studentRecordId 学生学期记录ID
     * @return 结果
     */
    public int deleteStStudentSubsidyDetailByStudentRecordId(Long studentRecordId);
}