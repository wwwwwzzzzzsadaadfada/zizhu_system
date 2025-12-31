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
import com.ruoyi.common.utils.SensitiveDataUtil;
import com.ruoyi.common.utils.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 受助学生信息服务实现类
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
@Service
public class StAidedStudentInfoServiceImpl implements IStAidedStudentInfoService 
{
    private static final Logger logger = LoggerFactory.getLogger(StAidedStudentInfoServiceImpl.class);
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

        List<StAidedStudentInfo> list = stAidedStudentInfoMapper.selectStAidedStudentInfoList(stAidedStudentInfo);
        
        // 对列表数据进行脚敏处理
        if (list != null && !list.isEmpty()) {
            list.forEach(this::maskSensitiveFields);
        }
        
        return list;
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
    
    /**
     * 解密并脱敏敏感字段
     * 用于列表查询等普通用户查看
     * 流程：加密数据 -> 解密 -> 脱敏 -> 返回给前端
     * 
     * @param stAidedStudentInfo 受助学生信息
     */
    private void maskSensitiveFields(StAidedStudentInfo stAidedStudentInfo)
    {
        if (stAidedStudentInfo == null)
        {
            return;
        }
        
        // 步骤1：解密身份证号，然后脱敏
        String decryptedIdCard = null;
        if (StringUtils.isNotBlank(stAidedStudentInfo.getIdCard()))
        {
            try {
                // 先解密
                decryptedIdCard = EncryptionUtil.decrypt(stAidedStudentInfo.getIdCard());
                // 再脱敏（前6位+中间8个*+后4位）
                stAidedStudentInfo.setIdCard(SensitiveDataUtil.maskIdCardNo(decryptedIdCard));
            } catch (Exception e) {
                logger.warn("解密身份证号失败，可能是明文数据，ID={}", stAidedStudentInfo.getId(), e);
                // 如果解密失败（可能是明文），直接脱敏
                decryptedIdCard = stAidedStudentInfo.getIdCard();
                stAidedStudentInfo.setIdCard(SensitiveDataUtil.maskIdCardNo(stAidedStudentInfo.getIdCard()));
            }
        }
        
        // 步骤2：解密联系电话，然后脱敏
        if (StringUtils.isNotBlank(stAidedStudentInfo.getPhone()))
        {
            try {
                // 先解密
                String decryptedPhone = EncryptionUtil.decrypt(stAidedStudentInfo.getPhone());
                // 再脱敏（前3位+中间4个*+后4位）
                stAidedStudentInfo.setPhone(SensitiveDataUtil.maskPhone(decryptedPhone));
            } catch (Exception e) {
                logger.warn("解密电话失败，可能是明文数据，ID={}", stAidedStudentInfo.getId(), e);
                // 如果解密失败（可能是明文），直接脱敏
                stAidedStudentInfo.setPhone(SensitiveDataUtil.maskPhone(stAidedStudentInfo.getPhone()));
            }
        }
        
        // 步骤3：解密住址，然后脱敏
        if (StringUtils.isNotBlank(stAidedStudentInfo.getDomicile()))
        {
            try {
                // 先解密
                String decryptedAddress = EncryptionUtil.decrypt(stAidedStudentInfo.getDomicile());
                // 再脱敏（保留省市县区镇，后续部分替换为**村**屯*号）
                stAidedStudentInfo.setDomicile(SensitiveDataUtil.maskAddress(decryptedAddress));
            } catch (Exception e) {
                logger.warn("解密住址失败，可能是明文数据，ID={}", stAidedStudentInfo.getId(), e);
                // 如果解密失败（可能是明文），直接脱敏
                stAidedStudentInfo.setDomicile(SensitiveDataUtil.maskAddress(stAidedStudentInfo.getDomicile()));
            }
        }
        
        // 步骤4：解密学籍号，然后脱敏
        if (StringUtils.isNotBlank(stAidedStudentInfo.getStudentNo()))
        {
            try {
                // 先解密
                String decryptedStudentNo = EncryptionUtil.decrypt(stAidedStudentInfo.getStudentNo());
                
                // 如果学籍号以G开头，后跟身份证号，则使用已脱敏的身份证号重新生成
                if (decryptedStudentNo.startsWith("G") && StringUtils.isNotBlank(decryptedIdCard))
                {
                    // 使用脱敏后的身份证号
                    stAidedStudentInfo.setStudentNo("G" + SensitiveDataUtil.maskIdCardNo(decryptedIdCard));
                }
                else
                {
                    // 直接脱敏学籍号（前3位+中间*+后4位）
                    if (decryptedStudentNo.length() > 7)
                    {
                        String masked = decryptedStudentNo.substring(0, 3) + 
                                        "****" + 
                                        decryptedStudentNo.substring(decryptedStudentNo.length() - 4);
                        stAidedStudentInfo.setStudentNo(masked);
                    }
                    else
                    {
                        stAidedStudentInfo.setStudentNo("****");
                    }
                }
            } catch (Exception e) {
                logger.warn("解密学籍号失败，可能是明文数据，ID={}", stAidedStudentInfo.getId(), e);
                // 如果解密失败（可能是明文），直接脱敏
                String studentNo = stAidedStudentInfo.getStudentNo();
                if (studentNo.startsWith("G") && StringUtils.isNotBlank(stAidedStudentInfo.getIdCard()))
                {
                    stAidedStudentInfo.setStudentNo("G" + stAidedStudentInfo.getIdCard());
                }
                else if (studentNo.length() > 7)
                {
                    String masked = studentNo.substring(0, 3) + 
                                    "****" + 
                                    studentNo.substring(studentNo.length() - 4);
                    stAidedStudentInfo.setStudentNo(masked);
                }
            }
        }
    }
}