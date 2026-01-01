package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.mapper.StFamilyMemberMapper;
import com.ruoyi.system.domain.StFamilyMember;
import org.jeecg.modules.jmreport.api.data.IDataSetFactory;
import org.jeecg.modules.jmreport.desreport.model.JmPage;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 家庭成员报表数据服务
 * 用于JimuReport JavaBean数据集
 * 
 * @author ruoyi
 * @date 2026-01-01
 */
@Component("familyMemberReportDataService")
public class FamilyMemberReportDataService implements IDataSetFactory
{
    private static final Logger log = LoggerFactory.getLogger(FamilyMemberReportDataService.class);

    /**
     * 获取Mapper（延迟获取，避免循环依赖）
     */
    private StFamilyMemberMapper getMapper()
    {
        return SpringUtils.getBean(StFamilyMemberMapper.class);
    }

    /**
     * 查询家庭成员数据（不分页）
     * 
     * @param param 查询参数，包含 studentId
     * @return 家庭成员列表
     */
    @Override
    public List<Map<String, Object>> createData(Map<String, Object> param)
    {
        log.info("✅ [家庭成员] 开始查询家庭成员数据（不分页）");
        log.info("📦 接收到的原始参数: {}", param);
        
        try
        {
            Long studentId = null;
            
            // 处理studentId参数
            if (param != null && param.containsKey("studentId"))
            {
                Object studentIdObj = param.get("studentId");
                String studentIdStr = studentIdObj != null ? studentIdObj.toString().trim() : "";
                
                // 忽略JimuReport的占位符格式（如 ${studentId}）
                if (!studentIdStr.isEmpty() && !studentIdStr.startsWith("${") && !studentIdStr.endsWith("}"))
                {
                    try
                    {
                        studentId = Long.parseLong(studentIdStr);
                        log.info("🔍 查询指定学生的家庭成员，studentId: {}", studentId);
                    }
                    catch (NumberFormatException e)
                    {
                        log.warn("⚠️ studentId格式不正确: {}", studentIdStr);
                    }
                }
                else
                {
                    log.warn("⚠️ studentId是占位符或无效值: {}", studentIdStr);
                }
            }
            
            // 查询家庭成员数据
            List<StFamilyMember> familyMembers;
            if (studentId != null)
            {
                StFamilyMember query = new StFamilyMember();
                query.setStudentId(studentId);
                familyMembers = getMapper().selectStFamilyMemberList(query);
            }
            else
            {
                familyMembers = new ArrayList<>();
                log.warn("⚠️ 未提供有效的studentId，返回空列表");
            }
            
            // 转换为Map格式
            List<Map<String, Object>> result = new ArrayList<>();
            if (familyMembers != null)
            {
                for (StFamilyMember member : familyMembers)
                {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", member.getId());
                    map.put("student_id", member.getStudentId());
                    map.put("name", member.getName());
                    map.put("age", member.getAge());
                    map.put("relation", member.getRelation());
                    map.put("employer", member.getEmployer());
                    map.put("occupation", member.getOccupation());
                    map.put("health_status", member.getHealthStatus());
                    result.add(map);
                }
            }
            
            log.info("✅ 查询到 {} 条家庭成员记录", result.size());
            
            return result;
        }
        catch (Exception e)
        {
            log.error("❌ 查询家庭成员数据失败", e);
            throw new RuntimeException("查询家庭成员数据失败: " + e.getMessage());
        }
    }

    /**
     * 查询家庭成员数据（分页）
     * 
     * @param param 查询参数
     * @return 分页结果
     */
    @Override
    public JmPage createPageData(Map<String, Object> param)
    {
        log.info("✅ [家庭成员] 开始查询家庭成员数据（分页）");
        log.info("📦 接收到的原始参数: {}", param);
        
        JmPage page = new JmPage();
        
        try
        {
            // 从参数中获取分页信息
            int pageNo = param.containsKey("pageNo") ? Integer.parseInt(param.get("pageNo").toString()) : 1;
            int pageSize = param.containsKey("pageSize") ? Integer.parseInt(param.get("pageSize").toString()) : 10;
            
            // 查询数据（直接调用 createData）
            List<Map<String, Object>> familyMembers = createData(param);
            
            // 设置分页结果
            page.setPageSize(pageSize);
            page.setTotal(familyMembers != null ? familyMembers.size() : 0);
            page.setRecords(familyMembers);
            
            log.info("✅ 家庭成员数据分页查询完成，总数: {}", page.getTotal());
            
            return page;
        }
        catch (Exception e)
        {
            log.error("❌ 查询家庭成员数据失败", e);
            throw new RuntimeException("查询家庭成员数据失败: " + e.getMessage());
        }
    }
}
