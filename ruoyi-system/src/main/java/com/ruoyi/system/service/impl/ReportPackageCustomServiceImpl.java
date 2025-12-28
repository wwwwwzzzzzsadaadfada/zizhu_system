package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.StReportPackageCustom;
import com.ruoyi.system.domain.StReportAttachment;
import com.ruoyi.system.mapper.StReportPackageCustomMapper;
import com.ruoyi.system.mapper.StReportAttachmentMapper;
import com.ruoyi.system.service.IReportPackageCustomService;

/**
 * 自定义档案包Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
@Service
public class ReportPackageCustomServiceImpl implements IReportPackageCustomService 
{
    private static final Logger log = LoggerFactory.getLogger(ReportPackageCustomServiceImpl.class);

    @Autowired
    private StReportPackageCustomMapper stReportPackageCustomMapper;

    @Autowired
    private StReportAttachmentMapper stReportAttachmentMapper;

    /**
     * 查询自定义档案包
     * 
     * @param id 自定义档案包主键
     * @return 自定义档案包
     */
    @Override
    public StReportPackageCustom selectStReportPackageCustomById(Long id)
    {
        return stReportPackageCustomMapper.selectStReportPackageCustomById(id);
    }

    /**
     * 根据档案包编码查询
     * 
     * @param packageCode 档案包编码
     * @return 自定义档案包
     */
    @Override
    public StReportPackageCustom selectStReportPackageCustomByCode(String packageCode)
    {
        return stReportPackageCustomMapper.selectStReportPackageCustomByCode(packageCode);
    }

    /**
     * 查询自定义档案包列表
     * 
     * @param stReportPackageCustom 自定义档案包
     * @return 自定义档案包
     */
    @Override
    public List<StReportPackageCustom> selectStReportPackageCustomList(StReportPackageCustom stReportPackageCustom)
    {
        return stReportPackageCustomMapper.selectStReportPackageCustomList(stReportPackageCustom);
    }

    /**
     * 创建自定义档案包
     * 
     * @param packageName 档案包名称
     * @param description 描述
     * @return 自定义档案包
     */
    @Override
    @Transactional
    public StReportPackageCustom createCustomPackage(String packageName, String description)
    {
        if (StringUtils.isBlank(packageName))
        {
            throw new ServiceException("档案包名称不能为空");
        }

        // 生成唯一的档案包编码
        String packageCode = generatePackageCode();

        // 创建档案包记录
        StReportPackageCustom packageCustom = new StReportPackageCustom();
        packageCustom.setPackageCode(packageCode);
        packageCustom.setPackageName(packageName);
        packageCustom.setPackageType("custom");
        packageCustom.setDescription(description);
        packageCustom.setFileCount(0);
        packageCustom.setTotalSize(0L);
        packageCustom.setStatus(1);
        packageCustom.setCreateBy(SecurityUtils.getUsername());

        stReportPackageCustomMapper.insertStReportPackageCustom(packageCustom);

        log.info("创建自定义档案包成功: packageCode={}, packageName={}", packageCode, packageName);

        return packageCustom;
    }

    /**
     * 新增自定义档案包
     * 
     * @param stReportPackageCustom 自定义档案包
     * @return 结果
     */
    @Override
    public int insertStReportPackageCustom(StReportPackageCustom stReportPackageCustom)
    {
        if (StringUtils.isBlank(stReportPackageCustom.getPackageCode()))
        {
            stReportPackageCustom.setPackageCode(generatePackageCode());
        }
        return stReportPackageCustomMapper.insertStReportPackageCustom(stReportPackageCustom);
    }

    /**
     * 修改自定义档案包
     * 
     * @param stReportPackageCustom 自定义档案包
     * @return 结果
     */
    @Override
    public int updateStReportPackageCustom(StReportPackageCustom stReportPackageCustom)
    {
        return stReportPackageCustomMapper.updateStReportPackageCustom(stReportPackageCustom);
    }

    /**
     * 批量删除自定义档案包
     * 
     * @param ids 需要删除的自定义档案包主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteStReportPackageCustomByIds(Long[] ids)
    {
        // 逻辑删除档案包
        int result = stReportPackageCustomMapper.deleteStReportPackageCustomByIds(ids);
        
        // 同时删除关联的附件
        for (Long id : ids)
        {
            StReportPackageCustom pkg = stReportPackageCustomMapper.selectStReportPackageCustomById(id);
            if (pkg != null && StringUtils.isNotBlank(pkg.getPackageCode()))
            {
                stReportAttachmentMapper.deleteAttachmentByTypeAndRelatedId("custom", pkg.getPackageCode());
            }
        }
        
        return result;
    }

    /**
     * 删除自定义档案包信息
     * 
     * @param id 自定义档案包主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteStReportPackageCustomById(Long id)
    {
        // 查询档案包信息
        StReportPackageCustom pkg = stReportPackageCustomMapper.selectStReportPackageCustomById(id);
        if (pkg != null && StringUtils.isNotBlank(pkg.getPackageCode()))
        {
            // 删除关联的附件
            stReportAttachmentMapper.deleteAttachmentByTypeAndRelatedId("custom", pkg.getPackageCode());
        }
        
        // 逻辑删除档案包
        return stReportPackageCustomMapper.deleteStReportPackageCustomById(id);
    }

    /**
     * 更新档案包文件统计信息
     * 
     * @param packageCode 档案包编码
     * @return 结果
     */
    @Override
    public int updateFileStatistics(String packageCode)
    {
        StReportPackageCustom pkg = stReportPackageCustomMapper.selectStReportPackageCustomByCode(packageCode);
        if (pkg == null)
        {
            throw new ServiceException("档案包不存在");
        }

        // 查询附件列表统计
        List<StReportAttachment> attachments = stReportAttachmentMapper.selectAttachmentByTypeAndRelatedId("custom", packageCode);
        
        int fileCount = attachments != null ? attachments.size() : 0;
        long totalSize = 0L;
        
        if (attachments != null)
        {
            for (StReportAttachment attachment : attachments)
            {
                if (attachment.getFileSize() != null)
                {
                    totalSize += attachment.getFileSize();
                }
            }
        }

        // 更新档案包统计信息
        pkg.setFileCount(fileCount);
        pkg.setTotalSize(totalSize);
        pkg.setUpdateBy(SecurityUtils.getUsername());

        return stReportPackageCustomMapper.updateStReportPackageCustom(pkg);
    }

    /**
     * 生成唯一的档案包编码
     * 
     * @return 档案包编码
     */
    private String generatePackageCode()
    {
        // 格式：CUSTOM_yyyyMMddHHmmss_随机字符
        String timestamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "CUSTOM_" + timestamp + "_" + random;
    }
}
