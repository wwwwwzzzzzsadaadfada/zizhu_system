package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.StStudents;

import org.apache.ibatis.annotations.Param;

/**
 * 困难学生基础信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-11-19
 */
public interface StStudentsMapper 
{
    /**
     * 查询困难学生基础信息
     * 
     * @param id 困难学生基础信息主键
     * @return 困难学生基础信息
     */
    public StStudents selectStStudentsById(String id);

    /**
     * 查询困难学生基础信息列表
     * 
     * @param stStudents 困难学生基础信息
     * @return 困难学生基础信息集合
     */
    public List<StStudents> selectStStudentsList(StStudents stStudents);

    /**
     * 新增困难学生基础信息
     * 
     * @param stStudents 困难学生基础信息
     * @return 结果
     */
    public int insertStStudents(StStudents stStudents);

    /**
     * 修改困难学生基础信息
     * 
     * @param stStudents 困难学生基础信息
     * @return 结果
     */
    public int updateStStudents(StStudents stStudents);

    /**
     * 删除困难学生基础信息
     * 
     * @param id 困难学生基础信息主键
     * @return 结果
     */
    public int deleteStStudentsById(String id);

    /**
     * 批量删除困难学生基础信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStStudentsByIds(String[] ids);

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
    public List<Map<String, Object>> selectGradeListByPlanId(String schoolingPlanId);

    /**
     * 根据年级ID查询班级列表
     * 
     * @param gradeId 年级ID
     * @return 班级列表
     */
    public List<Map<String, Object>> selectClassListByGradeId(String gradeId);

    /**
     * 批量更新困难类型和等级
     * 
     * @param params 包含ids和要更新的字段的Map
     * @return 结果
     */
    public int batchUpdateDifficultyInfo(Map<String, Object> params);

    /**
     * 根据身份证号查询学生
     * 
     * @param idCardNo 身份证号
     * @return 学生信息
     */
    public StStudents selectByIdCardNo(String idCardNo);

    /**
     * 根据学籍号查询学生
     * 
     * @param studentNo 学籍号
     * @return 学生信息
     */
    public StStudents selectByStudentNo(String studentNo);

    /**
     * 根据身份证号或学籍号查询学生（提高性能）
     * 
     * @param idCardNo 身份证号
     * @param studentNo 学籍号
     * @return 学生信息
     */
    public StStudents selectByIdCardNoOrStudentNo(@Param("idCardNo") String idCardNo, @Param("studentNo") String studentNo);
}
