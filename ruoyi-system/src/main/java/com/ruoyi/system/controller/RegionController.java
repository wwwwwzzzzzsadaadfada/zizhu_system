package com.ruoyi.system.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.RegionCodeUtils;

/**
 * 地区数据Controller
 * 提供地区数据的API接口，供前端级联选择器使用
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@RestController
@RequestMapping("/system/region")
public class RegionController extends BaseController
{
    /**
     * 获取地区树形数据（用于前端级联选择器）
     * 
     * 返回格式：
     * [
     *   {
     *     "value": "450100",
     *     "label": "南宁市",
     *     "children": [
     *       {
     *         "value": "450102",
     *         "label": "兴宁区",
     *         "children": [
     *           {
     *             "value": "450102001",
     *             "label": "朝阳街道",
     *             "children": []
     *           }
     *         ]
     *       }
     *     ]
     *   }
     * ]
     * 
     * @return 地区树形数据
     */
    @GetMapping("/tree")
    public AjaxResult getRegionTree()
    {
        try {
            List<RegionCodeUtils.RegionTreeNode> tree = RegionCodeUtils.buildRegionTree();
            return success(tree);
        } catch (Exception e) {
            logger.error("获取地区树形数据失败", e);
            return error("获取地区数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据地区代码获取地区名称
     * 
     * @param code 地区代码
     * @return 地区名称
     */
    @GetMapping("/name/{code}")
    public AjaxResult getRegionName(@PathVariable("code") String code)
    {
        try {
            String name = RegionCodeUtils.getRegionNameByCode(code);
            if (name != null) {
                return success(name);
            } else {
                return error("未找到对应的地区名称，code=" + code);
            }
        } catch (Exception e) {
            logger.error("获取地区名称失败", e);
            return error("获取地区名称失败：" + e.getMessage());
        }
    }
}

