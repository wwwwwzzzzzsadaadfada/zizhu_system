package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.domain.StFamilyMember;
import com.ruoyi.system.mapper.StFamilyMemberMapper;
import com.ruoyi.system.service.IStFamilyMemberService;

@Service
public class StFamilyMemberServiceImpl implements IStFamilyMemberService {

    @Autowired
    private StFamilyMemberMapper stFamilyMemberMapper;

    @Override
    public StFamilyMember selectStFamilyMemberById(Long id) {
        return stFamilyMemberMapper.selectStFamilyMemberById(id);
    }

    @Override
    public List<StFamilyMember> selectStFamilyMemberList(StFamilyMember query) {
        return stFamilyMemberMapper.selectStFamilyMemberList(query);
    }

    @Override
    public int insertStFamilyMember(StFamilyMember member) {
        return stFamilyMemberMapper.insertStFamilyMember(member);
    }

    @Override
    public int updateStFamilyMember(StFamilyMember member) {
        return stFamilyMemberMapper.updateStFamilyMember(member);
    }

    @Override
    public int deleteStFamilyMemberById(Long id) {
        return stFamilyMemberMapper.deleteStFamilyMemberById(id);
    }

    @Override
    public int deleteStFamilyMemberByIds(Long[] ids) {
        return stFamilyMemberMapper.deleteStFamilyMemberByIds(ids);
    }

    @Override
    @Transactional
    public void saveByStudentId(Long studentId, Long yearSemesterId, List<StFamilyMember> members) {
        if (studentId == null) {
            return;
        }
        // 清空旧数据
        stFamilyMemberMapper.deleteByStudentId(studentId);

        if (CollectionUtils.isEmpty(members)) {
            return;
        }

        List<StFamilyMember> insertList = new ArrayList<>();
        for (StFamilyMember item : members) {
            if (item == null) {
                continue;
            }
            if (item.getName() == null || item.getName().trim().isEmpty()) {
                continue;
            }
            StFamilyMember copy = new StFamilyMember();
            copy.setStudentId(studentId);
            copy.setYearSemesterId(item.getYearSemesterId() != null ? item.getYearSemesterId() : yearSemesterId);
            copy.setName(item.getName());
            copy.setAge(item.getAge());
            copy.setRelation(item.getRelation());
            copy.setEmployer(item.getEmployer());
            copy.setOccupation(item.getOccupation());
            copy.setHealthStatus(item.getHealthStatus());
            copy.setCreateBy(item.getCreateBy());
            copy.setUpdateBy(item.getUpdateBy());
            insertList.add(copy);
        }

        for (StFamilyMember insert : insertList) {
            stFamilyMemberMapper.insertStFamilyMember(insert);
        }
    }
}


