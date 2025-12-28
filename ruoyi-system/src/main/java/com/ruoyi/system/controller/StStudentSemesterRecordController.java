package com.ruoyi.system.controller;

import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.StStudentSemesterRecord;
import com.ruoyi.system.service.IStStudentSemesterRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 学生学期认定记录Controller
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
@RestController
@RequestMapping("/system/studentRecord")
public class StStudentSemesterRecordController extends BaseController
{
    @Autowired
    private IStStudentSemesterRecordService stStudentSemesterRecordService;

    /**
     * 查询学生学期认定记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:studentRecords:list')")
    @GetMapping("/list")
    public TableDataInfo list(StStudentSemesterRecord stStudentSemesterRecord)
    {
        startPage();
        List<StStudentSemesterRecord> list = stStudentSemesterRecordService.selectStStudentSemesterRecordList(stStudentSemesterRecord);
        return getDataTable(list);
    }

    /**
     * 导出学生学期认定记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:studentRecords:export')")
    @Log(title = "学生学期认定记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StStudentSemesterRecord stStudentSemesterRecord)
    {
        List<StStudentSemesterRecord> list = stStudentSemesterRecordService.selectStStudentSemesterRecordList(stStudentSemesterRecord);
        ExcelUtil<StStudentSemesterRecord> util = new ExcelUtil<StStudentSemesterRecord>(StStudentSemesterRecord.class);
        util.exportExcel(response, list, "学生学期认定记录数据");
    }

    /**
     * 获取学生学期认定记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:studentRecords:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stStudentSemesterRecordService.selectStStudentSemesterRecordById(id));
    }

    /**
     * 新增学生学期认定记录
     */
    @PreAuthorize("@ss.hasPermi('system:studentRecords:add')")
    @Log(title = "学生学期认定记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StStudentSemesterRecord stStudentSemesterRecord)
    {
        return toAjax(stStudentSemesterRecordService.insertStStudentSemesterRecord(stStudentSemesterRecord));
    }

    /**
     * 修改学生学期认定记录
     */
    @PreAuthorize("@ss.hasPermi('system:studentRecords:edit')")
    @Log(title = "学生学期认定记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StStudentSemesterRecord stStudentSemesterRecord)
    {
        return toAjax(stStudentSemesterRecordService.updateStStudentSemesterRecord(stStudentSemesterRecord));
    }

    /**
     * 删除学生学期认定记录
     */
    @PreAuthorize("@ss.hasPermi('system:studentRecords:remove')")
    @Log(title = "学生学期认定记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stStudentSemesterRecordService.deleteStStudentSemesterRecordByIds(ids));
    }

    /**
     * 获取学制列表
     */
    @GetMapping("/schoolPlans")
    public AjaxResult getSchoolPlanList()
    {
        List<Map<String, Object>> list = stStudentSemesterRecordService.selectSchoolPlanList();
        return success(list);
    }

    /**
     * 根据学制ID获取年级列表
     */
    @GetMapping("/grades/{schoolingPlanId}")
    public AjaxResult getGradeList(@PathVariable("schoolingPlanId") Long schoolingPlanId)
    {
        List<Map<String, Object>> list = stStudentSemesterRecordService.selectGradeListByPlanId(schoolingPlanId);
        return success(list);
    }

    /**
     * 根据年级ID获取班级列表
     */
    @GetMapping("/classes/{gradeId}")
    public AjaxResult getClassList(@PathVariable("gradeId") Long gradeId)
    {
        List<Map<String, Object>> list = stStudentSemesterRecordService.selectClassListByGradeId(gradeId);
        return success(list);
    }
    
    /**
     * 同步学生数据到学期记录
     */
    @PreAuthorize("@ss.hasPermi('system:studentRecords:add')")
    @Log(title = "同步学生数据", businessType = BusinessType.INSERT)
    @PostMapping("/sync/{yearSemesterId}")
    public AjaxResult syncStudents(@PathVariable("yearSemesterId") Long yearSemesterId)
    {
        int count = stStudentSemesterRecordService.syncStudentsToSemesterRecords(yearSemesterId);
        return success("成功同步 " + count + " 名学生");
    }
    
    /**
     * 同步单个学生到受助学生信息表
     *
     * @param studentBaseId 学生基础信息ID
     * @param academicYear 学年
     * @param semester 学期
     * @return 同步结果
     */
    @PreAuthorize("@ss.hasPermi('system:studentRecords:add')")
    @Log(title = "同步学生到受助学生信息表", businessType = BusinessType.UPDATE)
    @PostMapping("/syncAided/{studentBaseId}/{academicYear}/{semester}")
    public AjaxResult syncStudentToAidedTable(@PathVariable("studentBaseId") Long studentBaseId,
                                              @PathVariable("academicYear") String academicYear,
                                              @PathVariable("semester") String semester)
    {
        com.ruoyi.system.domain.SyncAidedStudentResult result = stStudentSemesterRecordService.syncStudentsToAidedTable(studentBaseId, academicYear, semester);
        if (result.isSuccess())
        {
            return success(result.getMessage());
        }
        else
        {
            return error(result.getErrorMessage());
        }
    }
    
    /**
     * 同步所有学生到受助学生信息表
     *
     * @param academicYear 学年
     * @param semester 学期
     * @return 同步结果
     */
    @PreAuthorize("@ss.hasPermi('system:studentRecords:add')")
    @Log(title = "同步所有学生到受助学生信息表", businessType = BusinessType.UPDATE)
    @PostMapping("/syncAllAided/{academicYear}/{semester}")
    public AjaxResult syncAllStudentsToAidedTable(@PathVariable("academicYear") String academicYear,
                                                 @PathVariable("semester") String semester)
    {
        com.ruoyi.system.domain.SyncAidedStudentResult result = stStudentSemesterRecordService.syncAllStudentsToAidedTable(academicYear, semester);
        if (result.isSuccess())
        {
            return success(result);
        }
        else
        {
            return error(result.getErrorMessage());
        }
    }
}
