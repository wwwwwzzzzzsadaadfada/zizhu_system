package com.ruoyi.system.domain;

import java.io.Serializable;

/**
 * 同步进度信息
 * 
 * @author ruoyi
 */
public class SyncProgress implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 总数 */
    private int total;

    /** 已处理数 */
    private int processed;

    /** 成功数 */
    private int success;

    /** 失败数 */
    private int failed;

    /** 进度百分比 */
    private int percentage;

    /** 状态：running-进行中, completed-已完成, failed-失败 */
    private String status;

    /** 当前操作描述 */
    private String message;

    /** 开始时间 */
    private Long startTime;

    /** 结束时间 */
    private Long endTime;

    public SyncProgress()
    {
        this.status = "running";
        this.startTime = System.currentTimeMillis();
    }

    public SyncProgress(int total)
    {
        this();
        this.total = total;
    }

    /**
     * 更新进度
     */
    public void updateProgress(int processed, int success, int failed, String message)
    {
        this.processed = processed;
        this.success = success;
        this.failed = failed;
        this.message = message;
        if (total > 0)
        {
            this.percentage = (int) ((processed * 100.0) / total);
        }
    }

    /**
     * 完成
     */
    public void complete()
    {
        this.status = "completed";
        this.endTime = System.currentTimeMillis();
        this.percentage = 100;
    }

    /**
     * 失败
     */
    public void fail(String message)
    {
        this.status = "failed";
        this.message = message;
        this.endTime = System.currentTimeMillis();
    }

    // Getter and Setter
    public int getTotal()
    {
        return total;
    }

    public void setTotal(int total)
    {
        this.total = total;
    }

    public int getProcessed()
    {
        return processed;
    }

    public void setProcessed(int processed)
    {
        this.processed = processed;
    }

    public int getSuccess()
    {
        return success;
    }

    public void setSuccess(int success)
    {
        this.success = success;
    }

    public int getFailed()
    {
        return failed;
    }

    public void setFailed(int failed)
    {
        this.failed = failed;
    }

    public int getPercentage()
    {
        return percentage;
    }

    public void setPercentage(int percentage)
    {
        this.percentage = percentage;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public Long getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Long startTime)
    {
        this.startTime = startTime;
    }

    public Long getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Long endTime)
    {
        this.endTime = endTime;
    }

    /**
     * 获取耗时（秒）
     */
    public long getElapsedSeconds()
    {
        long end = endTime != null ? endTime : System.currentTimeMillis();
        return (end - startTime) / 1000;
    }
}
