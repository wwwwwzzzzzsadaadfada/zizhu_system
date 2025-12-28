package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StSchoolYearSemester;
import org.apache.ibatis.annotations.Param;

/**
 * 学年学期Mapper接口
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
public interface StSchoolYearSemesterMapper 
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
     * 删除学年学期
     * 
     * @param id 学年学期主键
     * @return 结果
     */
    public int deleteStSchoolYearSemesterById(Long id);

    /**
     * 批量删除学年学期
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStSchoolYearSemesterByIds(Long[] ids);

    /**
     * 取消所有学期的当前状态
     * 
     * @return 结果
     */
    public int cancelAllCurrent();

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
    public StSchoolYearSemester selectCurrentSemester(@Param("ignorePage") boolean ignorePage);
    
    /**
     * 根据学年和学期查询学年学期记录
     * 
     * @param schoolYear 学年（如2024-2025）
     * @param semester 学期（1或2）
     * @return 学年学期记录
     */
    public StSchoolYearSemester selectBySchoolYearAndSemester(
        @Param("schoolYear") String schoolYear,
        @Param("semester") Integer semester
    );

}
