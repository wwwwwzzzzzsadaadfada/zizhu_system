package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.StStudentSemesterRecord;

/**
 * 学生学期认定记录Mapper接口
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
public interface StStudentSemesterRecordMapper 
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
     * 删除学生学期认定记录
     * 
     * @param id 学生学期认定记录主键
     * @return 结果
     */
    public int deleteStStudentSemesterRecordById(Long id);

    /**
     * 批量删除学生学期认定记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStStudentSemesterRecordByIds(Long[] ids);

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
     * 同步单个学生数据到受助学生信息表
     * 
     * @param studentBaseId 学生基础信息ID
     * @param academicYear 学年
     * @param semester 学期
     * @return 同步的结果
     */
    public int syncStudentToAidedTable(@Param("studentBaseId") Long studentBaseId, 
                                      @Param("academicYear") String academicYear, 
                                      @Param("semester") String semester);
    
    /**
     * 同步所有学生数据到受助学生信息表
     * 
     * @param academicYear 学年
     * @param semester 学期
     * @return 同步的结果
     */
    public int syncAllStudentsToAidedTable(@Param("academicYear") String academicYear, 
                                          @Param("semester") String semester);
    
    /**
     * 更新已存在受助学生的学期字段
     * 
     * @param academicYear 学年
     * @param semester 学期
     * @return 更新条数
     */
    public int updateExistingStudentsInAidedTable(@Param("academicYear") String academicYear,
                                                 @Param("semester") String semester);
    
    /**
     * 插入新学生到受助学生信息表（不存在的学生）
     * 
     * @param academicYear 学年
     * @param semester 学期
     * @return 插入条数
     */
    public int insertNewStudentsIntoAidedTable(@Param("academicYear") String academicYear,
                                               @Param("semester") String semester);
    
    /**
     * 根据学生ID和学年学期ID查询学期记录
     * 
     * @param studentBaseId 学生基础信息ID
     * @param yearSemesterId 学年学期ID
     * @return 学生学期记录
     */
    public StStudentSemesterRecord selectByStudentIdAndYearSemesterId(
        @Param("studentBaseId") Long studentBaseId,
        @Param("yearSemesterId") Long yearSemesterId
    );
    
    /**
     * 检查学生是否已存在受助信息
     * 
     * @param studentBaseId 学生基础信息ID
     * @param academicYear 学年
     * @param semester 学期
     * @return 是否存在
     */
    public boolean checkAidedStudentExists(@Param("studentBaseId") Long studentBaseId,
                                          @Param("academicYear") String academicYear,
                                          @Param("semester") String semester);
}