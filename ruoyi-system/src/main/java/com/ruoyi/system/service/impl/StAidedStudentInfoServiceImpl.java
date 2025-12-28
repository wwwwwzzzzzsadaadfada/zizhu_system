package com.ruoyi.system.service.impl;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StAidedStudentInfoMapper;
import com.ruoyi.system.mapper.StSchoolYearSemesterMapper;
import com.ruoyi.system.domain.StAidedStudentInfo;
import com.ruoyi.system.domain.StSchoolYearSemester;
import com.ruoyi.system.service.IStAidedStudentInfoService;

/**
 * 受助学生信息服务实现类
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
@Service
public class StAidedStudentInfoServiceImpl implements IStAidedStudentInfoService 
{
    @Autowired
    private StAidedStudentInfoMapper stAidedStudentInfoMapper;

    @Autowired
    private StSchoolYearSemesterMapper stSchoolYearSemesterMapper;

    /**
     * 查询受助学生信息
     * 
     * @param id 受助学生信息主键
     * @return 受助学生信息
     */
    @Override
    public StAidedStudentInfo selectStAidedStudentInfoById(Long id)
    {
        return stAidedStudentInfoMapper.selectStAidedStudentInfoById(id);
    }

    /**
     * 查询受助学生信息列表
     * 
     * @param stAidedStudentInfo 受助学生信息
     * @return 受助学生信息
     */
    @Override
    public List<StAidedStudentInfo> selectStAidedStudentInfoList(StAidedStudentInfo stAidedStudentInfo)
    {
        if (stAidedStudentInfo == null) {
            stAidedStudentInfo = new StAidedStudentInfo();
        }

        boolean missingYear = StringUtils.isBlank(stAidedStudentInfo.getAcademicYear());
        boolean missingSemester = StringUtils.isBlank(stAidedStudentInfo.getSemester());
        if (missingYear || missingSemester)
        {
            StSchoolYearSemester currentSemester = stSchoolYearSemesterMapper.selectCurrentSemester(true);
            if (currentSemester != null)
            {
                if (missingYear)
                {
                    stAidedStudentInfo.setAcademicYear(currentSemester.getSchoolYear());
                }
                if (missingSemester && currentSemester.getSemester() != null)
                {
                    stAidedStudentInfo.setSemester(String.valueOf(currentSemester.getSemester()));
                }
            }
        }

        return stAidedStudentInfoMapper.selectStAidedStudentInfoList(stAidedStudentInfo);
    }

    /**
     * 新增受助学生信息
     * 
     * @param stAidedStudentInfo 受助学生信息
     * @return 结果
     */
    @Override
    public int insertStAidedStudentInfo(StAidedStudentInfo stAidedStudentInfo)
    {
        return stAidedStudentInfoMapper.insertStAidedStudentInfo(stAidedStudentInfo);
    }

    /**
     * 修改受助学生信息
     * 
     * @param stAidedStudentInfo 受助学生信息
     * @return 结果
     */
    @Override
    public int updateStAidedStudentInfo(StAidedStudentInfo stAidedStudentInfo)
    {
        return stAidedStudentInfoMapper.updateStAidedStudentInfo(stAidedStudentInfo);
    }

    /**
     * 批量删除受助学生信息
     * 
     * @param ids 需要删除的受助学生信息主键数组
     * @return 结果
     */
    @Override
    public int deleteStAidedStudentInfoByIds(Long[] ids)
    {
        return stAidedStudentInfoMapper.deleteStAidedStudentInfoByIds(ids);
    }

    /**
     * 删除受助学生信息信息
     * 
     * @param id 受助学生信息主键
     * @return 结果
     */
    @Override
    public int deleteStAidedStudentInfoById(Long id)
    {
        return stAidedStudentInfoMapper.deleteStAidedStudentInfoById(id);
    }
}