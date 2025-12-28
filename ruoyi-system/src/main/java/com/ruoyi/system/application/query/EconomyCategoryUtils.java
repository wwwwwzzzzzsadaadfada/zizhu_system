package com.ruoyi.system.application.query;

/**
 * 经济分类工具类
 * 处理经济分类相关的业务逻辑
 * 
 * @author ruoyi
 */
public class EconomyCategoryUtils {
    
    /** 助学金经济分类代码 */
    public static final String SCHOLARSHIP = "1";
    
    /** 免学杂费经济分类代码 */
    public static final String TUITION_WAIVER = "2";
    
    /** 免学费经济分类代码 */
    public static final String TUITION_FREE = "3";
    
    /** 营养改善计划经济分类代码 */
    public static final String NUTRITION = "4";
    
    /**
     * 验证经济分类代码是否有效
     * 
     * @param economyCategory 经济分类代码
     * @return true=有效，false=无效
     */
    public static boolean isValid(String economyCategory) {
        if (economyCategory == null || economyCategory.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = economyCategory.trim();
        return SCHOLARSHIP.equals(trimmed)
            || TUITION_WAIVER.equals(trimmed)
            || TUITION_FREE.equals(trimmed)
            || NUTRITION.equals(trimmed);
    }
    
    /**
     * 将经济分类代码转换为中文名称
     * 
     * @param code 经济分类代码
     * @return 中文名称，如果代码无效则返回原代码
     */
    public static String codeToName(String code) {
        if (code == null || code.trim().isEmpty()) {
            return code;
        }
        
        String trimmed = code.trim();
        switch (trimmed) {
            case SCHOLARSHIP:
                return "助学金";
            case TUITION_WAIVER:
                return "免学杂费";
            case TUITION_FREE:
                return "免学费";
            case NUTRITION:
                return "营养改善计划";
            default:
                return code; // 如果代码无效，返回原代码
        }
    }
    
    /**
     * 将经济分类中文名称转换为代码
     * 
     * @param name 中文名称
     * @return 代码，如果名称无效则返回null
     */
    public static String nameToCode(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        
        String trimmed = name.trim();
        switch (trimmed) {
            case "助学金":
                return SCHOLARSHIP;
            case "免学杂费":
                return TUITION_WAIVER;
            case "免学费":
                return TUITION_FREE;
            case "营养改善计划":
                return NUTRITION;
            default:
                return null;
        }
    }
}

