package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.StSchoolYearSemester;
import com.ruoyi.system.domain.StSemesterBudget;
import com.ruoyi.system.domain.StSubsidyQuota;
import com.ruoyi.system.domain.StSubsidyQuotaDetail;
import com.ruoyi.system.domain.dto.CarryOverToBudgetRequest;
import com.ruoyi.system.domain.dto.BatchQuotaAllocationRequest;
import com.ruoyi.system.domain.dto.QuotaAllocationRequest;
import com.ruoyi.system.domain.vo.QuotaStatisticVO;
import com.ruoyi.system.mapper.StSchoolYearSemesterMapper;
import com.ruoyi.system.mapper.StSemesterBudgetMapper;
import com.ruoyi.system.mapper.StSubsidyQuotaDetailMapper;
import com.ruoyi.system.mapper.StSubsidyQuotaMapper;
import com.ruoyi.system.service.IStSubsidyQuotaService;
import com.ruoyi.system.application.query.QuotaBusinessRule;

/**
 * 资助指标下达Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-20
 */
@Service
public class StSubsidyQuotaServiceImpl implements IStSubsidyQuotaService 
{
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Autowired
    private StSubsidyQuotaMapper stSubsidyQuotaMapper;

    @Autowired
    private StSubsidyQuotaDetailMapper stSubsidyQuotaDetailMapper;

    @Autowired
    private StSemesterBudgetMapper stSemesterBudgetMapper;
    
    @Autowired
    private StSchoolYearSemesterMapper stSchoolYearSemesterMapper;

    /**
     * 查询资助指标下达
     * 
     * @param id 资助指标下达主键
     * @return 资助指标下达
     */
    @Override
    public StSubsidyQuota selectStSubsidyQuotaById(Long id)
    {
        StSubsidyQuota quota = stSubsidyQuotaMapper.selectStSubsidyQuotaById(id);
        if (quota != null) {
            List<StSubsidyQuotaDetail> detailList = stSubsidyQuotaDetailMapper.selectDetailListByQuotaId(id);
            attachComputedFields(detailList);
            // 如果是指标详情查询（用于结转），需要计算每个经济分类的实际可结转金额
            // 实际可结转金额 = 原始剩余额度 - 已结转金额
            // 注意：这个方法会修改 detailList 中每个 detail 的 availableAmount
            calculateCarryOverAvailableAmount(detailList, id);
            quota.setDetailList(detailList);
            // 在Java代码中设置计算字段（业务逻辑从SQL移到Java代码）
            StSchoolYearSemester currentSemester = stSchoolYearSemesterMapper.selectCurrentSemester(true);
            QuotaBusinessRule.attachComputedFields(quota, currentSemester, detailList);
            // 设置来源指标文号（如果需要）
            if (quota.getSourceQuotaId() != null && quota.getSourceQuotaDocNo() == null) {
                StSubsidyQuota sourceQuota = stSubsidyQuotaMapper.selectStSubsidyQuotaById(quota.getSourceQuotaId());
                if (sourceQuota != null) {
                    quota.setSourceQuotaDocNo(sourceQuota.getQuotaDocNo());
                }
            }
            // 设置经济分类文本
            quota.setEconomyCategoryText(formatEconomyCategoryText(quota));
        }
        return quota;
    }
    
    /**
     * 计算每个经济分类的实际可结转金额（原始剩余 - 已结转）
     * 
     * @param detailList 明细列表
     * @param quotaId 指标ID
     */
    private void calculateCarryOverAvailableAmount(List<StSubsidyQuotaDetail> detailList, Long quotaId) {
        if (detailList == null || detailList.isEmpty()) {
            return;
        }
        
        // 查询该指标作为来源指标时，每个经济分类已结转的金额
        List<Map<String, Object>> carriedOverList = stSubsidyQuotaMapper.getCarriedOverAmountByEconomyCategory(quotaId);
        
        // 转换为Map，key为经济分类，value为已结转金额
        Map<String, BigDecimal> carriedOverMap = new HashMap<>();
        if (carriedOverList != null) {
            for (Map<String, Object> row : carriedOverList) {
                String economyCategory = row.get("economyCategory") != null ? String.valueOf(row.get("economyCategory")) : null;
                Object amountObj = row.get("carriedOverAmount");
                BigDecimal amount = amountObj instanceof BigDecimal ? (BigDecimal) amountObj :
                                   amountObj != null ? new BigDecimal(amountObj.toString()) : ZERO;
                if (economyCategory != null && !economyCategory.isEmpty()) {
                    carriedOverMap.put(economyCategory, amount);
                }
            }
        }
        
        // 计算每个明细的实际可结转金额
        for (StSubsidyQuotaDetail detail : detailList) {
            // 先保存原始剩余额度（totalAmount - allocatedAmount）
            BigDecimal originalAvailable = safe(detail.getAvailableAmount());
            if (originalAvailable.compareTo(ZERO) <= 0) {
                // 原始剩余额度为0，直接设置为0，不显示
                detail.setAvailableAmount(ZERO);
                continue;
            }
            
            String economyCategory = detail.getEconomyCategory();
            if (economyCategory == null || economyCategory.isEmpty()) {
                continue;
            }
            
            // 从Map中获取已结转金额
            BigDecimal carriedOver = carriedOverMap.containsKey(economyCategory) 
                ? safe(carriedOverMap.get(economyCategory)) 
                : ZERO;
            
            // 实际可结转金额 = 原始剩余 - 已结转
            BigDecimal actualAvailable = originalAvailable.subtract(carriedOver);
            if (actualAvailable.compareTo(ZERO) < 0) {
                actualAvailable = ZERO; // 确保不为负数
            }
            
            // 更新可用金额（用于前端显示和判断）
            detail.setAvailableAmount(actualAvailable);
        }
    }

    /**
     * 查询资助指标下达列表
     * 
     * @param stSubsidyQuota 资助指标下达
     * @return 资助指标下达
     */
    @Override
    public List<StSubsidyQuota> selectStSubsidyQuotaList(StSubsidyQuota stSubsidyQuota)
    {
        List<StSubsidyQuota> list = stSubsidyQuotaMapper.selectStSubsidyQuotaList(stSubsidyQuota);
        // 在Java代码中设置计算字段（业务逻辑从SQL移到Java代码）
        if (list != null) {
            StSchoolYearSemester currentSemester = stSchoolYearSemesterMapper.selectCurrentSemester(true);
            for (StSubsidyQuota quota : list) {
                // 获取明细列表（如果collection已加载）
                List<StSubsidyQuotaDetail> detailList = quota.getDetailList();
                QuotaBusinessRule.attachComputedFields(quota, currentSemester, detailList);
                // 设置来源指标文号（如果需要）
                if (quota.getSourceQuotaId() != null && quota.getSourceQuotaDocNo() == null) {
                    StSubsidyQuota sourceQuota = stSubsidyQuotaMapper.selectStSubsidyQuotaById(quota.getSourceQuotaId());
                    if (sourceQuota != null) {
                        quota.setSourceQuotaDocNo(sourceQuota.getQuotaDocNo());
                    }
                }
            }
        }
        return list;
    }

    /**
     * 新增资助指标下达（支持批量插入经济分类明细）
     * 
     * @param stSubsidyQuota 资助指标下达
     * @return 结果
     */
    @Override
    @Transactional
    public int insertStSubsidyQuota(StSubsidyQuota stSubsidyQuota)
    {
        validateDetailListForCreate(stSubsidyQuota.getDetailList());
        // 如果未指定指标来源类型，默认为"本学期新下达"
        if (stSubsidyQuota.getQuotaSourceType() == null) {
            stSubsidyQuota.setQuotaSourceType(1);
        }
        // 如果是结转指标，验证功能分类一致性
        if ((stSubsidyQuota.getQuotaSourceType() == 2 || stSubsidyQuota.getQuotaSourceType() == 3) 
            && stSubsidyQuota.getSourceQuotaId() != null) {
            validateFunctionCategoryConsistency(stSubsidyQuota);
        }
        int result = stSubsidyQuotaMapper.insertStSubsidyQuota(stSubsidyQuota);
        persistNewDetails(stSubsidyQuota.getId(), stSubsidyQuota.getDetailList());
        refreshQuotaSnapshot(stSubsidyQuota.getId(), stSubsidyQuota.getStatus());
        return result;
    }

