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

    <!-- 顶部数据统计条 -->
    <div class="top-stats">
      <div class="stat-item" v-for="(item, index) in topStats" :key="index">
        <div class="stat-label">{{ item.label }}</div>
        <div class="stat-value">{{ item.value }}</div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧列 -->
      <div class="left-column">
        <!-- 困难类型分布 -->
        <div class="data-card">
          <div class="card-header">
            <span class="card-icon">▣</span>
            <h3 class="card-title">困难类型分布</h3>
          </div>
          <div class="card-body">
            <div ref="difficultyTypeChart" class="chart" style="height: 220px"></div>
          </div>
        </div>

        <!-- 各学段受助人数 -->
        <div class="data-card">
          <div class="card-header">
            <span class="card-icon">▣</span>
            <h3 class="card-title">各学段受助人数</h3>
          </div>
          <div class="card-body">
            <div ref="schoolLevelChart" class="chart" style="height: 220px"></div>
          </div>
        </div>

        <!-- 指标执行情况 -->
        <div class="data-card">
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
          <div class="card-header">
            <span class="card-icon">▣</span>
            <h3 class="card-title">广西崇左市区域分布</h3>
          </div>
          <div class="card-body">
            <div ref="mapChart" class="chart" style="height: 300px"></div>
          </div>
        </div>

        <!-- 月度受助趋势 -->
        <div class="data-card">
          <div class="card-header">
            <span class="card-icon">▣</span>
            <h3 class="card-title">本学期受助人数趋势</h3>
          </div>
          <div class="card-body">
            <div ref="trendChart" class="chart" style="height: 180px"></div>
          </div>
        </div>
      </div>

      <!-- 右侧列 -->
      <div class="right-column">
        <!-- 学段受助金额统计 -->
        <div class="data-card">
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
        <div class="data-card">
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
        <div class="data-card">
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
      mapChart: null
    }
  },
  mounted() {
    this.updateTime()
    this.initCharts()
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
    
    initCharts() {
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
  background: #E8F5E9;
  min-height: 100vh;
  font-family: 'Microsoft YaHei', Arial, sans-serif;

  // 顶部标题栏
  .dashboard-header {
    background: linear-gradient(to right, #43A047, #66BB6A);
    padding: 16px 32px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 3px solid #2E7D32;

    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;

      .header-icon {
        color: #FFFFFF;
        font-size: 24px;
        font-weight: bold;
      }

      .dashboard-title {
        margin: 0;
        font-size: 26px;
        font-weight: 600;
        color: #FFFFFF;
        letter-spacing: 2px;
      }
    }

    .header-right {
      .header-time {
        font-size: 14px;
        color: #FFFFFF;
        font-family: 'Courier New', monospace;
        background: rgba(255, 255, 255, 0.2);
        padding: 6px 16px;
        border-radius: 4px;
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
    background: #FFFFFF;
    border-radius: 4px;
    overflow: hidden;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
    display: flex;
    flex-direction: column;

    &.large-card {
      flex: 1;
    }

    .card-header {
      background: linear-gradient(to right, #66BB6A, #81C784);
      padding: 12px 16px;
      display: flex;
      align-items: center;
      gap: 8px;

      .card-icon {
        color: #FFFFFF;
        font-size: 16px;
      }

      .card-title {
        margin: 0;
        font-size: 15px;
        font-weight: 600;
        color: #FFFFFF;
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

