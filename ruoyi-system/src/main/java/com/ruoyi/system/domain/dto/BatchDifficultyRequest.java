package com.ruoyi.system.domain.dto;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 批量更新学生困难类型和等级的请求DTO
 * 
 * @author ruoyi
 * @date 2025-12-30
 */
public class BatchDifficultyRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 学生ID列表（必填，最多100条） */
    @NotEmpty(message = "学生ID列表不能为空")
    @Size(max = 100, message = "单次更新不能超过100条记录")
    private List<String> studentIds;

    /** 困难类型ID（可为null，表示不更新） */
    private String difficultyTypeId;

    /** 困难等级ID（可为null，表示不更新） */
    private String difficultyLevelId;

    /** 是否脱贫户（可为null，表示不更新） */
    private String isPovertyReliefFamily;

    /** 脱贫年份（可为null，表示不更新） */
    private Integer povertyReliefYear;

    public List<String> getStudentIds()
    {
        return studentIds;
    }

    public void setStudentIds(List<String> studentIds)
    {
        this.studentIds = studentIds;
    }

    public String getDifficultyTypeId()
    {
        return difficultyTypeId;
    }

    public void setDifficultyTypeId(String difficultyTypeId)
    {
        this.difficultyTypeId = difficultyTypeId;
    }

    public String getDifficultyLevelId()
    {
        return difficultyLevelId;
    }

    public void setDifficultyLevelId(String difficultyLevelId)
    {
        this.difficultyLevelId = difficultyLevelId;
    }

    public String getIsPovertyReliefFamily()
    {
        return isPovertyReliefFamily;
    }

    public void setIsPovertyReliefFamily(String isPovertyReliefFamily)
    {
        this.isPovertyReliefFamily = isPovertyReliefFamily;
    }

    public Integer getPovertyReliefYear()
    {
        return povertyReliefYear;
    }

    public void setPovertyReliefYear(Integer povertyReliefYear)
    {
        this.povertyReliefYear = povertyReliefYear;
    }
}
