/**
 * 将前端地区数据转换为后端JSON配置文件
 * 使用方法: node scripts/convert-region-data.js
 */

const fs = require('fs');
const path = require('path');

// 读取前端数据文件
const frontendDataPath = path.join(__dirname, '../ruoyi-ui/src/assets/data/guangxi-region.js');
const outputPath = path.join(__dirname, '../ruoyi-common/src/main/resources/config/guangxi-regions.json');

// 读取前端JS文件内容
let content = fs.readFileSync(frontendDataPath, 'utf8');

// 提取数据部分（移除注释和module.exports）
const dataMatch = content.match(/var guangxiRegions = (\[[\s\S]*?\]);/);
if (!dataMatch) {
    console.error('无法解析前端数据文件');
    process.exit(1);
}

// 使用eval执行获取数据（注意：仅用于构建脚本，生产环境应使用更安全的方式）
const guangxiRegions = eval('(' + dataMatch[1] + ')');

// 扁平化数据：提取所有地区代码和名称
const regionMap = {};

function extractRegions(regions) {
    if (!regions || !Array.isArray(regions)) {
        return;
    }
    
    regions.forEach(region => {
        if (region.value && region.label) {
            regionMap[region.value] = region.label;
        }
        
        // 递归处理子级
        if (region.children) {
            extractRegions(region.children);
        }
    });
}

// 提取所有地区数据
extractRegions(guangxiRegions);

// 确保输出目录存在
const outputDir = path.dirname(outputPath);
if (!fs.existsSync(outputDir)) {
    fs.mkdirSync(outputDir, { recursive: true });
}

// 写入JSON文件
fs.writeFileSync(outputPath, JSON.stringify(regionMap, null, 2), 'utf8');

console.log('✅ 转换完成！');
console.log(`📁 输出文件: ${outputPath}`);
console.log(`📊 共提取 ${Object.keys(regionMap).length} 条地区数据`);

