package com.ruoyi.system.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.EncryptionUtil;
import com.ruoyi.common.utils.SensitiveDataUtil;
import com.ruoyi.common.core.domain.entity.SysDictData;
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
        List<StStudents> list = stStudentsMapper.selectStStudentsList(stStudents);
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
    
    @Override
    public List<StStudents> selectStStudentsListForExport(StStudents stStudents)
    {
        List<StStudents> list = stStudentsMapper.selectStStudentsList(stStudents);
        if (list != null) {
            for (StStudents student : list) {
                String semesterLabel = com.ruoyi.common.utils.SemesterUtils.getSemesterLabel(student.getCurrentSemester());
                student.setCurrentSemesterLabel(semesterLabel);
                // 导出时解密敏感字段，但不解密后脱敏
                decryptSensitiveFieldsForExport(student);
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
        
        // ======== 第二步：数据转换和规范化（先执行，以便自动填充学籍号等字段） ========
        // 处理数据转换：学年学期映射、脱贫年份转换、地址拼接、学籍号自动填充
        normalizeDataFields(stStudents);
        normalizeNullableFields(stStudents);
        normalizeGenderValue(stStudents);
        
        // 数据规范化后，验证学籍号是否已自动填充
        if (StringUtils.isBlank(stStudents.getStudentNo())) {
            logger.error("[新增学生] 参数验证失败：学籍号不能为空（自动填充失败）");
            throw new RuntimeException("学籍号不能为空，请检查身份证号是否正确");
        }
        
        // ======== 第三步：唯一性检查 ========
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
        
        // ======== 第四步：业务数据验证 ========
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
        
        // ======== 第四步：数据转换和规范化 ========
        // 处理数据转换：学年学期映射、脱贫年份转换、地址拼接
        // 如果身份证号变化，自动更新学籍号为 "G" + 新身份证号（业务规则：学籍号 = "G" + 身份证号）
        if (idCardChanged && StringUtils.isNotBlank(stStudents.getIdCardNo())) {
            String oldIdCardNo = existing.getIdCardNo();
            String newIdCardNo = stStudents.getIdCardNo();
            
            // 获取旧学籍号（用于日志记录）
            String oldStudentNo = existing.getStudentNo();
            String decryptedOldStudentNo = oldStudentNo;
            try {
                decryptedOldStudentNo = EncryptionUtil.decrypt(oldStudentNo);
            } catch (Exception e) {
                // 解密失败，可能是明文，直接使用
                decryptedOldStudentNo = oldStudentNo;
            }
            
            // 业务规则：学籍号 = "G" + 身份证号
            // 当身份证号变化时，自动更新学籍号为 "G" + 新身份证号
            stStudents.setStudentNo("G" + newIdCardNo);
            logger.info("[修改学生] 身份证号变化，自动更新学籍号：旧身份证号={}，新身份证号={}，旧学籍号={}，新学籍号=G{}", 
                oldIdCardNo, newIdCardNo, decryptedOldStudentNo, newIdCardNo);
        }
        
        normalizeDataFields(stStudents);
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
     * 构建学制-年级-班级的树状结构（用于前端级联选择器）
     * 
     * @return 学制-年级-班级树状结构
     */
    @Override
    public List<Map<String, Object>> buildSchoolPlanGradeClassTree()
    {
        List<Map<String, Object>> tree = new java.util.ArrayList<>();
        
        try
        {
            // 1. 获取所有学制
            List<Map<String, Object>> planList = stStudentsMapper.selectSchoolPlanList();
            if (planList == null || planList.isEmpty())
            {
                logger.warn("[构建学制-年级-班级树] 学制列表为空");
                return tree;
            }
            
            // 2. 遍历每个学制，构建树结构
            for (Map<String, Object> plan : planList)
            {
                Object planIdObj = plan.get("id");
                if (planIdObj == null)
                {
                    continue;
                }
                
                Long planId = null;
                if (planIdObj instanceof Number)
                {
                    planId = ((Number) planIdObj).longValue();
                }
                else
                {
                    try
                    {
                        planId = Long.parseLong(planIdObj.toString());
                    }
                    catch (NumberFormatException e)
                    {
                        logger.warn("[构建学制-年级-班级树] 学制ID格式错误：{}", planIdObj);
                        continue;
                    }
                }
                
                // 创建学制节点
                Map<String, Object> planNode = new java.util.HashMap<>();
                planNode.put("value", planId);
                planNode.put("label", plan.get("name") != null ? plan.get("name").toString() : "未知学制");
                List<Map<String, Object>> gradeChildren = new java.util.ArrayList<>();
                
                // 3. 获取该学制下的所有年级
                List<Map<String, Object>> gradeList = stStudentsMapper.selectGradeListByPlanId(planId.toString());
                if (gradeList != null && !gradeList.isEmpty())
                {
                    for (Map<String, Object> grade : gradeList)
                    {
                        Object gradeIdObj = grade.get("id");
                        if (gradeIdObj == null)
                        {
                            continue;
                        }
                        
                        Long gradeId = null;
                        if (gradeIdObj instanceof Number)
                        {
                            gradeId = ((Number) gradeIdObj).longValue();
                        }
                        else
                        {
                            try
                            {
                                gradeId = Long.parseLong(gradeIdObj.toString());
                            }
                            catch (NumberFormatException e)
                            {
                                logger.warn("[构建学制-年级-班级树] 年级ID格式错误：{}", gradeIdObj);
                                continue;
                            }
                        }
                        
                        // 创建年级节点
                        Map<String, Object> gradeNode = new java.util.HashMap<>();
                        gradeNode.put("value", gradeId);
                        gradeNode.put("label", grade.get("name") != null ? grade.get("name").toString() : "未知年级");
                        List<Map<String, Object>> classChildren = new java.util.ArrayList<>();
                        
                        // 4. 获取该年级下的所有班级
                        List<Map<String, Object>> classList = stStudentsMapper.selectClassListByGradeId(gradeId.toString());
                        if (classList != null && !classList.isEmpty())
                        {
                            for (Map<String, Object> clazz : classList)
                            {
                                Object classIdObj = clazz.get("classId");
                                if (classIdObj == null)
                                {
                                    continue;
                                }
                                
                                Long classId = null;
                                if (classIdObj instanceof Number)
                                {
                                    classId = ((Number) classIdObj).longValue();
                                }
                                else
                                {
                                    try
                                    {
                                        classId = Long.parseLong(classIdObj.toString());
                                    }
                                    catch (NumberFormatException e)
                                    {
                                        logger.warn("[构建学制-年级-班级树] 班级ID格式错误：{}", classIdObj);
                                        continue;
                                    }
                                }
                                
                                // 创建班级节点
                                Map<String, Object> classNode = new java.util.HashMap<>();
                                classNode.put("value", classId);
                                classNode.put("label", clazz.get("className") != null ? clazz.get("className").toString() : "未知班级");
                                classChildren.add(classNode);
                            }
                        }
                        
                        // 如果没有班级，至少添加一个默认班级（1班）
                        if (classChildren.isEmpty())
                        {
                            Map<String, Object> defaultClassNode = new java.util.HashMap<>();
                            defaultClassNode.put("value", gradeId * 1000L + 1L); // 使用年级ID*1000+1作为默认班级ID
                            defaultClassNode.put("label", "1班");
                            classChildren.add(defaultClassNode);
                        }
                        
                        // 如果没有班级，不添加默认班级（由前端处理默认选择）
                        // 前端可以根据需要显示"1班"作为默认选项
                        gradeNode.put("children", classChildren);
                        gradeChildren.add(gradeNode);
                    }
                }
                
                planNode.put("children", gradeChildren);
                tree.add(planNode);
            }
            
            logger.info("[构建学制-年级-班级树] 构建完成，共 {} 个学制节点", tree.size());
            return tree;
        }
        catch (Exception e)
        {
            logger.error("[构建学制-年级-班级树] 构建失败", e);
            throw new RuntimeException("构建学制-年级-班级树失败：" + e.getMessage(), e);
        }
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

    /**
     * 数据字段规范化处理
     * 包括：学年学期字段映射、脱贫年份类型转换、户籍地址拼接
     * 
     * @param stStudents 学生信息
     */
    private void normalizeDataFields(StStudents stStudents)
    {
        if (stStudents == null)
        {
            return;
        }
        
        // 1. 处理学年学期字段映射：yearSemesterId -> currentYearSemesterId
        if (stStudents.getYearSemesterId() != null)
        {
            stStudents.setCurrentYearSemesterId(stStudents.getYearSemesterId());
            logger.debug("[数据规范化] 学年学期字段映射：yearSemesterId={} -> currentYearSemesterId={}", 
                stStudents.getYearSemesterId(), stStudents.getCurrentYearSemesterId());
        }
        else if (stStudents.getCurrentYearSemesterId() == null)
        {
            // 如果两个字段都为空，记录警告但不抛出异常（可能后续会设置）
            logger.warn("[数据规范化] 学年学期ID为空，可能导致数据不完整");
        }
        
        // 2. 处理脱贫年份类型转换和验证
        // 注意：Jackson会自动将字符串转换为Integer，但如果转换失败或值为空字符串，需要处理
        Integer povertyReliefYear = stStudents.getPovertyReliefYear();
        if (povertyReliefYear != null)
        {
            // 验证年份范围（1990-2099）
            if (povertyReliefYear < 1990 || povertyReliefYear > 2099)
            {
                logger.warn("[数据规范化] 脱贫年份超出范围：{}，将设置为null", povertyReliefYear);
                stStudents.setPovertyReliefYear(null);
            }
            else
            {
                logger.debug("[数据规范化] 脱贫年份验证通过：{}", povertyReliefYear);
            }
        }
        // 如果为null，保持不变（允许为空）
        
        // 2.5. 处理学籍号自动填充和格式修复
        // 策略：
        // 1. 如果学号为空，且身份证号有值，则学号 = "G" + 身份证号
        // 2. 如果学号不为空，但以多个"G"开头（如"GG..."），则修复为单个"G"开头
        // 3. 如果学号不为空，且不以"G"开头，且身份证号有值，则学号 = "G" + 身份证号（如果学号不等于身份证号）
        if (StringUtils.isNotBlank(stStudents.getIdCardNo()))
        {
            String idCardNo = stStudents.getIdCardNo();
            String studentNo = stStudents.getStudentNo();
            
            if (StringUtils.isBlank(studentNo))
            {
                // 学号为空，使用 "G" + 身份证号
                stStudents.setStudentNo("G" + idCardNo);
                logger.debug("[数据规范化] 学籍号自动填充：身份证号={} -> 学籍号=G{}", 
                    idCardNo, idCardNo);
            }
            else
            {
                // 学号不为空，检查并修复格式
                // 修复多个"G"开头的问题（如"GG..." -> "G..."）
                if (studentNo.startsWith("GG"))
                {
                    // 去掉多余的"G"，保留一个"G"
                    String fixedStudentNo = "G" + studentNo.substring(2);
                    stStudents.setStudentNo(fixedStudentNo);
                    logger.warn("[数据规范化] 学籍号格式修复：检测到多个G前缀，已修复 {} -> {}", 
                        studentNo, fixedStudentNo);
                }
                // 如果学号以单个"G"开头，但后面不是身份证号，可能需要修复
                else if (studentNo.startsWith("G") && studentNo.length() > 1)
                {
                    String idCardPart = studentNo.substring(1);
                    // 如果"G"后面的部分与身份证号不匹配，且学号长度与身份证号长度一致，可能需要修复
                    // 这里只记录日志，不强制修改（因为可能学号是手动输入的）
                    if (!idCardPart.equals(idCardNo) && idCardPart.length() == idCardNo.length())
                    {
                        logger.debug("[数据规范化] 学籍号格式检查：学号={}，身份证号={}，格式正确但内容可能不匹配", 
                            studentNo, idCardNo);
                    }
                }
                // 如果学号不以"G"开头，且等于身份证号，则添加"G"前缀
                else if (!studentNo.startsWith("G") && studentNo.equals(idCardNo))
                {
                    stStudents.setStudentNo("G" + idCardNo);
                    logger.debug("[数据规范化] 学籍号格式修复：添加G前缀 {} -> G{}", 
                        studentNo, idCardNo);
                }
            }
        }
        
        // 3. 处理户籍地址拼接：regionCodes + village + hamlet -> domicile
        // 策略说明：
        // - 优先使用 regionCodes 构建完整地址（如果前端传了 regionCodes）
        // - 如果前端同时传了 village 或 hamlet，追加到地址末尾
        // - 如果前端没有传 regionCodes，使用已有的 domicile（前端已完整拼接）
        if (stStudents.getRegionCodes() != null && !stStudents.getRegionCodes().isEmpty())
        {
            // 使用地区代码工具类构建完整地址
            String fullAddress = com.ruoyi.common.utils.RegionCodeUtils.buildFullAddress(
                stStudents.getRegionCodes(),
                stStudents.getVillage(),
                stStudents.getHamlet()
            );
            
            if (StringUtils.isNotBlank(fullAddress))
            {
                stStudents.setDomicile(fullAddress);
                logger.debug("[数据规范化] 户籍地址拼接完成：regionCodes={}, village={}, hamlet={} -> domicile={}", 
                    stStudents.getRegionCodes(), stStudents.getVillage(), stStudents.getHamlet(), fullAddress);
            }
            else
            {
                // 如果工具类无法构建地址，尝试使用已有 domicile + village + hamlet
                String baseAddress = stStudents.getDomicile();
                if (StringUtils.isBlank(baseAddress))
                {
                    baseAddress = "广西壮族自治区";
                    logger.warn("[数据规范化] 无法根据regionCodes构建地址，使用默认前缀，regionCodes={}", 
                        stStudents.getRegionCodes());
                }
                
                // 拼接完整地址
                StringBuilder addressBuilder = new StringBuilder(baseAddress);
                if (StringUtils.isNotBlank(stStudents.getVillage()))
                {
                    addressBuilder.append(stStudents.getVillage());
                }
                if (StringUtils.isNotBlank(stStudents.getHamlet()))
                {
                    addressBuilder.append(stStudents.getHamlet());
                }
                
                stStudents.setDomicile(addressBuilder.toString());
                logger.debug("[数据规范化] 户籍地址拼接完成（备用方案）：baseAddress={}, village={}, hamlet={} -> domicile={}", 
                    baseAddress, stStudents.getVillage(), stStudents.getHamlet(), addressBuilder.toString());
            }
        }
        // 如果没有regionCodes，使用已有的domicile（前端已经完整拼接好）
        else if (StringUtils.isBlank(stStudents.getDomicile()))
        {
            // 如果既没有regionCodes也没有domicile，记录警告
            logger.warn("[数据规范化] 户籍地址为空，可能导致数据不完整");
        }
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
        
        // 设置就读状态默认值为"在校"（字典值：1）
        if (StringUtils.isBlank(stStudents.getStudyStatus()))
        {
            stStudents.setStudyStatus("1");
            logger.debug("[数据规范化] 就读状态默认值设置：studyStatus=1（在校）");
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
        
        // 验证学制-年级-班级必须完整
        if (stStudents.getSchoolingPlanId() == null || stStudents.getGradeId() == null || stStudents.getClassId() == null)
        {
            logger.error("[业务数据验证] 学制-年级-班级信息不完整：schoolingPlanId={}, gradeId={}, classId={}", 
                stStudents.getSchoolingPlanId(), stStudents.getGradeId(), stStudents.getClassId());
            throw new RuntimeException("请完整选择学制-年级-班级");
        }
        
        // 验证就读状态，如果为空则设置为默认值"在校"（已在normalizeNullableFields中处理，这里只做验证）
        if (StringUtils.isBlank(stStudents.getStudyStatus()))
        {
            logger.warn("[业务数据验证] 就读状态为空，将使用默认值'在校'");
            // 已在normalizeNullableFields中设置默认值，这里只记录日志
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

    /**
     * 导入学生数据
     * 
     * @param studentsList 学生数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importStudents(List<StStudents> studentsList, Boolean isUpdateSupport, String operName)
    {
        if (studentsList == null || studentsList.isEmpty())
        {
            throw new RuntimeException("导入学生数据不能为空！");
        }
        
        // 检查导入数据中身份证号是否重复
        Set<String> idCardNos = new HashSet<>();
        Set<String> duplicateIdCards = new HashSet<>();
        for (StStudents student : studentsList) {
            if (student.getIdCardNo() != null && !student.getIdCardNo().trim().isEmpty()) {
                String idCardNo = student.getIdCardNo().trim();
                if (!idCardNos.add(idCardNo)) {
                    duplicateIdCards.add(idCardNo);
                }
            }
        }
        
        if (!duplicateIdCards.isEmpty()) {
            StringBuilder errorMsg = new StringBuilder("导入失败，以下身份证号在导入数据中重复：");
            for (String idCard : duplicateIdCards) {
                errorMsg.append("<br/>" + idCard);
            }
            throw new RuntimeException(errorMsg.toString());
        }
        
        // 预处理：将名称转换为ID
        for (StStudents student : studentsList) {
            convertNameToId(student);
        }
        
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        
        for (StStudents student : studentsList)
        {
            try
            {
                // 1. 数据预处理和验证
                if (StringUtils.isBlank(student.getName()))
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、学生姓名不能为空");
                    continue;
                }
                
                if (StringUtils.isBlank(student.getIdCardNo()))
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、学生 " + student.getName() + " 的身份证号不能为空");
                    continue;
                }
                
                if (StringUtils.isBlank(student.getStudentNo()))
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、学生 " + student.getName() + " 的学籍号不能为空");
                    continue;
                }
                
                // 2. 检查学生是否已存在（通过身份证号或学籍号）
                StStudents existingStudent = stStudentsMapper.selectByIdCardNoOrStudentNo(
                    student.getIdCardNo(), 
                    student.getStudentNo()
                );
                
                if (existingStudent == null)
                {
                    // 3. 新增学生
                    // 数据规范化
                    normalizeDataFields(student);
                    normalizeNullableFields(student);
                    normalizeGenderValue(student);
                    validateBusinessData(student);
                    
                    // 加密敏感字段
                    encryptSensitiveFields(student);
                    
                    // 设置创建人
                    student.setCreateBy(operName);
                    
                    // 执行插入
                    int rows = stStudentsMapper.insertStStudents(student);
                    
                    if (rows > 0)
                    {
                        // 如果新增成功，处理关联数据
                        try {
                            Long studentId = Long.valueOf(student.getId());
                            
                            // 保存家庭成员（如果有）
                            if (student.getFamilyMembers() != null && !student.getFamilyMembers().isEmpty()) {
                                logger.info("[导入] 开始保存家庭成员 - 学生ID：{}, 数量：{}", 
                                    studentId, student.getFamilyMembers().size());
                                
                                // 验证家庭成员数据完整性
                                validateFamilyMembers(student.getFamilyMembers());
                                
                                saveFamilyMembersWithStudentId(studentId, student);
                                
                                logger.info("[导入] 家庭成员保存成功 - 学生ID：{}", studentId);
                            }
                            
                            // 保存银行卡（如果有）
                            if (student.getBankCards() != null && !student.getBankCards().isEmpty()) {
                                logger.info("[导入] 开始保存银行卡 - 学生ID：{}, 数量：{}", 
                                    studentId, student.getBankCards().size());
                                
                                // 验证银行卡数据完整性
                                validateBankCards(student.getBankCards());
                                
                                saveBankCardsWithStudentId(studentId, student);
                                
                                logger.info("[导入] 银行卡保存成功 - 学生ID：{}", studentId);
                            }
                        } catch (NumberFormatException e) {
                            logger.error("[导入] 学生ID格式错误 - ID：{}", student.getId(), e);
                            throw new RuntimeException("学生ID格式错误，无法保存关联信息");
                        } catch (Exception e) {
                            logger.error("[导入] 保存关联数据失败 - 学生ID：{}", student.getId(), e);
                            throw new RuntimeException("保存关联数据失败：" + e.getMessage(), e);
                        }
                        
                        successNum++;
                        successMsg.append("<br/>" + successNum + "、学生 " + student.getName() + " 导入成功");
                    }
                    else
                    {
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、学生 " + student.getName() + " 导入失败");
                    }
                }
                else if (isUpdateSupport)
                {
                    // 4. 更新学生
                    // 使用已存在学生的ID
                    student.setId(existingStudent.getId());
                    
                    // 数据规范化
                    normalizeDataFields(student);
                    normalizeNullableFields(student);
                    normalizeGenderValue(student);
                    validateBusinessData(student);
                    
                    // 加密敏感字段
                    encryptSensitiveFields(student);
                    
                    // 设置更新人
                    student.setUpdateBy(operName);
                    
                    // 执行更新
                    int rows = stStudentsMapper.updateStStudents(student);
                    
                    if (rows > 0)
                    {
                        // 如果更新成功，处理关联数据
                        try {
                            Long studentId = Long.valueOf(student.getId());
                            
                            // 更新家庭成员（如果有）
                            if (student.getFamilyMembers() != null) {
                                logger.info("[导入] 开始更新家庭成员 - 学生ID：{}, 数量：{}", 
                                    studentId, student.getFamilyMembers().size());
                                
                                if (!student.getFamilyMembers().isEmpty()) {
                                    validateFamilyMembers(student.getFamilyMembers());
                                }
                                
                                saveFamilyMembersWithStudentId(studentId, student);
                                
                                logger.info("[导入] 家庭成员更新成功 - 学生ID：{}", studentId);
                            }
                            
                            // 更新银行卡（如果有）
                            if (student.getBankCards() != null) {
                                logger.info("[导入] 开始更新银行卡 - 学生ID：{}, 数量：{}", 
                                    studentId, student.getBankCards().size());
                                
                                if (!student.getBankCards().isEmpty()) {
                                    validateBankCards(student.getBankCards());
                                }
                                
                                saveBankCardsWithStudentId(studentId, student);
                                
                                logger.info("[导入] 银行卡更新成功 - 学生ID：{}", studentId);
                            }
                        } catch (NumberFormatException e) {
                            logger.error("[导入] 学生ID格式错误 - ID：{}", student.getId(), e);
                            throw new RuntimeException("学生ID格式错误");
                        } catch (Exception e) {
                            logger.error("[导入] 更新关联数据失败 - 学生ID：{}", student.getId(), e);
                            throw new RuntimeException("更新关联数据失败：" + e.getMessage(), e);
                        }
                        
                        successNum++;
                        successMsg.append("<br/>" + successNum + "、学生 " + student.getName() + " 更新成功");
                    }
                    else
                    {
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、学生 " + student.getName() + " 更新失败");
                    }
                }
                else
                {
                    // 5. 学生已存在且不允许更新
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、学生 " + student.getName() + " 已存在（身份证号或学籍号重复）");
                }
            }
            catch (DataIntegrityViolationException e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、学生 " + student.getName() + " 导入失败：";
                // 检查是否是身份证号重复的错误
                String errorMsg = e.getMostSpecificCause().getMessage();
                if (errorMsg.contains("uk_id_card") || (errorMsg.contains("Duplicate entry") && errorMsg.contains("for key"))) {
                    failureMsg.append(msg + "该学生的身份证号已存在，请检查是否重复导入");
                } else if (errorMsg.contains("uk_student_no") || errorMsg.contains("student_no")) {
                    failureMsg.append(msg + "该学生的学籍号已存在，请检查是否重复导入");
                } else {
                    failureMsg.append(msg + "数据重复或已存在");
                }
                logger.error(msg, e);
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、学生 " + student.getName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                logger.error(msg, e);
            }
        }
        
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new RuntimeException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        
        return successMsg.toString();
    }
    
    /**
     * 将学生对象中的名称字段转换为对应的ID
     * 同时确保ID字段的类型正确（Long类型）
     * 
     * @param student 学生对象
     */
    private void convertNameToId(StStudents student) {
        // 1. 首先确保ID字段的类型正确（如果是字符串，转换为Long）
        // 处理学制ID
        if (student.getSchoolingPlanId() != null) {
            try {
                Long planId = convertToLong(student.getSchoolingPlanId());
                student.setSchoolingPlanId(planId);
            } catch (Exception e) {
                logger.warn("[导入] 学制ID格式错误，将尝试通过名称转换：{}", student.getSchoolingPlanId());
                student.setSchoolingPlanId(null);
            }
        }
        
        // 处理年级ID
        if (student.getGradeId() != null) {
            try {
                Long gradeId = convertToLong(student.getGradeId());
                student.setGradeId(gradeId);
            } catch (Exception e) {
                logger.warn("[导入] 年级ID格式错误，将尝试通过名称转换：{}", student.getGradeId());
                student.setGradeId(null);
            }
        }
        
        // 处理班级ID
        if (student.getClassId() != null) {
            try {
                Long classId = convertToLong(student.getClassId());
                student.setClassId(classId);
            } catch (Exception e) {
                logger.warn("[导入] 班级ID格式错误，将尝试通过名称转换：{}", student.getClassId());
                student.setClassId(null);
            }
        }
        
        // 2. 如果ID字段是0或null，且名称字段有值，则使用名称字段进行转换
        if ((student.getSchoolingPlanId() == null || student.getSchoolingPlanId() == 0) && student.getSchoolingPlanName() != null && !student.getSchoolingPlanName().trim().isEmpty()) {
            Long planId = getBusinessIdByName(student.getSchoolingPlanName().trim(), "st_schooling_plans", "name", "id");
            if (planId != null) {
                student.setSchoolingPlanId(planId);
                logger.debug("[导入] 通过学制名称转换为ID：{} -> {}", student.getSchoolingPlanName(), planId);
            } else {
                logger.warn("[导入] 未找到学制名称对应的ID：{}", student.getSchoolingPlanName());
            }
        }
        
        if ((student.getGradeId() == null || student.getGradeId() == 0) && student.getGradeName() != null && !student.getGradeName().trim().isEmpty()) {
            Long gradeId = getGradeIdByName(student.getGradeName().trim());
            if (gradeId != null) {
                student.setGradeId(gradeId);
                logger.debug("[导入] 通过年级名称转换为ID：{} -> {}", student.getGradeName(), gradeId);
            } else {
                logger.warn("[导入] 未找到年级名称对应的ID：{}", student.getGradeName());
            }
        }
        
        if ((student.getClassId() == null || student.getClassId() == 0) && student.getClassName() != null && !student.getClassName().trim().isEmpty()) {
            Long classId = getClassIdByName(student.getClassName().trim());
            if (classId != null) {
                student.setClassId(classId);
                logger.debug("[导入] 通过班级名称转换为ID：{} -> {}", student.getClassName(), classId);
            } else {
                logger.warn("[导入] 未找到班级名称对应的ID：{}", student.getClassName());
            }
        }
        
        // 3. 记录最终的ID值（用于调试）
        logger.debug("[导入] 学制-年级-班级ID转换结果：schoolingPlanId={}, gradeId={}, classId={}", 
            student.getSchoolingPlanId(), student.getGradeId(), student.getClassId());
    }
    
    /**
     * 将对象转换为Long类型
     * 支持Number、String等类型
     * 
     * @param value 待转换的值
     * @return Long类型的值
     * @throws NumberFormatException 如果无法转换
     */
    private Long convertToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            String str = ((String) value).trim();
            if (str.isEmpty()) {
                return null;
            }
            return Long.parseLong(str);
        }
        return Long.parseLong(value.toString());
    }
    
    /**
     * 根据名称获取业务表中的ID
     * 
     * @param name 名称
     * @param tableName 表名
     * @param nameColumn 名称列名
     * @param idColumn ID列名
     * @return ID值
     */
    private Long getBusinessIdByName(String name, String tableName, String nameColumn, String idColumn) {
        // 通过Mapper查询业务表获取ID
        // 由于Mapper中没有直接的方法，我们使用已有的方法来获取数据
        if ("st_schooling_plans".equals(tableName)) {
            List<Map<String, Object>> planList = stStudentsMapper.selectSchoolPlanList();
            for (Map<String, Object> plan : planList) {
                if (name.equals(plan.get("name"))) {
                    Object id = plan.get("id");
                    if (id != null) {
                        try {
                            return Long.valueOf(id.toString());
                        } catch (NumberFormatException e) {
                            logger.warn("学制ID格式错误: name={}, id={}", name, id);
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * 根据年级名称获取年级ID
     * 
     * @param name 年级名称
     * @return 年级ID
     */
    private Long getGradeIdByName(String name) {
        // 从数据库中查询所有年级信息
        // 注意：由于没有直接的查询方法，我们需要获取所有数据并查找匹配项
        // 这里可以考虑优化，但目前先实现功能
        
        // 从st_grades表中查找
        // 我们需要一个查询所有年级的方法，但当前mapper中没有
        // 临时方案：先获取已有的查询方法返回的数据，如果需要更完整的实现，
        // 可能需要在mapper中添加新的方法
        
        // 由于当前mapper中没有直接查询年级/班级名称的方法，
        // 我们暂时使用一种简化的方法，基于现有的查询方法
        // 实际应用中可能需要在mapper中添加专门的查询方法
        
        // 为简化实现，我们直接从数据库中查询
        // 但目前mapper中没有对应的方法，需要使用其他方式
        
        // 这里我们先通过StStudentsMapper的关联查询来获取
        // 但更好的方法是创建新的Mapper方法
        
        // 为了实现功能，我们先使用现有的方法获取所有可能的值
        List<Map<String, Object>> planList = stStudentsMapper.selectSchoolPlanList();
        for (Map<String, Object> plan : planList) {
            // 尝试通过关联查询获取年级数据
            Long planId = Long.valueOf(plan.get("id").toString());
            List<Map<String, Object>> gradeList = stStudentsMapper.selectGradeListByPlanId(planId.toString());
            for (Map<String, Object> grade : gradeList) {
                if (name.equals(grade.get("name"))) {
                    Object id = grade.get("id");
                    if (id != null) {
                        try {
                            return Long.valueOf(id.toString());
                        } catch (NumberFormatException e) {
                            logger.warn("年级ID格式错误: name={}, id={}", name, id);
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * 根据班级名称获取班级ID
     * 
     * @param name 班级名称
     * @return 班级ID
     */
    private Long getClassIdByName(String name) {
        // 类似地，查询班级表
        // 通过已有的年级信息来查询班级
        List<Map<String, Object>> planList = stStudentsMapper.selectSchoolPlanList();
        for (Map<String, Object> plan : planList) {
            Long planId = Long.valueOf(plan.get("id").toString());
            List<Map<String, Object>> gradeList = stStudentsMapper.selectGradeListByPlanId(planId.toString());
            for (Map<String, Object> grade : gradeList) {
                Long gradeId = Long.valueOf(grade.get("id").toString());
                List<Map<String, Object>> classList = stStudentsMapper.selectClassListByGradeId(gradeId.toString());
                for (Map<String, Object> clazz : classList) {
                    if (name.equals(clazz.get("className"))) {
                        Object id = clazz.get("classId");
                        if (id != null) {
                            try {
                                return Long.valueOf(id.toString());
                            } catch (NumberFormatException e) {
                                logger.warn("班级ID格式错误: name={}, id={}", name, id);
                                return null;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * 解密敏感字段（用于导出，不解密后脱敏）
     * 
     * @param stStudents 学生信息
     */
    private void decryptSensitiveFieldsForExport(StStudents stStudents)
    {
        if (stStudents == null)
        {
            return;
        }
        
        // 解密身份证号
        if (StringUtils.isNotBlank(stStudents.getIdCardNo()))
        {
            try {
                stStudents.setIdCardNo(EncryptionUtil.decrypt(stStudents.getIdCardNo()));
            } catch (Exception e) {
                // 解密失败，可能是明文数据，保持原值
            }
        }
        
        // 解密住址
        if (StringUtils.isNotBlank(stStudents.getDomicile()))
        {
            try {
                stStudents.setDomicile(EncryptionUtil.decrypt(stStudents.getDomicile()));
            } catch (Exception e) {
                // 解密失败，可能是明文数据，保持原值
            }
        }
        
        // 解密手机号码
        if (StringUtils.isNotBlank(stStudents.getPhone()))
        {
            try {
                stStudents.setPhone(EncryptionUtil.decrypt(stStudents.getPhone()));
            } catch (Exception e) {
                // 解密失败，可能是明文数据，保持原值
            }
        }
        
        // 解密学籍号
        if (StringUtils.isNotBlank(stStudents.getStudentNo()))
        {
            try {
                stStudents.setStudentNo(EncryptionUtil.decrypt(stStudents.getStudentNo()));
            } catch (Exception e) {
                // 解密失败，可能是明文数据，保持原值
            }
        }
    }
    

}