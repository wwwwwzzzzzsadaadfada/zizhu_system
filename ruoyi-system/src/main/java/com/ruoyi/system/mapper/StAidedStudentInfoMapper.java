package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StAidedStudentInfo;

/**
 * 受助学生信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
public interface StAidedStudentInfoMapper 
{
    /**
     * 查询受助学生信息
     * 
     * @param id 受助学生信息主键
     * @return 受助学生信息
     */
    public StAidedStudentInfo selectStAidedStudentInfoById(Long id);

    /**
     * 查询受助学生信息列表
     * 
     * @param stAidedStudentInfo 受助学生信息
     * @return 受助学生信息集合
     */
    public List<StAidedStudentInfo> selectStAidedStudentInfoList(StAidedStudentInfo stAidedStudentInfo);

    /**
     * 新增受助学生信息
     * 
     * @param stAidedStudentInfo 受助学生信息
     * @return 结果
     */
    public int insertStAidedStudentInfo(StAidedStudentInfo stAidedStudentInfo);

    /**
     * 修改受助学生信息
     * 
     * @param stAidedStudentInfo 受助学生信息
     * @return 结果
     */
    public int updateStAidedStudentInfo(StAidedStudentInfo stAidedStudentInfo);

    /**
     * 删除受助学生信息
     * 
     * @param id 受助学生信息主键
     * @return 结果
     */
    public int deleteStAidedStudentInfoById(Long id);

    /**
     * 批量删除受助学生信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStAidedStudentInfoByIds(Long[] ids);
    
    /**
     * 根据学生ID和学期信息查询受助学生信息
     * 
     * @param studentId 学生ID
     * @param academicYear 学年
     * @param semester 学期
     * @return 受助学生信息
     */
    public StAidedStudentInfo selectByStudentIdAndSemester(Long studentId, String academicYear, String semester);
    
    /**
     * 只更新基础信息字段
     * 
     * @param stAidedStudentInfo 受助学生信息
     * @return 结果
     */
    public int updateBaseFields(StAidedStudentInfo stAidedStudentInfo);
}