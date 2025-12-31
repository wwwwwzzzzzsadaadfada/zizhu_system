package com.ruoyi.system.service.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.StStudentsBaseMapper;
import com.ruoyi.system.domain.StStudentsBase;
import com.ruoyi.system.service.IStStudentsBaseService;
import com.ruoyi.common.utils.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 学生基础信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
@Service
@Transactional
public class StStudentsBaseServiceImpl implements IStStudentsBaseService 
{
    private static final Logger logger = LoggerFactory.getLogger(StStudentsBaseServiceImpl.class);
    
    @Autowired
    private StStudentsBaseMapper stStudentsBaseMapper;

    /**
     * 查询学生基础信息
     * 
     * @param id 学生基础信息主键
     * @return 学生基础信息
     */
    @Override
    public StStudentsBase selectStStudentsBaseById(Long id)
    {
        return stStudentsBaseMapper.selectStStudentsBaseById(id);
    }

    /**
     * 查询学生基础信息列表
     * 
     * @param stStudentsBase 学生基础信息
     * @return 学生基础信息
     */
    @Override
    public List<StStudentsBase> selectStStudentsBaseList(StStudentsBase stStudentsBase)
    {
        return stStudentsBaseMapper.selectStStudentsBaseList(stStudentsBase);
    }

    /**
     * 新增学生基础信息
     * 
     * @param stStudentsBase 学生基础信息
     * @return 结果
     */
    @Override
    public int insertStStudentsBase(StStudentsBase stStudentsBase)
    {
        if (stStudentsBase == null) {
            throw new RuntimeException("学生信息不能为null");
        }
        
        // 加密敏感字段
        encryptSensitiveFields(stStudentsBase);
        
        return stStudentsBaseMapper.insertStStudentsBase(stStudentsBase);
    }

    /**
     * 修改学生基础信息
     * 
     * @param stStudentsBase 学生基础信息
     * @return 结果
     */
    @Override
    public int updateStStudentsBase(StStudentsBase stStudentsBase)
    {
        if (stStudentsBase == null || stStudentsBase.getId() == null) {
            throw new RuntimeException("学生信息不能为null");
        }
        // 检查该学生是否存在
        StStudentsBase existing = stStudentsBaseMapper.selectStStudentsBaseById(stStudentsBase.getId());
        if (existing == null) {
            throw new RuntimeException("学生信息不存在");
        }
        
        // 加密敏感字段
        encryptSensitiveFields(stStudentsBase);
        
        return stStudentsBaseMapper.updateStStudentsBase(stStudentsBase);
    }

    /**
     * 批量删除学生基础信息
     * 
     * @param ids 需要删除的学生基础信息主键
     * @return 结果
     */
    @Override
    public int deleteStStudentsBaseByIds(Long[] ids)
    {
        if (ids == null || ids.length == 0) {
            return 0;
        }
        return stStudentsBaseMapper.deleteStStudentsBaseByIds(ids);
    }

    /**
     * 删除学生基础信息信息
     * 
     * @param id 学生基础信息主键
     * @return 结果
     */
    @Override
    public int deleteStStudentsBaseById(Long id)
    {
        return stStudentsBaseMapper.deleteStStudentsBaseById(id);
    }

    /**
     * 根据身份证号查询学生基础信息
     * 
     * @param idCardNo 身份证号
     * @return 学生基础信息
     */
    @Override
    public StStudentsBase selectStStudentsBaseByIdCardNo(String idCardNo)
    {
        return stStudentsBaseMapper.selectStStudentsBaseByIdCardNo(idCardNo);
    }

    /**
     * 根据学籍号查询学生基础信息
     * 
     * @param studentNo 学籍号
     * @return 学生基础信息
     */
    @Override
    public StStudentsBase selectStStudentsBaseByStudentNo(String studentNo)
    {
        return stStudentsBaseMapper.selectStStudentsBaseByStudentNo(studentNo);
    }

    /**
     * 自动升年级
     */
    @Override
    public int promoteStudentsForSchoolYear(String schoolYear)
    {
        if (schoolYear == null || schoolYear.length() < 4)
        {
            return 0;
        }
        // 执行升年级操作（事务中执行）
        int startYear = Integer.parseInt(schoolYear.substring(0, 4));
        LocalDate promotionLocalDate = LocalDate.of(startYear, 9, 1);
        Date promotionDate = Date.valueOf(promotionLocalDate);

        List<Map<String, Object>> grades = stStudentsBaseMapper.selectAllGradesForPromotion();
        if (grades.isEmpty())
        {
            return 0;
        }

        Map<Long, Long> nextGradeMap = buildNextGradeMap(grades);
        List<StStudentsBase> candidates = stStudentsBaseMapper.selectStudentsForPromotion(promotionDate);
        if (candidates.isEmpty())
        {
            return 0;
        }

        int promoted = 0;
        for (StStudentsBase student : candidates)
        {
            Long currentGradeId = student.getGradeId();
            if (currentGradeId == null)
            {
                continue;
            }
            Long nextGradeId = nextGradeMap.get(currentGradeId);
            Long targetGradeId = nextGradeId != null ? nextGradeId : currentGradeId;
            int rows = stStudentsBaseMapper.updateStudentGradeForPromotion(student.getId(), targetGradeId, promotionDate);
            if (rows > 0 && nextGradeId != null)
            {
                promoted++;
            }
        }
        return promoted;
    }