    /**
     * 修改资助指标下达（支持增删改经济分类明细）
     * 
     * @param stSubsidyQuota 资助指标下达
     * @return 结果
     */
    @Override
    @Transactional
    public int updateStSubsidyQuota(StSubsidyQuota stSubsidyQuota)
    {
        if (stSubsidyQuota.getId() == null) {
            throw new ServiceException("缺少指标ID，无法修改");
        }
        StSubsidyQuota originalQuota = stSubsidyQuotaMapper.selectStSubsidyQuotaById(stSubsidyQuota.getId());
        if (originalQuota == null) {
            throw new ServiceException("指标不存在或已删除");
        }
        
        // 数据权限验证：检查是否有权修改该指标
        validateDataPermission(originalQuota);
        
        ensureQuotaEditable(originalQuota);
        // 如果是结转指标，验证功能分类一致性
        if ((stSubsidyQuota.getQuotaSourceType() == 2 || stSubsidyQuota.getQuotaSourceType() == 3) 
            && stSubsidyQuota.getSourceQuotaId() != null) {
            validateFunctionCategoryConsistency(stSubsidyQuota);
        }
        int result = stSubsidyQuotaMapper.updateStSubsidyQuota(stSubsidyQuota);
        if (stSubsidyQuota.getDetailList() != null) {
            upsertDetails(stSubsidyQuota.getId(), stSubsidyQuota.getDetailList());
        }
        refreshQuotaSnapshot(stSubsidyQuota.getId(), originalQuota.getStatus());
        return result;
    }

    private void persistNewDetails(Long quotaId, List<StSubsidyQuotaDetail> detailList) {
        if (detailList == null) {
            return;
        }
        Set<String> categories = new HashSet<>();
        for (StSubsidyQuotaDetail detail : detailList) {
            collectCategory(categories, detail.getEconomyCategory());
            normalizeDetailForCreate(quotaId, detail);
            stSubsidyQuotaDetailMapper.insertStSubsidyQuotaDetail(detail);
        }
    }

    private void upsertDetails(Long quotaId, List<StSubsidyQuotaDetail> incomingDetails) {
        List<StSubsidyQuotaDetail> existingList = stSubsidyQuotaDetailMapper.selectDetailListByQuotaId(quotaId);
        Map<Long, StSubsidyQuotaDetail> existingMap = new HashMap<>();
        if (existingList != null) {
            for (StSubsidyQuotaDetail detail : existingList) {
                existingMap.put(detail.getId(), detail);
            }
        }

        Set<String> categories = new HashSet<>();
        for (StSubsidyQuotaDetail detail : incomingDetails) {
            collectCategory(categories, detail.getEconomyCategory());
            if (detail.getId() == null) {
                normalizeDetailForCreate(quotaId, detail);
                stSubsidyQuotaDetailMapper.insertStSubsidyQuotaDetail(detail);
            } else {
                StSubsidyQuotaDetail original = existingMap.remove(detail.getId());
                if (original == null) {
                    throw new ServiceException("明细不存在或已删除");
                }
                // 如果明细是从其他明细结转来的（source_detail_id不为NULL），则不允许编辑
                if (original.getSourceDetailId() != null) {
                    throw new ServiceException("已结转的指标资金不允许编辑，明细ID：" + original.getId());
                }
                normalizeDetailForUpdate(quotaId, detail, original);
                stSubsidyQuotaDetailMapper.updateStSubsidyQuotaDetail(detail);
            }
        }

        if (!existingMap.isEmpty()) {
            for (StSubsidyQuotaDetail detail : existingMap.values()) {
                // 如果明细是从其他明细结转来的（source_detail_id不为NULL），则不允许删除
                if (detail.getSourceDetailId() != null) {
                    throw new ServiceException("已结转的指标资金不允许删除，明细ID：" + detail.getId());
                }
                if (safe(detail.getAllocatedAmount()).compareTo(ZERO) > 0) {
                    throw new ServiceException("明细已分配金额，不能删除：" + detail.getEconomyCategory());
                }
                stSubsidyQuotaDetailMapper.deleteStSubsidyQuotaDetailById(detail.getId());
            }
        }
    }

    /**
     * 批量删除资助指标下达（级联删除明细）
     * 
     * @param ids 需要删除的资助指标下达主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteStSubsidyQuotaByIds(Long[] ids)
    {
        int count = 0;
        if (ids != null) {
            for (Long id : ids) {
                count += deleteStSubsidyQuotaById(id);
            }
        }
        return count;
    }

    /**
     * 删除资助指标下达信息（级联删除明细）
     * 
     * @param id 资助指标下达主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteStSubsidyQuotaById(Long id)
    {
        if (id == null) {
            return 0;
        }
        
        // 数据权限验证：检查是否有权删除该指标
        validateDataPermissionById(id);
        
        List<StSubsidyQuotaDetail> detailList = stSubsidyQuotaDetailMapper.selectDetailListByQuotaId(id);
        if (detailList != null) {
            for (StSubsidyQuotaDetail detail : detailList) {
                // 直接查询是否有关联的预算记录（更准确）
                int budgetCount = stSemesterBudgetMapper.countByQuotaDetailId(detail.getId());
                if (budgetCount > 0) {
                    throw new ServiceException(String.format("指标明细[%s]已关联%d个预算，禁止删除指标", 
                        detail.getEconomyCategory(), budgetCount));
                }
            }
        }
        stSubsidyQuotaDetailMapper.deleteDetailsByQuotaId(id);
        return stSubsidyQuotaMapper.deleteStSubsidyQuotaById(id);
    }

    /**
     * 查询指标使用情况（含已使用名额统计）
     * 
     * @param stSubsidyQuota 资助指标下达
     * @return 资助指标下达集合（含使用情况）
     */
    @Override
    public List<StSubsidyQuota> selectStSubsidyQuotaListWithUsage(StSubsidyQuota stSubsidyQuota)
    {
        List<StSubsidyQuota> list = stSubsidyQuotaMapper.selectStSubsidyQuotaListWithUsage(stSubsidyQuota);
        // 在Java代码中设置计算字段（业务逻辑从SQL移到Java代码）
        if (list != null) {
            StSchoolYearSemester currentSemester = stSchoolYearSemesterMapper.selectCurrentSemester(true);
            for (StSubsidyQuota quota : list) {
                // 获取明细列表（collection会自动加载）
                List<StSubsidyQuotaDetail> detailList = quota.getDetailList();
                // 使用工具类设置所有计算字段
                QuotaBusinessRule.attachComputedFields(quota, currentSemester, detailList);
                // 设置来源指标文号（如果需要）
                if (quota.getSourceQuotaId() != null && quota.getSourceQuotaDocNo() == null) {
                    StSubsidyQuota sourceQuota = stSubsidyQuotaMapper.selectStSubsidyQuotaById(quota.getSourceQuotaId());
                    if (sourceQuota != null) {
                        quota.setSourceQuotaDocNo(sourceQuota.getQuotaDocNo());
                    }
                }
                // 设置经济分类文本
                quota.setEconomyCategoryText(formatEconomyCategoryText(quota));
                // 计算结转金额和分配到预算的金额
                calculateCarriedOverAndBudgetAmount(quota);
            }
        }
        return list;
    }
    
    /**
     * 格式化经济分类文本
     * 
     * @param quota 指标对象
     * @return 格式化后的文本，如"助学金、免学杂费"
     */
    private String formatEconomyCategoryText(StSubsidyQuota quota) {
        if (quota == null) {
            return "";
        }
        
        Set<String> categories = new HashSet<>();
        
        // 添加主表的经济分类
        if (quota.getEconomyCategory() != null && !quota.getEconomyCategory().isEmpty()) {
            categories.add(quota.getEconomyCategory());
        }
        
        // 添加明细表的经济分类
        if (quota.getDetailList() != null && !quota.getDetailList().isEmpty()) {
            for (StSubsidyQuotaDetail detail : quota.getDetailList()) {
                if (detail != null && detail.getEconomyCategory() != null && !detail.getEconomyCategory().isEmpty()) {
                    categories.add(detail.getEconomyCategory());
                }
            }
        }
        
        if (categories.isEmpty()) {
            return "";
        }
        
        // 转换为中文名称并拼接
        return categories.stream()
            .map(this::getEconomyCategoryName)
            .filter(name -> name != null && !name.isEmpty())
            .sorted()
            .collect(Collectors.joining("、"));
    }
    
