<template>
  <div class="dashboard-container">
    <!-- 顶部标题栏 -->
    <div class="dashboard-header">
      <div class="header-left">
        <span class="header-icon">≋</span>
        <h1 class="dashboard-title">学生资助数据中心</h1>
        <span class="header-icon">⋰⋱⋰</span>
      </div>
      <div class="header-center">
        <!-- 学段切换器 -->
        <div class="segment-switcher">
          <div 
            class="segment-item" 
            :class="{ active: currentSegment === 'all' }"
            @click="switchSegment('all')"
          >
            <svg class="segment-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="3" y="3" width="7" height="7" stroke="currentColor" stroke-width="2" rx="1"/>
              <rect x="14" y="3" width="7" height="7" stroke="currentColor" stroke-width="2" rx="1"/>
              <rect x="3" y="14" width="7" height="7" stroke="currentColor" stroke-width="2" rx="1"/>
              <rect x="14" y="14" width="7" height="7" stroke="currentColor" stroke-width="2" rx="1"/>
            </svg>
            <span class="segment-text">全部</span>
          </div>
          <div 
            class="segment-item" 
            :class="{ active: currentSegment === 'high' }"
            @click="switchSegment('high')"
          >
            <svg class="segment-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L2 7V17L12 22L22 17V7L12 2Z" stroke="currentColor" stroke-width="2"/>
              <path d="M12 12V22" stroke="currentColor" stroke-width="2"/>
              <path d="M2 7L12 12L22 7" stroke="currentColor" stroke-width="2"/>
            </svg>
            <span class="segment-text">高中</span>
          </div>
          <div 
            class="segment-item" 
            :class="{ active: currentSegment === 'compulsory' }"
            @click="switchSegment('compulsory')"
          >
            <svg class="segment-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4 19.5C4 18.837 4.26339 18.2011 4.73223 17.7322C5.20107 17.2634 5.83696 17 6.5 17H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M6.5 2H20V22H6.5C5.83696 22 5.20107 21.7366 4.73223 21.2678C4.26339 20.7989 4 20.163 4 19.5V4.5C4 3.83696 4.26339 3.20107 4.73223 2.73223C5.20107 2.26339 5.83696 2 6.5 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span class="segment-text">义教</span>
          </div>
          <div class="segment-slider" :style="sliderStyle"></div>
        </div>
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
      </div>
      
      <!-- 两个统计块 - 占据底部整个宽度 -->
      <div class="bottom-section">
        <!-- 指标下达表统计块 -->
        <div class="data-card quota-stats-card">
          <div class="card-header">
            <span class="card-icon">▣</span>
            <h3 class="card-title">指标下达表统计</h3>
            <div class="header-decoration">
              <span class="decoration-line"></span>
              <span class="decoration-dot"></span>
            </div>
          </div>
          <div class="card-body">
            <div class="stats-grid">
              <!-- 总指标 -->
              <div class="stat-card-tech total">
                <div class="card-circle-wrapper">
                  <div class="circle-outer">
                    <div class="circle-inner">
                      <svg viewBox="0 0 24 24" class="card-icon">
                        <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </div>
                  </div>
                </div>
                <div class="card-content">
                  <div class="card-label">总指标金额</div>
                  <div class="card-value">{{ formatBudget(quotaStats.totalQuota) }}</div>
                </div>
              </div>
              
              <!-- 已分配 -->
              <div class="stat-card-tech allocated">
                <div class="card-circle-wrapper">
                  <div class="circle-outer">
                    <div class="circle-inner">
                      <svg viewBox="0 0 24 24" class="card-icon">
                        <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="2"/>
                        <path d="M9 12L11 14L15 10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </div>
                  </div>
                </div>
                <div class="card-content">
                  <div class="card-label">已分配金额</div>
                  <div class="card-value">{{ formatBudget(quotaStats.allocatedAmount) }}</div>
                </div>
              </div>
              
              <!-- 可用指标 -->
              <div class="stat-card-tech available">
                <div class="card-circle-wrapper">
                  <div class="circle-outer">
                    <div class="circle-inner">
                      <svg viewBox="0 0 24 24" class="card-icon">
                        <path d="M19 21V5C19 3.89543 18.1046 3 17 3H7C5.89543 3 5 3.89543 5 5V21L12 18L19 21Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </div>
                  </div>
                </div>
                <div class="card-content">
                  <div class="card-label">可用指标</div>
                  <div class="card-value highlight">{{ formatBudget(quotaStats.availableQuota) }}</div>
                </div>
              </div>
              
              <!-- 分配率 -->
              <div class="stat-card-tech rate">
                <div class="card-circle-wrapper">
                  <svg viewBox="0 0 120 120" class="circular-chart-mini">
                    <circle cx="60" cy="60" r="50" class="chart-bg"/>
                    <circle cx="60" cy="60" r="50" class="chart-bar" 
                      :style="{ strokeDashoffset: 314 - (314 * quotaStats.allocationRate / 100) }"/>
                  </svg>
                  <div class="chart-center-mini">
                    <span class="percent-num-mini">{{ quotaStats.allocationRate }}%</span>
                  </div>
                </div>
                <div class="card-content">
                  <div class="card-label">分配率</div>
                  <div class="card-value percent">{{ quotaStats.allocationRate }}%</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 学期预算表统计块 -->
        <div class="data-card budget-stats-card">
          <div class="card-header">
            <span class="card-icon">▣</span>
            <h3 class="card-title">学期预算表统计</h3>
            <div class="header-decoration">
              <span class="decoration-line"></span>
              <span class="decoration-dot"></span>
            </div>
          </div>
          <div class="card-body">
            <div class="stats-grid">
              <!-- 总预算 -->
              <div class="stat-card-tech total">
                <div class="card-circle-wrapper">
                  <div class="circle-outer">
                    <div class="circle-inner">
                      <svg viewBox="0 0 24 24" class="card-icon">
                        <rect x="3" y="3" width="18" height="18" rx="2" stroke="currentColor" stroke-width="2" fill="none"/>
                        <path d="M3 9h18" stroke="currentColor" stroke-width="2"/>
                        <path d="M9 3v18" stroke="currentColor" stroke-width="2"/>
                      </svg>
                    </div>
                  </div>
                </div>
                <div class="card-content">
                  <div class="card-label">总预算金额</div>
                  <div class="card-value">{{ formatBudget(budgetStats.totalBudget) }}</div>
                </div>
              </div>
              
              <!-- 已使用 -->
              <div class="stat-card-tech used">
                <div class="card-circle-wrapper">
                  <div class="circle-outer">
                    <div class="circle-inner">
                      <svg viewBox="0 0 24 24" class="card-icon">
                        <path d="M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2"/>
                        <path d="M12 6V12L15 15" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                      </svg>
                    </div>
                  </div>
                </div>
                <div class="card-content">
                  <div class="card-label">已使用金额</div>
                  <div class="card-value">{{ formatBudget(budgetStats.usedAmount) }}</div>
                </div>
              </div>
              
              <!-- 锁定金额 -->
              <div class="stat-card-tech locked">
                <div class="card-circle-wrapper">
                  <div class="circle-outer">
                    <div class="circle-inner">
                      <svg viewBox="0 0 24 24" class="card-icon">
                        <rect x="5" y="11" width="14" height="10" rx="2" stroke="currentColor" stroke-width="2" fill="none"/>
                        <path d="M8 11V7C8 4.79086 9.79086 3 12 3C14.2091 3 16 4.79086 16 7V11" stroke="currentColor" stroke-width="2"/>
                      </svg>
                    </div>
                  </div>
                </div>
                <div class="card-content">
                  <div class="card-label">锁定金额</div>
                  <div class="card-value warning">{{ formatBudget(budgetStats.lockedAmount) }}</div>
                </div>
              </div>
              
              <!-- 可用预算 -->
              <div class="stat-card-tech available">
                <div class="card-circle-wrapper">
                  <div class="circle-outer">
                    <div class="circle-inner">
                      <svg viewBox="0 0 24 24" class="card-icon">
                        <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </div>
                  </div>
                </div>
                <div class="card-content">
                  <div class="card-label">可用预算</div>
                  <div class="card-value highlight">{{ formatBudget(budgetStats.availableBudget) }}</div>
                </div>
              </div>
              
              <!-- 使用率 -->
              <div class="stat-card-tech rate">
                <div class="card-circle-wrapper">
                  <svg viewBox="0 0 120 120" class="circular-chart-mini">
                    <circle cx="60" cy="60" r="50" class="chart-bg"/>
                    <circle cx="60" cy="60" r="50" class="chart-bar" 
                      :style="{ strokeDashoffset: 314 - (314 * budgetStats.usageRate / 100) }"/>
                  </svg>
                  <div class="chart-center-mini">
                    <span class="percent-num-mini">{{ budgetStats.usageRate }}%</span>
                  </div>
                </div>
                <div class="card-content">
                  <div class="card-label">使用率</div>
                  <div class="card-value percent">{{ budgetStats.usageRate }}%</div>
                </div>
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
import { getDashboardData } from '@/api/dashboard/index'

