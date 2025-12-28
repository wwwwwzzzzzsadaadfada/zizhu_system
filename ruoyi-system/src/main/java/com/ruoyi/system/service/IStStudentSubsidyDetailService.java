package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StStudentSubsidyDetail;

/**
 * 学生受助明细Service接口
 * 
 * @author ruoyi
 * @date 2024-11-22
 */
public interface IStStudentSubsidyDetailService 
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
     * 批量删除学生受助明细
     * 
     * @param ids 需要删除的学生受助明细主键集合
     * @return 结果
     */
    public int deleteStStudentSubsidyDetailByIds(Long[] ids);

    /**
     * 删除学生受助明细信息
     * 
     * @param id 学生受助明细主键
     * @return 结果
     */
    public int deleteStStudentSubsidyDetailById(Long id);
}