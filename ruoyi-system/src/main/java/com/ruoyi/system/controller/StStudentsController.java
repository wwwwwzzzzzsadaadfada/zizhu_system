package com.ruoyi.system.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.*;
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
import com.ruoyi.system.domain.StStudents;
import com.ruoyi.system.domain.dto.BatchDifficultyRequest;
import java.io.IOException;
import com.ruoyi.system.service.IStStudentsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 困难学生基础信息Controller
 * 
 * @author ruoyi
 * @date 2025-11-19
 */
@RestController
@RequestMapping("/system/students")
public class StStudentsController extends BaseController
{
    @Autowired
    private IStStudentsService stStudentsService;

    /**
     * 查询困难学生基础信息列表
     * 支持查询未认定的学生（用于批量认定功能）
     */
    @PreAuthorize("@ss.hasPermi('system:students:list')")
    @GetMapping("/list")
    public TableDataInfo list(StStudents stStudents)
    {
        startPage();
        List<StStudents> list = stStudentsService.selectStStudentsList(stStudents);
        return getDataTable(list);
    }

    /**
     * 导出困难学生基础信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:students:export')")
    @Log(title = "困难学生基础信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StStudents stStudents)
    {
        List<StStudents> list = stStudentsService.selectStStudentsListForExport(stStudents);
        ExcelUtil<StStudents> util = new ExcelUtil<StStudents>(StStudents.class);
        util.exportExcel(response, list, "困难学生基础信息数据");
    }
    
    /**
     * 获取困难学生基础信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:students:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(
        @PathVariable("id") @NotBlank(message = "ID不能为空") String id
    )
    {
        StStudents student = stStudentsService.selectStStudentsById(id);
        if (student == null) {
            return error("学生信息不存在");
        }
        return success(student);
    }

    /**
     * 新增困难学生基础信息
     * 
     * 返回数据说明：
     * - 新增成功后返回学生完整信息（包括ID）
     * - 前端根据返回ID判断是否需要留在当前页面
     */
    @PreAuthorize("@ss.hasPermi('system:students:add')")
    @Log(title = "困难学生基础信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Valid @RequestBody StStudents stStudents)
    {
        int rows = stStudentsService.insertStStudents(stStudents);
        
        if (rows > 0) {
            // 新增成功，返回学生ID和提示信息
            return AjaxResult.success(
                "学生基本信息保存成功，现在可以添加家庭成员和银行卡信息",
                stStudents
            );
        } else {
            return error("保存学生基本信息失败");
        }
    }

    /**
     * 修改困难学生基础信息
     * 
     * 返回数据说明：
     * - 编辑模式保存成功后返回成功信息
     * - 前端根据返回信息判断是否跳转回列表页
     */
    @PreAuthorize("@ss.hasPermi('system:students:edit')")
    @Log(title = "困难学生基础信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Valid @RequestBody StStudents stStudents)
    {
        int rows = stStudentsService.updateStStudents(stStudents);
        
        if (rows > 0) {
            // 更新成功
            return AjaxResult.success("保存成功");
        } else {
            return error("保存失败");
        }
    }

    /**
     * 删除困难学生基础信息
     */
    @PreAuthorize("@ss.hasPermi('system:students:remove')")
    @Log(title = "困难学生基础信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(
        @PathVariable @NotEmpty(message = "删除ID列表不能为NULL") String[] ids
    )
    {
        if (ids.length > 100) {
            return error("单次删除不能超过100条记录");
        }
        return toAjax(stStudentsService.deleteStStudentsByIds(ids));
    }

    /**
     * 获取学制列表
     */
    @GetMapping("/schoolPlans")
    public AjaxResult getSchoolPlanList()
    {
        List<Map<String, Object>> list = stStudentsService.selectSchoolPlanList();
        return success(list);
    }

    /**
     * 根据学制ID获取年级列表
     */
    @GetMapping("/grades/{schoolingPlanId}")
    public AjaxResult getGradeList(
        @PathVariable("schoolingPlanId") @NotNull(message = "学制ID不能为NULL") Long schoolingPlanId
    )
    {
        List<Map<String, Object>> list = stStudentsService.selectGradeListByPlanId(
            String.valueOf(schoolingPlanId)
        );
        return success(list);
    }

    /**
     * 根据年级ID获取班级列表
     */
    @GetMapping("/classes/{gradeId}")
    public AjaxResult getClassList(
        @PathVariable("gradeId") @NotNull(message = "年级ID不能为NULL") Long gradeId
    )
    {
        List<Map<String, Object>> list = stStudentsService.selectClassListByGradeId(
            String.valueOf(gradeId)
        );
        return success(list);
    }

    /**
     * 获取学制-年级-班级的树状结构（用于前端级联选择器）
     * 
     * 返回格式：
     * [
     *   {
     *     "value": 学制ID,
     *     "label": "学制名称",
     *     "children": [
     *       {
     *         "value": 年级ID,
     *         "label": "年级名称",
     *         "children": [
     *           {
     *             "value": 班级ID,
     *             "label": "班级名称"
     *           }
     *         ]
     *       }
     *     ]
     *   }
     * ]
     * 
     * @return 学制-年级-班级树状结构
     */
    @GetMapping("/schoolPlanGradeClassTree")
    public AjaxResult getSchoolPlanGradeClassTree()
    {
        try
        {
            List<Map<String, Object>> tree = stStudentsService.buildSchoolPlanGradeClassTree();
            return success(tree);
        }
        catch (Exception e)
        {
            logger.error("获取学制-年级-班级树状结构失败", e);
            return error("获取学制-年级-班级树状结构失败：" + e.getMessage());
        }
    }

    /**
     * 批量更新困难类型和等级
     */
    @PreAuthorize("@ss.hasPermi('system:students:edit')")
    @Log(title = "困难学生基础信息", businessType = BusinessType.UPDATE)
    @PutMapping("/batchUpdateDifficulty")
    public AjaxResult batchUpdateDifficulty(@Valid @RequestBody BatchDifficultyRequest request)
    {
        if (request.getStudentIds() == null || request.getStudentIds().isEmpty()) {
            return error("学生ID列表不能为空");
        }
        
        // 验证是否至少更新一个字段
        if (request.getDifficultyTypeId() == null && 
            request.getDifficultyLevelId() == null && 
            request.getIsPovertyReliefFamily() == null && 
            request.getPovertyReliefYear() == null) {
            return error("至少需要更新一个字段");
        }
        
        String[] ids = request.getStudentIds().toArray(new String[0]);
        int rows = stStudentsService.batchUpdateDifficultyInfo(
            ids,
            request.getDifficultyTypeId(),
            request.getDifficultyLevelId(),
            request.getIsPovertyReliefFamily(),
            request.getPovertyReliefYear()
        );
        
        return toAjax(rows);
    }

    /**
     * 导入学生数据
     */
    @PreAuthorize("@ss.hasPermi('system:students:import')")
    @Log(title = "困难学生基础信息", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importStudents(MultipartFile file, Boolean isUpdateSupport) {
        // ======== 第一步：文件基本验证 ========
        if (file == null || file.isEmpty()) {
            return error("请选择要上传的文件！");
        }
        
        // ======== 第二步：文件类型验证 ========
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return error("文件名不能为空！");
        }
        
        String fileName = originalFilename.toLowerCase();
        boolean isValidFileType = fileName.endsWith(".xlsx") || fileName.endsWith(".xls");
        if (!isValidFileType) {
            return error("文件格式不正确，请选择 Excel 文件（.xlsx 或 .xls）");
        }
        
        // ======== 第三步：文件大小验证 ========
        long maxFileSize = 10 * 1024 * 1024; // 10MB
        long fileSize = file.getSize();
        if (fileSize > maxFileSize) {
            double fileSizeMB = fileSize / 1024.0 / 1024.0;
            return error(String.format("文件大小不能超过 10MB，当前文件大小：%.2f MB", fileSizeMB));
        }
        
        // ======== 第四步：文件名安全性验证 ========
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            return error("文件名不合法，包含非法字符");
        }
        
        // ======== 第五步：执行导入 ========
        try {
            // 读取Excel文件并转换为学生列表
            ExcelUtil<StStudents> util = new ExcelUtil<>(StStudents.class);
            List<StStudents> studentsList = util.importExcel(file.getInputStream());
            
            if (studentsList == null || studentsList.isEmpty()) {
                return error("导入文件为空，请检查文件内容");
            }
            
            String operName = getUsername();
            String message = stStudentsService.importStudents(studentsList, isUpdateSupport, operName);
            return success(message);
        } catch (Exception e) {
            logger.error("导入学生数据失败", e);
            String msg = "导入失败！" + e.getMessage();
            return error(msg);
        }
    }
    
    /**
     * 下载导入模板
     */
    @PreAuthorize("@ss.hasPermi('system:students:import')")
    @Log(title = "困难学生基础信息", businessType = BusinessType.EXPORT)
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        try {
            ExcelUtil<StStudents> util = new ExcelUtil<>(StStudents.class);
            util.importTemplateExcel(response, "学生数据");
        } catch (Exception e) {
            logger.error("导出模板异常:", e);
        }
    }
}
