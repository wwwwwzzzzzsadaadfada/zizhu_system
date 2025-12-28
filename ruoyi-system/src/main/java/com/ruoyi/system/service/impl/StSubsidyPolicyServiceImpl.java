package com.ruoyi.system.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.StSubsidyPackage;
import com.ruoyi.system.domain.StSubsidyPolicy;
import com.ruoyi.system.domain.StSubsidyPolicyPackage;
import com.ruoyi.system.domain.StSubsidyPolicyScope;
import com.ruoyi.system.mapper.StSubsidyPackageMapper;
import com.ruoyi.system.mapper.StSubsidyPolicyMapper;
import com.ruoyi.system.mapper.StSubsidyPolicyPackageMapper;
import com.ruoyi.system.mapper.StSubsidyPolicyScopeMapper;
import com.ruoyi.system.service.IStSubsidyPolicyService;

/**
 * 资助政策Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
@Service
public class StSubsidyPolicyServiceImpl implements IStSubsidyPolicyService 
{
    private static final Logger log = LoggerFactory.getLogger(StSubsidyPolicyServiceImpl.class);

    @Autowired
    private StSubsidyPolicyMapper stSubsidyPolicyMapper;
    
    @Autowired
    private StSubsidyPolicyScopeMapper stSubsidyPolicyScopeMapper;
    
    @Autowired
    private StSubsidyPolicyPackageMapper stSubsidyPolicyPackageMapper;
    
    @Autowired
    private StSubsidyPackageMapper stSubsidyPackageMapper;

    /**
     * 查询资助政策
     * 
     * @param id 资助政策主键
     * @return 资助政策
     */
    @Override
    public StSubsidyPolicy selectStSubsidyPolicyById(Long id)
    {
        if (id == null) {
            throw new ServiceException("政策ID不能为空");
        }
        
        StSubsidyPolicy policy = stSubsidyPolicyMapper.selectStSubsidyPolicyById(id);
        if (policy != null) {
            // 加载关联数据
            loadPolicyRelations(policy);
        }
        return policy;
    }

    /**
     * 查询资助政策列表
     * 
     * @param stSubsidyPolicy 资助政策
     * @return 资助政策
     */
    @Override
    public List<StSubsidyPolicy> selectStSubsidyPolicyList(StSubsidyPolicy stSubsidyPolicy)
    {
        return stSubsidyPolicyMapper.selectStSubsidyPolicyList(stSubsidyPolicy);
    }

    /**
     * 查询有效政策列表
     * 
     * @param schoolingPlanId 学制ID（可选）
     * @param yearSemesterId 学年学期ID（可选）
     * @return 资助政策集合
     */
    @Override
    public List<StSubsidyPolicy> selectEffectivePolicies(Long schoolingPlanId, Long yearSemesterId)
    {
        Date currentDate = new Date();
        List<StSubsidyPolicy> policies = stSubsidyPolicyMapper.selectEffectivePolicies(schoolingPlanId, yearSemesterId, currentDate);
        
        // 加载关联数据
        for (StSubsidyPolicy policy : policies) {
            loadPolicyRelations(policy);
        }
        
        return policies;
    }

    /**
     * 新增资助政策
     * 
     * @param stSubsidyPolicy 资助政策
     * @return 结果
     */
    @Override
    @Transactional
    public int insertStSubsidyPolicy(StSubsidyPolicy stSubsidyPolicy)
    {
        // 1. 数据验证
        validatePolicy(stSubsidyPolicy, null);
        
        // 2. 生成政策编号（如果未提供）
        if (StringUtils.isEmpty(stSubsidyPolicy.getPolicyCode())) {
            stSubsidyPolicy.setPolicyCode(generatePolicyCode());
        }
        
        // 3. 检查政策编号唯一性
        if (stSubsidyPolicyMapper.countByPolicyCode(stSubsidyPolicy.getPolicyCode(), null) > 0) {
            throw new ServiceException("政策编号已存在：" + stSubsidyPolicy.getPolicyCode());
        }
        
        // 4. 设置创建信息
        String username = SecurityUtils.getUsername();
        stSubsidyPolicy.setCreatedBy(username);
        stSubsidyPolicy.setUpdatedBy(username);
        if (stSubsidyPolicy.getStatus() == null) {
            stSubsidyPolicy.setStatus(0); // 默认草稿状态
        }
        
        // 5. 插入政策主表
        int result = stSubsidyPolicyMapper.insertStSubsidyPolicy(stSubsidyPolicy);
        if (result <= 0) {
            throw new ServiceException("新增政策失败");
        }
        
        Long policyId = stSubsidyPolicy.getId();
        
        // 6. 保存适用范围
        if (stSubsidyPolicy.getScopeList() != null && !stSubsidyPolicy.getScopeList().isEmpty()) {
            for (StSubsidyPolicyScope scope : stSubsidyPolicy.getScopeList()) {
                scope.setPolicyId(policyId);
            }
            stSubsidyPolicyScopeMapper.batchInsertStSubsidyPolicyScope(stSubsidyPolicy.getScopeList());
        }
        
        // 7. 保存关联套餐
        if (stSubsidyPolicy.getPackageList() != null && !stSubsidyPolicy.getPackageList().isEmpty()) {
            // 过滤掉packageId为null的项
            List<StSubsidyPolicyPackage> validPackageList = new ArrayList<>();
            for (StSubsidyPolicyPackage policyPackage : stSubsidyPolicy.getPackageList()) {
                if (policyPackage.getPackageId() == null) {
                    continue; // 跳过packageId为null的项
                }
                
                policyPackage.setPolicyId(policyId);
                
                // 验证套餐是否存在
                StSubsidyPackage pkg = stSubsidyPackageMapper.selectStSubsidyPackageById(policyPackage.getPackageId());
                if (pkg == null) {
                    throw new ServiceException("关联的补助套餐不存在，ID：" + policyPackage.getPackageId());
                }
                
                // 强制从套餐表填充冗余字段，确保数据一致性（所有逻辑操作在后端）
                policyPackage.setPackageName(pkg.getPackageName());
                policyPackage.setSubsidyAmount(pkg.getSubsidyAmount());
                
                validPackageList.add(policyPackage);
            }
            
            // 只插入有效的套餐关联
            if (!validPackageList.isEmpty()) {
                stSubsidyPolicyPackageMapper.batchInsertStSubsidyPolicyPackage(validPackageList);
            }
        }
        
        log.info("新增政策成功，政策ID={}，政策编号={}，创建人={}", policyId, stSubsidyPolicy.getPolicyCode(), username);
        return result;
    }

    /**
     * 修改资助政策
     * 
     * @param stSubsidyPolicy 资助政策
     * @return 结果
     */
    @Override
    @Transactional
    public int updateStSubsidyPolicy(StSubsidyPolicy stSubsidyPolicy)
    {
        if (stSubsidyPolicy.getId() == null) {
            throw new ServiceException("政策ID不能为空");
        }
        
        // 1. 检查政策是否存在
        StSubsidyPolicy existingPolicy = stSubsidyPolicyMapper.selectStSubsidyPolicyById(stSubsidyPolicy.getId());
        if (existingPolicy == null) {
            throw new ServiceException("政策不存在，ID：" + stSubsidyPolicy.getId());
        }
        
        // 2. 已发布或已废止的政策不允许修改（只能废止后重新创建）
        if (existingPolicy.getStatus() != null && existingPolicy.getStatus() != 0) {
            throw new ServiceException("已发布或已废止的政策不允许修改，如需修改请先废止该政策");
        }
        
        // 3. 数据验证
        validatePolicy(stSubsidyPolicy, stSubsidyPolicy.getId());
        
        // 4. 检查政策编号唯一性
        if (StringUtils.isNotEmpty(stSubsidyPolicy.getPolicyCode())) {
            if (stSubsidyPolicyMapper.countByPolicyCode(stSubsidyPolicy.getPolicyCode(), stSubsidyPolicy.getId()) > 0) {
                throw new ServiceException("政策编号已存在：" + stSubsidyPolicy.getPolicyCode());
            }
        }
        
        // 5. 设置更新信息
        stSubsidyPolicy.setUpdatedBy(SecurityUtils.getUsername());
        
        // 6. 更新政策主表
        int result = stSubsidyPolicyMapper.updateStSubsidyPolicy(stSubsidyPolicy);
        if (result <= 0) {
            throw new ServiceException("更新政策失败");
        }
        
        Long policyId = stSubsidyPolicy.getId();
        
        // 7. 更新适用范围（先删除后插入）
        stSubsidyPolicyScopeMapper.deleteStSubsidyPolicyScopeByPolicyId(policyId);
        if (stSubsidyPolicy.getScopeList() != null && !stSubsidyPolicy.getScopeList().isEmpty()) {
            for (StSubsidyPolicyScope scope : stSubsidyPolicy.getScopeList()) {
                scope.setPolicyId(policyId);
            }
            stSubsidyPolicyScopeMapper.batchInsertStSubsidyPolicyScope(stSubsidyPolicy.getScopeList());
        }
        
        // 8. 更新关联套餐（先删除后插入）
        stSubsidyPolicyPackageMapper.deleteStSubsidyPolicyPackageByPolicyId(policyId);
        if (stSubsidyPolicy.getPackageList() != null && !stSubsidyPolicy.getPackageList().isEmpty()) {
            // 过滤掉packageId为null的项
            List<StSubsidyPolicyPackage> validPackageList = new ArrayList<>();
            for (StSubsidyPolicyPackage policyPackage : stSubsidyPolicy.getPackageList()) {
                if (policyPackage.getPackageId() == null) {
                    continue; // 跳过packageId为null的项
                }
                
                policyPackage.setPolicyId(policyId);
                
                // 验证套餐是否存在
                StSubsidyPackage pkg = stSubsidyPackageMapper.selectStSubsidyPackageById(policyPackage.getPackageId());
                if (pkg == null) {
                    throw new ServiceException("关联的补助套餐不存在，ID：" + policyPackage.getPackageId());
                }
                
                // 强制从套餐表填充冗余字段，确保数据一致性（所有逻辑操作在后端）
                policyPackage.setPackageName(pkg.getPackageName());
                policyPackage.setSubsidyAmount(pkg.getSubsidyAmount());
                
                validPackageList.add(policyPackage);
            }
            
            // 只插入有效的套餐关联
            if (!validPackageList.isEmpty()) {
                stSubsidyPolicyPackageMapper.batchInsertStSubsidyPolicyPackage(validPackageList);
            }
        }
        
        // 9. 附件不在这里更新，单独管理
        
        log.info("更新政策成功，政策ID={}，更新人={}", policyId, stSubsidyPolicy.getUpdatedBy());
        return result;
    }

    /**
     * 批量删除资助政策
     * 
     * @param ids 需要删除的资助政策主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteStSubsidyPolicyByIds(Long[] ids)
    {
        if (ids == null || ids.length == 0) {
            throw new ServiceException("请选择要删除的政策");
        }
        
        // 检查是否可以删除
        for (Long id : ids) {
            StSubsidyPolicy policy = stSubsidyPolicyMapper.selectStSubsidyPolicyById(id);
            if (policy == null) {
                continue;
            }
            
            // 已发布或已废止的政策不允许删除
            if (policy.getStatus() != null && policy.getStatus() != 0) {
                throw new ServiceException("已发布或已废止的政策不允许删除，政策编号：" + policy.getPolicyCode());
            }
        }
        
        // 删除关联数据（级联删除，但为了安全还是显式删除）
        for (Long id : ids) {
            stSubsidyPolicyScopeMapper.deleteStSubsidyPolicyScopeByPolicyId(id);
            stSubsidyPolicyPackageMapper.deleteStSubsidyPolicyPackageByPolicyId(id);
        }
        
        return stSubsidyPolicyMapper.deleteStSubsidyPolicyByIds(ids);
    }

    /**
     * 删除资助政策信息
     * 
     * @param id 资助政策主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteStSubsidyPolicyById(Long id)
    {
        if (id == null) {
            throw new ServiceException("政策ID不能为空");
        }
        
        StSubsidyPolicy policy = stSubsidyPolicyMapper.selectStSubsidyPolicyById(id);
        if (policy == null) {
            throw new ServiceException("政策不存在，ID：" + id);
        }
        
        // 已发布或已废止的政策不允许删除
        if (policy.getStatus() != null && policy.getStatus() != 0) {
            throw new ServiceException("已发布或已废止的政策不允许删除，政策编号：" + policy.getPolicyCode());
        }
        
        // 删除关联数据
        stSubsidyPolicyScopeMapper.deleteStSubsidyPolicyScopeByPolicyId(id);
        stSubsidyPolicyPackageMapper.deleteStSubsidyPolicyPackageByPolicyId(id);
        
        return stSubsidyPolicyMapper.deleteStSubsidyPolicyById(id);
    }

    /**
     * 发布政策
     * 
     * @param id 政策ID
     * @param publisher 发布人
     * @return 结果
     */
    @Override
    @Transactional
    public int publishPolicy(Long id, String publisher)
    {
        if (id == null) {
            throw new ServiceException("政策ID不能为空");
        }
        
        StSubsidyPolicy policy = stSubsidyPolicyMapper.selectStSubsidyPolicyById(id);
        if (policy == null) {
            throw new ServiceException("政策不存在，ID：" + id);
        }
        
        // 只能发布草稿状态的政策
        if (policy.getStatus() == null || policy.getStatus() != 0) {
            throw new ServiceException("只能发布草稿状态的政策，当前状态：" + getStatusText(policy.getStatus()));
        }
        
        // 验证必填项
        if (StringUtils.isEmpty(policy.getPolicyName())) {
            throw new ServiceException("政策名称不能为空");
        }
        if (policy.getEffectiveDate() == null) {
            throw new ServiceException("生效日期不能为空");
        }
        if (policy.getEffectiveDate().before(new Date())) {
            throw new ServiceException("生效日期不能早于当前日期");
        }
        if (policy.getExpiryDate() != null && policy.getExpiryDate().before(policy.getEffectiveDate())) {
            throw new ServiceException("失效日期不能早于生效日期");
        }
        
        // 检查是否有冲突的有效政策
        if (StringUtils.isNotEmpty(policy.getPolicyType())) {
            // 检查每个适用范围的冲突
            List<StSubsidyPolicyScope> scopeList = stSubsidyPolicyScopeMapper.selectStSubsidyPolicyScopeByPolicyId(id);
            if (scopeList != null && !scopeList.isEmpty()) {
                for (StSubsidyPolicyScope scope : scopeList) {
                    int conflictCount = stSubsidyPolicyMapper.countConflictingPolicies(
                        id, policy.getPolicyType(), scope.getSchoolingPlanId(), 
                        policy.getEffectiveDate(), policy.getExpiryDate()
                    );
                    if (conflictCount > 0) {
                        throw new ServiceException("存在冲突的有效政策，请检查政策类型、适用范围和生效时间");
                    }
                }
            } else {
                // 如果没有适用范围，检查所有学制
                int conflictCount = stSubsidyPolicyMapper.countConflictingPolicies(
                    id, policy.getPolicyType(), null, 
                    policy.getEffectiveDate(), policy.getExpiryDate()
                );
                if (conflictCount > 0) {
                    throw new ServiceException("存在冲突的有效政策，请检查政策类型和生效时间");
                }
            }
        }
        
        // 发布政策
        String publisherName = StringUtils.isNotEmpty(publisher) ? publisher : SecurityUtils.getUsername();
        Date publishTime = new Date();
        int result = stSubsidyPolicyMapper.publishPolicy(id, publishTime, publisherName);
        
        if (result > 0) {
            log.info("发布政策成功，政策ID={}，政策编号={}，发布人={}", id, policy.getPolicyCode(), publisherName);
        }
        
        return result;
    }

    /**
     * 废止政策
     * 
     * @param id 政策ID
     * @param abolishReason 废止原因
     * @return 结果
     */
    @Override
    @Transactional
    public int abolishPolicy(Long id, String abolishReason)
    {
        if (id == null) {
            throw new ServiceException("政策ID不能为空");
        }
        
        StSubsidyPolicy policy = stSubsidyPolicyMapper.selectStSubsidyPolicyById(id);
        if (policy == null) {
            throw new ServiceException("政策不存在，ID：" + id);
        }
        
        // 只能废止已发布状态的政策
        if (policy.getStatus() == null || policy.getStatus() != 1) {
            throw new ServiceException("只能废止已发布状态的政策，当前状态：" + getStatusText(policy.getStatus()));
        }
        
        // 废止政策
        Date abolishTime = new Date();
        int result = stSubsidyPolicyMapper.abolishPolicy(id, abolishTime, abolishReason);
        
        if (result > 0) {
            log.info("废止政策成功，政策ID={}，政策编号={}，废止原因={}", id, policy.getPolicyCode(), abolishReason);
        }
        
        return result;
    }

    /**
     * 验证政策数据
     */
    private void validatePolicy(StSubsidyPolicy policy, Long excludeId)
    {
        // 政策名称必填
        if (StringUtils.isEmpty(policy.getPolicyName())) {
            throw new ServiceException("政策名称不能为空");
        }
        
        // 政策版本必填
        if (StringUtils.isEmpty(policy.getPolicyVersion())) {
            throw new ServiceException("政策版本不能为空");
        }
        
        // 生效日期必填
        if (policy.getEffectiveDate() == null) {
            throw new ServiceException("生效日期不能为空");
        }
        
        // 失效日期不能早于生效日期
        if (policy.getExpiryDate() != null && policy.getExpiryDate().before(policy.getEffectiveDate())) {
            throw new ServiceException("失效日期不能早于生效日期");
        }
        
        // 政策编号格式验证（如果提供）
        if (StringUtils.isNotEmpty(policy.getPolicyCode())) {
            if (policy.getPolicyCode().length() > 50) {
                throw new ServiceException("政策编号长度不能超过50个字符");
            }
        }
    }

    /**
     * 生成政策编号
     */
    private String generatePolicyCode()
    {
        // 格式：ZC-YYYYMMDD-序号
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String prefix = "ZC-" + sdf.format(new Date()) + "-";
        // 查询当天已生成的数量
        StSubsidyPolicy query = new StSubsidyPolicy();
        query.setPolicyCode(prefix);
        List<StSubsidyPolicy> list = stSubsidyPolicyMapper.selectStSubsidyPolicyList(query);
        int count = list != null ? list.size() : 0;
        return prefix + String.format("%03d", count + 1);
    }

    /**
     * 加载政策的关联数据
     */
    private void loadPolicyRelations(StSubsidyPolicy policy)
    {
        Long policyId = policy.getId();
        
        // 加载适用范围
        List<StSubsidyPolicyScope> scopeList = stSubsidyPolicyScopeMapper.selectStSubsidyPolicyScopeByPolicyId(policyId);
        policy.setScopeList(scopeList);
        
        // 加载关联套餐
        List<StSubsidyPolicyPackage> packageList = stSubsidyPolicyPackageMapper.selectStSubsidyPolicyPackageByPolicyId(policyId);
        policy.setPackageList(packageList);
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(Integer status)
    {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "草稿";
            case 1:
                return "已发布";
            case 2:
                return "已废止";
            default:
                return "未知";
        }
    }
}

