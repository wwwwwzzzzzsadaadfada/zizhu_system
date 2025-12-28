package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.mapper.StStudentSemesterRecordMapper;
import com.ruoyi.system.mapper.StStudentsBaseMapper;
import com.ruoyi.system.mapper.StSemesterBudgetMapper;
import com.ruoyi.system.domain.StStudentSemesterRecord;
import com.ruoyi.system.domain.StStudentsBase;
import com.ruoyi.system.domain.StSemesterBudget;
import com.ruoyi.system.domain.StStudentSubsidyDetail;
import com.ruoyi.system.service.IStStudentSemesterRecordService;
import com.ruoyi.system.mapper.StStudentSubsidyDetailMapper;
import com.ruoyi.system.mapper.StSchoolYearSemesterMapper;
import com.ruoyi.system.domain.StSchoolYearSemester;
import com.ruoyi.system.domain.SyncAidedStudentResult;
/**
 * 学生学期认定记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
@Service
public class StStudentSemesterRecordServiceImpl implements IStStudentSemesterRecordService 
{
    @Autowired
    private StStudentSemesterRecordMapper stStudentSemesterRecordMapper;

    @Autowired
    private StStudentsBaseMapper stStudentsBaseMapper;

    @Autowired
    private StSemesterBudgetMapper stSemesterBudgetMapper;
    
    @Autowired
    private StStudentSubsidyDetailMapper stStudentSubsidyDetailMapper;

    @Autowired
    private StSchoolYearSemesterMapper stSchoolYearSemesterMapper;

    /**
     * 查询学生学期认定记录
     * 
     * @param id 学生学期认定记录主键
     * @return 学生学期认定记录
     */
    @Override
    public StStudentSemesterRecord selectStStudentSemesterRecordById(Long id)
    {
        return stStudentSemesterRecordMapper.selectStStudentSemesterRecordById(id);
    }

    /**
     * 查询学生学期认定记录列表
     * 
     * @param stStudentSemesterRecord 学生学期认定记录
     * @return 学生学期认定记录
     */
    @Override
    public List<StStudentSemesterRecord> selectStStudentSemesterRecordList(StStudentSemesterRecord stStudentSemesterRecord)
    {
        // 如果没有指定学年学期ID，则默认查询当前学期的学生记录
        if (stStudentSemesterRecord.getYearSemesterId() == null) {
            // 获取当前学期
            StSchoolYearSemester currentSemester = stSchoolYearSemesterMapper.selectCurrentSemester(true);
            if (currentSemester != null) {
                stStudentSemesterRecord.setYearSemesterId(currentSemester.getId());
            }
        }
        
        return stStudentSemesterRecordMapper.selectStStudentSemesterRecordList(stStudentSemesterRecord);
    }

    /**
     * 新增学生学期认定记录
     * 
     * @param stStudentSemesterRecord 学生学期认定记录
     * @return 结果
     */
    @Override
    @Transactional
    public int insertStStudentSemesterRecord(StStudentSemesterRecord stStudentSemesterRecord)
    {
        // 如果学生基础信息ID为空，则先插入学生基础信息
        if (stStudentSemesterRecord.getStudentBaseId() == null && stStudentSemesterRecord.getIdCardNo() != null)
        {
            // 先查询是否已存在该学生
            StStudentsBase existStudent = stStudentsBaseMapper.selectStStudentsBaseByIdCardNo(stStudentSemesterRecord.getIdCardNo());
            if (existStudent != null)
            {
                stStudentSemesterRecord.setStudentBaseId(existStudent.getId());
            }
            else
            {
                // 插入学生基础信息
                StStudentsBase studentBase = new StStudentsBase();
                studentBase.setName(stStudentSemesterRecord.getName());
                studentBase.setIdCardNo(stStudentSemesterRecord.getIdCardNo());
                studentBase.setGender(stStudentSemesterRecord.getGender());
                studentBase.setEthnicity(stStudentSemesterRecord.getEthnicity());
                studentBase.setDomicile(stStudentSemesterRecord.getDomicile());
                studentBase.setStudentNo(stStudentSemesterRecord.getStudentNo());
                stStudentsBaseMapper.insertStStudentsBase(studentBase);
                stStudentSemesterRecord.setStudentBaseId(studentBase.getId());
            }
        }
        
        return stStudentSemesterRecordMapper.insertStStudentSemesterRecord(stStudentSemesterRecord);
    }

    /**
     * 更新学生学期认定记录（重构版本）
     */
    @Override
    @Transactional
    public int updateStStudentSemesterRecord(StStudentSemesterRecord stStudentSemesterRecord)
    {
        // 先获取原始记录
        StStudentSemesterRecord oldRecord = stStudentSemesterRecordMapper.selectStStudentSemesterRecordById(stStudentSemesterRecord.getId());
        if (oldRecord == null) {
            throw new ServiceException("记录不存在");
        }

        // 处理受助明细列表
        List<StStudentSubsidyDetail> subsidyDetailList = stStudentSemesterRecord.getSubsidyDetails();
        BigDecimal totalSubsidyAmount = BigDecimal.ZERO;
        String mainEconomyCategory = null;
        
        if (subsidyDetailList != null && !subsidyDetailList.isEmpty())
        {
            // 收集所有涉及的预算ID和金额
            Map<Long, BigDecimal> budgetChanges = new HashMap<>();
            
            // 计算总受助金额并收集预算变更
            for (StStudentSubsidyDetail detail : subsidyDetailList) {
                // 设置学生记录ID
                detail.setStudentRecordId(stStudentSemesterRecord.getId());
                totalSubsidyAmount = totalSubsidyAmount.add(detail.getSubsidyAmount());
                
                // 记录预算变更
                Long budgetId = detail.getBudgetId();
                BigDecimal amount = detail.getSubsidyAmount();
                budgetChanges.merge(budgetId, amount, BigDecimal::add);
                
                // 设置主要经济分类
                if (mainEconomyCategory == null) {
                    mainEconomyCategory = detail.getEconomyCategory();
                }
            }
            
            // 删除原有的所有明细记录
            stStudentSubsidyDetailMapper.deleteStStudentSubsidyDetailByStudentRecordId(stStudentSemesterRecord.getId());
            
            // 插入新的明细记录
            for (StStudentSubsidyDetail detail : subsidyDetailList) {
                stStudentSubsidyDetailMapper.insertStStudentSubsidyDetail(detail);
            }
            
            // 更新预算已使用金额
            updateBudgetUsedAmounts(oldRecord, budgetChanges);
        }
        
        // 更新主记录的总受助金额和经济分类
        stStudentSemesterRecord.setSubsidyAmount(totalSubsidyAmount);
        if (stStudentSemesterRecord.getEconomyCategory() == null || stStudentSemesterRecord.getEconomyCategory().isEmpty()) {
            stStudentSemesterRecord.setEconomyCategory(mainEconomyCategory);
        }
        // 直接设置为已通过状态
        stStudentSemesterRecord.setApprovalStatus(1);
        
        return stStudentSemesterRecordMapper.updateStStudentSemesterRecord(stStudentSemesterRecord);
    }
    
    /**
     * 更新预算已使用金额（统一入口）
     */
    private void updateBudgetUsedAmounts(StStudentSemesterRecord oldRecord, Map<Long, BigDecimal> budgetChanges) {
        // 获取所有涉及的预算记录
        Set<Long> budgetIds = budgetChanges.keySet();
        for (Long budgetId : budgetIds) {
            StSemesterBudget budget = stSemesterBudgetMapper.selectStSemesterBudgetById(budgetId);
            if (budget != null) {
                // 计算该预算下的所有补助明细总额
                List<StStudentSubsidyDetail> details = stStudentSubsidyDetailMapper.selectStStudentSubsidyDetailListByBudgetId(budgetId);
                BigDecimal totalUsed = details.stream()
                    .map(StStudentSubsidyDetail::getSubsidyAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                // 更新预算已使用金额
                StSemesterBudget updateBudget = new StSemesterBudget();
                updateBudget.setId(budgetId);
                updateBudget.setUsedAmount(totalUsed);
                stSemesterBudgetMapper.updateStSemesterBudget(updateBudget);
            }
        }
    }

    /**
     * 批量删除学生学期认定记录
     * 
     * @param ids 需要删除的学生学期认定记录主键数组
     * @return 结果
     */
    @Override
    public int deleteStStudentSemesterRecordByIds(Long[] ids)
    {
        return stStudentSemesterRecordMapper.deleteStStudentSemesterRecordByIds(ids);
    }

    /**
     * 删除学生学期认定记录信息
     * 
     * @param id 学生学期认定记录主键
     * @return 结果
     */
    @Override
    public int deleteStStudentSemesterRecordById(Long id)
    {
        return stStudentSemesterRecordMapper.deleteStStudentSemesterRecordById(id);
    }

    /**
     * 查询学制列表
     * 
     * @return 学制列表
     */
    @Override
    public List<Map<String, Object>> selectSchoolPlanList()
    {
        return stStudentSemesterRecordMapper.selectSchoolPlanList();
    }

    /**
     * 根据学制ID查询年级列表
     * 
     * @param schoolingPlanId 学制ID
     * @return 年级列表
     */
    @Override
    public List<Map<String, Object>> selectGradeListByPlanId(Long schoolingPlanId)
    {
        return stStudentSemesterRecordMapper.selectGradeListByPlanId(schoolingPlanId);
    }

    /**
     * 根据年级ID查询班级列表
     * 
     * @param gradeId 年级ID
     * @return 班级列表
     */
    @Override
    public List<Map<String, Object>> selectClassListByGradeId(Long gradeId)
    {
        return stStudentSemesterRecordMapper.selectClassListByGradeId(gradeId);
    }
    
    /**
     * 同步学生数据到学期记录表
     * 
     * @param yearSemesterId 学年学期ID
     * @return 同步的学生数量
     */
    @Override
    @Transactional
    public int syncStudentsToSemesterRecords(Long yearSemesterId)
    {
        return stStudentSemesterRecordMapper.syncStudentsToSemesterRecords(yearSemesterId);
    }
    
    /**
     * 同步学生数据到受助学生信息表
     * 
     * @param studentBaseId 学生基础信息ID
     * @param academicYear 学年
     * @param semester 学期
     * @return 同步的结果
     */
    @Override
    @Transactional
    public SyncAidedStudentResult syncStudentsToAidedTable(Long studentBaseId, String academicYear, String semester)
    {
        try
        {
            // 数据验证：检查学生是否存在
            StStudentsBase student = stStudentsBaseMapper.selectStStudentsBaseById(studentBaseId);
            if (student == null)
            {
                return new SyncAidedStudentResult("学生不存在，无法同步");
            }

            // 解析学年学期
            String[] resolved = resolveAcademicYearAndSemester(academicYear, semester);
            
            // 检查该学生是否已存在受助信息（在同步前检查）
            boolean existsBefore = stStudentSemesterRecordMapper.checkAidedStudentExists(studentBaseId, resolved[0], resolved[1]);
            
            // 执行同步
            int result = stStudentSemesterRecordMapper.syncStudentToAidedTable(studentBaseId, resolved[0], resolved[1]);
            
            if (result > 0)
            {
                // 根据同步前是否存在返回不同的结果
                if (existsBefore)
                {
                    return new SyncAidedStudentResult(0, 1); // 更新
                }
                else
                {
                    return new SyncAidedStudentResult(1, 0); // 新增
                }
            }
            else
            {
                // 如果返回0，可能是更新时值没有变化，仍然认为是更新成功
                if (existsBefore)
                {
                    return new SyncAidedStudentResult(0, 1); // 更新（值未变化）
                }
                else
                {
                    return new SyncAidedStudentResult("同步失败，未影响任何记录");
                }
            }
        }
        catch (ServiceException e)
        {
            return new SyncAidedStudentResult(e.getMessage());
        }
        catch (Exception e)
        {
            return new SyncAidedStudentResult("同步失败：" + e.getMessage());
        }
    }
    
    /**
     * 同步所有学生数据到受助学生信息表
     * 
     * @param academicYear 学年
     * @param semester 学期
     * @return 同步的结果
     */
    @Override
    @Transactional
    public SyncAidedStudentResult syncAllStudentsToAidedTable(String academicYear, String semester)
    {
        try
        {
            // 解析学年学期
            String[] resolved = resolveAcademicYearAndSemester(academicYear, semester);
            
            // 数据验证：检查是否有学生数据
            int totalStudents = stStudentsBaseMapper.selectStStudentsBaseList(new StStudentsBase()).size();
            if (totalStudents == 0)
            {
                return new SyncAidedStudentResult("没有可同步的学生数据");
            }
            
            // 先更新已存在的学生
            int updated = stStudentSemesterRecordMapper.updateExistingStudentsInAidedTable(resolved[0], resolved[1]);
            
            // 再插入新学生
            int inserted = stStudentSemesterRecordMapper.syncAllStudentsToAidedTable(resolved[0], resolved[1]);
            
            return new SyncAidedStudentResult(inserted, updated);
        }
        catch (ServiceException e)
        {
            return new SyncAidedStudentResult(e.getMessage());
        }
        catch (Exception e)
        {
            return new SyncAidedStudentResult("同步失败：" + e.getMessage());
        }
    }

    /**
     * 解析/补全请求中的学年学期，默认回落到当前学期
     */
    private String[] resolveAcademicYearAndSemester(String academicYear, String semester)
    {
        boolean missingYear = StringUtils.isBlank(academicYear);
        boolean missingSemester = StringUtils.isBlank(semester);
        if (!missingYear && !missingSemester)
        {
            return new String[] { academicYear, semester };
        }

        StSchoolYearSemester currentSemester = stSchoolYearSemesterMapper.selectCurrentSemester(true);
        if (currentSemester == null || currentSemester.getSemester() == null)
        {
            throw new ServiceException("当前学年学期未配置，无法执行受助学生同步。");
        }

        String resolvedYear = missingYear ? currentSemester.getSchoolYear() : academicYear;
        String resolvedSemester = missingSemester ? String.valueOf(currentSemester.getSemester()) : semester;

        if (StringUtils.isBlank(resolvedYear) || StringUtils.isBlank(resolvedSemester))
        {
            throw new ServiceException("学年或学期信息不完整，无法执行受助学生同步。");
        }

        return new String[] { resolvedYear, resolvedSemester };
    }
}