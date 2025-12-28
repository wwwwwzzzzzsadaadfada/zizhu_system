package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.StSubsidyPolicy;
import com.ruoyi.system.service.IStSubsidyPolicyService;

/**
 * 资助政策Controller
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
@RestController
@RequestMapping("/system/subsidyPolicy")
public class StSubsidyPolicyController extends BaseController
{
    @Autowired
    private IStSubsidyPolicyService stSubsidyPolicyService;

    /**
     * 查询资助政策列表
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyPolicy:list')")
    @GetMapping("/list")
    public TableDataInfo list(StSubsidyPolicy stSubsidyPolicy)
    {
        startPage();
        List<StSubsidyPolicy> list = stSubsidyPolicyService.selectStSubsidyPolicyList(stSubsidyPolicy);
        return getDataTable(list);
    }

    /**
     * 导出资助政策列表
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyPolicy:export')")
    @Log(title = "资助政策", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StSubsidyPolicy stSubsidyPolicy)
    {
        List<StSubsidyPolicy> list = stSubsidyPolicyService.selectStSubsidyPolicyList(stSubsidyPolicy);
        ExcelUtil<StSubsidyPolicy> util = new ExcelUtil<StSubsidyPolicy>(StSubsidyPolicy.class);
        util.exportExcel(response, list, "资助政策数据");
    }

    /**
     * 获取资助政策详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyPolicy:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stSubsidyPolicyService.selectStSubsidyPolicyById(id));
    }

    /**
     * 查询有效政策列表
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyPolicy:query')")
    @GetMapping("/effective")
    public AjaxResult getEffectivePolicies(
        @RequestParam(required = false) Long schoolingPlanId,
        @RequestParam(required = false) Long yearSemesterId)
    {
        List<StSubsidyPolicy> list = stSubsidyPolicyService.selectEffectivePolicies(schoolingPlanId, yearSemesterId);
        return success(list);
    }

    /**
     * 新增资助政策
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyPolicy:add')")
    @Log(title = "资助政策", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StSubsidyPolicy stSubsidyPolicy)
    {
        int result = stSubsidyPolicyService.insertStSubsidyPolicy(stSubsidyPolicy);
        if (result > 0) {
            // 返回政策ID，方便前端后续操作（如上传附件）
            AjaxResult ajax = success();
            ajax.put("id", stSubsidyPolicy.getId());
            return ajax;
        }
        return toAjax(result);
    }

    /**
     * 修改资助政策
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyPolicy:edit')")
    @Log(title = "资助政策", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StSubsidyPolicy stSubsidyPolicy)
    {
        return toAjax(stSubsidyPolicyService.updateStSubsidyPolicy(stSubsidyPolicy));
    }

    /**
     * 删除资助政策
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyPolicy:remove')")
    @Log(title = "资助政策", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stSubsidyPolicyService.deleteStSubsidyPolicyByIds(ids));
    }

    /**
     * 发布政策
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyPolicy:publish')")
    @Log(title = "发布政策", businessType = BusinessType.UPDATE)
    @PutMapping("/publish/{id}")
    public AjaxResult publish(@PathVariable("id") Long id)
    {
        String publisher = SecurityUtils.getUsername();
        return toAjax(stSubsidyPolicyService.publishPolicy(id, publisher));
    }

    /**
     * 废止政策
     */
    @PreAuthorize("@ss.hasPermi('system:subsidyPolicy:abolish')")
    @Log(title = "废止政策", businessType = BusinessType.UPDATE)
    @PutMapping("/abolish/{id}")
    public AjaxResult abolish(@PathVariable("id") Long id, @RequestParam(required = false) String abolishReason)
    {
        return toAjax(stSubsidyPolicyService.abolishPolicy(id, abolishReason));
    }
}


