package com.ruoyi.system.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.StStudentsBase;

/**
 * 学生基础信息Mapper接口
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
public interface StStudentsBaseMapper 
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
     * 删除学生基础信息
     * 
     * @param id 学生基础信息主键
     * @return 结果
     */
    public int deleteStStudentsBaseById(Long id);

    /**
     * 批量删除学生基础信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStStudentsBaseByIds(Long[] ids);

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
     * 查询所有年级配置（用于升年级）
     */
    public List<Map<String, Object>> selectAllGradesForPromotion();

    /**
     * 查询需要升年级的学生
     */
    public List<StStudentsBase> selectStudentsForPromotion(@Param("promotionDate") Date promotionDate);

    /**
     * 更新学生年级
     */
    public int updateStudentGradeForPromotion(@Param("id") Long id,
                                              @Param("gradeId") Long gradeId,
                                              @Param("lastGradeUpdate") Date lastGradeUpdate);

    /**
     * 批量更新所有在读学生的当前学期ID
     * 
     * @param yearSemesterId 学年学期ID
     * @return 更新的记录数
     */
    public int updateAllStudentsCurrentSemester(@Param("yearSemesterId") Long yearSemesterId);
    
    /**
     * 查询报表学生数据（用于积木报表JavaBean数据集）
     * 
     * @param params 查询参数
     * @return 学生数据列表
     */
    public List<Map<String, Object>> selectStudentListForReport(Map<String, Object> params);
}
