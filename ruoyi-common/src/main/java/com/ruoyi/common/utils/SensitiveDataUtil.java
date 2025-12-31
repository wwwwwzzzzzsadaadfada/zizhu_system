package com.ruoyi.common.utils;

import cn.hutool.core.util.DesensitizedUtil;

/**
 * 敏感数据脱敏工具类
 * 使用 Hutool 工具包实现数据脱敏
 * 用于在返回给前端的数据中脱敏敏感字段
 * 
 * @author ruoyi
 * @date 2025-12-30
 */
public class SensitiveDataUtil
{
    /**
     * 脱敏身份证号
     * 使用 Hutool 的 DesensitizedUtil.idCardNum()
     * 显示前6位和后4位，中间用*代替
     * 如：110102199001011234 -> 110102********1234
     * 
     * @param idCardNo 身份证号
     * @return 脱敏后的身份证号
     */
    public static String maskIdCardNo(String idCardNo)
    {
        return DesensitizedUtil.idCardNum(idCardNo, 6, 4);
    }
    
    /**
     * 脱敏住址
     * 根据地址级别智能脱敏
     * - 市级地址（包含“市”和“区”）：保留省、市、区、镇，后面显示为**村**屯*号
     * - 县级地址（包含“县”，但不含“区”）：保留省、县、镇，后面显示为**村**屯*号
     * 
     * 接受的数据库特式地址：
     *   市级: 广西壮族自治区崇左市江州区翠柏路翠柏村1屯123号
     *         -> 广西崇左市江州区翠柏路**村**屯*号
     *   县级: 广西崇左大新县硕龙镇门屯村哈哈屯19号
     *         -> 广西崇左大新县硕龙镇**村**屯*号
     * 
     * @param address 住址
     * @return 脱敏后的住址
     */
    public static String maskAddress(String address)
    {
        if (address == null || address.isEmpty())
        {
            return address;
        }
        
        // 判断是否是市级地址（含“市”和“区”）
        boolean isCityLevel = address.contains("市") && address.contains("区");
        
        if (isCityLevel)
        {
            // 市级：提取省、市、区、镇后面的部分
            // 查找第一个“镇”或“乡”的位置，之后的内容脱敏
            int townIndex = address.indexOf("镇");
            int villageIndex = address.indexOf("乡");
            
            int splitIndex = -1;
            if (townIndex != -1 && villageIndex != -1)
            {
                splitIndex = Math.min(townIndex, villageIndex) + 1;
            }
            else if (townIndex != -1)
            {
                splitIndex = townIndex + 1;
            }
            else if (villageIndex != -1)
            {
                splitIndex = villageIndex + 1;
            }
            else
            {
                // 如果没有镇或乡，找第三个“区”字或其他行政位置
                int districtIndex = address.lastIndexOf("区");
                if (districtIndex != -1)
                {
                    splitIndex = districtIndex + 1;
                }
            }
            
            if (splitIndex > 0 && splitIndex < address.length())
            {
                return address.substring(0, splitIndex) + "**村**屯*号";
            }
        }
        else if (address.contains("县") && !address.contains("区"))
        {
            // 县级（仅有县，不有区）：判断是否也有“市”
            // 如果同时有“市”（如广西崇左市大新县），需要自动移除“市”
            String displayAddress = address;
            
            if (address.contains("市"))
            {
                // 找到“市”字，次一个※之前的为省，之后的为县
                // 例如：广西崇左市大新县 -> 广西崇左大新县
                int cityIndex = address.indexOf("市");
                displayAddress = address.substring(0, cityIndex) + address.substring(cityIndex + 1);
            }
            
            // 提取省、县、镇
            int townIndex = displayAddress.indexOf("镇");
            int villageIndex = displayAddress.indexOf("乡");
            
            int splitIndex = -1;
            if (townIndex != -1 && villageIndex != -1)
            {
                splitIndex = Math.min(townIndex, villageIndex) + 1;
            }
            else if (townIndex != -1)
            {
                splitIndex = townIndex + 1;
            }
            else if (villageIndex != -1)
            {
                splitIndex = villageIndex + 1;
            }
            else
            {
                // 如果没有镇或乡，找最后一个县字
                int countyIndex = displayAddress.lastIndexOf("县");
                if (countyIndex != -1)
                {
                    splitIndex = countyIndex + 1;
                }
            }
            
            if (splitIndex > 0 && splitIndex < displayAddress.length())
            {
                return displayAddress.substring(0, splitIndex) + "**村**屯*号";
            }
        }
        
        // 跌外情况：直接返回原地址（不脚敏）
        return address;
    }
    
    /**
     * 生成脱敏后的学籍号
     * 格式：G + 脱敏后的身份证号
     * 如：110102**********1234
     * 
     * @param idCardNo 原始身份证号
     * @return 脱敏后的学籍号
     */
    public static String maskStudentNo(String idCardNo)
    {
        if (idCardNo == null || idCardNo.isEmpty())
        {
            return "G";
        }
        
        // 对身份证号进行脱敏
        String maskedIdCardNo = maskIdCardNo(idCardNo);
        return "G" + maskedIdCardNo;
    }
    
    /**
     * 脱敏联系电话
     * 使用 Hutool 的 DesensitizedUtil.mobilePhone()
     * 显示前3位和后4位，中间用4位用*代替
     * 如：18278188453 -> 182****8453
     * 
     * @param phone 联系电话
     * @return 脱敏后的电话号码
     */
    public static String maskPhone(String phone)
    {
        return DesensitizedUtil.mobilePhone(phone);
    }
}
