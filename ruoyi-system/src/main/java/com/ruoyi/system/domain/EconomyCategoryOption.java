package com.ruoyi.system.domain;

/**
 * 经济分类选项DTO
 * 
 * @author ruoyi
 * @date 2025-11-26
 */
public class EconomyCategoryOption {
    /** 经济分类代码 */
    private String code;
    
    /** 经济分类名称 */
    private String name;
    
    public EconomyCategoryOption() {
    }
    
    public EconomyCategoryOption(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}