export default {
  name: "Index",
  data() {
    return {
      currentTime: '',
      currentSegment: 'all', // 当前选中的学段: all-全部, high-高中, compulsory-义教
      topStats: [
        { label: '在校学生', value: '0' },
        { label: '困难学生', value: '0' },
        { label: '受助人次', value: '0' },
        { label: '资助档案', value: '0' },
        { label: '资助项目', value: '0' }
      ],
      statsData: {
        totalStudents: 0,
        difficultyStudents: 0,
        totalBudget: 0,
        usedBudget: 0
      },
      quotaStats: {
        totalQuota: 0,
        allocatedAmount: 0,
        availableQuota: 0,
        allocationRate: 0
      },
      budgetStats: {
        totalBudget: 0,
        usedAmount: 0,
        lockedAmount: 0,
        availableBudget: 0,
        usageRate: 0
      },
      amountData: [],
      projectData: [],
      noticeList: [],
      difficultyTypeChart: null,
      schoolLevelChart: null,
      trendChart: null,
      mapChart: null,
      chongzuoGeoJson: null, // 存储崇左市GeoJSON数据
      difficultyTypeData: [], // 困难类型分布数据
      schoolLevelData: [], // 学段受助人数数据
      mapDataList: [], // 地图数据
      trendData: [] // 趋势数据
    }
  },
  computed: {
    // 滑块位置
    sliderStyle() {
      const positions = {
        all: '0%',
        high: '33.33%',
        compulsory: '66.66%'
      }
      return {
        left: positions[this.currentSegment]
      }
    }
  },
  mounted() {
    this.updateTime()
    this.loadDashboardData() // 加载控制舱数据
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
    // 加载控制舱数据
    async loadDashboardData(segment) {
      try {
        // 如果没有传入segment，使用当前选中的学段
        const currentSeg = segment || this.currentSegment
        
        // 调用API，直接传递segment参数
        const response = await getDashboardData(currentSeg)
        if (response.code === 200 && response.data) {
          const data = response.data
          
          // 更新顶部统计数据
          if (data.topStats) {
            this.topStats = [
              { label: '在校学生', value: String(data.topStats.totalStudents || 0) },
              { label: '困难学生', value: String(data.topStats.difficultyStudents || 0) },
              { label: '受助人次', value: String(data.topStats.subsidyTimes || 0) },
              { label: '资助档案', value: String(data.topStats.subsidyRecords || 0) },
              { label: '资助项目', value: String(data.topStats.subsidyProjects || 0) }
            ]
          }
          
          // 更新困难类型分布数据
          if (data.difficultyTypeDistribution) {
            this.difficultyTypeData = data.difficultyTypeDistribution
          }
          
          // 更新各学段受助人数数据
          if (data.schoolLevelStats) {
            this.schoolLevelData = data.schoolLevelStats
          }
          
          // 更新指标下达表统计
          if (data.quotaStats) {
            this.quotaStats = {
              totalQuota: data.quotaStats.totalQuota || 0,
              allocatedAmount: data.quotaStats.allocatedAmount || 0,
              availableQuota: data.quotaStats.availableQuota || 0,
              allocationRate: data.quotaStats.allocationRate || 0
            }
          }
          
          // 更新学期预算表统计
          if (data.budgetStats) {
            this.budgetStats = {
              totalBudget: data.budgetStats.totalBudget || 0,
              usedAmount: data.budgetStats.usedAmount || 0,
              lockedAmount: data.budgetStats.lockedAmount || 0,
              availableBudget: data.budgetStats.availableBudget || 0,
              usageRate: data.budgetStats.usageRate || 0
            }
          }
          
          // 更新地图数据
          if (data.mapData) {
            this.mapDataList = data.mapData
          }
          
          // 更新学段受助金额统计
          if (data.amountBySchoolLevel) {
            this.amountData = data.amountBySchoolLevel
          }
          
          // 更新资助项目列表
          if (data.projectList) {
            this.projectData = data.projectList
          }
          
          // 更新用户注册统计
          if (data.userRegistrations) {
            this.noticeList = data.userRegistrations
          }
          
          // 更新本学期受助人数趋势
          if (data.subsidyTrend) {
            this.trendData = data.subsidyTrend
          }
          
          // 数据加载完成后重新初始化图表
          if (this.chongzuoGeoJson) {
            this.initCharts()
          }
        }
      } catch (error) {
        console.error('加载控制舱数据失败:', error)
        this.$message.error('数据加载失败，请稍后重试')
        throw error
      }
    },
    
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
    
    // 格式化预算金额(转换为万元)
    formatBudget(amount) {
      if (!amount || amount === 0) return '0万元'
      const wan = (amount / 10000).toFixed(2)
      return wan + '万元'
    },
    
    // 切换学段
    switchSegment(segment) {
      if (this.currentSegment === segment) return
      
      this.currentSegment = segment
      
      // 显示加载提示
      const loadingMsg = this.$message({
        message: `正在加载${segment === 'all' ? '全部' : segment === 'high' ? '高中' : '义教'}数据...`,
        type: 'info',
        duration: 0
      })
      
      // 重新加载数据
      this.loadDashboardData(segment).then(() => {
        loadingMsg.close()
        this.$message.success(`已切换至${segment === 'all' ? '全部' : segment === 'high' ? '高中' : '义教'}学段`)
      }).catch(() => {
        loadingMsg.close()
      })
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
      
      // 使用后端数据
      const chartData = this.difficultyTypeData && this.difficultyTypeData.length > 0 
        ? this.difficultyTypeData 
        : []
      
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
            data: chartData
          }
        ]
      }
      
      this.difficultyTypeChart.setOption(option)
      window.addEventListener('resize', () => this.difficultyTypeChart && this.difficultyTypeChart.resize())
    },
    
    initSchoolLevelChart() {
      if (!this.$refs.schoolLevelChart) return
      this.schoolLevelChart = echarts.init(this.$refs.schoolLevelChart)
      
      // 使用后端数据
      const labels = this.schoolLevelData.map(item => item.label || '')
      const counts = this.schoolLevelData.map(item => item.count || 0)
      
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
          data: labels.length > 0 ? labels : ['小学', '初中', '高中'],
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
            data: counts.length > 0 ? counts : [0, 0, 0],
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
      
      // 使用后端数据，如果没有数据则使用默认值
      const mapData = this.mapDataList && this.mapDataList.length > 0 
        ? this.mapDataList 
        : [
          { name: '江州区', value: 0 },
          { name: '扶绥县', value: 0 },
          { name: '宁明县', value: 0 },
          { name: '龙州县', value: 0 },
          { name: '大新县', value: 0 },
          { name: '天等县', value: 0 },
          { name: '凭祥市', value: 0 }
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
      
      // 使用后端数据
      const months = this.trendData.map(item => item.month || '')
      const counts = this.trendData.map(item => item.count || 0)
      
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['受助人数'],
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
          data: months.length > 0 ? months : ['暂无数据'],
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
            name: '受助人数',
            type: 'line',
            data: counts.length > 0 ? counts : [0],
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
    
    .header-center {
      flex: 1;
      display: flex;
      justify-content: center;
      padding: 0 40px;
    }
    
    // 学段切换器
    .segment-switcher {
      display: flex;
      position: relative;
      background: rgba(255, 255, 255, 0.6);
      backdrop-filter: blur(10px);
      border-radius: 25px;
      padding: 4px;
      border: 2px solid rgba(102, 187, 106, 0.3);
      box-shadow: 
        0 4px 12px rgba(102, 187, 106, 0.15),
        inset 0 1px 2px rgba(255, 255, 255, 0.9);
      
      .segment-item {
        position: relative;
        z-index: 2;
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 8px 20px;
        border-radius: 20px;
        cursor: pointer;
        transition: all 0.3s ease;
        color: #757575;
        font-size: 14px;
        font-weight: 500;
        user-select: none;
        
        .segment-icon {
          width: 18px;
          height: 18px;
          transition: all 0.3s ease;
        }
        
        .segment-text {
          white-space: nowrap;
        }
        
        &:hover:not(.active) {
          color: #43A047;
          background: rgba(232, 245, 233, 0.4);
          
          .segment-icon {
            transform: scale(1.1);
          }
        }
        
        &.active {
          color: #FFFFFF;
          font-weight: 600;
          
          .segment-icon {
            transform: scale(1.15);
          }
        }
      }
      
      .segment-slider {
        position: absolute;
        top: 4px;
        left: 4px;
        width: calc(33.33% - 4px);
        height: calc(100% - 8px);
        background: linear-gradient(135deg, #66BB6A 0%, #43A047 100%);
        border-radius: 20px;
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        z-index: 1;
        box-shadow: 
          0 4px 12px rgba(67, 160, 71, 0.3),
          inset 0 1px 2px rgba(255, 255, 255, 0.3);
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
    grid-template-rows: auto auto;
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
    
    // 底部区域 - 横跨三列
    .bottom-section {
      grid-column: 1 / 4; // 横跨所有列
      margin-top: -16px; // 调整与上方内容的间距
      
      .budget-card {
        min-height: 240px; // 增加最小高度
        
        .card-header {
          position: relative;
          
          .header-decoration {
            margin-left: auto;
            display: flex;
            align-items: center;
            gap: 8px;
            
            .decoration-line {
              width: 60px;
              height: 2px;
              background: linear-gradient(to right, transparent, #66BB6A);
            }
            
            .decoration-dot {
              width: 8px;
              height: 8px;
              background: #66BB6A;
              border-radius: 50%;
              box-shadow: 0 0 10px rgba(102, 187, 106, 0.6);
              animation: pulse 2s ease-in-out infinite;
            }
          }
        }
      }
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
  
  // 固定高度卡片
  .fixed-height-card {
    flex-shrink: 0;
  }
  
  // 自动扩展卡片(填充剩余空间)
  .flex-expand-card {
    flex: 1;
    min-height: 0;
  }

  // 预算指标卡片
  .budget-stats {
    display: flex;
    flex-direction: column;
    gap: 16px;
    padding: 8px;
    
    // 竖向布局样式(上下结构)
    &.budget-stats-vertical {
      flex-direction: column;
      gap: 20px;
      padding: 24px;
    }
    
    // 上方三个指标卡片的容器
    .budget-items-row {
      display: flex;
      gap: 0;
      align-items: stretch;
      justify-content: space-between;
    }
    
    // 公式连接符
    .formula-connector {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 0 20px;
      position: relative;
      
      .connector-line {
        flex: 1;
        width: 2px;
        background: linear-gradient(to bottom, transparent, rgba(102, 187, 106, 0.3), transparent);
        min-height: 20px;
      }
      
      .connector-symbol {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        background: rgba(255, 255, 255, 0.9);
        border: 2px solid rgba(102, 187, 106, 0.3);
        box-shadow: 0 4px 12px rgba(102, 187, 106, 0.15);
        transition: all 0.3s;
        z-index: 1;
        
        svg {
          width: 24px;
          height: 24px;
          color: #66BB6A;
        }
      }
      
      &.minus-connector .connector-symbol {
        background: linear-gradient(135deg, #FFF3E0 0%, #FFE0B2 100%);
        border-color: rgba(245, 124, 0, 0.3);
        
        svg {
          color: #F57C00;
        }
        
        &:hover {
          transform: scale(1.15) rotate(180deg);
          box-shadow: 0 6px 20px rgba(245, 124, 0, 0.3);
        }
      }
      
      &.equals-connector .connector-symbol {
        background: linear-gradient(135deg, #E8F5E9 0%, #C8E6C9 100%);
        border-color: rgba(46, 125, 50, 0.3);
        
        svg {
          color: #2E7D32;
        }
        
        &:hover {
          transform: scale(1.15);
          box-shadow: 0 6px 20px rgba(46, 125, 50, 0.3);
        }
      }
    }

    .budget-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 16px;
      background: rgba(255, 255, 255, 0.6);
      backdrop-filter: blur(10px);
      border-radius: 12px;
      border: 1px solid rgba(102, 187, 106, 0.2);
      box-shadow: 0 2px 8px rgba(102, 187, 106, 0.1);
      transition: all 0.3s;
      
      // 上下布局时的样式
      .budget-items-row & {
        flex: 1;
        flex-direction: row;
        align-items: center;
        gap: 20px;
        padding: 28px 24px;
        position: relative;
        overflow: hidden;
        min-width: 0; // 确保弹性布局
        
        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          height: 4px;
          opacity: 0.6;
        }
        
        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 0;
          width: 0;
          height: 3px;
          background: currentColor;
          transition: width 0.4s ease;
        }
        
        &:hover::after {
          width: 100%;
        }
      }
      
      // 总指标特殊样式
      &.budget-item-total {
        &::before {
          background: linear-gradient(to right, #1976D2, #64B5F6);
        }
        
        &::after {
          background: linear-gradient(to right, #1976D2, #64B5F6);
        }
      }
      
      // 已分配特殊样式
      &.budget-item-used {
        &::before {
          background: linear-gradient(to right, #F57C00, #FFB74D);
        }
        
        &::after {
          background: linear-gradient(to right, #F57C00, #FFB74D);
        }
      }
      
      // 结余资金特殊样式
      &.budget-item-balance {
        &::before {
          background: linear-gradient(to right, #2E7D32, #66BB6A);
        }
        
        &::after {
          background: linear-gradient(to right, #2E7D32, #66BB6A);
        }
        
        // 结果强调
        .budget-items-row & {
          box-shadow: 0 4px 16px rgba(46, 125, 50, 0.2);
          border-width: 2px;
          border-color: rgba(46, 125, 50, 0.3);
        }
      }

      &:hover {
        transform: translateX(3px);
        box-shadow: 0 4px 12px rgba(102, 187, 106, 0.2);
        background: rgba(241, 248, 233, 0.6);
        
        .budget-items-row & {
          transform: translateY(-8px) scale(1.03);
          box-shadow: 0 12px 32px rgba(102, 187, 106, 0.3);
        }
      }
      
      .budget-icon-wrapper {
        width: 56px;
        height: 56px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
        transition: all 0.3s;
        
        &.total-icon {
          background: linear-gradient(135deg, #E3F2FD 0%, #BBDEFB 100%);
          color: #1976D2;
          box-shadow: 0 4px 12px rgba(25, 118, 210, 0.2);
        }
        
        &.used-icon {
          background: linear-gradient(135deg, #FFF3E0 0%, #FFE0B2 100%);
          color: #F57C00;
          box-shadow: 0 4px 12px rgba(245, 124, 0, 0.2);
        }
        
        &.balance-icon {
          background: linear-gradient(135deg, #E8F5E9 0%, #C8E6C9 100%);
          color: #2E7D32;
          box-shadow: 0 4px 12px rgba(46, 125, 50, 0.2);
        }
        
        .budget-icon {
          width: 28px;
          height: 28px;
        }
        
        .budget-item:hover & {
          transform: rotate(360deg) scale(1.1);
        }
      }
      
      .budget-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 4px;
      }

      .budget-label {
        font-size: 14px;
        color: #666;
        font-weight: 500;
        
        .budget-items-row & {
          font-size: 15px;
          color: #757575;
        }
      }

      .budget-value {
        font-size: 18px;
        font-weight: 700;
        font-family: 'Source Han Sans CN', 'Microsoft YaHei', Arial, sans-serif;
        
        .budget-items-row & {
          font-size: 32px;
          line-height: 1.2;
        }

        &.total {
          color: #1976D2;
        }

        &.used {
          color: #F57C00;
        }

        &.balance {
          color: #2E7D32;
        }
      }
      
      .budget-desc {
        font-size: 12px;
        color: #9E9E9E;
        
        .budget-items-row & {
          font-size: 13px;
        }
      }
    }

    .budget-progress {
      margin-top: 8px;
      padding: 12px 16px;
      background: rgba(255, 255, 255, 0.6);
      backdrop-filter: blur(10px);
      border-radius: 12px;
      border: 1px solid rgba(102, 187, 106, 0.2);
      
      // 下方执行进度占满宽
      &.budget-progress-full {
        margin-top: 0;
        padding: 24px 32px;
        display: flex;
        flex-direction: column;
        gap: 16px;
        position: relative;
        overflow: hidden;
        
        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          height: 3px;
          background: linear-gradient(to right, transparent, #66BB6A, transparent);
          opacity: 0.3;
        }
        
        &:hover {
          box-shadow: 0 8px 24px rgba(102, 187, 106, 0.25);
          background: rgba(241, 248, 233, 0.6);
        }
      }
      
      .progress-header {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 8px;
        
        .progress-icon-wrapper {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          background: linear-gradient(135deg, #E8F5E9 0%, #C8E6C9 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          color: #43A047;
          box-shadow: 0 4px 12px rgba(67, 160, 71, 0.2);
          
          .progress-icon {
            width: 22px;
            height: 22px;
          }
        }
        
        .progress-label {
          font-size: 16px;
          font-weight: 600;
          color: #424242;
        }
      }
      
      .progress-bar-wrapper {
        display: flex;
        align-items: center;
        gap: 16px;
      }

      .progress-bar {
        flex: 1;
        height: 12px;
        background: #E0E0E0;
        border-radius: 6px;
        overflow: hidden;
        position: relative;
        box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
        
        .budget-progress-full & {
          height: 24px;
          border-radius: 12px;
        }

        .progress-fill {
          height: 100%;
          background: linear-gradient(to right, #66BB6A, #43A047, #66BB6A);
          background-size: 200% 100%;
          border-radius: 6px;
          transition: width 0.6s ease;
          position: relative;
          animation: shimmer 2s linear infinite;
          
          .budget-progress-full & {
            border-radius: 12px;
          }
          
          .progress-shine {
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(to right, transparent, rgba(255, 255, 255, 0.5), transparent);
            animation: shine 2s ease-in-out infinite;
          }
        }
      }

      .progress-text {
        font-size: 13px;
        color: #424242;
        font-weight: 600;
        min-width: 50px;
        text-align: right;
        
        .budget-progress-full & {
          font-size: 20px;
          font-weight: 700;
          color: #43A047;
        }
      }
      
      .progress-stats {
        display: flex;
        align-items: center;
        gap: 24px;
        padding-top: 16px;
        border-top: 1px solid rgba(102, 187, 106, 0.2);
        
        .stat-item {
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 6px;
          
          .stat-label {
            font-size: 13px;
            color: #9E9E9E;
          }
          
          .stat-value {
            font-size: 18px;
            font-weight: 600;
            color: #424242;
          }
        }
        
        .stat-divider {
          width: 1px;
          height: 40px;
          background: linear-gradient(to bottom, transparent, #C8E6C9, transparent);
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
  
  
  // 两个统计块布局
  .bottom-section {
    display: grid;
    grid-template-columns: 1fr 1fr; // 两栏并排
    gap: 24px;
    grid-column: 1 / -1; // 占据整个宽度
  }
  
  // 指标下达表统计卡片
  .quota-stats-card {
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr); // 2x2网格
      gap: 16px;
      padding: 20px;
    }
  }
  
  // 学期预算表统计卡片
  .budget-stats-card {
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr); // 2x2网格 + 1个占满2栏
      grid-template-rows: auto auto auto;
      gap: 16px;
      padding: 20px;
      
      // 使用率占据整个宽度
      .stat-card-tech.rate {
        grid-column: 1 / -1;
      }
    }
  }
  
  // 统计卡片 - 横向布局
  .stat-card-tech {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 18px 20px;
    background: #ffffff;
    border-radius: 16px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
    
    // 圆形图标容器
    .card-circle-wrapper {
      flex-shrink: 0;
      width: 70px;
      height: 70px;
      position: relative;
    }
    
    // 外层圆环
    .circle-outer {
      width: 100%;
      height: 100%;
      border-radius: 50%;
      background: rgba(76, 175, 80, 0.1);
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.3s ease;
    }
    
    // 内层圆环
    .circle-inner {
      width: 80%;
      height: 80%;
      border-radius: 50%;
      background: #ffffff;
      border: 2px solid rgba(76, 175, 80, 0.4);
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 2px 8px rgba(76, 175, 80, 0.15);
      transition: all 0.3s ease;
      
      .card-icon {
        width: 24px;
        height: 24px;
        color: #4CAF50;
        transition: all 0.3s ease;
        fill: none;
      }
    }
    
    // 小型环形图
    .circular-chart-mini {
      width: 70px;
      height: 70px;
      transform: rotate(-90deg);
      
      .chart-bg {
        fill: none;
        stroke: #f0f0f0;
        stroke-width: 10;
      }
      
      .chart-bar {
        fill: none;
        stroke: #4CAF50;
        stroke-width: 10;
        stroke-linecap: round;
        stroke-dasharray: 314;
        transition: stroke-dashoffset 0.6s ease;
      }
    }
    
    .chart-center-mini {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      text-align: center;
      
      .percent-num-mini {
        font-size: 18px;
        font-weight: 700;
        color: #4CAF50;
        font-family: 'Arial', sans-serif;
      }
    }
    
    // 卡片内容
    .card-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 4px;
      
      .card-label {
        font-size: 13px;
        color: #666;
        font-weight: 500;
      }
      
      .card-value {
        font-size: 22px;
        color: #333;
        font-weight: 700;
        font-family: 'Arial', sans-serif;
        transition: all 0.3s ease;
        
        &.highlight {
          color: #2E7D32;
          font-size: 24px;
        }
        
        &.warning {
          color: #FF9800;
        }
        
        &.percent {
          color: #4CAF50;
        }
      }
    }
    
    // 总指标/总预算样式
    &.total {
      .circle-outer {
        background: rgba(129, 199, 132, 0.1);
      }
      
      .circle-inner {
        border-color: rgba(129, 199, 132, 0.4);
        
        .card-icon {
          color: #66BB6A;
        }
      }
      
      &:hover {
        box-shadow: 0 4px 16px rgba(129, 199, 132, 0.2);
        
        .circle-inner {
          transform: scale(1.1);
          box-shadow: 0 4px 12px rgba(129, 199, 132, 0.25);
          
          .card-icon {
            transform: rotate(360deg);
          }
        }
      }
    }
    
    // 已分配/已使用样式
    &.allocated,
    &.used {
      .circle-outer {
        background: rgba(255, 183, 77, 0.1);
      }
      
      .circle-inner {
        border-color: rgba(255, 152, 0, 0.4);
        
        .card-icon {
          color: #FF9800;
        }
      }
      
      &:hover {
        box-shadow: 0 4px 16px rgba(255, 183, 77, 0.2);
        
        .circle-inner {
          transform: scale(1.1);
          box-shadow: 0 4px 12px rgba(255, 183, 77, 0.25);
          
          .card-icon {
            transform: rotate(360deg);
          }
        }
      }
    }
    
    // 锁定金额样式
    &.locked {
      .circle-outer {
        background: rgba(255, 152, 0, 0.1);
      }
      
      .circle-inner {
        border-color: rgba(245, 124, 0, 0.4);
        
        .card-icon {
          color: #F57C00;
        }
      }
      
      &:hover {
        box-shadow: 0 4px 16px rgba(255, 152, 0, 0.2);
        
        .circle-inner {
          transform: scale(1.1);
          box-shadow: 0 4px 12px rgba(255, 152, 0, 0.25);
          
          .card-icon {
            transform: rotate(360deg);
          }
        }
      }
    }
    
    // 可用指标/可用预算样式
    &.available {
      .circle-outer {
        background: rgba(76, 175, 80, 0.15);
      }
      
      .circle-inner {
        border-color: #4CAF50;
        box-shadow: 0 3px 10px rgba(76, 175, 80, 0.2);
        
        .card-icon {
          color: #2E7D32;
        }
      }
      
      &:hover {
        box-shadow: 0 6px 20px rgba(76, 175, 80, 0.3);
        background: rgba(232, 245, 233, 0.5);
        
        .circle-outer {
          background: rgba(76, 175, 80, 0.2);
          transform: scale(1.05);
        }
        
        .circle-inner {
          transform: scale(1.15);
          box-shadow: 0 6px 16px rgba(76, 175, 80, 0.35);
          
          .card-icon {
            transform: rotate(360deg) scale(1.2);
          }
        }
      }
    }
    
    // 分配率/使用率样式
    &.rate {
      justify-content: center;
      
      .circle-outer {
        background: rgba(76, 175, 80, 0.1);
      }
      
      .card-content {
        text-align: center;
      }
      
      &:hover {
        box-shadow: 0 4px 16px rgba(76, 175, 80, 0.2);
        transform: scale(1.02);
      }
    }
    
    &:hover {
      transform: translateX(6px);
    }
  }
  
  // 响应式设计
  @media (max-width: 1600px) {
    .bottom-section {
      grid-template-columns: 1fr; // 单栏布局
      
      .quota-stats-card,
      .budget-stats-card {
        .stats-grid {
          grid-template-columns: repeat(2, 1fr);
        }
      }
    }
  }
  
  @media (max-width: 768px) {
    .quota-stats-card,
    .budget-stats-card {
      .stats-grid {
        grid-template-columns: 1fr; // 单栏布局
      }
    }
  }
  
  // 动画效果
  @keyframes pulse {
    0%, 100% {
      opacity: 1;
      transform: scale(1);
    }
    50% {
      opacity: 0.6;
      transform: scale(1.2);
    }
  }
  
  @keyframes shimmer {
    0% {
      background-position: 200% 0;
    }
    100% {
      background-position: -200% 0;
    }
  }
  
  @keyframes shine {
    0% {
      left: -100%;
    }
    100% {
      left: 200%;
    }
  }
}
</style>

