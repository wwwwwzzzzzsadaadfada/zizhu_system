package com.ruoyi.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.system.mapper.StStudentsMapper;
import com.ruoyi.system.mapper.StSchoolYearSemesterMapper;
import com.ruoyi.system.domain.StStudents;
import com.ruoyi.system.domain.StSchoolYearSemester;
import com.ruoyi.system.service.IStStudentsService;
import com.ruoyi.system.service.IStFamilyMemberService;
import com.ruoyi.system.domain.StFamilyMember;
import com.ruoyi.system.service.IStStudentBankCardService;
import com.ruoyi.system.domain.StStudentBankCard;

/**
 * 困难学生基础信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-11-19
 */
@Service
public class StStudentsServiceImpl implements IStStudentsService 
{
    @Autowired
    private StStudentsMapper stStudentsMapper;

    @Autowired
    private StSchoolYearSemesterMapper stSchoolYearSemesterMapper;

    @Autowired
    private IStFamilyMemberService stFamilyMemberService;

    @Autowired
    private IStStudentBankCardService stStudentBankCardService;

    /**
     * 查询困难学生基础信息
     * 
     * @param id 困难学生基础信息主键
     * @return 困难学生基础信息
     */
    @Override
    public StStudents selectStStudentsById(String id)
    {
        StStudents student = stStudentsMapper.selectStStudentsById(id);
        // 载入家庭成员
        if (student != null && StringUtils.isNotBlank(student.getId())) {
            try {
                Long sid = Long.valueOf(student.getId());
                List<StFamilyMember> members = stFamilyMemberService.selectStFamilyMemberList(buildFamilyQuery(sid, student.getCurrentYearSemesterId()));
                student.setFamilyMembers(members);
                List<StStudentBankCard> bankCards = stStudentBankCardService.selectStStudentBankCardList(buildBankCardQuery(sid));
                student.setBankCards(bankCards);
            } catch (NumberFormatException ignore) {
                // ignore invalid id format
            }
            // 使用工具类确保学期标签的一致性（即使SQL已计算，也通过工具类校验和设置）
            String semesterLabel = com.ruoyi.common.utils.SemesterUtils.getSemesterLabel(student.getCurrentSemester());
            student.setCurrentSemesterLabel(semesterLabel);
        }
        return student;
    }

    /**
     * 查询困难学生基础信息列表
     * 如果没有指定 currentYearSemesterId，则默认只查询当前学期的学生
     * 
     * @param stStudents 困难学生基础信息
     * @return 困难学生基础信息
     */
    @Override
    public List<StStudents> selectStStudentsList(StStudents stStudents)
    {
        // 如果前端传递了 yearSemesterId，转换为 currentYearSemesterId
        if (stStudents.getYearSemesterId() != null && stStudents.getCurrentYearSemesterId() == null)
        {
            stStudents.setCurrentYearSemesterId(stStudents.getYearSemesterId());
        }
        
        // 如果没有指定学期ID，则自动设置为当前学期ID
        if (stStudents.getCurrentYearSemesterId() == null)
        {
            StSchoolYearSemester currentSemester = stSchoolYearSemesterMapper.selectCurrentSemester(true);
            if (currentSemester != null)
            {
                stStudents.setCurrentYearSemesterId(currentSemester.getId());
            }
        }
        List<StStudents> list = stStudentsMapper.selectStStudentsList(stStudents);
        // 使用工具类确保学期标签的一致性（即使SQL已计算，也通过工具类校验和设置）
        if (list != null) {
            for (StStudents student : list) {
                String semesterLabel = com.ruoyi.common.utils.SemesterUtils.getSemesterLabel(student.getCurrentSemester());
                student.setCurrentSemesterLabel(semesterLabel);
            }
        }
        return list;
    }

    /**
     * 新增困难学生基础信息
     * 
     * @param stStudents 困难学生基础信息
     * @return 结果
     */
    @Override
    public int insertStStudents(StStudents stStudents)
    {
        normalizeNullableFields(stStudents);
        int rows = stStudentsMapper.insertStStudents(stStudents);
        // 保存家庭成员
        saveFamilyMembersIfPresent(stStudents);
        saveBankCardsIfPresent(stStudents);
        return rows;
    }

