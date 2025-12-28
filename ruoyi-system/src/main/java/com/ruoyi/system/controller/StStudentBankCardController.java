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
import com.ruoyi.system.domain.StStudentBankCard;
import com.ruoyi.system.service.IStStudentBankCardService;

/**
 * 学生银行卡信息Controller
 */
@RestController
@RequestMapping("/system/studentBankCard")
public class StStudentBankCardController extends BaseController {

    @Autowired
    private IStStudentBankCardService stStudentBankCardService;

    /**
     * 查询银行卡列表（按 studentId）
     */
    @PreAuthorize("@ss.hasPermi('system:students:list')")
    @GetMapping("/list")
    public AjaxResult list(StStudentBankCard query) {
        List<StStudentBankCard> list = stStudentBankCardService.selectStStudentBankCardList(query);
        return success(list);
    }

    /**
     * 获取银行卡详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:students:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(stStudentBankCardService.selectStStudentBankCardById(id));
    }

    /**
     * 新增银行卡
     */
    @PreAuthorize("@ss.hasPermi('system:students:add')")
    @Log(title = "学生银行卡", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StStudentBankCard card) {
        return toAjax(stStudentBankCardService.insertStStudentBankCard(card));
    }

    /**
     * 修改银行卡
     */
    @PreAuthorize("@ss.hasPermi('system:students:edit')")
    @Log(title = "学生银行卡", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StStudentBankCard card) {
        return toAjax(stStudentBankCardService.updateStStudentBankCard(card));
    }

    /**
     * 删除银行卡
     */
    @PreAuthorize("@ss.hasPermi('system:students:remove')")
    @Log(title = "学生银行卡", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(stStudentBankCardService.deleteStStudentBankCardByIds(ids));
    }

    /**
     * 覆盖保存某个学生的银行卡列表（随学生表单一起）
     */
    @PreAuthorize("@ss.hasPermi('system:students:edit')")
    @Log(title = "学生银行卡", businessType = BusinessType.UPDATE)
    @PostMapping("/batch/save")
    public AjaxResult batchSave(@RequestBody BankCardBatchSaveRequest request) {
        stStudentBankCardService.saveByStudentId(request.getStudentId(), request.getCards());
        return success();
    }

    public static class BankCardBatchSaveRequest {
        private Long studentId;
        private List<StStudentBankCard> cards;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public List<StStudentBankCard> getCards() {
            return cards;
        }

        public void setCards(List<StStudentBankCard> cards) {
            this.cards = cards;
        }
    }
}


