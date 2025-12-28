package com.ruoyi.common.utils;

import java.util.List;
import java.util.function.Function;

/**
 * 学期工具类
 * 提供学期代码与中文标签之间的转换功能
 * 
 * @author ruoyi
 */
public class SemesterUtils
{
    /** 秋季学期代码 */
    public static final Integer SEMESTER_AUTUMN = 1;
    
    /** 春季学期代码 */
    public static final Integer SEMESTER_SPRING = 2;
    
    /** 秋季学期标签 */
    public static final String SEMESTER_AUTUMN_LABEL = "秋季学期";
    
    /** 春季学期标签 */
    public static final String SEMESTER_SPRING_LABEL = "春季学期";

    /**
     * 将学期代码转换为中文标签
     * 
     * @param semester 学期代码（1=秋季学期，2=春季学期）
     * @return 学期中文名称，如果输入为null或无效值则返回null
     */
    public static String getSemesterLabel(Integer semester)
    {
        if (semester == null)
        {
            return null;
        }
        
        switch (semester)
        {
            case 1:
                return SEMESTER_AUTUMN_LABEL;
            case 2:
                return SEMESTER_SPRING_LABEL;
            default:
                return null;
        }
    }

    /**
     * 批量设置学期标签（用于Service层后处理）
     * 用于确保即使SQL已计算semesterLabel，也能通过工具类校验和统一处理
     * 
     * @param <T> 实体类型
     * @param list 实体列表
     * @param getSemester 获取学期代码的方法引用
     * @param setSemesterLabel 设置学期标签的方法引用
     */
    public static <T> void setSemesterLabels(List<T> list, 
                                              Function<T, Integer> getSemester, 
                                              java.util.function.BiConsumer<T, String> setSemesterLabel)
    {
        if (list == null || list.isEmpty())
        {
            return;
        }
        
        for (T item : list)
        {
            if (item == null)
            {
                continue;
            }
            
            Integer semester = getSemester.apply(item);
            String label = getSemesterLabel(semester);
            setSemesterLabel.accept(item, label);
        }
    }

    /**
     * 验证学期代码是否有效
     * 
     * @param semester 学期代码
     * @return true=有效，false=无效
     */
    public static boolean isValidSemester(Integer semester)
    {
        return semester != null && (semester == SEMESTER_AUTUMN || semester == SEMESTER_SPRING);
    }
}