    /**
     * 修改困难学生基础信息
     * 
     * @param stStudents 困难学生基础信息
     * @return 结果
     */
    @Override
    public int updateStStudents(StStudents stStudents)
    {
        normalizeNullableFields(stStudents);
        int rows = stStudentsMapper.updateStStudents(stStudents);
        saveFamilyMembersIfPresent(stStudents);
        saveBankCardsIfPresent(stStudents);
        return rows;
    }

    /**
     * 批量删除困难学生基础信息
     * 
     * @param ids 需要删除的困难学生基础信息主键
     * @return 结果
     */
    @Override
    public int deleteStStudentsByIds(String[] ids)
    {
        return stStudentsMapper.deleteStStudentsByIds(ids);
    }

    /**
     * 删除困难学生基础信息信息
     * 
     * @param id 困难学生基础信息主键
     * @return 结果
     */
    @Override
    public int deleteStStudentsById(String id)
    {
        return stStudentsMapper.deleteStStudentsById(id);
    }

    /**
     * 查询学制列表
     * 
     * @return 学制列表
     */
    @Override
    public List<Map<String, Object>> selectSchoolPlanList()
    {
        return stStudentsMapper.selectSchoolPlanList();
    }

    /**
     * 根据学制ID查询年级列表
     * 
     * @param schoolingPlanId 学制ID
     * @return 年级列表
     */
    @Override
    public List<Map<String, Object>> selectGradeListByPlanId(String schoolingPlanId)
    {
        return stStudentsMapper.selectGradeListByPlanId(schoolingPlanId);
    }