    private Map<Long, Long> buildNextGradeMap(List<Map<String, Object>> grades)
    {
        Map<Long, Long> nextGradeMap = new HashMap<>();
        Map<Long, Long> lastGradePerPlan = new HashMap<>();
        for (Map<String, Object> row : grades)
        {
            Long gradeId = toLong(row.get("id"));
            Long planId = toLong(row.get("schoolingPlanId"));
            if (gradeId == null || planId == null)
            {
                continue;
            }
            Long previousGrade = lastGradePerPlan.put(planId, gradeId);
            if (previousGrade != null)
            {
                nextGradeMap.put(previousGrade, gradeId);
            }
        }
        return nextGradeMap;
    }

    private Long toLong(Object value)
    {
        if (value instanceof Number)
        {
            return ((Number) value).longValue();
        }
        if (value != null)
        {
            return Long.parseLong(value.toString());
        }
        return null;
    }
    
    /**
     * 加密敏感字段
     * 将身份证号、住址、手机号码和学籍号进行 AES 加密
     * 
     * @param stStudentsBase 学生信息
     */
    private void encryptSensitiveFields(StStudentsBase stStudentsBase)
    {
        if (stStudentsBase == null)
        {
            return;
        }
        
        // 加密身份证号
        if (StringUtils.isNotBlank(stStudentsBase.getIdCardNo()))
        {
            try {
                // 先检查是否已经是加密数据（避免重复加密）
                String idCard = stStudentsBase.getIdCardNo();
                if (!isEncrypted(idCard)) {
                    stStudentsBase.setIdCardNo(EncryptionUtil.encrypt(idCard));
                    logger.debug("身份证号已加密");
                }
            } catch (Exception e) {
                logger.error("加密身份证号失败", e);
            }
        }
        
        // 加密住址
        if (StringUtils.isNotBlank(stStudentsBase.getDomicile()))
        {
            try {
                String domicile = stStudentsBase.getDomicile();
                if (!isEncrypted(domicile)) {
                    stStudentsBase.setDomicile(EncryptionUtil.encrypt(domicile));
                    logger.debug("住址已加密");
                }
            } catch (Exception e) {
                logger.error("加密住址失败", e);
            }
        }
        
        // 加密手机号码
        if (StringUtils.isNotBlank(stStudentsBase.getPhone()))
        {
            try {
                String phone = stStudentsBase.getPhone();
                if (!isEncrypted(phone)) {
                    stStudentsBase.setPhone(EncryptionUtil.encrypt(phone));
                    logger.debug("手机号码已加密");
                }
            } catch (Exception e) {
                logger.error("加密手机号码失败", e);
            }
        }
        
        // 加密学籍号
        if (StringUtils.isNotBlank(stStudentsBase.getStudentNo()))
        {
            try {
                String studentNo = stStudentsBase.getStudentNo();
                if (!isEncrypted(studentNo)) {
                    stStudentsBase.setStudentNo(EncryptionUtil.encrypt(studentNo));
                    logger.debug("学籍号已加密");
                }
            } catch (Exception e) {
                logger.error("加密学籍号失败", e);
            }
        }
    }
    
    /**
     * 判断字符串是否为加密数据
     * 加密数据通常是Base64格式，长度较长且包含+/=等字符
     * 
     * @param text 字符串
     * @return 是否为加密数据
     */
    private boolean isEncrypted(String text)
    {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        
        // 身份证号格式：18位数字或前17位数字+X
        if (text.matches("^\\d{17}[\\dXx]$")) {
            return false; // 明文身份证号
        }
        
        // 手机号码格式：11位数字，以1开头
        if (text.matches("^1[3-9]\\d{9}$")) {
            return false; // 明文手机号
        }
        
        // 学籍号格式：通常以G开头后跟18位数字或全数字
        if (text.matches("^G?\\d{10,20}$")) {
            return false; // 明文学籍号
        }
        
        // 加密数据通常包含Base64字符（+/=）且长度较长
        return text.length() > 30 && (text.contains("+") || text.contains("/") || text.contains("="));
    }

    /**
     * 批量更新所有在读学生的当前学期ID
     */
    @Override
    public int updateAllStudentsCurrentSemester(Long yearSemesterId)
    {
        if (yearSemesterId == null)
        {
            return 0;
        }
        return stStudentsBaseMapper.updateAllStudentsCurrentSemester(yearSemesterId);
    }
}
