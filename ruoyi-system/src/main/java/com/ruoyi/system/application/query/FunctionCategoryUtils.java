package com.ruoyi.system.application.query;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 功能分类工具类
 * 处理功能分类相关的业务逻辑
 * 
 * @author ruoyi
 */
public class FunctionCategoryUtils {
    
    /** 小学功能分类代码 */
    public static final String PRIMARY = "1";
    
    /** 初中功能分类代码 */
    public static final String JUNIOR = "2";
    
    /** 高中功能分类代码 */
    public static final String SENIOR = "3";
    
    /**
     * 确定目标功能分类列表（业务规则：义教阶段预算组合）
     * 
     * 规则：小学（1）和初中（2）可以互相看到对方的预算，其他功能分类只查询匹配的分类
     * 
     * @param functionCategory 功能分类代码
     * @return 需要查询的功能分类列表，如果输入无效则返回空列表
     */
    public static List<String> determineTargetCategories(String functionCategory) {
        if (functionCategory == null || functionCategory.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String trimmed = functionCategory.trim();
        
        // 业务规则：义教阶段预算组合（小学和初中可以互相看到对方的预算）
        if (PRIMARY.equals(trimmed) || JUNIOR.equals(trimmed)) {
            return Arrays.asList(PRIMARY, JUNIOR);
        }
        
        // 其他功能分类（如高中）：只查询匹配的功能分类
        return Collections.singletonList(trimmed);
    }
    
    /**
     * 判断是否为义教阶段功能分类（小学或初中）
     * 
     * @param functionCategory 功能分类代码
     * @return true=是义教阶段，false=不是
     */
    public static boolean isCompulsoryEducationStage(String functionCategory) {
        if (functionCategory == null) {
            return false;
        }
        return PRIMARY.equals(functionCategory) || JUNIOR.equals(functionCategory);
    }
    
    /**
     * 验证功能分类代码是否有效
     * 
     * @param functionCategory 功能分类代码
     * @return true=有效，false=无效
     */
    public static boolean isValid(String functionCategory) {
        if (functionCategory == null || functionCategory.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = functionCategory.trim();
        return PRIMARY.equals(trimmed)
            || JUNIOR.equals(trimmed)
            || SENIOR.equals(trimmed);
    }
    
    /**
     * 将功能分类代码转换为中文名称
     * 
     * @param code 功能分类代码
     * @return 中文名称，如果代码无效则返回原代码
     */
    public static String codeToName(String code) {
        if (code == null || code.trim().isEmpty()) {
            return code;
        }
        
        String trimmed = code.trim();
        switch (trimmed) {
            case PRIMARY:
                return "小学";
            case JUNIOR:
                return "初中";
            case SENIOR:
                return "高中";
            default:
                return code;
        }
    }
}

