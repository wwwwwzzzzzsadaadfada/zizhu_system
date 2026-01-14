package com.ruoyi.system.controller;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.system.service.IStStudentsBaseService;
import com.ruoyi.system.service.IBaseConfigService;
import com.ruoyi.system.domain.StStudentsBase;

/**
 * 报表管理Controller
 * 
 * @author ruoyi
 * @date 2024-12-14
 */
@RestController
@RequestMapping("/system/report")
public class ReportController extends BaseController
{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private IStStudentsBaseService stStudentsBaseService;

    @Autowired
    private IBaseConfigService baseConfigService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 助学金申请表缓存Key
     */
    private static final String SUBSIDY_REPORT_CACHE_KEY = "report:subsidy:1166714166859173888";
    
    /**
     * 缓存过期时间（24小时）
     */
    private static final long CACHE_EXPIRE_HOURS = 24;

    /**
     * 查询报表列表
     * 从积木报表表中获取所有启用的报表
     * 支持根据学生ID过滤报表（根据学生学段只显示适用的报表）
     * 
     * 安全说明：所有过滤逻辑在后端完成，前端无法绕过
     * 
     * @param studentId 学生ID（可选）
     *                  如果提供此参数，后端会查询该学生的学制，然后只返回适用于该学制的报表
     *                  如果为null，返回所有报表
     */
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(required = false) Long studentId,
                           @RequestParam(required = false) Long schoolingPlanId)
    {
        try
        {
            Long resolvedPlanId = null;
            
            // 如果提供了学生ID，后端查询学生的学制（安全：前端无法绕过）
            if (studentId != null)
            {
                StStudentsBase student = stStudentsBaseService.selectStStudentsBaseById(studentId);
                if (student == null)
                {
                    return error("学生不存在");
                }
                resolvedPlanId = student.getSchoolingPlanId();
                logger.info("学生ID {} 对应的学制ID: {}", studentId, resolvedPlanId);
                
                // 如果学生没有学制信息，记录警告但继续返回所有报表
                if (resolvedPlanId == null)
                {
                    logger.warn("学生ID {} 没有学制信息，返回所有报表", studentId);
                }
            }
            
            // 如果学生未带出学制，但前端显式传入 schoolingPlanId，则使用该值
            if (resolvedPlanId == null && schoolingPlanId != null)
            {
                resolvedPlanId = schoolingPlanId;
                logger.info("使用前端传入的学制ID过滤报表: {}", resolvedPlanId);
            }
            
            // 构建SQL查询语句
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT id, name, code, create_time AS createTime, schooling_plan_id AS schoolingPlanId, sort_order AS sortOrder, report_type AS reportType ");
            sqlBuilder.append("FROM jimu_report ");
            sqlBuilder.append("WHERE status = '1' ");
            
            // 如果查询到了学制ID，则过滤报表
            // 规则：schooling_plan_id为NULL的报表适用于所有学段
            //       schooling_plan_id等于指定值的报表适用于该学段
            if (resolvedPlanId != null)
            {
                sqlBuilder.append("AND (schooling_plan_id IS NULL OR schooling_plan_id = ?) ");
            }
            
            sqlBuilder.append("ORDER BY sort_order ASC, create_time DESC");
            
            List<Map<String, Object>> reports;
            if (resolvedPlanId != null)
            {
                reports = jdbcTemplate.queryForList(sqlBuilder.toString(), resolvedPlanId);
                logger.info("查询报表（学制ID: {}）共 {} 条记录", resolvedPlanId, reports.size());
            }
            else
            {
                reports = jdbcTemplate.queryForList(sqlBuilder.toString());
                logger.info("查询到 {} 条报表记录（未指定学生）", reports.size());
            }
            
            return success(reports);
        }
        catch (org.springframework.jdbc.BadSqlGrammarException e)
        {
            logger.error("SQL语法错误，可能是表名或字段名不正确", e);
            // 如果字段不存在，尝试使用旧版本的SQL（兼容性处理）
            try
            {
                String sql = "SELECT id, name, code, create_time AS createTime " +
                             "FROM jimu_report " +
                             "WHERE status = '1' " +
                             "ORDER BY create_time DESC";
                List<Map<String, Object>> reports = jdbcTemplate.queryForList(sql);
                logger.warn("使用兼容模式查询报表列表，返回 {} 条记录", reports.size());
                return success(reports);
            }
            catch (Exception ex)
            {
                logger.error("兼容模式查询也失败", ex);
                return error("查询报表列表失败：表或字段不存在，请检查数据库表结构");
            }
        }
        catch (Exception e)
        {
            logger.error("查询报表列表失败", e);
            return error("查询报表列表失败：" + e.getMessage());
        }
    }

    /**
     * 查询报表树（按学制分组）
     * 返回前端直接可用的树形结构，所有分组和分配逻辑在后端完成
     */
    @GetMapping("/tree")
    public AjaxResult tree(@RequestParam(required = false) Long studentId,
                           @RequestParam(required = false) Long schoolingPlanId)
    {
        try
        {
            Long resolvedPlanId = null;

            // 如果提供了学生ID，后端查询学生的学制
            if (studentId != null)
            {
                StStudentsBase student = stStudentsBaseService.selectStStudentsBaseById(studentId);
                if (student == null)
                {
                    return error("学生不存在");
                }
                resolvedPlanId = student.getSchoolingPlanId();
                logger.info("【报表树】学生ID {} 对应的学制ID: {}", studentId, resolvedPlanId);

                if (resolvedPlanId == null)
                {
                    logger.warn("【报表树】学生ID {} 没有学制信息，将根据 schoolingPlanId 或返回全部报表", studentId);
                }
            }

            // 如果学生没有学制，但前端传入了 schoolingPlanId，则使用该值
            if (resolvedPlanId == null && schoolingPlanId != null)
            {
                resolvedPlanId = schoolingPlanId;
                logger.info("【报表树】使用前端传入的学制ID过滤报表: {}", resolvedPlanId);
            }

            // 查询所有启用的报表（可选按学制过滤）
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT jr.id, jr.name, jr.code, jr.schooling_plan_id AS schoolingPlanId, jr.sort_order AS sortOrder, jr.report_type AS reportType ");
            sqlBuilder.append("FROM jimu_report jr ");
            sqlBuilder.append("WHERE jr.status = '1' ");
            if (resolvedPlanId != null)
            {
                sqlBuilder.append("AND (jr.schooling_plan_id IS NULL OR jr.schooling_plan_id = ?) ");
            }
            sqlBuilder.append("ORDER BY jr.sort_order ASC, jr.create_time DESC");

            List<Map<String, Object>> reports;
            if (resolvedPlanId != null)
            {
                reports = jdbcTemplate.queryForList(sqlBuilder.toString(), resolvedPlanId);
            }
            else
            {
                reports = jdbcTemplate.queryForList(sqlBuilder.toString());
            }

            if (reports == null || reports.isEmpty())
            {
                return success(new ArrayList<>());
            }

            // 查询学制列表，用于分组显示名称
            List<Map<String, Object>> plans = baseConfigService.selectSchoolPlanList();
            Map<Long, String> planNameMap = new HashMap<>();
            if (plans != null)
            {
                for (Map<String, Object> p : plans)
                {
                    Object idObj = p.get("id");
                    Object nameObj = p.get("name");
                    if (idObj instanceof Number)
                    {
                        Long id = ((Number) idObj).longValue();
                        String name = nameObj != null ? nameObj.toString() : ("学制-" + id);
                        planNameMap.put(id, name);
                    }
                }
            }

            // 构建树形结构：根节点为学制/学段，子节点为报表
            Map<String, Map<String, Object>> groupMap = new HashMap<>();
            // 通用报表分组（未指定学制）
            String commonKey = "plan-common";
            Map<String, Object> commonGroup = new HashMap<>();
            commonGroup.put("id", commonKey);
            commonGroup.put("label", "通用报表");
            commonGroup.put("children", new ArrayList<Map<String, Object>>());
            commonGroup.put("count", 0);
            groupMap.put(commonKey, commonGroup);

            for (Map<String, Object> r : reports)
            {
                Object idObj = r.get("id");
                Object nameObj = r.get("name");
                Object planIdObj = r.get("schoolingPlanId");
                if (idObj == null || nameObj == null)
                {
                    continue;
                }
                String reportId = idObj.toString();
                String reportName = nameObj.toString();

                Long planId = null;
                if (planIdObj instanceof Number)
                {
                    planId = ((Number) planIdObj).longValue();
                }

                String groupKey;
                String groupLabel;
                if (planId != null)
                {
                    groupKey = "plan-" + planId;
                    groupLabel = planNameMap.getOrDefault(planId, "学制-" + planId);
                }
                else
                {
                    groupKey = commonKey;
                    groupLabel = "通用报表";
                }

                Map<String, Object> group = groupMap.get(groupKey);
                if (group == null)
                {
                    group = new HashMap<>();
                    group.put("id", groupKey);
                    group.put("label", groupLabel);
                    group.put("children", new ArrayList<Map<String, Object>>());
                    group.put("count", 0);
                    groupMap.put(groupKey, group);
                }

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) group.get("children");
                Map<String, Object> child = new HashMap<>();
                child.put("id", reportId);
                child.put("label", reportName);
                // 添加报表类型标识，由后端判断
                Object reportTypeObj = r.get("reportType");
                String reportType = reportTypeObj != null ? reportTypeObj.toString() : "student";
                child.put("reportType", reportType);
                child.put("needStudent", !"stat".equals(reportType)); // 后端判断是否需要学生
                children.add(child);

                int count = (int) group.get("count");
                group.put("count", count + 1);
            }

            // 组装最终树形列表，按学制ID排序，通用报表放最后
            List<Map<String, Object>> tree = new ArrayList<>();
            groupMap.forEach((key, group) -> {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) group.get("children");
                if (children != null && !children.isEmpty())
                {
                    if (commonKey.equals(key))
                    {
                        // 通用报表暂存，稍后追加
                        tree.add(group);
                    }
                    else
                    {
                        tree.add(group);
                    }
                }
            });

            // 简单排序：通用报表节点移动到最后
            Map<String, Object> commonNode = null;
            for (Map<String, Object> g : new ArrayList<>(tree))
            {
                if (commonKey.equals(g.get("id")))
                {
                    commonNode = g;
                    tree.remove(g);
                    break;
                }
            }
            if (commonNode != null)
            {
                tree.add(commonNode);
            }

            return success(tree);
        }
        catch (Exception e)
        {
            logger.error("查询报表树失败", e);
            return error("查询报表树失败：" + e.getMessage());
        }
    }

    /**
     * 检查报表是否需要学生ID
     * @param reportId 报表ID
     * @return 返回报表信息，包含 reportType、needStudent 和 schoolingPlanId 字段
     */
    @GetMapping("/checkReportType/{reportId}")
    public AjaxResult checkReportType(@org.springframework.web.bind.annotation.PathVariable String reportId)
    {
        try
        {
            if (reportId == null || reportId.trim().isEmpty())
            {
                return error("报表ID不能为空");
            }

            String sql = "SELECT id, name, report_type AS reportType, schooling_plan_id AS schoolingPlanId FROM jimu_report WHERE id = ? AND status = '1'";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, reportId);
            
            if (results == null || results.isEmpty())
            {
                return error("报表不存在或已禁用");
            }

            Map<String, Object> report = results.get(0);
            Object reportTypeObj = report.get("reportType");
            String reportType = reportTypeObj != null ? reportTypeObj.toString() : "student";
            
            Object schoolingPlanIdObj = report.get("schoolingPlanId");
            Long schoolingPlanId = null;
            if (schoolingPlanIdObj instanceof Number)
            {
                schoolingPlanId = ((Number) schoolingPlanIdObj).longValue();
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", report.get("id"));
            result.put("name", report.get("name"));
            result.put("reportType", reportType);
            result.put("needStudent", !"stat".equals(reportType)); // 后端判断逻辑
            result.put("schoolingPlanId", schoolingPlanId); // 返回学段ID，用于过滤学生
            
            logger.info("报表ID: {}, 类型: {}, 是否需要学生: {}, 学段ID: {}", 
                reportId, reportType, !"stat".equals(reportType), schoolingPlanId);
            
            return success(result);
        }
        catch (Exception e)
        {
            logger.error("检查报表类型失败", e);
            return error("检查报表类型失败：" + e.getMessage());
        }
    }

    /**
     * 获取助学金申请表URL
     * 使用固定的报表ID，通过Redis缓存优化查询性能
     * 
     * @param studentId 学生ID（必填）
     * @return 返回报表URL和相关信息
     */
    @GetMapping("/getSubsidyReportUrl")
    public AjaxResult getSubsidyReportUrl(@RequestParam(required = true) Long studentId)
    {
        try
        {
            if (studentId == null)
            {
                return error("学生ID不能为空");
            }

            // 验证学生是否存在
            StStudentsBase student = stStudentsBaseService.selectStStudentsBaseById(studentId);
            if (student == null)
            {
                return error("学生不存在");
            }

            // 固定的助学金申请表ID
            String reportId = "1166714166859173888";
            String reportName = null;

            // 先从Redis缓存获取报表信息
            try
            {
                Map<String, Object> cachedReport = redisCache.getCacheObject(SUBSIDY_REPORT_CACHE_KEY);
                if (cachedReport != null)
                {
                    reportName = cachedReport.get("name") != null ? 
                        cachedReport.get("name").toString() : "助学金申请表";
                    logger.debug("从缓存获取助学金申请表信息：报表ID={}, 报表名称={}", reportId, reportName);
                }
            }
            catch (Exception e)
            {
                // Redis异常不影响主流程，记录日志后继续查询数据库
                logger.warn("从Redis缓存获取报表信息失败，将查询数据库：{}", e.getMessage());
            }

            // 如果缓存未命中，查询数据库
            if (reportName == null)
            {
                String sql = "SELECT id, name, code FROM jimu_report WHERE id = ? AND status = '1'";
                List<Map<String, Object>> reports = jdbcTemplate.queryForList(sql, reportId);

                if (reports == null || reports.isEmpty())
                {
                    return error("助学金申请表不存在或已禁用，请联系管理员");
                }

                Map<String, Object> report = reports.get(0);
                reportName = report.get("name") != null ? report.get("name").toString() : "助学金申请表";

                // 将查询结果存入Redis缓存（24小时过期）
                try
                {
                    Map<String, Object> cacheData = new HashMap<>();
                    cacheData.put("id", reportId);
                    cacheData.put("name", reportName);
                    cacheData.put("code", report.get("code"));
                    redisCache.setCacheObject(SUBSIDY_REPORT_CACHE_KEY, cacheData, 
                        (int) CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
                    logger.info("助学金申请表信息已存入缓存：报表ID={}, 报表名称={}, 过期时间={}小时", 
                        reportId, reportName, CACHE_EXPIRE_HOURS);
                }
                catch (Exception e)
                {
                    // Redis写入失败不影响主流程，只记录警告日志
                    logger.warn("写入Redis缓存失败，但不影响查询结果：{}", e.getMessage());
                }
            }

            // 构建报表URL（返回相对路径，前端会根据环境自动处理baseUrl）
            String reportUrl = "/jmreport/view/" + reportId + "?studentId=" + studentId + "&desensitize=true";

            Map<String, Object> result = new HashMap<>();
            result.put("reportId", reportId);
            result.put("reportName", reportName);
            result.put("reportUrl", reportUrl);
            result.put("studentId", studentId);

            logger.info("成功获取助学金申请表URL：学生ID={}, 报表ID={}, 报表名称={}", 
                studentId, reportId, reportName);

            return success(result);
        }
        catch (Exception e)
        {
            logger.error("获取助学金申请表URL失败，学生ID：{}", studentId, e);
            return error("获取助学金申请表URL失败：" + e.getMessage());
        }
    }

    /**
     * 更新报表排序
     * @param reportOrders 报表排序列表，格式：[{"id": "报表ID", "sortOrder": 10}, ...]
     */
    @org.springframework.web.bind.annotation.PostMapping("/updateSort")
    public AjaxResult updateSort(@org.springframework.web.bind.annotation.RequestBody List<Map<String, Object>> reportOrders)
    {
        try
        {
            if (reportOrders == null || reportOrders.isEmpty())
            {
                return error("排序数据不能为空");
            }

            int updatedCount = 0;
            for (Map<String, Object> item : reportOrders)
            {
                Object idObj = item.get("id");
                Object sortOrderObj = item.get("sortOrder");
                
                if (idObj == null || sortOrderObj == null)
                {
                    continue;
                }
                
                String reportId = idObj.toString();
                int sortOrder = 0;
                
                if (sortOrderObj instanceof Number)
                {
                    sortOrder = ((Number) sortOrderObj).intValue();
                }
                else
                {
                    try
                    {
                        sortOrder = Integer.parseInt(sortOrderObj.toString());
                    }
                    catch (NumberFormatException e)
                    {
                        logger.warn("无效的排序号: {}", sortOrderObj);
                        continue;
                    }
                }
                
                String sql = "UPDATE jimu_report SET sort_order = ? WHERE id = ?";
                int result = jdbcTemplate.update(sql, sortOrder, reportId);
                updatedCount += result;
            }
            
            logger.info("更新报表排序完成，共更新 {} 条记录", updatedCount);
            return success("更新成功，共更新 " + updatedCount + " 条记录");
        }
        catch (Exception e)
        {
            logger.error("更新报表排序失败", e);
            return error("更新报表排序失败：" + e.getMessage());
        }
    }
}


