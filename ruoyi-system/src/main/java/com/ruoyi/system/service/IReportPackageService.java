package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StReportPackage;
import com.ruoyi.system.domain.StReportPackageItem;

/**
 * 报表打包服务接口
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public interface IReportPackageService 
{
    /**
     * 查询报表打包记录
     * 
     * @param id 报表打包记录主键
     * @return 报表打包记录
     */
    public StReportPackage selectStReportPackageById(Long id);

    /**
     * 查询报表打包记录列表
     * 
     * @param stReportPackage 报表打包记录
     * @return 报表打包记录集合
     */
    public List<StReportPackage> selectStReportPackageList(StReportPackage stReportPackage);

    /**
     * 新增报表打包记录（手动选择文件打包）
     * 
     * @param pdfIds PDF记录ID列表
     * @param packageName 打包名称（如果为空，后端自动生成）
     * @return 打包记录
     */
    public StReportPackage createPackage(List<Long> pdfIds, String packageName);

    /**
     * 预览打包信息（不实际打包，仅返回统计信息）
     * 
     * @param pdfIds PDF记录ID列表
     * @return 打包预览信息（包含文件数量、报表统计、建议的打包名称等）
     */
    public java.util.Map<String, Object> previewPackage(List<Long> pdfIds);

    /**
     * 按条件打包全部档案
     * 
     * @param queryParams 查询条件（与StReportPdf查询条件一致）
     * @param packageName 打包名称
     * @return 打包记录
     */
    public StReportPackage createPackageByCondition(com.ruoyi.system.domain.StReportPdf queryParams, String packageName);

    /**
     * 一键打包全部文件包（所有报表）
     * 
     * @param yearSemesterId 学年学期ID（可选）
     * @param schoolingPlanId 学制ID（可选）
     * @param gradeId 年级ID（可选）
     * @param classId 班级ID（可选）
     * @param studentName 学生姓名（可选）
     * @param beginCreateTime 开始时间（可选）
     * @param endCreateTime 结束时间（可选）
     * @param packageName 打包名称
     * @return 打包记录
     */
    public StReportPackage createAllPackage(Long yearSemesterId, Long schoolingPlanId, Long gradeId, Long classId,
                                           String studentName, String beginCreateTime, String endCreateTime,
                                           String packageName);

    /**
     * 混合打包多个文件包（报表）
     * 
     * @param reportNames 报表名称列表
     * @param yearSemesterId 学年学期ID（可选）
     * @param schoolingPlanId 学制ID（可选）
     * @param gradeId 年级ID（可选）
     * @param classId 班级ID（可选）
     * @param studentName 学生姓名（可选）
     * @param beginCreateTime 开始时间（可选）
     * @param endCreateTime 结束时间（可选）
     * @param packageName 打包名称
     * @return 打包记录
     */
    public StReportPackage createMixedPackage(List<String> reportNames, Long yearSemesterId, 
                                             Long schoolingPlanId, Long gradeId, Long classId,
                                             String studentName, String beginCreateTime, String endCreateTime,
                                             String packageName);

    /**
     * 混合打包（报表档案 + 自定义档案包）
     * 
     * @param reportIdentifiers 报表标识列表（reportId或reportName）
     * @param useReportId 是否使用reportId模式（true=reportId, false=reportName）
     * @param customPackageCodes 自定义档案包编码列表
     * @param packageName 打包名称
     * @return 打包记录
     */
    public StReportPackage createHybridPackage(List<String> reportIdentifiers, boolean useReportId, 
                                                List<String> customPackageCodes, String packageName);

    /**
     * 根据打包记录ID查询明细列表
     * 
     * @param packageId 打包记录ID
     * @return 明细列表
     */
    public List<StReportPackageItem> selectPackageItemListByPackageId(Long packageId);

    /**
     * 删除报表打包记录
     * 
     * @param id 报表打包记录主键
     * @return 结果
     */
    public int deleteStReportPackageById(Long id);

    /**
     * 批量删除报表打包记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStReportPackageByIds(Long[] ids);
}

