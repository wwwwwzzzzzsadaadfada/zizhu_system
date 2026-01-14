package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.StStudents;

/**
 * 困难学生基础信息Service接口
 * 
 * @author ruoyi
 * @date 2025-11-19
 */
public interface IStStudentsService 
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
     * 查询困难学生基础信息列表（用于导出，返回明文敏感数据）
     * 
     * @param stStudents 困难学生基础信息
     * @return 困难学生基础信息集合
     */
    public List<StStudents> selectStStudentsListForExport(StStudents stStudents);

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
     * 批量删除困难学生基础信息
     * 
     * @param ids 需要删除的困难学生基础信息主键集合
     * @return 结果
     */
    public int deleteStStudentsByIds(String[] ids);

    /**
     * 删除困难学生基础信息信息
     * 
     * @param id 困难学生基础信息主键
     * @return 结果
     */
    public int deleteStStudentsById(String id);

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
     * 构建学制-年级-班级的树状结构（用于前端级联选择器）
     * 
     * @return 学制-年级-班级树状结构
     */
    public List<Map<String, Object>> buildSchoolPlanGradeClassTree();

    /**
     * 批量更新困难类型和等级
     * 
     * @param ids 学生ID数组
     * @param difficultyTypeId 困难类型ID（可为null，表示不更新）
     * @param difficultyLevelId 困难等级ID（可为null，表示不更新）
     * @param isPovertyReliefFamily 是否脱贫户（可为null，表示不更新）
     * @param povertyReliefYear 脱贫年份（可为null，表示不更新）
     * @return 结果
     */
    public int batchUpdateDifficultyInfo(String[] ids, String difficultyTypeId, String difficultyLevelId, String isPovertyReliefFamily, Integer povertyReliefYear);

    /**
     * 导入学生数据
     * 
     * @param studentsList 学生数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    public String importStudents(List<StStudents> studentsList, Boolean isUpdateSupport, String operName);
}
