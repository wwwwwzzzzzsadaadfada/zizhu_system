package com.ruoyi.system.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.StFamilyMember;
import com.ruoyi.system.service.IStFamilyMemberService;

/**
 * 家庭成员Controller
 */
@RestController
@RequestMapping("/system/familyMember")
public class StFamilyMemberController extends BaseController {

    @Autowired
    private IStFamilyMemberService stFamilyMemberService;

    /**
     * 查询家庭成员列表（支持按 studentId）
     */
    @PreAuthorize("@ss.hasPermi('system:students:list')")
    @GetMapping("/list")
    public AjaxResult list(StFamilyMember query) {
        List<StFamilyMember> list = stFamilyMemberService.selectStFamilyMemberList(query);
        return success(list);
    }

    /**
     * 获取家庭成员详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:students:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(stFamilyMemberService.selectStFamilyMemberById(id));
    }

    /**
     * 新增家庭成员
     */
    @PreAuthorize("@ss.hasPermi('system:students:add')")
    @Log(title = "家庭成员", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StFamilyMember member) {
        return toAjax(stFamilyMemberService.insertStFamilyMember(member));
    }

    /**
     * 修改家庭成员
     */
    @PreAuthorize("@ss.hasPermi('system:students:edit')")
    @Log(title = "家庭成员", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StFamilyMember member) {
        return toAjax(stFamilyMemberService.updateStFamilyMember(member));
    }

    /**
     * 删除家庭成员
     */
    @PreAuthorize("@ss.hasPermi('system:students:remove')")
    @Log(title = "家庭成员", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(stFamilyMemberService.deleteStFamilyMemberByIds(ids));
    }

    /**
     * 覆盖保存某个学生的家庭成员（随学生表单一起）
     */
    @PreAuthorize("@ss.hasPermi('system:students:edit')")
    @Log(title = "家庭成员", businessType = BusinessType.UPDATE)
    @PostMapping("/batch/save")
    public AjaxResult batchSave(@RequestBody FamilyMemberBatchSaveRequest request) {
        stFamilyMemberService.saveByStudentId(request.getStudentId(), request.getYearSemesterId(), request.getMembers());
        return success();
    }

    public static class FamilyMemberBatchSaveRequest {
        private Long studentId;
        private Long yearSemesterId;
        private List<StFamilyMember> members;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public Long getYearSemesterId() {
            return yearSemesterId;
        }

        public void setYearSemesterId(Long yearSemesterId) {
            this.yearSemesterId = yearSemesterId;
        }

        public List<StFamilyMember> getMembers() {
            return members;
        }

        public void setMembers(List<StFamilyMember> members) {
            this.members = members;
        }
    }
}


