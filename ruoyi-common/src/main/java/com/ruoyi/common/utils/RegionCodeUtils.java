package com.ruoyi.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 地区代码工具类
 * 提供地区代码到名称的映射功能
 * 数据来源：国家统计局公开数据（广西壮族自治区）
 * 
 * 使用说明：
 * 1. 方案A（推荐）：从配置文件加载完整数据（见 loadFromConfig 方法）
 * 2. 方案B：使用第三方API（见 getRegionNameByApi 方法）
 * 3. 方案C：使用本地静态数据（当前实现，仅包含部分数据）
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
public class RegionCodeUtils {
    
    private static final Logger log = LoggerFactory.getLogger(RegionCodeUtils.class);
    
    /**
     * 地区代码到名称的映射表
     * key: 地区代码（如：450100）
     * value: 地区名称（如：南宁市）
     */
    private static final Map<String, String> REGION_CODE_MAP = new HashMap<>();
    
    static {
        // 初始化基础数据（包含常用地区）
        initializeBasicData();
        
        // 尝试从配置文件加载完整数据
        // 如果配置文件存在，会覆盖或补充基础数据
        loadFromConfig();
    }
    
    /**
     * 初始化基础数据
     * 注意：这里只包含部分常用数据，完整数据应该从配置文件加载
     */
    private static void initializeBasicData() {
        // 南宁市
        REGION_CODE_MAP.put("450100", "南宁市");
        REGION_CODE_MAP.put("450102", "兴宁区");
        REGION_CODE_MAP.put("450103", "青秀区");
        REGION_CODE_MAP.put("450105", "江南区");
        REGION_CODE_MAP.put("450107", "西乡塘区");
        REGION_CODE_MAP.put("450108", "良庆区");
        REGION_CODE_MAP.put("450109", "邕宁区");
        REGION_CODE_MAP.put("450110", "武鸣区");
        REGION_CODE_MAP.put("450123", "隆安县");
        REGION_CODE_MAP.put("450124", "马山县");
        REGION_CODE_MAP.put("450125", "上林县");
        REGION_CODE_MAP.put("450126", "宾阳县");
        REGION_CODE_MAP.put("450127", "横县");
        
        // 崇左市（重点区域）
        REGION_CODE_MAP.put("451400", "崇左市");
        REGION_CODE_MAP.put("451402", "江州区");
        REGION_CODE_MAP.put("451421", "扶绥县");
        REGION_CODE_MAP.put("451422", "宁明县");
        REGION_CODE_MAP.put("451423", "龙州县");
        REGION_CODE_MAP.put("451424", "大新县");
        REGION_CODE_MAP.put("451425", "天等县");
        REGION_CODE_MAP.put("451481", "凭祥市");
        
        // 乡镇级别（示例，实际应该包含所有乡镇）
        REGION_CODE_MAP.put("450102001", "朝阳街道");
        REGION_CODE_MAP.put("450102002", "民生街道");
        REGION_CODE_MAP.put("450102003", "三塘镇");
        REGION_CODE_MAP.put("450102004", "五塘镇");
        REGION_CODE_MAP.put("450102005", "昆仑镇");
        
        // 注意：完整数据应该从配置文件加载
        // 建议使用 loadFromConfig() 方法从 JSON/YAML 配置文件加载
    }
    
    /**
     * 从配置文件加载完整地区数据
     * 配置文件格式：JSON（扁平化，code -> name 映射）
     * 位置：classpath:config/guangxi-regions.json
     * 
     * JSON格式示例：
     * {
     *   "450100": "南宁市",
     *   "450102": "兴宁区",
     *   "450102001": "朝阳街道"
     * }
     * 
     * @return 是否加载成功
     */
    public static boolean loadFromConfig() {
        try {
            // 使用ClassLoader加载资源文件
            java.io.InputStream is = RegionCodeUtils.class
                .getClassLoader()
                .getResourceAsStream("config/guangxi-regions.json");
            
            if (is == null) {
                log.warn("[地区数据] 配置文件不存在：config/guangxi-regions.json，使用基础数据");
                return false;
            }
            
            // 读取文件内容
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(is, "UTF-8"));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            is.close();
            
            // 使用Fastjson2解析JSON
            com.alibaba.fastjson2.JSONObject jsonObject = 
                com.alibaba.fastjson2.JSON.parseObject(content.toString());
            
            // 清空现有数据（可选，如果希望保留基础数据则注释掉）
            // REGION_CODE_MAP.clear();
            
            // 加载所有地区数据
            int count = 0;
            for (String key : jsonObject.keySet()) {
                String value = jsonObject.getString(key);
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    REGION_CODE_MAP.put(key, value);
                    count++;
                }
            }
            
