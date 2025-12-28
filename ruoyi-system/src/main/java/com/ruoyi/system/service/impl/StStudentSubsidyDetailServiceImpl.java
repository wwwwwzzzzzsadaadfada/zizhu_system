package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StStudentSubsidyDetailMapper;
import com.ruoyi.system.domain.StStudentSubsidyDetail;
import com.ruoyi.system.service.IStStudentSubsidyDetailService;

/**
 * 学生受助明细Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-22
 */
@Service
public class StStudentSubsidyDetailServiceImpl implements IStStudentSubsidyDetailService 
{
    @Autowired
    private StStudentSubsidyDetailMapper stStudentSubsidyDetailMapper;

    /**
     * 查询学生受助明细
     * 
     * @param id 学生受助明细主键
     * @return 学生受助明细
     */
    @Override
    public StStudentSubsidyDetail selectStStudentSubsidyDetailById(Long id)
    {
        return stStudentSubsidyDetailMapper.selectStStudentSubsidyDetailById(id);
    }

    /**
     * 查询学生受助明细列表
     * 
     * @param stStudentSubsidyDetail 学生受助明细
     * @return 学生受助明细
     */
    @Override
    public List<StStudentSubsidyDetail> selectStStudentSubsidyDetailList(StStudentSubsidyDetail stStudentSubsidyDetail)
    {
        return stStudentSubsidyDetailMapper.selectStStudentSubsidyDetailList(stStudentSubsidyDetail);
    }

    /**
     * 新增学生受助明细
     * 
     * @param stStudentSubsidyDetail 学生受助明细
     * @return 结果
     */
    @Override
    public int insertStStudentSubsidyDetail(StStudentSubsidyDetail stStudentSubsidyDetail)
    {
        return stStudentSubsidyDetailMapper.insertStStudentSubsidyDetail(stStudentSubsidyDetail);
    }

    /**
     * 修改学生受助明细
     * 
     * @param stStudentSubsidyDetail 学生受助明细
     * @return 结果
     */
    @Override
    public int updateStStudentSubsidyDetail(StStudentSubsidyDetail stStudentSubsidyDetail)
    {
        return stStudentSubsidyDetailMapper.updateStStudentSubsidyDetail(stStudentSubsidyDetail);
    }

    /**
     * 批量删除学生受助明细
     * 
     * @param ids 需要删除的学生受助明细主键数组
     * @return 结果
     */
    @Override
    public int deleteStStudentSubsidyDetailByIds(Long[] ids)
    {
        return stStudentSubsidyDetailMapper.deleteStStudentSubsidyDetailByIds(ids);
    }

    /**
     * 删除学生受助明细信息
     * 
     * @param id 学生受助明细主键
     * @return 结果
     */
    @Override
    public int deleteStStudentSubsidyDetailById(Long id)
    {
        return stStudentSubsidyDetailMapper.deleteStStudentSubsidyDetailById(id);
    }
}