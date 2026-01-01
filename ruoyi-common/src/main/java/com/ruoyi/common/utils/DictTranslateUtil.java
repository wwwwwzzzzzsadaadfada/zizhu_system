package com.ruoyi.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据字典转换工具类
 * 用于将代码值转换为显示文本
 * 
 * @author ruoyi
 * @date 2025-12-30
 */
public class DictTranslateUtil
{
    private static final Logger log = LoggerFactory.getLogger(DictTranslateUtil.class);
    
    // 性别字典
    private static final Map<String, String> GENDER_MAP = new HashMap<>();
    
    // 民族字典（常用民族）
    private static final Map<String, String> ETHNICITY_MAP = new HashMap<>();
    
    // 是否字典
    private static final Map<String, String> YES_NO_MAP = new HashMap<>();
    
    // 政治面貌字典
    private static final Map<String, String> POLITICAL_STATUS_MAP = new HashMap<>();
    
    static
    {
        // 初始化性别字典（根据项目规范：0=女，1=男）
        GENDER_MAP.put("0", "女");
        GENDER_MAP.put("1", "男");
        
        // 初始化民族字典（56个民族）
        ETHNICITY_MAP.put("01", "汉族");
        ETHNICITY_MAP.put("02", "蒙古族");
        ETHNICITY_MAP.put("03", "回族");
        ETHNICITY_MAP.put("04", "藏族");
        ETHNICITY_MAP.put("05", "维吾尔族");
        ETHNICITY_MAP.put("06", "苗族");
        ETHNICITY_MAP.put("07", "彝族");
        ETHNICITY_MAP.put("08", "壮族");
        ETHNICITY_MAP.put("09", "布依族");
        ETHNICITY_MAP.put("10", "朝鲜族");
        ETHNICITY_MAP.put("11", "满族");
        ETHNICITY_MAP.put("12", "侗族");
        ETHNICITY_MAP.put("13", "瑶族");
        ETHNICITY_MAP.put("14", "白族");
        ETHNICITY_MAP.put("15", "土家族");
        ETHNICITY_MAP.put("16", "哈尼族");
        ETHNICITY_MAP.put("17", "哈萨克族");
        ETHNICITY_MAP.put("18", "傣族");
        ETHNICITY_MAP.put("19", "黎族");
        ETHNICITY_MAP.put("20", "傈僳族");
        ETHNICITY_MAP.put("21", "佤族");
        ETHNICITY_MAP.put("22", "畲族");
        ETHNICITY_MAP.put("23", "高山族");
        ETHNICITY_MAP.put("24", "拉祜族");
        ETHNICITY_MAP.put("25", "水族");
        ETHNICITY_MAP.put("26", "东乡族");
        ETHNICITY_MAP.put("27", "纳西族");
        ETHNICITY_MAP.put("28", "景颇族");
        ETHNICITY_MAP.put("29", "柯尔克孜族");
        ETHNICITY_MAP.put("30", "土族");
        ETHNICITY_MAP.put("31", "达斡尔族");
        ETHNICITY_MAP.put("32", "仫佬族");
        ETHNICITY_MAP.put("33", "羌族");
        ETHNICITY_MAP.put("34", "布朗族");
        ETHNICITY_MAP.put("35", "撒拉族");
        ETHNICITY_MAP.put("36", "毛南族");
        ETHNICITY_MAP.put("37", "仡佬族");
        ETHNICITY_MAP.put("38", "锡伯族");
        ETHNICITY_MAP.put("39", "阿昌族");
        ETHNICITY_MAP.put("40", "普米族");
        ETHNICITY_MAP.put("41", "塔吉克族");
        ETHNICITY_MAP.put("42", "怒族");
        ETHNICITY_MAP.put("43", "乌孜别克族");
        ETHNICITY_MAP.put("44", "俄罗斯族");
        ETHNICITY_MAP.put("45", "鄂温克族");
        ETHNICITY_MAP.put("46", "德昂族");
        ETHNICITY_MAP.put("47", "保安族");
        ETHNICITY_MAP.put("48", "裕固族");
        ETHNICITY_MAP.put("49", "京族");
        ETHNICITY_MAP.put("50", "塔塔尔族");
        ETHNICITY_MAP.put("51", "独龙族");
        ETHNICITY_MAP.put("52", "鄂伦春族");
        ETHNICITY_MAP.put("53", "赫哲族");
        ETHNICITY_MAP.put("54", "门巴族");
        ETHNICITY_MAP.put("55", "珞巴族");
        ETHNICITY_MAP.put("56", "基诺族");
        
        // 初始化是否字典
        YES_NO_MAP.put("0", "否");
        YES_NO_MAP.put("1", "是");
        YES_NO_MAP.put("false", "否");
        YES_NO_MAP.put("true", "是");
        
        // 初始化政治面貌字典
        POLITICAL_STATUS_MAP.put("01", "中共党员");
        POLITICAL_STATUS_MAP.put("02", "中共预备党员");
        POLITICAL_STATUS_MAP.put("03", "共青团员");
        POLITICAL_STATUS_MAP.put("04", "民革党员");
        POLITICAL_STATUS_MAP.put("05", "民盟盟员");
        POLITICAL_STATUS_MAP.put("06", "民建会员");
        POLITICAL_STATUS_MAP.put("07", "民进会员");
        POLITICAL_STATUS_MAP.put("08", "农工党党员");
        POLITICAL_STATUS_MAP.put("09", "致公党党员");
        POLITICAL_STATUS_MAP.put("10", "九三学社社员");
        POLITICAL_STATUS_MAP.put("11", "台盟盟员");
        POLITICAL_STATUS_MAP.put("12", "无党派人士");
        POLITICAL_STATUS_MAP.put("13", "群众");
    }
    
