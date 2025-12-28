package com.ruoyi.system.service;

import java.util.Date;
import java.util.List;
import com.ruoyi.system.domain.StStudentSubsidyRecord;
import com.ruoyi.system.domain.dto.BatchPaymentRequest;
import com.ruoyi.system.domain.dto.PaymentImportDTO;
import com.ruoyi.system.domain.vo.BatchPaymentResultVO;
import com.ruoyi.system.domain.vo.PaymentImportResultVO;

/**
 * 学生补助发放记录Service接口
 * 
 * @author ruoyi
 * @date 2024-11-21
 */
public interface IStStudentSubsidyRecordService 
{
    /**
     * 查询学生补助发放记录
     * 
     * @param id 学生补助发放记录主键
     * @return 学生补助发放记录
     */
    public StStudentSubsidyRecord selectStStudentSubsidyRecordById(Long id);

    /**
     * 查询学生补助发放记录列表
     * 
     * @param stStudentSubsidyRecord 学生补助发放记录
     * @return 学生补助发放记录集合
     */
    public List<StStudentSubsidyRecord> selectStStudentSubsidyRecordList(StStudentSubsidyRecord stStudentSubsidyRecord);

    /**
     * 新增学生补助发放记录
     * 
     * @param stStudentSubsidyRecord 学生补助发放记录
     * @return 结果
     */
    public int insertStStudentSubsidyRecord(StStudentSubsidyRecord stStudentSubsidyRecord);

    /**
     * 修改学生补助发放记录
     * 
     * @param stStudentSubsidyRecord 学生补助发放记录
     * @return 结果
     */
    public int updateStStudentSubsidyRecord(StStudentSubsidyRecord stStudentSubsidyRecord);

    /**
     * 批量删除学生补助发放记录
     * 
     * @param ids 需要删除的学生补助发放记录主键集合
     * @return 结果
     */
    public int deleteStStudentSubsidyRecordByIds(Long[] ids);

    /**
     * 删除学生补助发放记录信息
     * 
     * @param id 学生补助发放记录主键
     * @return 结果
     */
    public int deleteStStudentSubsidyRecordById(Long id);

    /**
     * 审批补助
     * 
     * @param id 补助记录ID
     * @param approvalStatus 审批状态
     * @param approvalMemo 审批意见
     * @return 结果
     */
    public int approveSubsidy(Long id, Integer approvalStatus, String approvalMemo);
    
    /**
     * 退回补助（已审批通过的记录退回）
     * 
     * @param id 补助记录ID
     * @param returnMemo 退回原因
     * @return 结果
     */
    public int returnSubsidy(Long id, String returnMemo);
    
    /**
     * 批量发放补助（批量录入方式）
     * 
     * @param recordIds 补助记录ID列表（如果有待发放记录）
     * @param paymentDate 发放日期
     * @param paymentPerson 发放人
     * @param memo 备注
     * @return 成功发放的数量
     */
    public int batchPayment(List<Long> recordIds, Date paymentDate, String paymentPerson, String memo);
    
    /**
     * 批量发放补助（支持直接录入金额）
     * 
     * @param request 批量发放请求
     * @return 批量发放结果
     */
    public BatchPaymentResultVO batchPaymentWithStudents(BatchPaymentRequest request);
    
    /**
     * Excel导入批量发放
     * 
     * @param importList 导入数据列表
     * @return 导入结果
     */
    public PaymentImportResultVO importPaymentFromExcel(List<PaymentImportDTO> importList);
}
