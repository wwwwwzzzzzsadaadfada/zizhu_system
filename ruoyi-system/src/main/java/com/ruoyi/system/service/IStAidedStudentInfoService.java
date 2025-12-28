package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StAidedStudentInfo;

/**
 * 受助学生信息服务接口
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
public interface IStAidedStudentInfoService 
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
     * 批量删除受助学生信息
     * 
     * @param ids 需要删除的受助学生信息主键集合
     * @return 结果
     */
    public int deleteStAidedStudentInfoByIds(Long[] ids);

    /**
     * 删除受助学生信息信息
     * 
     * @param id 受助学生信息主键
     * @return 结果
     */
    public int deleteStAidedStudentInfoById(Long id);
}