    /**
     * 转换性别代码为文本
     * 
     * @param genderCode 性别代码（0-未知, 1-男, 2-女）
     * @return 性别文本
     */
    public static String translateGender(String genderCode)
    {
        if (genderCode == null || genderCode.trim().isEmpty())
        {
            return "未知";
        }
        return GENDER_MAP.getOrDefault(genderCode.trim(), genderCode);
    }
    
    /**
     * 转换民族代码为文本
     * 
     * @param ethnicityCode 民族代码（01-56）
     * @return 民族名称
     */
    public static String translateEthnicity(String ethnicityCode)
    {
        if (ethnicityCode == null || ethnicityCode.trim().isEmpty())
        {
            return "";
        }
        
        String code = ethnicityCode.trim();
        
        // 如果已经是文字，直接返回
        if (!code.matches("\\d+"))
        {
            return code;
        }
        
        // 补齐两位数字（如 "1" -> "01"）
        if (code.length() == 1)
        {
            code = "0" + code;
        }
        
        return ETHNICITY_MAP.getOrDefault(code, ethnicityCode);
    }
    
    /**
     * 转换是否代码为文本
     * 
     * @param yesNoCode 是否代码（0-否, 1-是）
     * @return 是否文本
     */
    public static String translateYesNo(String yesNoCode)
    {
        if (yesNoCode == null || yesNoCode.trim().isEmpty())
        {
            return "否";
        }
        return YES_NO_MAP.getOrDefault(yesNoCode.trim().toLowerCase(), yesNoCode);
    }
    
    /**
     * 转换是否代码为文本（支持数字、布尔值）
     * 
     * @param yesNo 是否值
     * @return 是否文本
     */
    public static String translateYesNo(Object yesNo)
    {
        if (yesNo == null)
        {
            return "否";
        }
        
        if (yesNo instanceof Boolean)
        {
            return ((Boolean) yesNo) ? "是" : "否";
        }
        
        if (yesNo instanceof Number)
        {
            return ((Number) yesNo).intValue() == 1 ? "是" : "否";
        }
        
        return translateYesNo(yesNo.toString());
    }
    
    /**
     * 转换政治面貌代码为文本
     * 
     * @param politicalStatusCode 政治面貌代码
     * @return 政治面貌文本
     */
    public static String translatePoliticalStatus(String politicalStatusCode)
    {
        if (politicalStatusCode == null || politicalStatusCode.trim().isEmpty())
        {
            return "群众";
        }
        
        String code = politicalStatusCode.trim();
        
        // 如果已经是文字，直接返回
        if (!code.matches("\\d+"))
        {
            return code;
        }
        
        // 补齐两位数字
        if (code.length() == 1)
        {
            code = "0" + code;
        }
        
        return POLITICAL_STATUS_MAP.getOrDefault(code, politicalStatusCode);
    }
    
    /**
     * 通用字段转换方法
     * 根据字段名自动选择转换方式
     * 
     * @param fieldName 字段名
     * @param value 字段值
     * @return 转换后的值
     */
    public static String translate(String fieldName, Object value)
    {
        if (value == null)
        {
            return "";
        }
        
        String strValue = value.toString();
        
        // 根据字段名判断转换方式
        if (fieldName.contains("gender") || fieldName.contains("性别"))
        {
            return translateGender(strValue);
        }
        else if (fieldName.contains("ethnicity") || fieldName.contains("民族"))
        {
            return translateEthnicity(strValue);
        }
        else if (fieldName.contains("is_") || fieldName.contains("是否"))
        {
            return translateYesNo(value);
        }
        else if (fieldName.contains("political") || fieldName.contains("政治"))
        {
            return translatePoliticalStatus(strValue);
        }
        
        return strValue;
    }
}
