<template>
  <div class="dashboard-container">
    <!-- 顶部标题栏 -->
    <div class="dashboard-header">
      <div class="header-left">
        <span class="header-icon">≋</span>
        <h1 class="dashboard-title">学生资助数据中心</h1>
        <span class="header-icon">⋰⋱⋰</span>
      </div>
      <div class="header-right">
        <div class="header-time">{{ currentTime }}</div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧列 -->
      <div class="left-column">
        <!-- 困难类型分布 -->
        <div class="data-card fixed-height-card">
          <div class="card-header">
            <span class="card-icon">▣</span>
            <h3 class="card-title">困难类型分布</h3>
          </div>
          <div class="card-body">
            <div ref="difficultyTypeChart" class="chart" style="height: 220px"></div>
          </div>
        </div>

        <!-- 各学段受助人数 -->
        <div class="data-card fixed-height-card">
          <div class="card-header">
            <span class="card-icon">▣</span>
            <h3 class="card-title">各学段受助人数</h3>
          </div>
          <div class="card-body">
            <div ref="schoolLevelChart" class="chart" style="height: 220px"></div>
          </div>
        </div>

        <!-- 指标执行情况 -->
        <div class="data-card fixed-height-card">
          <div class="card-header">
            <span class="card-icon">▣</span>
            <h3 class="card-title">指标执行情况</h3>
          </div>
          <div class="card-body">
            <div class="quota-list">
              <div v-for="(item, index) in quotaData" :key="index" class="quota-item">
                <div class="quota-name">{{ item.name }}</div>
                <div class="quota-detail">
                  <div class="quota-bar">
                    <div class="quota-bar-fill" :style="{ width: item.percent + '%' }"></div>
                  </div>
                  <div class="quota-text">
                    <span class="quota-used">{{ item.used }}</span> / <span class="quota-total">{{ item.total }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 中间列 -->
      <div class="center-column">
        <!-- 崇左市地图 -->
        <div class="data-card map-card">
          <!-- 数据统计卡片(替代标题栏) -->
          <div class="stats-cards">
            <div class="stat-card-item" v-for="(item, index) in topStats" :key="index">
              <div class="stat-card-label">{{ item.label }}</div>
              <div class="stat-card-value">{{ item.value }}</div>
            </div>
          </div>
          
          <div class="card-body">
            <div ref="mapChart" class="chart" style="height: 380px; margin-top: 2px"></div>
          </div>
        </div>

        <!-- 占位空间 - 用于对齐 -->
        <div class="spacer" style="height: 104px"></div>

        <!-- 月度受助趋势 -->
        <div class="data-card fixed-height-card">
          <div class="card-header">
            <span class="card-icon">▣</span>
            <h3 class="card-title">本学期受助人数趋势</h3>
          </div>
          <div class="card-body">
            <div ref="trendChart" class="chart" style="height: 240px"></div>
          </div>
        </div>
      </div>

      <!-- 右侧列 -->
      <div class="right-column">
        <!-- 学段受助金额统计 -->
        <div class="data-card fixed-height-card">
          <div class="card-header">
            <span class="card-icon">▣</span>
            <h3 class="card-title">学段受助金额统计</h3>
          </div>
          <div class="card-body">
            <div class="amount-table">
              <div class="table-header">
                <div class="table-cell">学段</div>
                <div class="table-cell">受助人数</div>
                <div class="table-cell">受助金额</div>
              </div>
              <div class="table-row" v-for="(item, index) in amountData" :key="index">
                <div class="table-cell">{{ item.label }}</div>
                <div class="table-cell">{{ item.count }}</div>
                <div class="table-cell amount">{{ item.amount }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 资助项目列表 -->
        <div class="data-card fixed-height-card">
          <div class="card-header">
            <span class="card-icon">▣</span>
            <h3 class="card-title">资助项目列表</h3>
          </div>
          <div class="card-body">
            <div class="project-table">
              <div class="table-header">
                <div class="table-cell">状态</div>
                <div class="table-cell">项目类型</div>
                <div class="table-cell">时间</div>
              </div>
              <div class="table-row" v-for="(item, index) in projectData" :key="index">
                <div class="table-cell">
                  <span class="status-badge" :class="item.status">{{ item.statusText }}</span>
                </div>
                <div class="table-cell">{{ item.type }}</div>
                <div class="table-cell time">{{ item.time }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 公告栏 -->
        <div class="data-card fixed-height-card">
          <div class="card-header">
            <span class="card-icon">▣</span>
            <h3 class="card-title">用户注册统计</h3>
          </div>
          <div class="card-body">
            <div class="notice-table">
              <div class="table-header">
                <div class="table-cell">用户名</div>
                <div class="table-cell">注册时间</div>
              </div>
              <div class="table-row" v-for="(item, index) in noticeList.slice(0, 3)" :key="index">
                <div class="table-cell">{{ item.userName }}</div>
                <div class="table-cell time">{{ item.time }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: "Index",
  data() {
    return {
      currentTime: '',
      topStats: [
        { label: '在校学生', value: '3256' },
        { label: '困难学生', value: '856' },
        { label: '受助人次', value: '1520' },
        { label: '资助档案', value: '1520' },
        { label: '资助项目', value: '12' }
      ],
      statsData: {
        totalStudents: 3256,
        difficultyStudents: 856,
        totalBudget: 185.6,
        usedBudget: 142.3
      },
      quotaData: [
        { name: '助学金', used: 520, total: 600, percent: 87 },
        { name: '免学杂费', used: 230, total: 300, percent: 77 },
        { name: '营养改善', used: 180, total: 200, percent: 90 }
      ],
      amountData: [
        { label: '高中', amount: '86.5万元', count: 356 },
        { label: '初中', amount: '42.8万元', count: 285 },
        { label: '小学', amount: '13.0万元', count: 215 }
      ],
      projectData: [
        { status: 'pending', statusText: '已处理', type: '助学金', time: '2023-12-18 14:30' },
        { status: 'success', statusText: 'YG', type: '免学杂费', time: '2023-12-15 10:20' },
        { status: 'success', statusText: 'YG', type: '营养改善', time: '2023-12-10 16:45' }
      ],
      noticeList: [
        { userName: 'app注册统计', time: '2023-03' },
        { userName: 'app注册统计', time: '2023-04' },
        { userName: 'app注册统计', time: '2023-05' }
      ],
      difficultyTypeChart: null,
      schoolLevelChart: null,
      trendChart: null,
      mapChart: null,
      chongzuoGeoJson: null // 存储崇左市GeoJSON数据
    }
  },
  mounted() {
    this.updateTime()
    this.loadChongzuoGeoJson() // 先加载地图数据
    this.timeInterval = setInterval(this.updateTime, 1000)
  },
  beforeDestroy() {
    if (this.timeInterval) {
      clearInterval(this.timeInterval)
    }
    if (this.difficultyTypeChart) this.difficultyTypeChart.dispose()
    if (this.schoolLevelChart) this.schoolLevelChart.dispose()
    if (this.trendChart) this.trendChart.dispose()
    if (this.mapChart) this.mapChart.dispose()
  },
  methods: {
    updateTime() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const hours = String(now.getHours()).padStart(2, '0')
      const minutes = String(now.getMinutes()).padStart(2, '0')
      const seconds = String(now.getSeconds()).padStart(2, '0')
      this.currentTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    
    // 加载崇左市GeoJSON数据
    async loadChongzuoGeoJson() {
      try {
        const response = await fetch('https://geo.datav.aliyun.com/areas_v3/bound/451400_full.json')
        this.chongzuoGeoJson = await response.json()
        
        // 地图数据加载完成后再初始化图表
        this.initCharts()
      } catch (error) {
        console.error('加载崇左市地图数据失败:', error)
        this.$message.error('地图数据加载失败，请检查网络连接')
      }
    },
    
    // 根据受助学生数量计算绿色深浅
    getGreenColorByValue(value) {
      // 定义绿色梯度
      const colors = [
        { threshold: 50, color: '#E8F5E9' },   // 极浅绿
        { threshold: 100, color: '#C8E6C9' },  // 浅绿
        { threshold: 150, color: '#A5D6A7' },  // 中浅绿
        { threshold: 200, color: '#81C784' },  // 中绿
        { threshold: 250, color: '#66BB6A' },  // 中深绿
        { threshold: Infinity, color: '#4CAF50' } // 深绿
      ]
      
      for (let i = 0; i < colors.length; i++) {
        if (value <= colors[i].threshold) {
          return colors[i].color
        }
      }
      
      return '#4CAF50'
    },
    
    initCharts() {
      // 如果地图数据还未加载，等待
      if (!this.chongzuoGeoJson) {
        return
      }
      
      this.$nextTick(() => {
        this.initDifficultyTypeChart()
        this.initSchoolLevelChart()
        this.initMapChart()
        this.initTrendChart()
      })
    },
    
    initDifficultyTypeChart() {
      if (!this.$refs.difficultyTypeChart) return
      this.difficultyTypeChart = echarts.init(this.$refs.difficultyTypeChart)
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}人 ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: '5%',
          top: 'center',
          textStyle: { color: '#333', fontSize: 12 }
        },
        color: ['#52C41A', '#13C2C2', '#1890FF', '#FA8C16', '#F5222D'],
        series: [
          {
            type: 'pie',
            radius: ['45%', '70%'],
            center: ['35%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 4,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: { show: false },
            data: [
              { value: 256, name: '脱贫户' },
              { value: 186, name: '监测户' },
              { value: 142, name: '低保户' },
              { value: 178, name: '残疾学生' },
              { value: 94, name: '其他' }
            ]
          }
        ]
      }
      
      this.difficultyTypeChart.setOption(option)
      window.addEventListener('resize', () => this.difficultyTypeChart && this.difficultyTypeChart.resize())
    },
    
    initSchoolLevelChart() {
      if (!this.$refs.schoolLevelChart) return
      this.schoolLevelChart = echarts.init(this.$refs.schoolLevelChart)
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: {
          left: '8%',
          right: '8%',
          bottom: '15%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['小学', '初中', '高中'],
          axisLine: { lineStyle: { color: '#E0E6ED' } },
          axisLabel: { color: '#666', fontSize: 12 }
        },
        yAxis: {
          type: 'value',
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: { color: '#666' },
          splitLine: { lineStyle: { color: '#F0F2F5', type: 'dashed' } }
        },
        series: [
          {
            type: 'bar',
            data: [215, 285, 356],
            itemStyle: { color: '#52C41A', borderRadius: [4, 4, 0, 0] },
            barWidth: '40%',
            label: {
              show: true,
              position: 'top',
              formatter: '{c}人',
              color: '#52C41A',
              fontSize: 12,
              fontWeight: 600
            }
          }
        ]
      }
      
      this.schoolLevelChart.setOption(option)
      window.addEventListener('resize', () => this.schoolLevelChart && this.schoolLevelChart.resize())
    },
    
    initMapChart() {
      if (!this.$refs.mapChart || !this.chongzuoGeoJson) return
      
      // 注册崇左市地图
      echarts.registerMap('chongzuo', this.chongzuoGeoJson)
      
      this.mapChart = echarts.init(this.$refs.mapChart)
      
      // 崇左市各县区受助学生数据（模拟数据）
      const mapData = [
        { name: '江州区', value: 256 },
        { name: '扶绥县', value: 186 },
        { name: '宁明县', value: 142 },
        { name: '龙州县', value: 178 },
        { name: '大新县', value: 94 },
        { name: '天等县', value: 125 },
        { name: '凭祥市', value: 163 }
      ]
      
      const option = {
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#66BB6A',
          borderWidth: 1,
          padding: [8, 12],
          textStyle: {
            color: '#333',
            fontSize: 12
          },
          formatter: function(params) {
            if (params.data && params.data.value) {
              return '<strong>' + params.name + '</strong><br/>受助学生: <span style="color:#2E7D32;font-weight:bold">' + params.value + '</span> 人'
            }
            return '<strong>' + params.name + '</strong><br/>受助学生: <span style="color:#999">0</span> 人'
          }
        },
        series: [
          {
            name: '受助学生分布',
            type: 'map',
            map: 'chongzuo',
            roam: true,
            zoom: 1.3,
            scaleLimit: {
              min: 0.8,
              max: 3
            },
            label: {
              show: true,
              color: '#333',
              fontSize: 11,
              fontWeight: 'normal'
            },
            itemStyle: {
              areaColor: '#FFFFFF',
              borderColor: '#E0E0E0',
              borderWidth: 1
            },
            emphasis: {
              label: {
                show: true,
                color: '#2E7D32',
                fontSize: 12,
                fontWeight: 'bold'
              },
              itemStyle: {
                areaColor: '#A5D6A7',
                borderColor: '#66BB6A',
                borderWidth: 2,
                shadowBlur: 10,
                shadowColor: 'rgba(102, 187, 106, 0.3)'
              }
            },
            select: {
              label: {
                color: '#1B5E20',
                fontWeight: 'bold'
              },
              itemStyle: {
                areaColor: '#81C784',
                borderColor: '#4CAF50'
              }
            },
            data: mapData.map(item => ({
              name: item.name,
              value: item.value,
              itemStyle: {
                areaColor: item.value > 0 ? this.getGreenColorByValue(item.value) : '#FFFFFF',
                borderColor: item.value > 0 ? '#66BB6A' : '#E0E0E0',
                borderWidth: 1
              }
            }))
          }
        ]
      }
      
      this.mapChart.setOption(option)
      
      // 响应式调整
      window.addEventListener('resize', () => {
        if (this.mapChart) {
          this.mapChart.resize()
        }
      })
    },
    
    initTrendChart() {
      if (!this.$refs.trendChart) return
      this.trendChart = echarts.init(this.$refs.trendChart)
      
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['新增用户', '累计受助总人次'],
          bottom: '5%',
          textStyle: { color: '#666' }
        },
        grid: {
          left: '5%',
          right: '5%',
          bottom: '12%',
          top: '8%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['1日', '5日', '10日', '15日', '18日', '19日', '20日', '21日', '22日', '23日', '24日'],
          axisLine: { lineStyle: { color: '#E0E6ED' } },
          axisLabel: { color: '#666' }
        },
        yAxis: {
          type: 'value',
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: { color: '#666' },
          splitLine: { lineStyle: { color: '#F0F2F5', type: 'dashed' } }
        },
        series: [
          {
            name: '新增用户',
            type: 'line',
            data: [80, 85, 78, 75, 72, 70, 68, 65, 62, 60, 58],
            smooth: true,
            itemStyle: { color: '#FA8C16' },
            lineStyle: { width: 2 },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0, y: 0, x2: 0, y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(250, 140, 22, 0.3)' },
                  { offset: 1, color: 'rgba(250, 140, 22, 0.05)' }
                ]
              }
            }
          },
          {
            name: '累计受助总人次',
            type: 'line',
            data: [85, 88, 82, 80, 78, 75, 73, 70, 68, 65, 63],
            smooth: true,
            itemStyle: { color: '#52C41A' },
            lineStyle: { width: 2 },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0, y: 0, x2: 0, y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(82, 196, 26, 0.3)' },
                  { offset: 1, color: 'rgba(82, 196, 26, 0.05)' }
                ]
              }
            }
          }
        ]
      }
      
      this.trendChart.setOption(option)
      window.addEventListener('resize', () => this.trendChart && this.trendChart.resize())
    }
  }
}
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 0;
  background: linear-gradient(to bottom, #E8F5E9 0%, #F1F8E9 30%, #FFFFFF 70%);
  min-height: 100vh;
  font-family: 'Microsoft YaHei', Arial, sans-serif;

  // 顶部标题栏
  .dashboard-header {
    background: linear-gradient(to bottom, rgba(232, 245, 233, 0.8), rgba(255, 255, 255, 0.8)); // 浅绿色渐变白色
    backdrop-filter: blur(20px) saturate(180%); // 毛玻璃效果
    -webkit-backdrop-filter: blur(20px) saturate(180%);
    border-top: 4px solid #66BB6A; // 顶部绿色粗边框
    border-left: 2px solid rgba(102, 187, 106, 0.3); // 半透明绿色边框
    border-right: 2px solid rgba(102, 187, 106, 0.3); // 半透明绿色边框
    border-bottom: 2px solid rgba(102, 187, 106, 0.3); // 半透明绿色边框
    border-radius: 0 0 20px 20px; // 左下右下圆角
    padding: 20px 32px;
    margin: 0 16px 1px 16px; // 减小底部边距,让内容更靠近
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 
      0 8px 32px rgba(102, 187, 106, 0.15), // 增强阴影
      inset 0 1px 2px rgba(255, 255, 255, 0.9); // 增强高光

    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;

      .header-icon {
        color: #66BB6A; // 绿色图标
        font-size: 28px;
        font-weight: bold;
        text-shadow: 0 2px 4px rgba(102, 187, 106, 0.3);
      }

      .dashboard-title {
        margin: 0;
        font-size: 28px;
        font-weight: 700; // 加粗
        color: #2E7D32; // 深绿色
        letter-spacing: 3px;
        text-shadow: 0 2px 4px rgba(46, 125, 50, 0.2);
      }
    }

    .header-right {
      .header-time {
        font-size: 15px;
        color: #2E7D32; // 深绿色
        font-weight: 600;
        font-family: 'Courier New', monospace;
        background: rgba(232, 245, 233, 0.6); // 浅绿色背景
        backdrop-filter: blur(10px);
        -webkit-backdrop-filter: blur(10px);
        padding: 8px 18px;
        border-radius: 20px;
        border: 1px solid rgba(102, 187, 106, 0.3);
        box-shadow: 
          0 4px 12px rgba(102, 187, 106, 0.1),
          inset 0 1px 1px rgba(255, 255, 255, 0.8);
      }
    }
  }

  // 顶部数据统计条
  .top-stats {
    background: #FFFFFF;
    padding: 20px 32px;
    display: flex;
    justify-content: space-around;
    align-items: center;
    border-bottom: 2px solid #C8E6C9;

    .stat-item {
      text-align: center;
      padding: 0 20px;
      border-right: 1px solid #E0E0E0;

      &:last-child {
        border-right: none;
      }

      .stat-label {
        font-size: 14px;
        color: #757575;
        margin-bottom: 8px;
      }

      .stat-value {
        font-size: 32px;
        font-weight: 600;
        color: #2E7D32;
        font-family: Arial, sans-serif;
      }
    }
  }

  // 数据统计卡片(中间列顶部)
  .stats-cards {
    display: flex;
    gap: 12px;
    padding: 16px;
    background: linear-gradient(to bottom, rgba(200, 230, 201, 0.85), rgba(232, 245, 233, 0.8)); // 增强绿色渐变
    backdrop-filter: blur(20px) saturate(180%); // 增强毛玻璃效果
    -webkit-backdrop-filter: blur(20px) saturate(180%);
    border-top: 4px solid #66BB6A; // 顶部绿色粗边框
    border-left: 2px solid rgba(102, 187, 106, 0.3); // 半透明绿色边框
    border-right: 2px solid rgba(102, 187, 106, 0.3); // 半透明绿色边框
    border-bottom: 2px solid rgba(102, 187, 106, 0.3); // 半透明绿色边框
    border-radius: 0 0 20px 20px; // 左下右下圆角
    box-shadow: 
      0 8px 32px rgba(102, 187, 106, 0.15), // 增强阴影
      inset 0 1px 2px rgba(255, 255, 255, 0.9); // 增强高光

    .stat-card-item {
      flex: 1;
      background: rgba(255, 255, 255, 0.7); // 半透明白色背景
      backdrop-filter: blur(10px); // 背景模糊效果
      -webkit-backdrop-filter: blur(10px); // Safari支持
      border: 2px solid rgba(102, 187, 106, 0.3); // 半透明边框
      border-radius: 30px; // 增大圆角,更明显的圆角效果
      padding: 14px 8px;
      text-align: center;
      box-shadow: 
        0 8px 32px rgba(102, 187, 106, 0.15), // 外阴影
        inset 0 1px 1px rgba(255, 255, 255, 0.9), // 内部高光
        inset 0 -1px 1px rgba(0, 0, 0, 0.05); // 内部底部阴影
      transition: all 0.3s;

      &:hover {
        transform: translateY(-3px) scale(1.02); // 上移并稍微放大
        box-shadow: 
          0 12px 40px rgba(102, 187, 106, 0.25), // 加强外阴影
          inset 0 1px 2px rgba(255, 255, 255, 1), // 加强高光
          inset 0 -1px 2px rgba(0, 0, 0, 0.08);
        border-color: rgba(67, 160, 71, 0.5); // 边框颜色加深
        background: rgba(241, 248, 233, 0.8); // 浅绿色半透明背景
      }

      .stat-card-label {
        font-size: 14px;
        color: #666;
        font-weight: 500;
        margin-bottom: 8px;
      }

      .stat-card-value {
        font-size: 24px;
        font-weight: 700;
        color: #2E7D32;
        font-family: Arial, sans-serif;
        line-height: 1;
      }
    }
  }

  // 主要内容区域
  .main-content {
    display: grid;
    grid-template-columns: 26% 48% 26%;
    gap: 16px;
    padding: 16px;
    height: calc(100vh - 200px);

    .left-column,
    .center-column,
    .right-column {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
  }

  // 数据卡片
  .data-card {
    background: linear-gradient(to bottom, rgba(200, 230, 201, 0.8), rgba(232, 245, 233, 0.75)); // 增强绿色渐变
    backdrop-filter: blur(20px) saturate(180%); // 增强毛玻璃效果
    -webkit-backdrop-filter: blur(20px) saturate(180%); // Safari支持
    border-top: 4px solid #66BB6A; // 顶部绿色粗边框
    border-left: 2px solid rgba(102, 187, 106, 0.3); // 半透明绿色边框
    border-right: 2px solid rgba(102, 187, 106, 0.3); // 半透明绿色边框
    border-bottom: 2px solid rgba(102, 187, 106, 0.3); // 半透明绿色边框
    border-radius: 0 0 20px 20px; // 左下右下圆角
    overflow: hidden;
    box-shadow: 
      0 8px 32px rgba(102, 187, 106, 0.15), // 增强阴影
      inset 0 1px 2px rgba(255, 255, 255, 0.9); // 增强高光
    display: flex;
    flex-direction: column;

    &.large-card {
      flex: 1;
    }

    // 固定高度卡片 - 确保横向对齐
    &.fixed-height-card {
      height: 310px; // 统一高度

      .card-body {
        overflow: hidden; // 隐藏滚动条
      }
    }

    // 地图卡片特殊样式 - 与背景融为一体
    &.map-card {
      background: transparent;
      border: none;
      box-shadow: none;

      .card-body {
        padding: 8px 0;
        background: transparent;
      }
    }

    .card-header {
      background: linear-gradient(to right, rgba(200, 230, 201, 0.6), rgba(232, 245, 233, 0.5)); // 增强绿色横向渐变
      backdrop-filter: blur(15px) saturate(180%); // 模糊效果
      -webkit-backdrop-filter: blur(15px) saturate(180%);
      border-bottom: 1px solid rgba(102, 187, 106, 0.3);
      padding: 14px 18px;
      display: flex;
      align-items: center;
      gap: 10px;

      .card-icon {
        color: #66BB6A;
        font-size: 18px;
        font-weight: bold;
      }

      .card-title {
        margin: 0;
        font-size: 17px; // 字体增大
        font-weight: 700; // 加粗
        color: #2E7D32;
        letter-spacing: 0.5px; // 字间距
      }
    }

    .card-body {
      padding: 16px;
      flex: 1;
      overflow: auto;
    }
  }

  // 指标列表
  .quota-list {
    .quota-item {
      margin-bottom: 20px;

      &:last-child {
        margin-bottom: 0;
      }

      .quota-name {
        font-size: 14px;
        color: #424242;
        margin-bottom: 8px;
        font-weight: 500;
      }

      .quota-detail {
        display: flex;
        align-items: center;
        gap: 12px;

        .quota-bar {
          flex: 1;
          height: 10px;
          background: #E0E0E0;
          border-radius: 5px;
          overflow: hidden;

          .quota-bar-fill {
            height: 100%;
            background: #66BB6A;
            transition: width 0.3s;
            border-radius: 5px;
          }
        }

        .quota-text {
          font-size: 13px;
          color: #757575;
          white-space: nowrap;
          min-width: 70px;
          text-align: right;

          .quota-used {
            color: #2E7D32;
            font-weight: 600;
          }

          .quota-total {
            color: #757575;
          }
        }
      }
    }
  }

  // 表格样式
  .amount-table,
  .project-table,
  .notice-table {
    .table-header {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      background: #F1F8E9;
      padding: 10px;
      border-radius: 4px;
      margin-bottom: 8px;

      .table-cell {
        font-size: 13px;
        color: #424242;
        font-weight: 600;
        text-align: center;
      }
    }

    .table-row {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      padding: 12px 10px;
      border-bottom: 1px solid #F5F5F5;
      transition: background 0.2s;

      &:hover {
        background: #F9FBE7;
      }

      &:last-child {
        border-bottom: none;
      }

      .table-cell {
        font-size: 13px;
        color: #616161;
        text-align: center;
        display: flex;
        align-items: center;
        justify-content: center;

        &.amount {
          color: #F57C00;
          font-weight: 600;
        }

        &.time {
          color: #9E9E9E;
          font-size: 12px;
        }

        .status-badge {
          padding: 2px 8px;
          border-radius: 2px;
          font-size: 12px;
          font-weight: 500;

          &.pending {
            background: #FFF3E0;
            color: #E65100;
          }

          &.success {
            background: #E8F5E9;
            color: #2E7D32;
          }
        }
      }
    }
  }

  // 响应式
  @media (max-width: 1400px) {
    .main-content {
      grid-template-columns: 1fr;
      height: auto;

      .center-column {
        order: -1;
      }
    }
  }
}
</style>