            log.info("[地区数据] 成功从配置文件加载 {} 条地区数据", count);
            return true;
            
        } catch (Exception e) {
            log.error("[地区数据] 从配置文件加载数据失败，使用基础数据", e);
            return false;
        }
    }
    
    /**
     * 根据地区代码获取地区名称
     * 
     * @param code 地区代码（如：450100）
     * @return 地区名称（如：南宁市），如果不存在则返回null
     */
    public static String getRegionNameByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return REGION_CODE_MAP.get(code);
    }
    
    /**
     * 根据地区代码列表获取地区名称列表
     * 
     * @param codes 地区代码列表（如：["450100", "450102", "450102001"]）
     * @return 地区名称列表（如：["南宁市", "兴宁区", "朝阳街道"]）
     */
    public static List<String> getRegionNamesByCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> names = new ArrayList<>();
        for (String code : codes) {
            String name = getRegionNameByCode(code);
            if (StringUtils.isNotBlank(name)) {
                names.add(name);
            } else {
                // 如果找不到对应的名称，记录警告
                // 实际应用中应该记录日志
            }
        }
        return names;
    }
    
    /**
     * 根据地区代码列表构建完整地址
     * 
     * @param codes 地区代码列表
     * @param village 村/社区
     * @param hamlet 屯/组
     * @return 完整地址
     */
    public static String buildFullAddress(List<String> codes, String village, String hamlet) {
        if (codes == null || codes.isEmpty()) {
            return null;
        }
        
        StringBuilder address = new StringBuilder("广西壮族自治区");
        
        // 获取地区名称列表
        List<String> names = getRegionNamesByCodes(codes);
        for (String name : names) {
            address.append(name);
        }
        
        // 拼接村/社区
        if (StringUtils.isNotBlank(village)) {
            address.append(village);
        }
        
        // 拼接屯/组
        if (StringUtils.isNotBlank(hamlet)) {
            address.append(hamlet);
        }
        
        return address.toString();
    }
    
    /**
     * 检查地区代码是否存在
     * 
     * @param code 地区代码
     * @return 是否存在
     */
    public static boolean isValidRegionCode(String code) {
        return StringUtils.isNotBlank(code) && REGION_CODE_MAP.containsKey(code);
    }
    
    /**
     * 使用第三方API获取地区名称（备用方案）
     * 
     * 推荐API：
     * 1. 高德地图API（免费额度：每日30万次）
     *    - 接口：https://restapi.amap.com/v3/config/district
     *    - 需要：注册高德开放平台账号，获取API Key
     *    - 文档：https://lbs.amap.com/api/webservice/guide/api/district
     * 
     * 2. 百度地图API（免费额度：每日6万次）
     *    - 接口：https://api.map.baidu.com/geocoding/v3/
     *    - 需要：注册百度地图开放平台账号，获取AK
     *    - 文档：https://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-geocoding
     * 
     * 3. 国家统计局公开数据（完全免费，但需要定期更新）
     *    - 数据源：http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/
     *    - 建议：下载数据后转换为本地配置文件
     * 
     * @param code 地区代码
     * @return 地区名称，如果API调用失败则返回null
     */
    public static String getRegionNameByApi(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        
        // 优先使用本地缓存
        String cachedName = REGION_CODE_MAP.get(code);
        if (StringUtils.isNotBlank(cachedName)) {
            return cachedName;
        }
        
        // TODO: 实现第三方API调用
        // 示例：高德地图API调用
        // String apiKey = "your-amap-api-key";
        // String url = "https://restapi.amap.com/v3/config/district?key=" + apiKey + "&keywords=" + code;
        // 调用HTTP接口，解析返回的JSON，提取地区名称
        
        log.warn("[地区数据] 第三方API调用功能待实现，code={}", code);
        return null;
    }
    
    /**
     * 批量添加地区数据（用于从配置文件或数据库加载）
     * 
     * @param code 地区代码
     * @param name 地区名称
     */
    public static void addRegionData(String code, String name) {
        if (StringUtils.isNotBlank(code) && StringUtils.isNotBlank(name)) {
            REGION_CODE_MAP.put(code, name);
        }
    }
    
    /**
     * 获取已加载的地区数量
     * 
     * @return 地区数量
     */
    public static int getRegionCount() {
        return REGION_CODE_MAP.size();
    }
    
    /**
     * 地区树节点（用于前端级联选择器）
     */
    public static class RegionTreeNode {
        private String value;  // 地区代码
        private String label;  // 地区名称
        private List<RegionTreeNode> children;  // 子节点
        
        public RegionTreeNode() {
        }
        
        public RegionTreeNode(String value, String label) {
            this.value = value;
            this.label = label;
            this.children = new ArrayList<>();
        }
        
        public String getValue() {
            return value;
        }
        
        public void setValue(String value) {
            this.value = value;
        }
        
        public String getLabel() {
            return label;
        }
        
        public void setLabel(String label) {
            this.label = label;
        }
        
        public List<RegionTreeNode> getChildren() {
            return children;
        }
        
        public void setChildren(List<RegionTreeNode> children) {
            this.children = children;
        }
    }
    
    /**
     * 将扁平化的地区数据转换为树形结构（供前端级联选择器使用）
     * 
     * 地区代码层级规则：
     * - 省级：450000（广西壮族自治区，通常不显示）
     * - 市级：450100（南宁市）- 6位，后2位是00
     * - 县级：450102（兴宁区）- 6位，后2位不是00
     * - 乡镇级：450102001（朝阳街道）- 9位
     * 
     * @return 树形结构的地区数据列表
     */
    public static List<RegionTreeNode> buildRegionTree() {
        List<RegionTreeNode> tree = new ArrayList<>();
        
        // 用于存储已创建的节点，key为地区代码
        Map<String, RegionTreeNode> nodeMap = new HashMap<>();
        
        // 第一遍：创建所有节点
        for (Map.Entry<String, String> entry : REGION_CODE_MAP.entrySet()) {
            String code = entry.getKey();
            String name = entry.getValue();
            
            if (StringUtils.isBlank(code) || StringUtils.isBlank(name)) {
                continue;
            }
            
            // 创建当前节点
            RegionTreeNode currentNode = new RegionTreeNode(code, name);
            nodeMap.put(code, currentNode);
        }
        
        // 第二遍：建立父子关系
        for (Map.Entry<String, String> entry : REGION_CODE_MAP.entrySet()) {
            String code = entry.getKey();
            RegionTreeNode currentNode = nodeMap.get(code);
            
            if (currentNode == null) {
                continue;
            }
            
            int codeLength = code.length();
            
            if (codeLength == 6) {
                // 6位代码：可能是市级或县级
                String suffix = code.substring(4, 6);
                if ("00".equals(suffix)) {
                    // 市级代码（如：450100），作为顶级节点
                    tree.add(currentNode);
                } else {
                    // 县级代码（如：450102），需要找到父级（市级）
                    String parentCode = code.substring(0, 4) + "00";
                    RegionTreeNode parentNode = nodeMap.get(parentCode);
                    if (parentNode != null) {
                        parentNode.getChildren().add(currentNode);
                    } else {
                        // 如果父节点不存在，创建虚拟父节点
                        String parentName = getRegionNameByCode(parentCode);
                        if (StringUtils.isBlank(parentName)) {
                            parentName = "未知市";
                        }
                        parentNode = new RegionTreeNode(parentCode, parentName);
                        parentNode.getChildren().add(currentNode);
                        nodeMap.put(parentCode, parentNode);
                        tree.add(parentNode);
                    }
                }
            } else if (codeLength == 9) {
                // 9位代码：乡镇级（如：450102001）
                // 父级是县级（前6位）
                String parentCode = code.substring(0, 6);
                RegionTreeNode parentNode = nodeMap.get(parentCode);
                if (parentNode != null) {
                    parentNode.getChildren().add(currentNode);
                } else {
                    // 如果父节点不存在，创建虚拟父节点
                    String parentName = getRegionNameByCode(parentCode);
                    if (StringUtils.isBlank(parentName)) {
                        parentName = "未知区县";
                    }
                    parentNode = new RegionTreeNode(parentCode, parentName);
                    parentNode.getChildren().add(currentNode);
                    nodeMap.put(parentCode, parentNode);
                    
                    // 继续向上查找市级父节点
                    String cityCode = parentCode.substring(0, 4) + "00";
                    RegionTreeNode cityNode = nodeMap.get(cityCode);
                    if (cityNode != null) {
                        cityNode.getChildren().add(parentNode);
                    } else {
                        String cityName = getRegionNameByCode(cityCode);
                        if (StringUtils.isBlank(cityName)) {
                            cityName = "未知市";
                        }
                        cityNode = new RegionTreeNode(cityCode, cityName);
                        cityNode.getChildren().add(parentNode);
                        nodeMap.put(cityCode, cityNode);
                        tree.add(cityNode);
                    }
                }
            }
        }
        
        // 对树进行排序（按代码排序）
        sortTree(tree);
        
        log.info("[地区数据] 构建地区树完成，共 {} 个顶级节点", tree.size());
        return tree;
    }
    
    /**
     * 递归排序树节点
     */
    private static void sortTree(List<RegionTreeNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        
        // 按代码排序
        nodes.sort((a, b) -> {
            if (a.getValue() == null && b.getValue() == null) {
                return 0;
            }
            if (a.getValue() == null) {
                return 1;
            }
            if (b.getValue() == null) {
                return -1;
            }
            return a.getValue().compareTo(b.getValue());
        });
        
        // 递归排序子节点
        for (RegionTreeNode node : nodes) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                sortTree(node.getChildren());
            }
        }
    }
}

