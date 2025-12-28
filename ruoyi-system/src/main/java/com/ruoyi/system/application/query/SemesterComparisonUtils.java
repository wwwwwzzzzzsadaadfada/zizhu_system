package com.ruoyi.system.application.query;

/**
 * 学期比较工具类
 * 用于比较学期的时间顺序，基于学年（schoolYear）和学期（semester）进行比较
 * 而不是基于ID进行比较（ID可能不反映实际的时间顺序）
 * 
 * @author ruoyi
 */
public class SemesterComparisonUtils {
    
    /**
     * 比较两个学期的时间顺序
     * 
     * 比较规则：
     * 1. 先比较学年（schoolYear），学年小的更早
     * 2. 如果学年相同，再比较学期（semester），1（秋季）< 2（春季）
     * 
     * 学年格式：如 "2024-2025"，提取起始年份进行比较
     * 学期：1=秋季学期，2=春季学期
     * 
     * @param schoolYear1 第一个学期的学年（如 "2024-2025"）
     * @param semester1 第一个学期的学期（1=秋季，2=春季）
     * @param schoolYear2 第二个学期的学年（如 "2024-2025"）
     * @param semester2 第二个学期的学期（1=秋季，2=春季）
     * @return 负数表示semester1早于semester2，0表示相等，正数表示semester1晚于semester2
     * @throws IllegalArgumentException 如果参数格式不正确
     */
    public static int compare(String schoolYear1, Integer semester1, 
                              String schoolYear2, Integer semester2) {
        // 参数验证
        if (schoolYear1 == null || schoolYear1.trim().isEmpty()) {
            throw new IllegalArgumentException("第一个学期的学年不能为空");
        }
        if (schoolYear2 == null || schoolYear2.trim().isEmpty()) {
            throw new IllegalArgumentException("第二个学期的学年不能为空");
        }
        if (semester1 == null) {
            throw new IllegalArgumentException("第一个学期的学期不能为空");
        }
        if (semester2 == null) {
            throw new IllegalArgumentException("第二个学期的学期不能为空");
        }
        
        // 提取学年的起始年份
        Integer year1 = extractStartYear(schoolYear1);
        Integer year2 = extractStartYear(schoolYear2);
        
        // 先比较学年
        int yearCompare = Integer.compare(year1, year2);
        if (yearCompare != 0) {
            return yearCompare;
        }
        
        // 学年相同，比较学期（1=秋季 < 2=春季）
        return Integer.compare(semester1, semester2);
    }
    
    /**
     * 判断第一个学期是否早于第二个学期
     * 
     * @param schoolYear1 第一个学期的学年
     * @param semester1 第一个学期的学期
     * @param schoolYear2 第二个学期的学年
     * @param semester2 第二个学期的学期
     * @return true=semester1早于semester2，false=否则
     */
    public static boolean isBefore(String schoolYear1, Integer semester1, 
                                   String schoolYear2, Integer semester2) {
        return compare(schoolYear1, semester1, schoolYear2, semester2) < 0;
    }
    
    /**
     * 判断第一个学期是否晚于第二个学期
     * 
     * @param schoolYear1 第一个学期的学年
     * @param semester1 第一个学期的学期
     * @param schoolYear2 第二个学期的学年
     * @param semester2 第二个学期的学期
     * @return true=semester1晚于semester2，false=否则
     */
    public static boolean isAfter(String schoolYear1, Integer semester1, 
                                  String schoolYear2, Integer semester2) {
        return compare(schoolYear1, semester1, schoolYear2, semester2) > 0;
    }
    
    /**
     * 判断两个学期是否相等
     * 
     * @param schoolYear1 第一个学期的学年
     * @param semester1 第一个学期的学期
     * @param schoolYear2 第二个学期的学年
     * @param semester2 第二个学期的学期
     * @return true=相等，false=不相等
     */
    public static boolean isEqual(String schoolYear1, Integer semester1, 
                                  String schoolYear2, Integer semester2) {
        return compare(schoolYear1, semester1, schoolYear2, semester2) == 0;
    }
    
    /**
     * 从学年字符串中提取起始年份
     * 
     * 支持的格式：
     * - "2024-2025" -> 2024
     * - "2024" -> 2024（兼容单个年份）
     * 
     * @param schoolYear 学年字符串（如 "2024-2025"）
     * @return 起始年份
     * @throws IllegalArgumentException 如果格式不正确
     */
    private static Integer extractStartYear(String schoolYear) {
        if (schoolYear == null || schoolYear.trim().isEmpty()) {
            throw new IllegalArgumentException("学年字符串不能为空");
        }
        
        String trimmed = schoolYear.trim();
        
        // 如果包含 "-"，提取前半部分
        int dashIndex = trimmed.indexOf('-');
        if (dashIndex > 0) {
            String startYearStr = trimmed.substring(0, dashIndex).trim();
            try {
                return Integer.parseInt(startYearStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("学年格式不正确，无法解析起始年份: " + schoolYear, e);
            }
        }
        
        // 如果没有 "-"，尝试直接解析为年份
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("学年格式不正确，无法解析为年份: " + schoolYear, e);
        }
    }
}


