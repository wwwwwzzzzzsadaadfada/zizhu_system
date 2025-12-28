package com.ruoyi.system.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.StStudentSubsidyRecord;

/**
 * 学生补助发放记录Mapper接口
 * 
 * @author ruoyi
 * @date 2024-11-21
 */
public interface StStudentSubsidyRecordMapper 
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
     * 删除学生补助发放记录
     * 
     * @param id 学生补助发放记录主键
     * @return 结果
     */
    public int deleteStStudentSubsidyRecordById(Long id);

    /**
     * 批量删除学生补助发放记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStStudentSubsidyRecordByIds(Long[] ids);

    /**
     * 审批补助
     * 
     * @param id 补助记录ID
     * @param approvalStatus 审批状态
     * @param approver 审批人
     * @param approvalMemo 审批意见
     * @return 结果
     */
    public int approveSubsidy(Long id, Integer approvalStatus, String approver, String approvalMemo);
    
    /**
     * 批量发放补助
     * 
     * @param recordIds 补助记录ID列表
     * @param paymentDate 发放日期
     * @param paymentPerson 发放人
     * @param memo 备注
     * @return 结果
     */
    public int batchPayment(
        @Param("recordIds") List<Long> recordIds, 
        @Param("paymentDate") Date paymentDate, 
        @Param("paymentPerson") String paymentPerson, 
        @Param("memo") String memo
    );
    
    /**
     * 根据学生ID和金额查找待发放的补助记录
     * 
     * @param studentBaseId 学生基础信息ID
     * @param subsidyAmount 补助金额
     * @return 补助记录列表
     */
    public List<StStudentSubsidyRecord> selectPendingPaymentByStudentAndAmount(
        @Param("studentBaseId") Long studentBaseId, 
        @Param("subsidyAmount") BigDecimal subsidyAmount
    );
    
    /**
     * 检查学生是否已有未审核的相同经济分类的记录
     * 
     * @param studentId 学生ID
     * @param economyCategory 经济分类
     * @param yearSemesterId 学年学期ID
     * @return 未审核的记录数量
     */
    public int countPendingRecordsByStudentAndEconomyCategory(
        @Param("studentId") Long studentId,
        @Param("economyCategory") String economyCategory,
        @Param("yearSemesterId") Long yearSemesterId
    );
    
    /**
     * 检查学生是否已有已审核通过的相同经济分类的记录
     * 
     * @param studentId 学生ID
     * @param economyCategory 经济分类
     * @param yearSemesterId 学年学期ID
     * @return 已审核通过的记录数量
     */
    public int countApprovedRecordsByStudentAndEconomyCategory(
        @Param("studentId") Long studentId,
        @Param("economyCategory") String economyCategory,
        @Param("yearSemesterId") Long yearSemesterId
    );
}
