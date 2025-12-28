package com.ruoyi.system.application.query;

/**
 * 指标来源工具类
 * 处理指标来源类型相关的业务逻辑
 * 
 * @author ruoyi
 */
public class QuotaSourceUtils {
    
    /** 指标来源类型：下达 */
    public static final Integer SOURCE_TYPE_ISSUED = 1;
    
    /** 指标来源类型：结转 */
    public static final Integer SOURCE_TYPE_CARRY_OVER = 2;
    
    /** 指标来源类型：其他结转 */
    public static final Integer SOURCE_TYPE_CARRY_OVER_OTHER = 3;
    
    /**
     * 判断是否为结转指标（quota_source_type in (2,3)）
     * 
     * @param quotaSourceType 指标来源类型
     * @return true=是结转指标，false=不是
     */
    public static boolean isCarryOver(Integer quotaSourceType) {
        return quotaSourceType != null 
            && (SOURCE_TYPE_CARRY_OVER.equals(quotaSourceType) 
                || SOURCE_TYPE_CARRY_OVER_OTHER.equals(quotaSourceType));
    }
    
    /**
     * 判断是否为下达指标（quota_source_type = 1）
     * 
     * @param quotaSourceType 指标来源类型
     * @return true=是下达指标，false=不是
     */
    public static boolean isIssued(Integer quotaSourceType) {
        return SOURCE_TYPE_ISSUED.equals(quotaSourceType);
    }
    
    /**
     * 验证指标来源类型是否有效
     * 
     * @param quotaSourceType 指标来源类型
     * @return true=有效，false=无效
     */
    public static boolean isValid(Integer quotaSourceType) {
        if (quotaSourceType == null) {
            return false;
        }
        return SOURCE_TYPE_ISSUED.equals(quotaSourceType)
            || SOURCE_TYPE_CARRY_OVER.equals(quotaSourceType)
            || SOURCE_TYPE_CARRY_OVER_OTHER.equals(quotaSourceType);
    }
}