    /**
     * 获取经济分类中文名称
     * 
     * @param code 经济分类代码
     * @return 中文名称
     */
    private String getEconomyCategoryName(String code) {
        if (code == null || code.isEmpty()) {
            return "";
        }
        switch (code) {
            case "1":
                return "助学金";
            case "2":
                return "免学杂费";
            case "3":
                return "免学费";
            case "4":
                return "营养改善计划";
            default:
                return code;
        }
    }
    
    /**
     * 修复所有指标的已分配金额（根据学期预算重新计算）
     * 
     * @return 修复的指标数量
     */
    @Override
    public int fixAllocatedAmount()
    {
        return stSubsidyQuotaMapper.fixAllocatedAmount();
    }
    
    /**
     * 根据经济分类更新指标明细的已分配金额
     * 
     * @param quotaId 指标ID
     * @param economyCategory 经济分类
     * @param amount 金额
     * @return 结果
     */
    @Override
    public int updateAllocatedAmountByEconomyCategory(Long quotaId, String economyCategory, BigDecimal amount)
    {
        return stSubsidyQuotaMapper.updateAllocatedAmountByEconomyCategory(quotaId, economyCategory, amount);
    }

    @Override
    public List<StSubsidyQuotaDetail> listAllocatableDetails(StSubsidyQuotaDetail filter) {
        List<StSubsidyQuotaDetail> details = stSubsidyQuotaDetailMapper.selectStSubsidyQuotaDetailList(filter);
        attachComputedFields(details);
        return details.stream()
            .filter(detail -> detail.getStatus() == null || detail.getStatus() == 1)
            .filter(detail -> safe(detail.getAvailableAmount()).compareTo(ZERO) > 0)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long allocateQuota(QuotaAllocationRequest request) {
        return allocateQuotaInternal(request);
    }

    @Override
    @Transactional
    public List<Long> allocateQuotaBatch(BatchQuotaAllocationRequest request) {
        if (request == null || request.getYearSemesterId() == null) {
            throw new ServiceException("请选择目标学年学期");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ServiceException("请选择至少一条经济分类进行分配");
        }
        List<Long> budgetIds = new ArrayList<>();
        for (BatchQuotaAllocationRequest.AllocationItem item : request.getItems()) {
            if (item == null || item.getQuotaDetailId() == null) {
                throw new ServiceException("存在缺少指标明细的分配项");
            }
            QuotaAllocationRequest single = new QuotaAllocationRequest();
            single.setQuotaDetailId(item.getQuotaDetailId());
            single.setYearSemesterId(request.getYearSemesterId());
            single.setAllocateAmount(item.getAllocateAmount());
            single.setBudgetType(item.getBudgetType());
            single.setMemo(item.getMemo());
            budgetIds.add(allocateQuotaInternal(single));
        }
        return budgetIds;
    }

    /**
     * 单条分配的内部实现，供单条和批量复用。
     */
    private Long allocateQuotaInternal(QuotaAllocationRequest request) {
        if (request == null || request.getQuotaDetailId() == null) {
            throw new ServiceException("缺少指标明细参数");
        }
        if (request.getYearSemesterId() == null) {
            throw new ServiceException("请选择目标学年学期");
        }
        
        // 验证只能分配到当前学期（非当前学期预算不允许直接分配，只能结转给当前学期）
        StSchoolYearSemester currentSemester = stSchoolYearSemesterMapper.selectCurrentSemester(true);
        if (currentSemester == null) {
            throw new ServiceException("无法获取当前学期，请先设置当前学期");
        }
        if (!request.getYearSemesterId().equals(currentSemester.getId())) {
            throw new ServiceException(String.format("只能分配到当前学期（学期ID=%d），非当前学期预算不允许直接分配，请先结转到当前学期", 
                currentSemester.getId()));
        }
        
        BigDecimal allocateAmount = safe(request.getAllocateAmount());
        if (allocateAmount.compareTo(ZERO) <= 0) {
            throw new ServiceException("分配金额必须大于0");
        }
        // 使用悲观锁查询指标明细（防止并发分配导致超分配）
        StSubsidyQuotaDetail detail = stSubsidyQuotaDetailMapper.selectStSubsidyQuotaDetailByIdForUpdate(request.getQuotaDetailId());
        if (detail == null) {
            throw new ServiceException("指标明细不存在");
        }
        attachComputedFields(detail);
        if (detail.getStatus() != null && detail.getStatus() == 0) {
            throw new ServiceException("明细已停用，无法分配");
        }
        // 在锁保护下重新计算可用金额（确保准确性）
        BigDecimal total = safe(detail.getTotalAmount());
        BigDecimal allocated = safe(detail.getAllocatedAmount());
        BigDecimal available = total.subtract(allocated);
        if (allocateAmount.compareTo(available) > 0) {
            throw new ServiceException(String.format("分配金额超出剩余额度，可用金额：%.2f，分配金额：%.2f", available, allocateAmount));
        }

        StSubsidyQuota quota = stSubsidyQuotaMapper.selectStSubsidyQuotaById(detail.getQuotaId());
        if (quota == null) {
            throw new ServiceException("指标不存在");
        }
        ensureQuotaEditable(quota);

        StSemesterBudget budget = new StSemesterBudget();
        budget.setYearSemesterId(request.getYearSemesterId());
        budget.setQuotaId(quota.getId());
        budget.setQuotaDetailId(detail.getId());
        budget.setBudgetAmount(allocateAmount);
        budget.setUsedAmount(ZERO);
        budget.setLockedAmount(ZERO);
        budget.setStatus(1);
        budget.setBudgetType(determineBudgetType(request.getBudgetType(), detail.getEconomyCategory()));
        budget.setMemo(request.getMemo());
        
        // 设置结转标识：优先级：明细 > 指标 > 根据quota_source_type判断
        if ("1".equals(detail.getIsCarryover())) {
            // 明细已明确标记为结转资金，优先继承明细的结转属性
            budget.setIsCarryover(detail.getIsCarryover());
            budget.setSourceSemesterId(detail.getSourceSemesterId());
        } else if ("1".equals(quota.getIsCarryover())) {
            // 指标已明确标记为结转资金，继承指标的结转属性
            budget.setIsCarryover(quota.getIsCarryover());
            budget.setSourceSemesterId(quota.getSourceSemesterId());
        } else if (quota.getQuotaSourceType() != null && (quota.getQuotaSourceType() == 2 || quota.getQuotaSourceType() == 3)) {
            // 指标来源类型为结转（2=上学期结转，3=历史学期结转），应该标记为结转资金
            budget.setIsCarryover("1");
            // 如果指标有sourceQuotaId，尝试获取来源指标的学期ID
            if (quota.getSourceQuotaId() != null) {
                StSubsidyQuota sourceQuota = stSubsidyQuotaMapper.selectStSubsidyQuotaById(quota.getSourceQuotaId());
                if (sourceQuota != null) {
                    budget.setSourceSemesterId(sourceQuota.getYearSemesterId());
                } else {
                    // 来源指标不存在，使用当前指标的学期ID
                    budget.setSourceSemesterId(quota.getYearSemesterId());
                }
            } else {
                // 没有sourceQuotaId，使用当前指标的学期ID
                budget.setSourceSemesterId(quota.getYearSemesterId());
            }
        }

        // 如已存在同一学年学期同一明细的预算，则累加金额；否则新建
        StSemesterBudget existing = stSemesterBudgetMapper.selectByQuotaDetailAndSemester(
            detail.getId(), request.getYearSemesterId());
        if (existing != null) {
            int budgetResult = stSemesterBudgetMapper.increaseBudgetAmount(existing.getId(), allocateAmount);
            if (budgetResult == 0) {
                throw new ServiceException("更新学期预算失败");
            }
            budget.setId(existing.getId());
        } else {
            int budgetResult = stSemesterBudgetMapper.insertStSemesterBudget(budget);
            if (budgetResult == 0) {
                throw new ServiceException("创建学期预算失败");
            }
        }
        
        // 更新指标明细已分配金额
        int detailResult = stSubsidyQuotaDetailMapper.updateAllocatedAmount(detail.getId(), allocateAmount);
        if (detailResult == 0) {
            // 如果更新失败，需要回滚预算（在事务中会自动回滚）
            throw new ServiceException("更新指标明细已分配金额失败，可能金额不足");
        }
        
        // 更新指标已分配金额
        int quotaResult = stSubsidyQuotaMapper.updateAllocatedAmount(quota.getId(), allocateAmount);
        if (quotaResult == 0) {
            // 如果更新失败，需要回滚预算和明细（在事务中会自动回滚）
            throw new ServiceException("更新指标已分配金额失败，可能金额不足");
        }
        
        refreshQuotaSnapshot(quota.getId(), quota.getStatus());
        return budget.getId();
    }

    private void validateDetailListForCreate(List<StSubsidyQuotaDetail> detailList) {
        if (detailList == null || detailList.isEmpty()) {
            throw new ServiceException("请至少添加一个指标明细");
        }
    }

    private void collectCategory(Set<String> categories, String categoryCode) {
        if (categoryCode == null || categoryCode.trim().isEmpty()) {
            throw new ServiceException("经济分类不能为空");
        }
        if (!categories.add(categoryCode)) {
            throw new ServiceException("同一指标下经济分类不能重复");
        }
    }

    private void normalizeDetailForCreate(Long quotaId, StSubsidyQuotaDetail detail) {
        detail.setQuotaId(quotaId);
        detail.setTotalAmount(safe(detail.getTotalAmount()));
        if (detail.getTotalAmount().compareTo(ZERO) < 0) {
            throw new ServiceException("指标明细金额不能小于0");
        }
        detail.setAllocatedAmount(ZERO);
        if (detail.getStatus() == null) {
            detail.setStatus(1);
        }
    }

    private void normalizeDetailForUpdate(Long quotaId, StSubsidyQuotaDetail target, StSubsidyQuotaDetail original) {
        target.setQuotaId(quotaId);
        BigDecimal newTotal = target.getTotalAmount() != null ? target.getTotalAmount() : original.getTotalAmount();
        BigDecimal allocated = safe(original.getAllocatedAmount());
        if (newTotal.compareTo(allocated) < 0) {
            throw new ServiceException("调整后的金额不能小于已分配金额");
        }
        target.setTotalAmount(newTotal);
        target.setAllocatedAmount(allocated);
        if (target.getStatus() == null) {
            target.setStatus(original.getStatus());
        }
    }

    private void ensureQuotaEditable(StSubsidyQuota quota) {
        if (quota != null && quota.getStatus() != null && quota.getStatus() == 4) {
            throw new ServiceException("指标已锁定，无法修改");
        }
    }

    private void refreshQuotaSnapshot(Long quotaId, Integer originalStatus) {
        if (quotaId == null) {
            return;
        }
        List<StSubsidyQuotaDetail> details = stSubsidyQuotaDetailMapper.selectDetailListByQuotaId(quotaId);
        BigDecimal total = ZERO;
        BigDecimal allocated = ZERO;
        if (details != null) {
            for (StSubsidyQuotaDetail detail : details) {
                total = total.add(safe(detail.getTotalAmount()));
                allocated = allocated.add(safe(detail.getAllocatedAmount()));
            }
        }
        int status = determineStatus(total, allocated);
        if (Objects.equals(originalStatus, 4)) {
            status = 4;
        }
        StSubsidyQuota aggregate = new StSubsidyQuota();
        aggregate.setId(quotaId);
        aggregate.setTotalQuota(total);
        aggregate.setAllocatedAmount(allocated);
        aggregate.setStatus(status);
        stSubsidyQuotaMapper.updateQuotaAggregateFields(aggregate);
    }

    private int determineStatus(BigDecimal total, BigDecimal allocated) {
        if (total.compareTo(ZERO) <= 0) {
            return 1;
        }
        if (allocated.compareTo(ZERO) <= 0) {
            return 1;
        }
        if (allocated.compareTo(total) >= 0) {
            return 3;
        }
        return 2;
    }

    private void attachComputedFields(List<StSubsidyQuotaDetail> details) {
        if (details == null) {
            return;
        }
        details.forEach(this::attachComputedFields);
    }

    private void attachComputedFields(StSubsidyQuotaDetail detail) {
        if (detail == null) {
            return;
        }
        detail.setTotalAmount(safe(detail.getTotalAmount()));
        detail.setAllocatedAmount(safe(detail.getAllocatedAmount()));
        detail.setAvailableAmount(detail.getTotalAmount().subtract(detail.getAllocatedAmount()));
        // 设置是否可编辑（业务逻辑从SQL移到Java代码）
        detail.setIsEditable(QuotaBusinessRule.isEditable(detail));
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? ZERO : value;
    }
    
    /**
     * 计算指标的结转金额和分配到预算的金额
     * 
     * @param quota 指标对象
     */
    private void calculateCarriedOverAndBudgetAmount(StSubsidyQuota quota) {
        if (quota == null || quota.getId() == null) {
            return;
        }
        
        // 查询结转金额（该指标结转到其他指标的金额）
        BigDecimal carriedOverAmount = stSubsidyQuotaDetailMapper.sumCarriedOverAmount(quota.getId());
        quota.setCarriedOverAmount(carriedOverAmount != null ? carriedOverAmount : ZERO);
        
        // 计算分配到预算的金额 = 已分配金额 - 结转金额
        BigDecimal allocatedAmount = safe(quota.getAllocatedAmount());
        BigDecimal allocatedToBudget = allocatedAmount.subtract(quota.getCarriedOverAmount());
        quota.setAllocatedToBudget(allocatedToBudget.compareTo(ZERO) < 0 ? ZERO : allocatedToBudget);
    }
    
    /**
     * 验证数据权限（检查是否为数据创建者或管理员）
     * 
     * @param quota 指标对象
     * @throws ServiceException 如果无权操作
     */
    private void validateDataPermission(StSubsidyQuota quota) {
        if (quota == null) {
            return;
        }
        
        // 管理员拥有所有权限
        if (SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            return;
        }
        
        // 检查是否为数据创建者
        String currentUser = SecurityUtils.getUsername();
        if (quota.getCreateBy() != null && !quota.getCreateBy().equals(currentUser)) {
            throw new ServiceException("无权操作其他用户创建的指标数据");
        }
    }
    
    /**
     * 根据ID验证数据权限
     * 
     * @param quotaId 指标ID
     * @throws ServiceException 如果指标不存在或无权操作
     */
    private void validateDataPermissionById(Long quotaId) {
        if (quotaId == null) {
            return;
        }
        StSubsidyQuota quota = stSubsidyQuotaMapper.selectStSubsidyQuotaById(quotaId);
        if (quota == null) {
            throw new ServiceException("指标不存在或已被删除");
        }
        validateDataPermission(quota);
    }

    private String determineBudgetType(String customType, String economyCategory) {
        if (customType != null && !customType.trim().isEmpty()) {
            return customType;
        }
        if (economyCategory == null) {
            return "其他";
        }
        switch (economyCategory) {
            case "1":
                return "助学金";
            case "2":
                return "免学杂费";
            case "3":
                return "免学费";
            case "4":
                return "营养改善计划";
            default:
                return "其他";
        }
    }
    
    /**
     * 验证功能分类一致性（结转指标必须与来源指标功能分类相同）
     * 
     * @param quota 当前指标
     */
    /**
     * 验证功能分类一致性（结转指标必须与来源指标功能分类相同）
     * 
     * @param quota 当前指标
     */
    private void validateFunctionCategoryConsistency(StSubsidyQuota quota) {
        if (quota.getSourceQuotaId() == null) {
            return; // 没有来源指标，无需验证
        }
        
        StSubsidyQuota sourceQuota = stSubsidyQuotaMapper.selectStSubsidyQuotaById(quota.getSourceQuotaId());
        if (sourceQuota == null) {
            throw new ServiceException("来源指标不存在");
        }
        
        String sourceCategory = sourceQuota.getFunctionCategory();
        String targetCategory = quota.getFunctionCategory();
        
        if (sourceCategory == null || targetCategory == null) {
            throw new ServiceException("功能分类不能为空");
        }
        
        if (!sourceCategory.equals(targetCategory)) {
            throw new ServiceException(String.format(
                "功能分类不一致！来源指标的功能分类为'%s'，当前指标的功能分类为'%s'。结转时功能分类必须相同（高中的资金不能转到小学和初中）。",
                sourceCategory, targetCategory
            ));
        }
    }
    
    /**
     * 更新来源指标结转后的状态和已分配金额
     * 
     * @param sourceQuota 来源指标
     * @param totalCarryOverAmount 总结转金额
     * @param totalAvailable 总可用金额
     */
    private void updateSourceQuotaAfterCarryOver(
        StSubsidyQuota sourceQuota, 
        BigDecimal totalCarryOverAmount, 
        BigDecimal totalAvailable) {
        
        // 更新来源指标的已分配金额
        int quotaResult = stSubsidyQuotaMapper.updateAllocatedAmount(
            sourceQuota.getId(), totalCarryOverAmount);
        if (quotaResult == 0) {
            throw new ServiceException(String.format(
                "更新来源指标已分配金额失败：指标ID=%d，结转金额=%.2f",
                sourceQuota.getId(), totalCarryOverAmount));
        }

        // 结转后状态：全部结转则锁定（4），部分结转则部分分配（3）
        Integer newSourceStatus;
        if (totalCarryOverAmount.compareTo(totalAvailable) >= 0) {
            newSourceStatus = 4; // 锁定
        } else if (totalCarryOverAmount.compareTo(ZERO) > 0) {
            newSourceStatus = 3; // 部分分配
        } else {
            newSourceStatus = sourceQuota.getStatus();
        }
        
        StSubsidyQuota updateStatusObj = new StSubsidyQuota();
        updateStatusObj.setId(sourceQuota.getId());
        updateStatusObj.setStatus(newSourceStatus);
        stSubsidyQuotaMapper.updateStSubsidyQuota(updateStatusObj);

        // 刷新来源指标快照
        refreshQuotaSnapshot(sourceQuota.getId(), newSourceStatus);
    }

    @Override
    public QuotaStatisticVO statistics(StSubsidyQuota stSubsidyQuota) {
        // 查询所有符合条件的数据（不受分页限制）
        List<StSubsidyQuota> list = stSubsidyQuotaMapper.selectStSubsidyQuotaListWithUsage(stSubsidyQuota);
        
        QuotaStatisticVO vo = new QuotaStatisticVO();
        Map<String, BigDecimal> allocatedByEconomy = new HashMap<>();
        
        for (StSubsidyQuota quota : list) {
            BigDecimal total = safe(quota.getTotalQuota());
            BigDecimal allocated = safe(quota.getAllocatedAmount());
            BigDecimal available = quota.getAvailableQuota() != null 
                ? safe(quota.getAvailableQuota()) 
                : total.subtract(allocated);
            
            vo.setTotalQuota(vo.getTotalQuota().add(total));
            vo.setAllocatedAmount(vo.getAllocatedAmount().add(allocated));
            vo.setAvailableAmount(vo.getAvailableAmount().add(available));
            
            // 按经济分类统计已分配金额
            if (quota.getDetailList() != null && !quota.getDetailList().isEmpty()) {
                for (StSubsidyQuotaDetail detail : quota.getDetailList()) {
                    if (detail != null && detail.getEconomyCategory() != null) {
                        BigDecimal detailAllocated = safe(detail.getAllocatedAmount());
                        if (detailAllocated.compareTo(ZERO) > 0) {
                            String key = detail.getEconomyCategory();
                            allocatedByEconomy.put(key, 
                                allocatedByEconomy.getOrDefault(key, ZERO).add(detailAllocated));
                        }
                    }
                }
            } else if (quota.getEconomyCategory() != null && allocated.compareTo(ZERO) > 0) {
                String key = quota.getEconomyCategory();
                allocatedByEconomy.put(key, 
                    allocatedByEconomy.getOrDefault(key, ZERO).add(allocated));
            }
        }
        
        vo.setAllocatedByEconomy(allocatedByEconomy);
        return vo;
    }
    
    /**
     * 查询指标已分配的预算列表
     *
     * @param quotaId 指标ID
     * @return 预算列表
     */
    @Override
    public List<StSemesterBudget> listAllocatedBudgets(Long quotaId)
    {
        if (quotaId == null) {
            throw new ServiceException("指标ID不能为空");
        }
        StSemesterBudget query = new StSemesterBudget();
        query.setQuotaId(quotaId);
        List<StSemesterBudget> budgets = stSemesterBudgetMapper.selectStSemesterBudgetList(query);
        // 为每个预算计算实际的锁定金额和收回状态
        if (budgets != null) {
            for (StSemesterBudget budget : budgets) {
                java.math.BigDecimal actualLocked = stSemesterBudgetMapper.calculateActualLockedAmount(budget.getId());
                budget.setLockedAmount(actualLocked);
                // 重新计算可用金额
                BigDecimal used = budget.getUsedAmount() != null ? budget.getUsedAmount() : BigDecimal.ZERO;
                BigDecimal locked = actualLocked != null ? actualLocked : BigDecimal.ZERO;
                BigDecimal available = budget.getBudgetAmount().subtract(used).subtract(locked);
                budget.setAvailableAmount(available);
                
                // 计算收回状态
                if (used.compareTo(BigDecimal.ZERO) > 0) {
                    budget.setReclaimStatus("已使用");
                    budget.setReclaimStatusType("danger");
                    budget.setIsReclaimable(false);
                } else if (locked.compareTo(BigDecimal.ZERO) > 0) {
                    budget.setReclaimStatus("已锁定");
                    budget.setReclaimStatusType("warning");
                    budget.setIsReclaimable(false);
                } else {
                    budget.setReclaimStatus("可收回");
                    budget.setReclaimStatusType("success");
                    budget.setIsReclaimable(true);
                }
                // 使用工具类确保学期标签的一致性（即使SQL已计算，也通过工具类校验和设置）
                String semesterLabel = com.ruoyi.common.utils.SemesterUtils.getSemesterLabel(budget.getSemester());
                budget.setSemesterLabel(semesterLabel);
            }
        }
        return budgets;
    }
    
    /**
     * 收回已分配的预算
     *
     * @param budgetIds 要收回的预算ID数组
     * @return 收回的预算数量
     */
    @Override
    @Transactional
    public int reclaimBudgets(Long[] budgetIds)
    {
        if (budgetIds == null || budgetIds.length == 0) {
            return 0;
        }
        
        int reclaimedCount = 0;
        for (Long budgetId : budgetIds) {
            // 查询预算信息
            StSemesterBudget budget = stSemesterBudgetMapper.selectStSemesterBudgetById(budgetId);
            if (budget == null) {
                continue;
            }
            
            // 数据权限验证：检查预算关联的指标是否有权操作
            if (budget.getQuotaId() != null) {
                validateDataPermissionById(budget.getQuotaId());
            }
            
            // 检查预算是否可以收回（未使用且无锁定金额）
            java.math.BigDecimal actualLocked = stSemesterBudgetMapper.calculateActualLockedAmount(budgetId);
            BigDecimal usedAmount = safe(budget.getUsedAmount());
            BigDecimal lockedAmount = actualLocked != null ? actualLocked : BigDecimal.ZERO;
            
            if (usedAmount.compareTo(ZERO) > 0) {
                throw new ServiceException(String.format("预算[ID:%d]已使用金额 %s 元，无法收回", 
                    budgetId, usedAmount));
            }
            if (lockedAmount.compareTo(ZERO) > 0) {
                throw new ServiceException(String.format("预算[ID:%d]有锁定金额 %s 元，无法收回", 
                    budgetId, lockedAmount));
            }
            
            // 删除预算
            int deleteResult = stSemesterBudgetMapper.deleteStSemesterBudgetById(budgetId);
            if (deleteResult == 0) {
                throw new ServiceException(String.format("删除预算[ID:%d]失败", budgetId));
            }
            
            // 减少指标明细的已分配金额（收回预算后，已分配金额减少，剩余额度增加）
            if (budget.getQuotaDetailId() != null) {
                BigDecimal reclaimAmount = budget.getBudgetAmount();
                // 使用负数减少已分配金额：allocated_amount = allocated_amount + (-reclaimAmount)
                // 剩余额度会自动增加：available_quota = total_quota - allocated_amount
                int detailResult = stSubsidyQuotaDetailMapper.updateAllocatedAmount(
                    budget.getQuotaDetailId(), reclaimAmount.negate());
                if (detailResult == 0) {
                    throw new ServiceException(String.format("更新指标明细[ID:%d]已分配金额失败", 
                        budget.getQuotaDetailId()));
                }
            }
            
            // 减少指标的已分配金额（收回预算后，已分配金额减少，剩余额度增加）
            if (budget.getQuotaId() != null) {
                BigDecimal reclaimAmount = budget.getBudgetAmount();
                // 使用负数减少已分配金额：allocated_amount = allocated_amount + (-reclaimAmount)
                // 剩余额度会自动增加：available_quota = total_quota - allocated_amount
                int quotaResult = stSubsidyQuotaMapper.updateAllocatedAmount(
                    budget.getQuotaId(), reclaimAmount.negate());
                if (quotaResult == 0) {
                    throw new ServiceException(String.format("更新指标[ID:%d]已分配金额失败", 
                        budget.getQuotaId()));
                }
                
                // 刷新指标快照
                StSubsidyQuota quota = stSubsidyQuotaMapper.selectStSubsidyQuotaById(budget.getQuotaId());
                if (quota != null) {
                    refreshQuotaSnapshot(quota.getId(), quota.getStatus());
                }
            }
            
            reclaimedCount++;
        }
        
        return reclaimedCount;
    }
    
    @Override
    @Transactional
    public List<Long> carryOverToBudget(CarryOverToBudgetRequest request) {
        if (request == null || request.getSourceQuotaId() == null) {
            throw new ServiceException("缺少来源指标ID");
        }
        if (request.getYearSemesterId() == null) {
            throw new ServiceException("请选择目标学年学期");
        }
        
        // 数据权限验证：检查是否有权操作来源指标
        validateDataPermissionById(request.getSourceQuotaId());
        
        // 查询来源指标
        StSubsidyQuota sourceQuota = stSubsidyQuotaMapper.selectStSubsidyQuotaById(request.getSourceQuotaId());
        if (sourceQuota == null) {
            throw new ServiceException("来源指标不存在");
        }
        
        // 验证是否为历史学期
        com.ruoyi.system.domain.StSchoolYearSemester currentSemester = 
            stSchoolYearSemesterMapper.selectCurrentSemester(true);
        if (currentSemester == null) {
            throw new ServiceException("无法获取当前学期，请先设置当前学期");
        }
        if (sourceQuota.getYearSemesterId() >= currentSemester.getId()) {
            throw new ServiceException("只能结转历史学期的指标，当前指标不是历史学期");
        }
        
        // 查询来源指标的明细列表
        List<StSubsidyQuotaDetail> detailList = stSubsidyQuotaDetailMapper.selectDetailListByQuotaId(request.getSourceQuotaId());
        if (detailList == null || detailList.isEmpty()) {
            throw new ServiceException("来源指标没有明细数据");
        }
        
        // 先设置明细的计算字段（availableAmount = totalAmount - allocatedAmount）
        attachComputedFields(detailList);
        
        // 计算每个明细的实际可结转金额（减去已结转金额）
        calculateCarryOverAvailableAmount(detailList, request.getSourceQuotaId());

        // 根据请求过滤明细（如果指定了经济分类）
        List<StSubsidyQuotaDetail> filteredDetails = detailList;
        if (request.getEconomyCategories() != null && !request.getEconomyCategories().isEmpty()) {
            Set<String> categorySet = new HashSet<>(request.getEconomyCategories());
            filteredDetails = detailList.stream()
                .filter(detail -> detail.getEconomyCategory() != null 
                    && categorySet.contains(detail.getEconomyCategory()))
                .collect(Collectors.toList());
        }
        
        // 过滤出有可结转金额的明细
        filteredDetails = filteredDetails.stream()
            .filter(detail -> safe(detail.getAvailableAmount()).compareTo(ZERO) > 0)
            .collect(Collectors.toList());
        
        if (filteredDetails.isEmpty()) {
            throw new ServiceException("没有可结转的剩余额度");
        }
        
        // 批量创建预算
        List<Long> budgetIds = new ArrayList<>();
        String baseMemo = request.getMemo() != null ? request.getMemo() : 
            String.format("从指标\"%s\"结转", sourceQuota.getQuotaDocNo());
        
        for (StSubsidyQuotaDetail detail : filteredDetails) {
            BigDecimal carryOverAmount = safe(detail.getAvailableAmount());
            
            // 如已存在同一学年学期同一明细的预算，则累加金额；否则新建
            StSemesterBudget existing = stSemesterBudgetMapper.selectByQuotaDetailAndSemester(
                detail.getId(), request.getYearSemesterId());
            
            if (existing != null) {
                int budgetResult = stSemesterBudgetMapper.increaseBudgetAmount(existing.getId(), carryOverAmount);
                if (budgetResult == 0) {
                    throw new ServiceException(String.format(
                        "更新学期预算失败：预算ID=%d，结转金额=%.2f，指标文号=%s",
                        existing.getId(), carryOverAmount, sourceQuota.getQuotaDocNo()));
                }
                budgetIds.add(existing.getId());
            } else {
                // 创建预算
                StSemesterBudget budget = new StSemesterBudget();
                budget.setYearSemesterId(request.getYearSemesterId());
                budget.setQuotaId(sourceQuota.getId());
                budget.setQuotaDetailId(detail.getId());
                budget.setBudgetAmount(carryOverAmount);
                budget.setUsedAmount(ZERO);
                budget.setLockedAmount(ZERO);
                budget.setStatus(1);
                budget.setBudgetType(determineBudgetType(null, detail.getEconomyCategory()));
                budget.setMemo(baseMemo);
                // 设置结转标识：因为这是从历史学期结转到当前学期的预算
                // 如果来源指标已经有结转标识，则继承；否则设置为结转资金
                if ("1".equals(sourceQuota.getIsCarryover())) {
                    // 继承来源指标的结转属性
                    budget.setIsCarryover(sourceQuota.getIsCarryover());
                    budget.setSourceSemesterId(sourceQuota.getSourceSemesterId());
                } else {
                    // 来源指标未标记，但这是从历史学期结转，应该标记为结转资金
                    budget.setIsCarryover("1");
                    budget.setSourceSemesterId(sourceQuota.getYearSemesterId()); // 记录来源学期ID
                }
                
                int budgetResult = stSemesterBudgetMapper.insertStSemesterBudget(budget);
                if (budgetResult == 0) {
                    throw new ServiceException(String.format(
                        "创建学期预算失败：结转金额=%.2f，经济分类=%s，指标文号=%s",
                        carryOverAmount, detail.getEconomyCategory(), sourceQuota.getQuotaDocNo()));
                }
                budgetIds.add(budget.getId());
            }
            
            // 更新指标明细已分配金额
            int detailResult = stSubsidyQuotaDetailMapper.updateAllocatedAmount(detail.getId(), carryOverAmount);
            if (detailResult == 0) {
                throw new ServiceException(String.format(
                    "更新指标明细已分配金额失败：明细ID=%d，结转金额=%.2f，经济分类=%s",
                    detail.getId(), carryOverAmount, detail.getEconomyCategory()));
            }
            
            // 更新指标已分配金额
            int quotaResult = stSubsidyQuotaMapper.updateAllocatedAmount(sourceQuota.getId(), carryOverAmount);
            if (quotaResult == 0) {
                throw new ServiceException(String.format(
                    "更新指标已分配金额失败：指标ID=%d，结转金额=%.2f，指标文号=%s",
                    sourceQuota.getId(), carryOverAmount, sourceQuota.getQuotaDocNo()));
            }
        }
        
        // 刷新指标快照
        refreshQuotaSnapshot(sourceQuota.getId(), sourceQuota.getStatus());
        
        return budgetIds;
    }
    
    @Override
    public List<String> getQuotaDocNosByYearSemesterAndFunction(Long yearSemesterId, String functionCategory) {
        if (yearSemesterId == null || functionCategory == null || functionCategory.isEmpty()) {
            return new ArrayList<>();
        }
        return stSubsidyQuotaMapper.selectQuotaDocNosByYearSemesterAndFunction(yearSemesterId, functionCategory);
    }
    
    @Override
    @Transactional
    public Long carryOverToTargetQuota(com.ruoyi.system.domain.dto.CarryOverToTargetQuotaRequest request) {
        if (request == null || request.getSourceQuotaId() == null) {
            throw new ServiceException("缺少来源指标ID");
        }
        if (request.getTargetQuotaDocNo() == null || request.getTargetQuotaDocNo().isEmpty()) {
            throw new ServiceException("缺少目标指标文号");
        }
        if (request.getYearSemesterId() == null) {
            throw new ServiceException("请选择目标学年学期");
        }
        
        // 数据权限验证：检查是否有权操作来源指标
        validateDataPermissionById(request.getSourceQuotaId());
        
        // 查询来源指标
        StSubsidyQuota sourceQuota = stSubsidyQuotaMapper.selectStSubsidyQuotaById(request.getSourceQuotaId());
        if (sourceQuota == null) {
            throw new ServiceException("来源指标不存在");
        }
        
        // 验证是否为历史学期
        com.ruoyi.system.domain.StSchoolYearSemester currentSemester = 
            stSchoolYearSemesterMapper.selectCurrentSemester(true);
        if (currentSemester == null) {
            throw new ServiceException("无法获取当前学期，请先设置当前学期");
        }
        if (sourceQuota.getYearSemesterId() >= currentSemester.getId()) {
            throw new ServiceException("只能结转历史学期的指标，当前指标不是历史学期");
        }
        
        // 查询来源指标的明细列表
        List<StSubsidyQuotaDetail> detailList = stSubsidyQuotaDetailMapper.selectDetailListByQuotaId(request.getSourceQuotaId());
        if (detailList == null || detailList.isEmpty()) {
            throw new ServiceException("来源指标没有明细数据");
        }
        
        // 先设置明细的计算字段（availableAmount = totalAmount - allocatedAmount）
        attachComputedFields(detailList);
        
        // 计算每个明细的实际可结转金额（减去已结转金额）
        calculateCarryOverAvailableAmount(detailList, request.getSourceQuotaId());
        
        // 来源指标当前可结转总额（用于结转后判断状态）
        BigDecimal totalAvailable = detailList.stream()
            .map(d -> safe(d.getAvailableAmount()))
            .reduce(ZERO, BigDecimal::add);
        
        // 根据请求过滤明细（如果指定了经济分类）
        List<StSubsidyQuotaDetail> filteredDetails = detailList;
        if (request.getEconomyCategories() != null && !request.getEconomyCategories().isEmpty()) {
            filteredDetails = detailList.stream()
                .filter(d -> request.getEconomyCategories().contains(d.getEconomyCategory()))
                .filter(d -> safe(d.getAvailableAmount()).compareTo(ZERO) > 0)
                .collect(java.util.stream.Collectors.toList());
        } else {
            filteredDetails = detailList.stream()
                .filter(d -> safe(d.getAvailableAmount()).compareTo(ZERO) > 0)
                .collect(java.util.stream.Collectors.toList());
        }
        
        if (filteredDetails.isEmpty()) {
            throw new ServiceException("来源指标没有可结转的剩余额度");
        }
        
        // 检查目标指标是否存在（精确匹配指标文号、学年学期和功能分类）
        // 先trim指标文号，避免空格导致查询不到
        String targetDocNo = request.getTargetQuotaDocNo() != null ? request.getTargetQuotaDocNo().trim() : null;
        if (targetDocNo == null || targetDocNo.isEmpty()) {
            throw new ServiceException("目标指标文号不能为空");
        }
        
        // 使用来源指标的功能分类来查询目标指标（功能分类必须一致）
        String sourceFunctionCategory = sourceQuota.getFunctionCategory();
        
        // 查询目标指标是否存在（精确匹配指标文号和学年学期）
        StSubsidyQuota targetQuota = stSubsidyQuotaMapper.selectStSubsidyQuotaByDocNoAndSemester(
            targetDocNo, request.getYearSemesterId());
        
        // 如果查询到了目标指标，验证功能分类是否匹配（必须完全相同）
        if (targetQuota != null) {
            String targetFunctionCategory = targetQuota.getFunctionCategory();
            if (sourceFunctionCategory != null && targetFunctionCategory != null
                && !sourceFunctionCategory.equals(targetFunctionCategory)) {
                // 功能分类不匹配，视为不存在（将创建新指标）
                targetQuota = null;
            }
        }
        
        // 如果查询不到，尝试查询该学期的所有指标（只查询功能分类相同的指标），检查是否有指标文号匹配的情况
        if (targetQuota == null) {
            List<StSubsidyQuota> allQuotasInSemester = stSubsidyQuotaMapper.selectStSubsidyQuotaList(
                new StSubsidyQuota() {{
                    setYearSemesterId(request.getYearSemesterId());
                    // 只查询功能分类相同的指标
                    setFunctionCategory(sourceFunctionCategory);
                }});
            // 检查是否有指标文号匹配（去除空格后比较）
            String normalizedInputDocNo = targetDocNo.trim().replaceAll("\\s+", "");
            for (StSubsidyQuota q : allQuotasInSemester) {
                if (q.getQuotaDocNo() != null) {
                    String dbDocNo = q.getQuotaDocNo().trim();
                    String normalizedDbDocNo = dbDocNo.replaceAll("\\s+", "");
                    // 精确匹配或去除所有空白字符后匹配（功能分类已经在查询时过滤了）
                    if (dbDocNo.equals(targetDocNo.trim()) || normalizedDbDocNo.equals(normalizedInputDocNo)) {
                        targetQuota = q;
                        break;
                    }
                }
            }
        }
        
        BigDecimal totalCarryOverAmount = ZERO;
        // 结转后的来源指标状态（默认保持原状态）
        Integer newSourceStatus = sourceQuota.getStatus();
        
        // 生成备注信息：从前端传来的memo，如果没有则自动生成
        // 优先使用前端传来的memo（已经包含学年学期信息）
        String baseMemo;
        if (request.getMemo() != null && !request.getMemo().isEmpty()) {
            baseMemo = request.getMemo();
        } else {
            // 如果前端没有传memo，后端查询学年学期信息生成
            com.ruoyi.system.domain.StSchoolYearSemester sourceSemester = 
                stSchoolYearSemesterMapper.selectStSchoolYearSemesterById(sourceQuota.getYearSemesterId());
            if (sourceSemester != null) {
                String schoolYear = sourceSemester.getSchoolYear() != null ? sourceSemester.getSchoolYear() : "";
                String semesterLabel = sourceSemester.getSemesterLabel() != null ? sourceSemester.getSemesterLabel() : "";
                if (!schoolYear.isEmpty() || !semesterLabel.isEmpty()) {
                    baseMemo = String.format("从%s%s结转", schoolYear, semesterLabel);
                } else {
                    baseMemo = "结转";
                }
            } else {
                baseMemo = "结转";
            }
        }
        
        if (targetQuota != null) {
            // 目标指标已存在，将资金增加到目标指标（不创建新指标）
            // 验证功能分类一致性：功能分类必须完全相同（小学只能与小学结转，初中只能与初中结转，高中只能与高中结转）
            // 注意：功能分类已经在查询时验证过，这里再次验证以确保一致性
            String targetFunctionCategory = targetQuota.getFunctionCategory();
            if (sourceFunctionCategory != null && targetFunctionCategory != null
                && !sourceFunctionCategory.equals(targetFunctionCategory)) {
                String sourceName = com.ruoyi.system.application.query.FunctionCategoryUtils.codeToName(sourceFunctionCategory);
                String targetName = com.ruoyi.system.application.query.FunctionCategoryUtils.codeToName(targetFunctionCategory);
                throw new ServiceException(String.format("目标指标的功能分类（%s）与来源指标的功能分类（%s）不一致，无法结转。功能分类必须完全相同（小学只能与小学结转，初中只能与初中结转，高中只能与高中结转）", 
                    targetName, sourceName));
            }
            
            // 遍历每个经济分类，增加目标指标的明细
            List<StSubsidyQuotaDetail> newDetailList = new ArrayList<>();
            List<Map<String, Object>> updateList = new ArrayList<>();
            
            for (StSubsidyQuotaDetail sourceDetail : filteredDetails) {
                BigDecimal carryOverAmount = safe(sourceDetail.getAvailableAmount());
                totalCarryOverAmount = totalCarryOverAmount.add(carryOverAmount);
                
                // 查找目标指标中是否已有该经济分类的明细
                List<StSubsidyQuotaDetail> targetDetails = stSubsidyQuotaDetailMapper.selectDetailListByQuotaId(targetQuota.getId());
                StSubsidyQuotaDetail targetDetail = targetDetails.stream()
                    .filter(d -> sourceDetail.getEconomyCategory().equals(d.getEconomyCategory()))
                    .findFirst()
                    .orElse(null);
                
                if (targetDetail != null) {
                    // 目标指标已有该经济分类
                    // 方案：创建新的明细记录来记录结转资金，保持数据清晰和可追溯
                    // 这样每条明细都有明确的来源，便于统计和审计
                    StSubsidyQuotaDetail carryOverDetail = new StSubsidyQuotaDetail();
                    carryOverDetail.setQuotaId(targetQuota.getId());
                    carryOverDetail.setSourceDetailId(sourceDetail.getId()); // 记录来源明细ID
                    carryOverDetail.setEconomyCategory(sourceDetail.getEconomyCategory());
                    carryOverDetail.setTotalAmount(carryOverAmount);
                    carryOverDetail.setAllocatedAmount(ZERO);
                    carryOverDetail.setMemo(baseMemo);
                    carryOverDetail.setStatus(1);
                    // 设置结转标识
                    carryOverDetail.setIsCarryover("1");
                    carryOverDetail.setSourceSemesterId(sourceQuota.getYearSemesterId());
                    newDetailList.add(carryOverDetail);
                } else {
                    // 目标指标没有该经济分类，创建新明细
                    StSubsidyQuotaDetail newDetail = new StSubsidyQuotaDetail();
                    newDetail.setQuotaId(targetQuota.getId());
                    newDetail.setSourceDetailId(sourceDetail.getId()); // 记录来源明细ID
                    newDetail.setEconomyCategory(sourceDetail.getEconomyCategory());
                    newDetail.setTotalAmount(carryOverAmount);
                    newDetail.setAllocatedAmount(ZERO);
                    newDetail.setMemo(baseMemo);
                    newDetail.setStatus(1);
                    // 设置结转标识
                    newDetail.setIsCarryover("1");
                    newDetail.setSourceSemesterId(sourceQuota.getYearSemesterId());
                    newDetailList.add(newDetail);
                }
                
                // 准备批量更新数据
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("detailId", sourceDetail.getId());
                updateMap.put("amount", carryOverAmount);
                updateList.add(updateMap);
            }
            
            // 批量插入明细（一次性插入所有明细）
            if (!newDetailList.isEmpty()) {
                int insertCount = stSubsidyQuotaDetailMapper.batchInsertStSubsidyQuotaDetail(newDetailList);
                if (insertCount != newDetailList.size()) {
                    throw new ServiceException(String.format(
                        "批量插入明细失败：预期%d条，实际%d条",
                        newDetailList.size(), insertCount));
                }
            }
            
            // 批量更新来源指标明细已分配金额（一次SQL更新所有明细）
            if (!updateList.isEmpty()) {
                int updateCount = stSubsidyQuotaDetailMapper.batchUpdateAllocatedAmount(updateList);
                if (updateCount != updateList.size()) {
                    throw new ServiceException(String.format(
                        "批量更新来源指标明细已分配金额失败：预期%d条，实际%d条，目标指标已存在",
                        updateList.size(), updateCount));
                }
            }
            
            // 更新目标指标的总指标（从明细表汇总）
            refreshQuotaSnapshot(targetQuota.getId(), targetQuota.getStatus());
            
            // 更新来源指标的状态和已分配金额
            updateSourceQuotaAfterCarryOver(sourceQuota, totalCarryOverAmount, totalAvailable);
            
            return targetQuota.getId();
        } else {
            // 目标指标不存在，创建新指标
            // 验证功能分类：如果请求中指定了功能分类，必须与来源指标的功能分类完全一致
            // 业务规则：功能分类必须完全相同（小学只能与小学结转，初中只能与初中结转，高中只能与高中结转）
            String requestFunctionCategory = request.getFunctionCategory();
            if (requestFunctionCategory != null && sourceFunctionCategory != null
                && !requestFunctionCategory.equals(sourceFunctionCategory)) {
                String sourceName = com.ruoyi.system.application.query.FunctionCategoryUtils.codeToName(sourceFunctionCategory);
                String requestName = com.ruoyi.system.application.query.FunctionCategoryUtils.codeToName(requestFunctionCategory);
                throw new ServiceException(String.format("指定的功能分类（%s）与来源指标的功能分类（%s）不一致，无法结转。功能分类必须完全相同（小学只能与小学结转，初中只能与初中结转，高中只能与高中结转）", 
                    requestName, sourceName));
            }
            
            StSubsidyQuota newQuota = new StSubsidyQuota();
            newQuota.setYearSemesterId(request.getYearSemesterId());
            newQuota.setQuotaDocNo(targetDocNo);
            // 始终使用来源指标的功能分类
            newQuota.setFunctionCategory(sourceFunctionCategory);
            newQuota.setQuotaSourceType(2); // 上学期结转
            newQuota.setSourceQuotaId(sourceQuota.getId());
            // 设置结转标识
            newQuota.setIsCarryover("1"); // 标记为结转资金
            newQuota.setSourceSemesterId(sourceQuota.getYearSemesterId()); // 记录来源学期
            newQuota.setIssueDate(new java.util.Date());
            newQuota.setBudgetProjectName(sourceQuota.getBudgetProjectName());
            newQuota.setBudgetLevel(sourceQuota.getBudgetLevel());
            newQuota.setStatus(1); // 默认启用
            newQuota.setMemo(baseMemo);
            
            int result = stSubsidyQuotaMapper.insertStSubsidyQuota(newQuota);
            if (result == 0) {
                throw new ServiceException("创建目标指标失败");
            }
            
            // 创建明细
            List<StSubsidyQuotaDetail> newDetailList = new ArrayList<>();
            List<Map<String, Object>> updateList = new ArrayList<>();
            
            for (StSubsidyQuotaDetail sourceDetail : filteredDetails) {
                BigDecimal carryOverAmount = safe(sourceDetail.getAvailableAmount());
                totalCarryOverAmount = totalCarryOverAmount.add(carryOverAmount);
                
                StSubsidyQuotaDetail newDetail = new StSubsidyQuotaDetail();
                newDetail.setQuotaId(newQuota.getId());
                newDetail.setSourceDetailId(sourceDetail.getId()); // 记录来源明细ID
                newDetail.setEconomyCategory(sourceDetail.getEconomyCategory());
                newDetail.setTotalAmount(carryOverAmount);
                newDetail.setAllocatedAmount(ZERO);
                newDetail.setMemo(baseMemo);
                newDetail.setStatus(1);
                // 设置结转标识
                newDetail.setIsCarryover("1");
                newDetail.setSourceSemesterId(sourceQuota.getYearSemesterId());
                newDetailList.add(newDetail);
                
                // 准备批量更新数据
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("detailId", sourceDetail.getId());
                updateMap.put("amount", carryOverAmount);
                updateList.add(updateMap);
            }
            
            // 批量插入明细（一次性插入所有明细）
            if (!newDetailList.isEmpty()) {
                int insertCount = stSubsidyQuotaDetailMapper.batchInsertStSubsidyQuotaDetail(newDetailList);
                if (insertCount != newDetailList.size()) {
                    throw new ServiceException(String.format(
                        "批量插入明细失败：预期%d条，实际%d条",
                        newDetailList.size(), insertCount));
                }
            }
            
            // 批量更新来源指标明细已分配金额（一次SQL更新所有明细）
            if (!updateList.isEmpty()) {
                int updateCount = stSubsidyQuotaDetailMapper.batchUpdateAllocatedAmount(updateList);
                if (updateCount != updateList.size()) {
                    throw new ServiceException(String.format(
                        "批量更新来源指标明细已分配金额失败：预期%d条，实际%d条，创建新指标",
                        updateList.size(), updateCount));
                }
            }
            
            // 刷新新指标快照
            refreshQuotaSnapshot(newQuota.getId(), newQuota.getStatus());
            
            // 更新来源指标的状态和已分配金额
            updateSourceQuotaAfterCarryOver(sourceQuota, totalCarryOverAmount, totalAvailable);
            
            return newQuota.getId();
        }
    }
}