package com.ruoyi.system.domain;

import java.io.Serializable;

/**
 * 同步受助学生信息结果
 * 
 * @author ruoyi
 */
public class SyncAidedStudentResult implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 新增的学生数量 */
    private int insertedCount;

    /** 更新的学生数量 */
    private int updatedCount;

    /** 同步的学生总数 */
    private int totalCount;

    /** 是否成功 */
    private boolean success;

    /** 错误消息 */
    private String errorMessage;

    public SyncAidedStudentResult()
    {
    }

    public SyncAidedStudentResult(int insertedCount, int updatedCount)
    {
        this.insertedCount = insertedCount;
        this.updatedCount = updatedCount;
        this.totalCount = insertedCount + updatedCount;
        this.success = true;
    }

    public SyncAidedStudentResult(String errorMessage)
    {
        this.success = false;
        this.errorMessage = errorMessage;
    }

    public int getInsertedCount()
    {
        return insertedCount;
    }

    public void setInsertedCount(int insertedCount)
    {
        this.insertedCount = insertedCount;
        this.totalCount = this.insertedCount + this.updatedCount;
    }

    public int getUpdatedCount()
    {
        return updatedCount;
    }

    public void setUpdatedCount(int updatedCount)
    {
        this.updatedCount = updatedCount;
        this.totalCount = this.insertedCount + this.updatedCount;
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    /**
     * 获取同步结果消息
     */
    public String getMessage()
    {
        if (!success)
        {
            return errorMessage != null ? errorMessage : "同步失败";
        }

        if (insertedCount > 0 && updatedCount > 0)
        {
            return "同步完成，已同步" + insertedCount + "人，更新" + updatedCount + "人！";
        }
        else if (insertedCount > 0)
        {
            return "同步完成，已同步" + insertedCount + "人！";
        }
        else if (updatedCount > 0)
        {
            return "同步完成，已更新" + updatedCount + "人！";
        }
        else
        {
            return "同步完成，无数据变更！";
        }
    }
}


