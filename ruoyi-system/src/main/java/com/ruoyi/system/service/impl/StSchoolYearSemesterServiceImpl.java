package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.StSchoolYearSemesterMapper;
import com.ruoyi.system.domain.StSchoolYearSemester;
import com.ruoyi.system.service.IStSchoolYearSemesterService;
import com.ruoyi.system.service.IStStudentsBaseService;

/**
 * 学年学期Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
@Service
public class StSchoolYearSemesterServiceImpl implements IStSchoolYearSemesterService 
{
    @Autowired
    private StSchoolYearSemesterMapper stSchoolYearSemesterMapper;

    @Autowired
    private IStStudentsBaseService stStudentsBaseService;

    /**
     * 查询学年学期
     * 
     * @param id 学年学期主键
     * @return 学年学期
     */
    @Override
    public StSchoolYearSemester selectStSchoolYearSemesterById(Long id)
    {
        StSchoolYearSemester semester = stSchoolYearSemesterMapper.selectStSchoolYearSemesterById(id);
        // 使用工具类确保学期标签的一致性（即使SQL已计算，也通过工具类校验和设置）
        if (semester != null) {
            String semesterLabel = com.ruoyi.common.utils.SemesterUtils.getSemesterLabel(semester.getSemester());
            semester.setSemesterLabel(semesterLabel);
        }
        return semester;
    }

    /**
     * 查询学年学期列表
     * 
     * @param stSchoolYearSemester 学年学期
     * @return 学年学期
     */
    @Override
    public List<StSchoolYearSemester> selectStSchoolYearSemesterList(StSchoolYearSemester stSchoolYearSemester)
    {
        List<StSchoolYearSemester> list = stSchoolYearSemesterMapper.selectStSchoolYearSemesterList(stSchoolYearSemester);
        // 使用工具类确保学期标签的一致性（即使SQL已计算，也通过工具类校验和设置）
        if (list != null) {
            for (StSchoolYearSemester semester : list) {
                String semesterLabel = com.ruoyi.common.utils.SemesterUtils.getSemesterLabel(semester.getSemester());
                semester.setSemesterLabel(semesterLabel);
            }
        }
        return list;
    }

    /**
     * 新增学年学期
     * 
     * @param stSchoolYearSemester 学年学期
     * @return 结果
     */
    @Override
    public int insertStSchoolYearSemester(StSchoolYearSemester stSchoolYearSemester)
    {
        return stSchoolYearSemesterMapper.insertStSchoolYearSemester(stSchoolYearSemester);
    }

    /**
     * 修改学年学期
     * 
     * @param stSchoolYearSemester 学年学期
     * @return 结果
     */
    @Override
    public int updateStSchoolYearSemester(StSchoolYearSemester stSchoolYearSemester)
    {
        return stSchoolYearSemesterMapper.updateStSchoolYearSemester(stSchoolYearSemester);
    }

    /**
     * 批量删除学年学期
     * 
     * @param ids 需要删除的学年学期主键
     * @return 结果
     */
    @Override
    public int deleteStSchoolYearSemesterByIds(Long[] ids)
    {
        return stSchoolYearSemesterMapper.deleteStSchoolYearSemesterByIds(ids);
    }

    /**
     * 删除学年学期信息
     * 
     * @param id 学年学期主键
     * @return 结果
     */
    @Override
    public int deleteStSchoolYearSemesterById(Long id)
    {
        return stSchoolYearSemesterMapper.deleteStSchoolYearSemesterById(id);
    }

    /**
     * 设置当前学期
     * 
     * @param id 学年学期主键
     * @return 结果
     */
    @Override
    @Transactional
    public int setCurrentSemester(Long id)
    {
        // 先取消所有学期的当前状态
        stSchoolYearSemesterMapper.cancelAllCurrent();
        // 设置指定学期为当前学期
        int rows = stSchoolYearSemesterMapper.setCurrentSemester(id);
        StSchoolYearSemester current = stSchoolYearSemesterMapper.selectStSchoolYearSemesterById(id);
        if (current != null)
        {
            // 如果是新学年的第一学期，执行升年级操作
            if (current.getSemester() != null && current.getSemester() == 1)
            {
                stStudentsBaseService.promoteStudentsForSchoolYear(current.getSchoolYear());
            }
            // 更新所有在读学生的当前学期ID
            stStudentsBaseService.updateAllStudentsCurrentSemester(current.getId());
        }
        return rows;
    }

    /**
     * 获取当前学期
     * 
     * @return 当前学期
     */
    @Override
    public StSchoolYearSemester selectCurrentSemester()
    {
        StSchoolYearSemester semester = stSchoolYearSemesterMapper.selectCurrentSemester(true);
        // 使用工具类确保学期标签的一致性（即使SQL已计算，也通过工具类校验和设置）
        if (semester != null) {
            String semesterLabel = com.ruoyi.common.utils.SemesterUtils.getSemesterLabel(semester.getSemester());
            semester.setSemesterLabel(semesterLabel);
        }
        return semester;
    }
}
