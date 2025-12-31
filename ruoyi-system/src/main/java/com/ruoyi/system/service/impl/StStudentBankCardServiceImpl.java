package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.domain.StStudentBankCard;
import com.ruoyi.system.mapper.StStudentBankCardMapper;
import com.ruoyi.system.service.IStStudentBankCardService;

/**
 * 学生银行卡Service业务层处理
 */
@Service
public class StStudentBankCardServiceImpl implements IStStudentBankCardService {

    @Autowired
    private StStudentBankCardMapper stStudentBankCardMapper;

    @Override
    public StStudentBankCard selectStStudentBankCardById(Long id) {
        return stStudentBankCardMapper.selectStStudentBankCardById(id);
    }

    @Override
    public List<StStudentBankCard> selectStStudentBankCardList(StStudentBankCard query) {
        return stStudentBankCardMapper.selectStStudentBankCardList(query);
    }

    @Override
    public int insertStStudentBankCard(StStudentBankCard card) {
        return stStudentBankCardMapper.insertStStudentBankCard(card);
    }

    @Override
    public int updateStStudentBankCard(StStudentBankCard card) {
        return stStudentBankCardMapper.updateStStudentBankCard(card);
    }

    @Override
    public int deleteStStudentBankCardById(Long id) {
        return stStudentBankCardMapper.deleteStStudentBankCardById(id);
    }

    @Override
    public int deleteStStudentBankCardByIds(Long[] ids) {
        return stStudentBankCardMapper.deleteStStudentBankCardByIds(ids);
    }

    @Override
    public int deleteByStudentId(Long studentId) {
        return stStudentBankCardMapper.deleteByStudentId(studentId);
    }

    /**
     * 覆盖保存学生银行卡列表（先清空再插入）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveByStudentId(Long studentId, List<StStudentBankCard> cards) {
        if (studentId == null) {
            return;
        }
        // 删除已有
        stStudentBankCardMapper.deleteByStudentId(studentId);

        if (CollectionUtils.isEmpty(cards)) {
            return;
        }

        List<StStudentBankCard> prepared = new ArrayList<>();
        boolean hasPrimary = false;
        for (StStudentBankCard card : cards) {
            if (card == null) {
                continue;
            }
            StStudentBankCard copy = new StStudentBankCard();
            copy.setStudentId(studentId);
            copy.setBankAccountNo(card.getBankAccountNo());
            copy.setBankType(card.getBankType() != null ? card.getBankType() : "信用社");
            copy.setBankName(card.getBankName());
            copy.setBranchName(card.getBranchName());
            copy.setAccountHolder(card.getAccountHolder());
            Integer isPrimary = card.getIsPrimary();
            copy.setIsPrimary(isPrimary != null ? isPrimary : 0);
            copy.setStatus(card.getStatus() != null ? card.getStatus() : 0);
            copy.setCreatedBy(card.getCreatedBy());
            copy.setUpdatedBy(card.getUpdatedBy());
            prepared.add(copy);
            if (Objects.equals(isPrimary, 1)) {
                hasPrimary = true;
            }
        }

        // 如果没有显式主卡，默认第一张为主卡
        if (!prepared.isEmpty() && !hasPrimary) {
            prepared.get(0).setIsPrimary(1);
        }

        for (StStudentBankCard card : prepared) {
            stStudentBankCardMapper.insertStStudentBankCard(card);
        }
    }
}


