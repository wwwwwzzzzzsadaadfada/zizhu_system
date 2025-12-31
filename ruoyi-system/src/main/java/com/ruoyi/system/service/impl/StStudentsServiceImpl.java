package com.ruoyi.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.EncryptionUtil;
import com.ruoyi.common.utils.SensitiveDataUtil;
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
@Transactional
public class StStudentsServiceImpl implements IStStudentsService
{
    private static final Logger logger = LoggerFactory.getLogger(StStudentsServiceImpl.class);
    
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
            // 使用工具类确保学期标签的一致性（即使 SQL 已计算，也通过工具类校验和设置）
            String semesterLabel = com.ruoyi.common.utils.SemesterUtils.getSemesterLabel(student.getCurrentSemester());
            student.setCurrentSemesterLabel(semesterLabel);
            // 解密敏感字段
            decryptSensitiveFields(student);
            // 转换性别值：兼容历史数据（2 → 0）
            normalizeGenderValue(student);
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
        // ... existing code ...
        List<StStudents> list = stStudentsMapper.selectStStudentsList(stStudents);
        // ... existing code ...
        if (list != null) {
            for (StStudents student : list) {
                String semesterLabel = com.ruoyi.common.utils.SemesterUtils.getSemesterLabel(student.getCurrentSemester());
                student.setCurrentSemesterLabel(semesterLabel);
                // 列表查询仅做脱敏，不做解密（提高性能）
                maskSensitiveFields(student);
            }
        }
        return list;
    }

    /**
     * 新增困难学生基础信息
     * 
     * 业务逻辑：
     * 1. 严格的参数验证和唯一性检查
     * 2. 先保存学生基本信息，获取生成的学生ID
     * 3. 只有学生基本信息保存成功后，才能保存家庭成员和银行卡
     * 4. 所有操作在同一事务中，保证数据一致性
     * 5. 任何步骤失败都会回滚整个事务
     * 
     * @param stStudents 困难学生基础信息
     * @return 结果
     */
    @Override
    public int insertStStudents(StStudents stStudents)
    {
        // ======== 第一步：严格的参数验证 ========
        if (stStudents == null) {
            logger.error("[新增学生] 参数验证失败：学生信息不能为空");
            throw new RuntimeException("学生信息不能为空");
        }
        
        // 验证必填字段
        if (StringUtils.isBlank(stStudents.getName())) {
            logger.error("[新增学生] 参数验证失败：学生姓名不能为空");
            throw new RuntimeException("学生姓名不能为空");
        }
        
        if (StringUtils.isBlank(stStudents.getIdCardNo())) {
            logger.error("[新增学生] 参数验证失败：身份证号不能为空");
            throw new RuntimeException("身份证号不能为空");
        }
        
        if (StringUtils.isBlank(stStudents.getStudentNo())) {
            logger.error("[新增学生] 参数验证失败：学籍号不能为空");
            throw new RuntimeException("学籍号不能为空");
        }
        
        // ======== 第二步：唯一性检查 ========
        StStudents existing = stStudentsMapper.selectByIdCardNoOrStudentNo(
            stStudents.getIdCardNo(), 
            stStudents.getStudentNo()
        );
        
        if (existing != null) {
            if (StringUtils.equals(existing.getIdCardNo(), stStudents.getIdCardNo())) {
                logger.error("[新增学生] 唯一性检查失败：身份证号已存在 - {}", stStudents.getIdCardNo());
                throw new RuntimeException("该身份证号已存在，请勿重复添加");
            } else if (StringUtils.equals(existing.getStudentNo(), stStudents.getStudentNo())) {
                logger.error("[新增学生] 唯一性检查失败：学籍号已存在 - {}", stStudents.getStudentNo());
                throw new RuntimeException("该学籍号已存在，请勿重复添加");
            }
        }
        
        // ======== 第三步：数据规范化和验证 ========
        normalizeNullableFields(stStudents);
        normalizeGenderValue(stStudents);
        validateBusinessData(stStudents);
        
        // ======== 第四步：加密敏感字段 ========
        encryptSensitiveFields(stStudents);
        
        // ======== 第五步：保存学生基本信息（核心步骤） ========
        logger.info("[新增学生] 开始保存学生基本信息 - 姓名：{}, 身份证号：{}", 
            stStudents.getName(), stStudents.getIdCardNo());
        
        int rows = stStudentsMapper.insertStStudents(stStudents);
        
        if (rows <= 0) {
            logger.error("[新增学生] 保存学生基本信息失败 - 姓名：{}", stStudents.getName());
            throw new RuntimeException("保存学生基本信息失败");
        }
        
        logger.info("[新增学生] 学生基本信息保存成功 - 学生ID：{}, 姓名：{}", 
            stStudents.getId(), stStudents.getName());
        
        // ======== 第六步：验证学生ID已生成 ========
        if (StringUtils.isBlank(stStudents.getId())) {
            logger.error("[新增学生] 学生ID生成失败 - 姓名：{}", stStudents.getName());
            throw new RuntimeException("学生ID生成失败，无法保存关联信息");
        }
        
        // ======== 第七步：保存关联数据（家庭成员和银行卡） ========
        // 重要：只有在学生基本信息保存成功且ID生成后，才能保存关联数据
        try {
            Long studentId = Long.valueOf(stStudents.getId());
            
            // 保存家庭成员（如果有）
            if (stStudents.getFamilyMembers() != null && !stStudents.getFamilyMembers().isEmpty()) {
                logger.info("[新增学生] 开始保存家庭成员 - 学生ID：{}, 数量：{}", 
                    studentId, stStudents.getFamilyMembers().size());
                
                // 验证家庭成员数据完整性
                validateFamilyMembers(stStudents.getFamilyMembers());
                
                saveFamilyMembersWithStudentId(studentId, stStudents);
                
                logger.info("[新增学生] 家庭成员保存成功 - 学生ID：{}", studentId);
            }
            
            // 保存银行卡（如果有）
            if (stStudents.getBankCards() != null && !stStudents.getBankCards().isEmpty()) {
                logger.info("[新增学生] 开始保存银行卡 - 学生ID：{}, 数量：{}", 
                    studentId, stStudents.getBankCards().size());
                
                // 验证银行卡数据完整性
                validateBankCards(stStudents.getBankCards());
                
                saveBankCardsWithStudentId(studentId, stStudents);
                
                logger.info("[新增学生] 银行卡保存成功 - 学生ID：{}", studentId);
            }
        } catch (NumberFormatException e) {
            logger.error("[新增学生] 学生ID格式错误 - ID：{}", stStudents.getId(), e);
            throw new RuntimeException("学生ID格式错误，无法保存关联信息");
        } catch (Exception e) {
            logger.error("[新增学生] 保存关联数据失败 - 学生ID：{}", stStudents.getId(), e);
            // 抛出异常触发事务回滚
            throw new RuntimeException("保存关联数据失败：" + e.getMessage(), e);
        }
        
        logger.info("[新增学生] 学生信息及关联数据保存完成 - 学生ID：{}, 姓名：{}", 
            stStudents.getId(), stStudents.getName());
        
        return rows;
    }

    /**
     * 修改困难学生基础信息
     * 
     * 业务逻辑：
     * 1. 严格验证学生是否存在
     * 2. 检查身份证号和学籍号的唯一性（仅当修改时）
     * 3. 先更新学生基本信息
     * 4. 只有基本信息更新成功后，才更新关联数据
     * 5. 所有操作在同一事务中
     * 
     * @param stStudents 困难学生基础信息
     * @return 结果
     */
    @Override
    public int updateStStudents(StStudents stStudents)
    {
        // ======== 第一步：参数验证 ========
        if (stStudents == null || StringUtils.isBlank(stStudents.getId())) {
            logger.error("[修改学生] 参数验证失败：学生信息或ID不能为空");
            throw new RuntimeException("学生信息不能为空");
        }
        
        // ======== 第二步：检查学生是否存在 ========
        StStudents existing = stStudentsMapper.selectStStudentsById(stStudents.getId());
        if (existing == null) {
            logger.error("[修改学生] 学生不存在 - ID：{}", stStudents.getId());
            throw new RuntimeException("学生信息不存在");
        }
        
        logger.info("[修改学生] 开始更新学生信息 - ID：{}, 姓名：{}", 
            stStudents.getId(), stStudents.getName());
        
        // ======== 第三步：检查身份证号和学籍号唯一性（仅当修改时） ========
        boolean idCardChanged = StringUtils.isNotBlank(stStudents.getIdCardNo()) && 
            !stStudents.getIdCardNo().equals(existing.getIdCardNo());
        boolean studentNoChanged = StringUtils.isNotBlank(stStudents.getStudentNo()) &&
            !stStudents.getStudentNo().equals(existing.getStudentNo());
        
        if (idCardChanged || studentNoChanged) {
            StStudents conflict = stStudentsMapper.selectByIdCardNoOrStudentNo(
                idCardChanged ? stStudents.getIdCardNo() : existing.getIdCardNo(),
                studentNoChanged ? stStudents.getStudentNo() : existing.getStudentNo()
            );
            
            if (conflict != null && !conflict.getId().equals(stStudents.getId())) {
                if (idCardChanged && StringUtils.equals(conflict.getIdCardNo(), stStudents.getIdCardNo())) {
                    logger.error("[修改学生] 身份证号已被他人使用 - 身份证号：{}", stStudents.getIdCardNo());
                    throw new RuntimeException("该身份证号已被他人使用");
                } else if (studentNoChanged && StringUtils.equals(conflict.getStudentNo(), stStudents.getStudentNo())) {
                    logger.error("[修改学生] 学籍号已被他人使用 - 学籍号：{}", stStudents.getStudentNo());
                    throw new RuntimeException("该学籍号已被他人使用");
                }
            }
        }
        
        // ======== 第四步：数据规范化和验证 ========
        normalizeNullableFields(stStudents);
        normalizeGenderValue(stStudents);
        validateBusinessData(stStudents);
        
        // ======== 第五步：加密敏感字段 ========
        encryptSensitiveFields(stStudents);
        
        // ======== 第六步：更新学生基本信息 ========
        int rows = stStudentsMapper.updateStStudents(stStudents);
        
        if (rows <= 0) {
            logger.error("[修改学生] 更新学生基本信息失败 - ID：{}", stStudents.getId());
            throw new RuntimeException("更新学生基本信息失败");
        }
        
        logger.info("[修改学生] 学生基本信息更新成功 - ID：{}", stStudents.getId());
        
        // ======== 第七步：更新关联数据 ========
        try {
            Long studentId = Long.valueOf(stStudents.getId());
            
            // 更新家庭成员（如果有）
            if (stStudents.getFamilyMembers() != null) {
                logger.info("[修改学生] 开始更新家庭成员 - 学生ID：{}, 数量：{}", 
                    studentId, stStudents.getFamilyMembers().size());
                
                if (!stStudents.getFamilyMembers().isEmpty()) {
                    validateFamilyMembers(stStudents.getFamilyMembers());
                }
                
                saveFamilyMembersWithStudentId(studentId, stStudents);
                
                logger.info("[修改学生] 家庭成员更新成功 - 学生ID：{}", studentId);
            }
            
            // 更新银行卡（如果有）
            if (stStudents.getBankCards() != null) {
                logger.info("[修改学生] 开始更新银行卡 - 学生ID：{}, 数量：{}", 
                    studentId, stStudents.getBankCards().size());
                
                if (!stStudents.getBankCards().isEmpty()) {
                    validateBankCards(stStudents.getBankCards());
                }
                
                saveBankCardsWithStudentId(studentId, stStudents);
                
                logger.info("[修改学生] 银行卡更新成功 - 学生ID：{}", studentId);
            }
        } catch (NumberFormatException e) {
            logger.error("[修改学生] 学生ID格式错误 - ID：{}", stStudents.getId(), e);
            throw new RuntimeException("学生ID格式错误");
        } catch (Exception e) {
            logger.error("[修改学生] 更新关联数据失败 - 学生ID：{}", stStudents.getId(), e);
            throw new RuntimeException("更新关联数据失败：" + e.getMessage(), e);
        }
        
        logger.info("[修改学生] 学生信息及关联数据更新完成 - ID：{}", stStudents.getId());
        
        return rows;
    }

    /**
     * 批量删除困难学生基础信息
     * 
     * @param ids 需要删除的困难学生基础信息主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteStStudentsByIds(String[] ids)
    {
        if (ids == null || ids.length == 0) {
            return 0;
        }
        
        // 批量删除限制：最多500条
        if (ids.length > 500) {
            throw new RuntimeException("批量删除数量不能超过500条，当前请求数量: " + ids.length);
        }
        
        // 1. 先清理关联数据（家庭成员和银行卡）
        for (String id : ids) {
            try {
                Long studentId = Long.parseLong(id);
                stFamilyMemberService.deleteByStudentId(studentId);
                stStudentBankCardService.deleteByStudentId(studentId);
            } catch (NumberFormatException e) {
                logger.warn("删除学生关联数据时ID转换失败: {}", id);
            }
        }
        
        // 2. 执行删除学生主记录
        int rows = stStudentsMapper.deleteStStudentsByIds(ids);
        
        return rows;
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
        // 1. 验证参数
        if (ids == null || ids.length == 0)
        {
            return 0;
        }
        
        // 2. 验证是否至少有一个字段要更新
        if (difficultyTypeId == null && difficultyLevelId == null && 
            isPovertyReliefFamily == null && povertyReliefYear == null)
        {
            throw new RuntimeException("至少需要更新一个字段");
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        
        // 3. 只有非空值才加入更新参数
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
        
        // 4. 执行批量更新（事务中执行）
        int rows = stStudentsMapper.batchUpdateDifficultyInfo(params);
        
        return rows;
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

    /**
     * 加密敏感字段
     * 将身份证号和住址进行 AES 加密
     * 
     * @param stStudents 学生信息
     */
    private void encryptSensitiveFields(StStudents stStudents)
    {
        if (stStudents == null)
        {
            return;
        }
        
        // 加密身份证号
        if (StringUtils.isNotBlank(stStudents.getIdCardNo()))
        {
            stStudents.setIdCardNo(EncryptionUtil.encrypt(stStudents.getIdCardNo()));
        }
        
        // 加密住址
        if (StringUtils.isNotBlank(stStudents.getDomicile()))
        {
            stStudents.setDomicile(EncryptionUtil.encrypt(stStudents.getDomicile()));
        }
        
        // 加密手机号码
        if (StringUtils.isNotBlank(stStudents.getPhone()))
        {
            stStudents.setPhone(EncryptionUtil.encrypt(stStudents.getPhone()));
        }
        
        // 加密学籍号
        if (StringUtils.isNotBlank(stStudents.getStudentNo()))
        {
            stStudents.setStudentNo(EncryptionUtil.encrypt(stStudents.getStudentNo()));
        }
    }
    
    /**
     * 解密敏感字段
     * 仅用于尘曦域查询（单条记录查询），每次查询后解密
     * 
     * @param stStudents 学生信息
     */
    private void decryptSensitiveFields(StStudents stStudents)
    {
        if (stStudents == null)
        {
            return;
        }
        
        // 解密身份证号
        if (StringUtils.isNotBlank(stStudents.getIdCardNo()))
        {
            stStudents.setIdCardNo(EncryptionUtil.decrypt(stStudents.getIdCardNo()));
        }
        
        // 解密住址
        if (StringUtils.isNotBlank(stStudents.getDomicile()))
        {
            stStudents.setDomicile(EncryptionUtil.decrypt(stStudents.getDomicile()));
        }
        
        // 解密手机号码
        if (StringUtils.isNotBlank(stStudents.getPhone()))
        {
            stStudents.setPhone(EncryptionUtil.decrypt(stStudents.getPhone()));
        }
        
        // 解密学籍号
        if (StringUtils.isNotBlank(stStudents.getStudentNo()))
        {
            stStudents.setStudentNo(EncryptionUtil.decrypt(stStudents.getStudentNo()));
        }
    }
    
    /**
     * 脱敏敏感字段
     * 用于列表查询等普通用户查看
     * 注意：此方法会先解密，再脱敏
     * 
     * @param stStudents 学生信息
     */
    private void maskSensitiveFields(StStudents stStudents)
    {
        if (stStudents == null)
        {
            return;
        }
        
        // 第一步：解密身份证号、住址、手机号码和学籍号
        String decryptedIdCardNo = null;
        if (StringUtils.isNotBlank(stStudents.getIdCardNo()))
        {
            try {
                decryptedIdCardNo = EncryptionUtil.decrypt(stStudents.getIdCardNo());
            } catch (Exception e) {
                // 解密失败，可能是明文数据，直接使用原值
                decryptedIdCardNo = stStudents.getIdCardNo();
            }
        }
        
        String decryptedDomicile = null;
        if (StringUtils.isNotBlank(stStudents.getDomicile()))
        {
            try {
                decryptedDomicile = EncryptionUtil.decrypt(stStudents.getDomicile());
            } catch (Exception e) {
                // 解密失败，可能是明文数据，直接使用原值
                decryptedDomicile = stStudents.getDomicile();
            }
        }
        
        String decryptedPhone = null;
        if (StringUtils.isNotBlank(stStudents.getPhone()))
        {
            try {
                decryptedPhone = EncryptionUtil.decrypt(stStudents.getPhone());
            } catch (Exception e) {
                // 解密失败，可能是明文数据，直接使用原值
                decryptedPhone = stStudents.getPhone();
            }
        }
        
        String decryptedStudentNo = null;
        if (StringUtils.isNotBlank(stStudents.getStudentNo()))
        {
            try {
                decryptedStudentNo = EncryptionUtil.decrypt(stStudents.getStudentNo());
            } catch (Exception e) {
                // 解密失败，可能是明文数据，直接使用原值
                decryptedStudentNo = stStudents.getStudentNo();
            }
        }
        
        // 第二步：对解密后的数据进行脱敏
        if (StringUtils.isNotBlank(decryptedIdCardNo))
        {
            stStudents.setIdCardNo(SensitiveDataUtil.maskIdCardNo(decryptedIdCardNo));
        }
        
        if (StringUtils.isNotBlank(decryptedDomicile))
        {
            stStudents.setDomicile(SensitiveDataUtil.maskAddress(decryptedDomicile));
        }
        
        // 脱敏联系电话（前3位+中间4位星号+后4位）
        if (StringUtils.isNotBlank(decryptedPhone))
        {
            stStudents.setPhone(SensitiveDataUtil.maskPhone(decryptedPhone));
        }
        
        // 脱敏学籍号
        if (StringUtils.isNotBlank(decryptedStudentNo))
        {
            stStudents.setStudentNo(SensitiveDataUtil.maskStudentNo(decryptedStudentNo));
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

    /**
     * 使用明确的学生ID保存家庭成员（用于新增逻辑）
     * 确保家庭成员必须关联到已存在的学生ID
     */
    private void saveFamilyMembersWithStudentId(Long studentId, StStudents stStudents) {
        if (studentId == null || stStudents == null || stStudents.getFamilyMembers() == null) {
            return;
        }
        
        logger.debug("[保存家庭成员] 学生ID：{}, 学年学期ID：{}, 成员数：{}", 
            studentId, stStudents.getCurrentYearSemesterId(), stStudents.getFamilyMembers().size());
        
        stFamilyMemberService.saveByStudentId(studentId, stStudents.getCurrentYearSemesterId(), stStudents.getFamilyMembers());
    }

    /**
     * 使用明确的学生ID保存银行卡（用于新增逻辑）
     * 确保银行卡必须关联到已存在的学生ID
     */
    private void saveBankCardsWithStudentId(Long studentId, StStudents stStudents) {
        if (studentId == null || stStudents == null || stStudents.getBankCards() == null) {
            return;
        }
        
        logger.debug("[保存银行卡] 学生ID：{}, 卡数：{}", 
            studentId, stStudents.getBankCards().size());
        
        stStudentBankCardService.saveByStudentId(studentId, stStudents.getBankCards());
    }

    /**
     * 验证家庭成员数据的完整性和合法性
     * 必填字段：姓名、关系
     */
    private void validateFamilyMembers(List<StFamilyMember> familyMembers) {
        if (familyMembers == null || familyMembers.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < familyMembers.size(); i++) {
            StFamilyMember member = familyMembers.get(i);
            
            if (StringUtils.isBlank(member.getName())) {
                logger.error("[验证家庭成员] 第{}个成员姓名为空", i + 1);
                throw new RuntimeException("第" + (i + 1) + "个家庭成员的姓名不能为空");
            }
            
            if (StringUtils.isBlank(member.getRelation())) {
                logger.error("[验证家庭成员] 第{}个成员关系为空 - 姓名：{}", i + 1, member.getName());
                throw new RuntimeException("第" + (i + 1) + "个家庭成员的关系不能为空");
            }
            
            // 验证年龄合理性（如果填写）
            if (member.getAge() != null && (member.getAge() < 0 || member.getAge() > 150)) {
                logger.error("[验证家庭成员] 年龄不合理 - 姓名：{}, 年龄：{}", member.getName(), member.getAge());
                throw new RuntimeException("家庭成员\"" + member.getName() + "\"的年龄不合理");
            }
        }
        
        logger.debug("[验证家庭成员] 验证通过 - 数量：{}", familyMembers.size());
    }

    /**
     * 验证银行卡数据的完整性和合法性
     * 必填字段：卡号、开户行、开卡人
     * 卡号格式：19位数字
     * 业务规则：只能有一张主卡，isPrimary=1
     */
    private void validateBankCards(List<StStudentBankCard> bankCards) {
        if (bankCards == null || bankCards.isEmpty()) {
            return;
        }
        
        int primaryCount = 0; // 统计主卡数量
        
        for (int i = 0; i < bankCards.size(); i++) {
            StStudentBankCard card = bankCards.get(i);
            
            if (StringUtils.isBlank(card.getBankAccountNo())) {
                logger.error("[验证银行卡] 第{}张卡的卡号为空", i + 1);
                throw new RuntimeException("第" + (i + 1) + "张银行卡的卡号不能为空");
            }
            
            // 验证卡号格式：19位数字
            if (!card.getBankAccountNo().matches("^\\d{19}$")) {
                logger.error("[验证银行卡] 卡号格式错误 - 卡号：{}", card.getBankAccountNo());
                throw new RuntimeException("银行卡号必须为19位数字");
            }
            
            if (StringUtils.isBlank(card.getBankName())) {
                logger.error("[验证银行卡] 第{}张卡的开户行为空 - 卡号：{}", i + 1, card.getBankAccountNo());
                throw new RuntimeException("第" + (i + 1) + "张银行卡的开户行不能为空");
            }
            
            if (StringUtils.isBlank(card.getAccountHolder())) {
                logger.error("[验证银行卡] 第{}张卡的开卡人为空 - 卡号：{}", i + 1, card.getBankAccountNo());
                throw new RuntimeException("第" + (i + 1) + "张银行卡的开卡人不能为空");
            }
            
            // 如果没有指定银行类型，默认为信用社
            if (StringUtils.isBlank(card.getBankType())) {
                card.setBankType("信用社");
            }
            
            // 统计主卡数量
            if (card.getIsPrimary() != null && card.getIsPrimary() == 1) {
                primaryCount++;
            }
        }
        
        // 验证主卡唯一性：只能有一张主卡
        if (primaryCount > 1) {
            logger.error("[验证银行卡] 主卡数量超过限制 - 当前主卡数：{}", primaryCount);
            throw new RuntimeException("只能设置一张主卡，当前有" + primaryCount + "张主卡");
        }
        
        // 如果没有主卡，自动将第一张设置为主卡
        if (primaryCount == 0 && !bankCards.isEmpty()) {
            logger.info("[验证银行卡] 没有主卡，自动将第一张设置为主卡");
            bankCards.get(0).setIsPrimary(1);
        }
        
        logger.debug("[验证银行卡] 验证通过 - 数量：{}, 主卡数：{}", bankCards.size(), primaryCount == 0 ? 1 : primaryCount);
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

    /**
     * 规范化性别值：兼容历史数据
     * 将性别值 2（女）转换为 0（女）以符合后端验证规则 ^[01]?$
     * 此逻理必须在后端处理，以确保数据一致性和安全性
     * 
     * @param stStudents 学生信息
     */
    private void normalizeGenderValue(StStudents stStudents)
    {
        if (stStudents == null || StringUtils.isBlank(stStudents.getGender()))
        {
            return;
        }
        
        // 将历史数据中的性别值 2 转换为 0
        if ("2".equals(stStudents.getGender()))
        {
            stStudents.setGender("0");
        }
    }

    /**
     * 验证业务数据的合法性
     * 所有数据验证逻辑必须在后端实现，不依赖前端验证
     * 
     * @param stStudents 学生信息
     */
    private void validateBusinessData(StStudents stStudents)
    {
        if (stStudents == null)
        {
            return;
        }
        
        // 验证联系电话格式（非必填）
        if (StringUtils.isNotBlank(stStudents.getPhone()))
        {
            if (!stStudents.getPhone().matches("^1[3-9]\\d{9}$"))
            {
                throw new RuntimeException("联系电话格式不正确，应为11位手机号码");
            }
        }
        
        // 验证出生日期不能大于当前日期
        if (stStudents.getBirthday() != null)
        {
            if (stStudents.getBirthday().after(new java.util.Date()))
            {
                throw new RuntimeException("出生日期不能大于当前日期");
            }
        }
        
        // 验证入学时间不能大于当前日期
        if (stStudents.getEnrollmentDate() != null)
        {
            if (stStudents.getEnrollmentDate().after(new java.util.Date()))
            {
                throw new RuntimeException("入学时间不能大于当前日期");
            }
        }
        
        // 验证最近升级日期不能大于当前日期
        if (stStudents.getLastGradeUpdate() != null)
        {
            if (stStudents.getLastGradeUpdate().after(new java.util.Date()))
            {
                throw new RuntimeException("最近升级日期不能大于当前日期");
            }
        }
    }
}