    /**
     * 根据年级ID查询班级列表
     * 
     * @param gradeId 年级ID
     * @return 班级列表
     */
    @Override
    public List<Map<String, Object>> selectClassListByGradeId(String gradeId)
    {
        return stStudentsMapper.selectClassListByGradeId(gradeId);
    }

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
    @Override
    public int batchUpdateDifficultyInfo(String[] ids, String difficultyTypeId, String difficultyLevelId, String isPovertyReliefFamily, Integer povertyReliefYear)
    {
        if (ids == null || ids.length == 0)
        {
            return 0;
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        
        // 只有非空值才加入更新参数
        if (difficultyTypeId != null)
        {
            // 空字符串转换为null
            if (difficultyTypeId.isEmpty())
            {
                params.put("difficultyTypeId", null);
            }
            else
            {
                params.put("difficultyTypeId", difficultyTypeId);
                // 如果更新了困难类型，根据困难类型自动设置是否脱贫户
                String difficultyLabel = DictUtils.getDictLabel("sys_difficulty_type", difficultyTypeId);
                boolean isPovertyReliefType = StringUtils.isNotBlank(difficultyLabel) 
                    && (difficultyLabel.contains("脱贫") || difficultyLabel.contains("脱贫户"));
                
                if (isPovertyReliefType)
                {
                    // 如果是脱贫户类型，自动设置 isPovertyReliefFamily = "1"
                    params.put("isPovertyReliefFamily", "1");
                }
                else
                {
                    // 如果不是脱贫户类型，设置 isPovertyReliefFamily = "0"，并清空脱贫年份
                    params.put("isPovertyReliefFamily", "0");
                    params.put("povertyReliefYear", null);
                }
            }
        }
        
        if (difficultyLevelId != null)
        {
            if (difficultyLevelId.isEmpty())
            {
                params.put("difficultyLevelId", null);
            }
            else
            {
                params.put("difficultyLevelId", difficultyLevelId);
            }
        }
        
        // 如果前端明确指定了 isPovertyReliefFamily，且没有更新困难类型，则使用前端传入的值
        if (isPovertyReliefFamily != null && !params.containsKey("isPovertyReliefFamily"))
        {
            params.put("isPovertyReliefFamily", isPovertyReliefFamily);
        }
        
        // 如果前端明确指定了 povertyReliefYear，且没有更新困难类型，则使用前端传入的值
        if (povertyReliefYear != null && !params.containsKey("povertyReliefYear"))
        {
            params.put("povertyReliefYear", povertyReliefYear);
        }
        
        return stStudentsMapper.batchUpdateDifficultyInfo(params);
    }

    private void normalizeNullableFields(StStudents stStudents)
    {
        if (stStudents == null)
        {
            return;
        }
        // 将空字符串转换为 null，确保能正确保存到数据库
        if (StringUtils.isBlank(stStudents.getDifficultyTypeId()))
        {
            stStudents.setDifficultyTypeId(null);
        }
        if (StringUtils.isBlank(stStudents.getDifficultyLevelId()))
        {
            stStudents.setDifficultyLevelId(null);
        }
        
        // 根据困难类型自动设置是否脱贫户
        setPovertyReliefFamilyByDifficultyType(stStudents);
    }
    
    /**
     * 根据困难类型自动设置是否脱贫户
     * 如果困难类型是脱贫户类型，则自动设置 isPovertyReliefFamily = "1"
     * 否则设置为 "0"，并清空脱贫年份
     * 
     * @param stStudents 学生信息
     */
    private void setPovertyReliefFamilyByDifficultyType(StStudents stStudents)
    {
        if (stStudents == null || StringUtils.isBlank(stStudents.getDifficultyTypeId()))
        {
            // 如果没有困难类型，保持原有值或设置为 "0"
            if (stStudents != null && stStudents.getIsPovertyReliefFamily() == null)
            {
                stStudents.setIsPovertyReliefFamily("0");
            }
            return;
        }
        
        // 查询困难类型的字典标签
        String difficultyLabel = DictUtils.getDictLabel("sys_difficulty_type", stStudents.getDifficultyTypeId());
        
        // 判断是否是脱贫户类型（标签中包含"脱贫"关键字）
        boolean isPovertyReliefType = StringUtils.isNotBlank(difficultyLabel) 
            && (difficultyLabel.contains("脱贫") || difficultyLabel.contains("脱贫户"));
        
        if (isPovertyReliefType)
        {
            // 如果是脱贫户类型，设置 isPovertyReliefFamily = "1"
            stStudents.setIsPovertyReliefFamily("1");
        }
        else
        {
            // 如果不是脱贫户类型，设置 isPovertyReliefFamily = "0"，并清空脱贫年份
            stStudents.setIsPovertyReliefFamily("0");
            stStudents.setPovertyReliefYear(null);
        }
    }

    private void saveFamilyMembersIfPresent(StStudents stStudents) {
        if (stStudents == null || stStudents.getFamilyMembers() == null) {
            return;
        }
        try {
            Long studentId = stStudents.getId() != null ? Long.valueOf(stStudents.getId()) : null;
            if (studentId != null) {
                stFamilyMemberService.saveByStudentId(studentId, stStudents.getCurrentYearSemesterId(), stStudents.getFamilyMembers());
            }
        } catch (NumberFormatException ignore) {
            // ignore
        }
    }

    private void saveBankCardsIfPresent(StStudents stStudents) {
        if (stStudents == null || stStudents.getBankCards() == null) {
            return;
        }
        try {
            Long studentId = stStudents.getId() != null ? Long.valueOf(stStudents.getId()) : null;
            if (studentId != null) {
                stStudentBankCardService.saveByStudentId(studentId, stStudents.getBankCards());
            }
        } catch (NumberFormatException ignore) {
            // ignore
        }
    }

    private StFamilyMember buildFamilyQuery(Long studentId, Long yearSemesterId) {
        StFamilyMember q = new StFamilyMember();
        q.setStudentId(studentId);
        q.setYearSemesterId(yearSemesterId);
        return q;
    }

    private StStudentBankCard buildBankCardQuery(Long studentId) {
        StStudentBankCard q = new StStudentBankCard();
        q.setStudentId(studentId);
        return q;
    }
}
