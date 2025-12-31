package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.StStudentSemesterRecord;

/**
 * 学生学期认定记录Service接口
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
public interface IStStudentSemesterRecordService 
{
    /**
     * 查询学生学期认定记录
     * 
     * @param id 学生学期认定记录主键
     * @return 学生学期认定记录
     */
    public StStudentSemesterRecord selectStStudentSemesterRecordById(Long id);

    /**
     * 查询学生学期认定记录列表
     * 
     * @param stStudentSemesterRecord 学生学期认定记录
     * @return 学生学期认定记录集合
     */
    public List<StStudentSemesterRecord> selectStStudentSemesterRecordList(StStudentSemesterRecord stStudentSemesterRecord);

    /**
     * 新增学生学期认定记录
     * 
     * @param stStudentSemesterRecord 学生学期认定记录
     * @return 结果
     */
    public int insertStStudentSemesterRecord(StStudentSemesterRecord stStudentSemesterRecord);

    /**
     * 修改学生学期认定记录
     * 
     * @param stStudentSemesterRecord 学生学期认定记录
     * @return 结果
     */
    public int updateStStudentSemesterRecord(StStudentSemesterRecord stStudentSemesterRecord);

    /**
     * 批量删除学生学期认定记录
     * 
     * @param ids 需要删除的学生学期认定记录主键集合
     * @return 结果
     */
    public int deleteStStudentSemesterRecordByIds(Long[] ids);

    /**
     * 删除学生学期认定记录信息
     * 
     * @param id 学生学期认定记录主键
     * @return 结果
     */
    public int deleteStStudentSemesterRecordById(Long id);

    /**
     * 查询学制列表
     * 
     * @return 学制列表
     */
    public List<Map<String, Object>> selectSchoolPlanList();

    /**
     * 根据学制ID查询年级列表
     * 
     * @param schoolingPlanId 学制ID
     * @return 年级列表
     */
    public List<Map<String, Object>> selectGradeListByPlanId(Long schoolingPlanId);

    /**
     * 根据年级ID查询班级列表
     * 
     * @param gradeId 年级ID
     * @return 班级列表
     */
    public List<Map<String, Object>> selectClassListByGradeId(Long gradeId);
    
    /**
     * 同步学生数据到学期记录表
     * 
     * @param yearSemesterId 学年学期ID
     * @return 同步的学生数量
     */
    public int syncStudentsToSemesterRecords(Long yearSemesterId);
    
    /**
     * 同步学生数据到受助学生信息表
     * 
     * @param studentBaseId 学生基础信息ID
     * @param academicYear 学年
     * @param semester 学期
     * @return 同步的结果
     */
    public com.ruoyi.system.domain.SyncAidedStudentResult syncStudentsToAidedTable(Long studentBaseId, String academicYear, String semester);
    
    /**
     * 同步所有学生数据到受助学生信息表
     * 
     * @param academicYear 学年
     * @param semester 学期
     * @return 同步的结果
     */
    public com.ruoyi.system.domain.SyncAidedStudentResult syncAllStudentsToAidedTable(String academicYear, String semester);

    /**
     * 获取同步进度
     * 
     * @param academicYear 学年
     * @param semester 学期
     * @return 进度信息
     */
    public com.ruoyi.system.domain.SyncProgress getSyncProgress(String academicYear, String semester);
}