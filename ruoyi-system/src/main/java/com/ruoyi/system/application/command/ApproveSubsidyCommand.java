package com.ruoyi.system.application.command;

/**
 * 审批补助命令
 * 
 * @author ruoyi
 * @date 2024-11-26
 */
public class ApproveSubsidyCommand {
    
    /** 补助记录ID */
    private Long recordId;
    
    /** 审批状态：1-通过，2-驳回 */
    private Integer approvalStatus;
    
    /** 审批意见 */
    private String approvalMemo;
    
    public Long getRecordId() {
        return recordId;
    }
    
    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }
    
    public Integer getApprovalStatus() {
        return approvalStatus;
    }
    
    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
    
    public String getApprovalMemo() {
        return approvalMemo;
    }
    
    public void setApprovalMemo(String approvalMemo) {
        this.approvalMemo = approvalMemo;
    }
    
    /**
     * 是否为审批通过
     */
    public boolean isApproved() {
        return approvalStatus != null && approvalStatus == 1;
    }
    
    /**
     * 是否为审批驳回
     */
    public boolean isRejected() {
        return approvalStatus != null && approvalStatus == 2;
    }
}



