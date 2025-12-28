<template>
  <div class="dashboard-container">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <div class="welcome-text">
          <h1 class="welcome-title">档案打包管理系统</h1>
          <p class="welcome-subtitle">欢迎回来，{{ userName }}！</p>
          <p class="welcome-time">{{ currentTime }}</p>
        </div>
        <div class="quick-actions">
          <el-button type="primary" icon="el-icon-folder-opened" @click="goToArchive">查看档案</el-button>
          <el-button type="success" icon="el-icon-box" @click="goToPackage">档案打包</el-button>
        </div>
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="stat-card" @click="goToArchive">
            <div class="stat-icon archive-icon">
              <i class="el-icon-document"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statsData.totalArchives }}</div>
              <div class="stat-label">档案总数</div>
              <div class="stat-trend" :class="statsData.archiveTrend > 0 ? 'trend-up' : 'trend-down'">
                <i :class="statsData.archiveTrend > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
                {{ Math.abs(statsData.archiveTrend) }}%
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="stat-card" @click="goToPackage">
            <div class="stat-icon package-icon">
              <i class="el-icon-box"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statsData.totalPackages }}</div>
              <div class="stat-label">打包文件</div>
              <div class="stat-trend" :class="statsData.packageTrend > 0 ? 'trend-up' : 'trend-down'">
                <i :class="statsData.packageTrend > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
                {{ Math.abs(statsData.packageTrend) }}%
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="stat-card">
            <div class="stat-icon storage-icon">
              <i class="el-icon-folder"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statsData.storageUsed }}</div>
              <div class="stat-label">存储使用</div>
              <div class="stat-progress">
                <el-progress :percentage="statsData.storagePercent" :show-text="false" :stroke-width="4"></el-progress>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="stat-card">
            <div class="stat-icon report-icon">
              <i class="el-icon-s-data"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statsData.totalReports }}</div>
              <div class="stat-label">报表类型</div>
              <div class="stat-info">本学期活跃</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-section">
      <!-- 归档趋势图 -->
      <el-col :xs="24" :lg="16">
        <div class="chart-card">
          <div class="card-header">
            <h3 class="card-title">
              <i class="el-icon-s-marketing"></i>
              归档趋势统计
            </h3>
            <el-radio-group v-model="trendPeriod" size="small" @change="loadTrendData">
              <el-radio-button label="week">近7天</el-radio-button>
              <el-radio-button label="month">近30天</el-radio-button>
              <el-radio-button label="year">本学年</el-radio-button>
            </el-radio-group>
          </div>
          <div class="card-body">
            <div ref="trendChart" class="chart" style="height: 300px"></div>
          </div>
        </div>
      </el-col>

      <!-- 档案类型分布 -->
      <el-col :xs="24" :lg="8">
        <div class="chart-card">
          <div class="card-header">
            <h3 class="card-title">
              <i class="el-icon-pie-chart"></i>
              档案类型分布
            </h3>
          </div>
          <div class="card-body">
            <div ref="typeChart" class="chart" style="height: 300px"></div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 底部区域 -->
    <el-row :gutter="20" class="bottom-section">
      <!-- 最近操作 -->
      <el-col :xs="24" :lg="12">
        <div class="list-card">
          <div class="card-header">
            <h3 class="card-title">
              <i class="el-icon-time"></i>
              最近操作
            </h3>
            <el-link type="primary" :underline="false" @click="goToArchive">查看全部</el-link>
          </div>
          <div class="card-body">
            <el-timeline>
              <el-timeline-item
                v-for="(activity, index) in recentActivities"
                :key="index"
                :timestamp="activity.timestamp"
                :color="activity.color"
              >
                <div class="activity-item">
                  <i :class="activity.icon"></i>
                  {{ activity.content }}
                </div>
              </el-timeline-item>
            </el-timeline>
            <div v-if="recentActivities.length === 0" class="empty-state">
              <i class="el-icon-document"></i>
              <p>暂无操作记录</p>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 待办事项 -->
      <el-col :xs="24" :lg="12">
        <div class="list-card">
          <div class="card-header">
            <h3 class="card-title">
              <i class="el-icon-bell"></i>
              待办提醒
            </h3>
          </div>
          <div class="card-body">
            <div class="todo-list">
              <div
                v-for="(todo, index) in todoList"
                :key="index"
                class="todo-item"
                :class="'todo-' + todo.type"
              >
                <div class="todo-icon">
                  <i :class="todo.icon"></i>
                </div>
                <div class="todo-content">
                  <div class="todo-title">{{ todo.title }}</div>
                  <div class="todo-desc">{{ todo.desc }}</div>
                </div>
                <el-button type="text" size="small" @click="handleTodo(todo)">处理</el-button>
              </div>
            </div>
            <div v-if="todoList.length === 0" class="empty-state">
              <i class="el-icon-success"></i>
              <p>暂无待办事项</p>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: "Index",
  data() {
    return {
      userName: this.$store.state.user.name || '用户',
      currentTime: '',
      trendPeriod: 'week',
      statsData: {
        totalArchives: 0,
        totalPackages: 0,
        storageUsed: '0 GB',
        storagePercent: 0,
        totalReports: 0,
        archiveTrend: 0,
        packageTrend: 0
      },
      recentActivities: [],
      todoList: [],
      trendChart: null,
      typeChart: null
    }
  },
  mounted() {
    this.updateTime()
    this.loadDashboardData()
    this.initCharts()
    // 每分钟更新时间
    this.timeInterval = setInterval(this.updateTime, 60000)
  },
  beforeDestroy() {
    if (this.timeInterval) {
      clearInterval(this.timeInterval)
    }
    if (this.trendChart) {
      this.trendChart.dispose()
    }
    if (this.typeChart) {
      this.typeChart.dispose()
    }
  },
  methods: {
    updateTime() {
      const now = new Date()
      const hours = now.getHours()
      let greeting = '早上好'
      if (hours >= 12 && hours < 18) {
        greeting = '下午好'
      } else if (hours >= 18) {
        greeting = '晚上好'
      }
      this.currentTime = `${greeting}，${now.toLocaleDateString('zh-CN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}`
    },
    
    async loadDashboardData() {
      try {
        // TODO: 调用后端接口获取统计数据
        // 模拟数据
        this.statsData = {
          totalArchives: 1248,
          totalPackages: 156,
          storageUsed: '23.6 GB',
          storagePercent: 47,
          totalReports: 28,
          archiveTrend: 12.5,
          packageTrend: 8.3
        }
        
        this.recentActivities = [
          { timestamp: '2024-12-27 14:30', content: '生成了"高中助学金发放汇总表"档案', icon: 'el-icon-document', color: '#409EFF' },
          { timestamp: '2024-12-27 11:20', content: '打包了"2024学年第一学期"档案包', icon: 'el-icon-box', color: '#67C23A' },
          { timestamp: '2024-12-26 16:45', content: '删除了过期档案文件', icon: 'el-icon-delete', color: '#F56C6C' },
          { timestamp: '2024-12-26 10:15', content: '创建了自定义档案包"资助档案"', icon: 'el-icon-folder-add', color: '#E6A23C' }
        ]
        
        this.todoList = [
          { type: 'warning', icon: 'el-icon-warning', title: '档案待审核', desc: '有3个档案包等待审核', action: 'review' },
          { type: 'info', icon: 'el-icon-info', title: '存储空间提醒', desc: '存储空间使用已达47%，建议清理', action: 'clean' }
        ]
      } catch (error) {
        console.error('加载控制台数据失败:', error)
      }
    },
    
    initCharts() {
      this.$nextTick(() => {
        this.initTrendChart()
        this.initTypeChart()
      })
    },
    
    initTrendChart() {
      if (!this.$refs.trendChart) return
      
      this.trendChart = echarts.init(this.$refs.trendChart)
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['归档数量', '打包数量'],
          bottom: 0
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '12%',
          top: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
          axisLine: {
            lineStyle: {
              color: '#e0e6ed'
            }
          },
          axisLabel: {
            color: '#666'
          }
        },
        yAxis: {
          type: 'value',
          axisLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          axisLabel: {
            color: '#666'
          },
          splitLine: {
            lineStyle: {
              color: '#f0f2f5',
              type: 'dashed'
            }
          }
        },
        series: [
          {
            name: '归档数量',
            type: 'bar',
            data: [120, 132, 101, 134, 90, 230, 210],
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#409EFF' },
                { offset: 1, color: '#6CB5FF' }
              ])
            },
            barWidth: '30%'
          },
          {
            name: '打包数量',
            type: 'line',
            data: [15, 18, 12, 20, 11, 28, 25],
            smooth: true,
            itemStyle: {
              color: '#67C23A'
            },
            lineStyle: {
              width: 3
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
                { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
              ])
            }
          }
        ]
      }
      
      this.trendChart.setOption(option)
      window.addEventListener('resize', () => {
        this.trendChart && this.trendChart.resize()
      })
    },
    
    initTypeChart() {
      if (!this.$refs.typeChart) return
      
      this.typeChart = echarts.init(this.$refs.typeChart)
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: '5%',
          top: 'center',
          icon: 'circle'
        },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['35%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 16,
                fontWeight: 'bold'
              },
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            data: [
              { value: 548, name: '报表档案', itemStyle: { color: '#409EFF' } },
              { value: 335, name: '自定义档案', itemStyle: { color: '#67C23A' } },
              { value: 234, name: '混合档案', itemStyle: { color: '#E6A23C' } },
              { value: 131, name: '其他', itemStyle: { color: '#909399' } }
            ]
          }
        ]
      }
      
      this.typeChart.setOption(option)
      window.addEventListener('resize', () => {
        this.typeChart && this.typeChart.resize()
      })
    },
    
    loadTrendData() {
      // TODO: 根据时间范围重新加载趋势数据
      console.log('切换时间范围:', this.trendPeriod)
    },
    
    goToArchive() {
      this.$router.push('/system/report/reportArchive')
    },
    
    goToPackage() {
      this.$router.push('/system/report/reportArchive')
    },
    
    handleTodo(todo) {
      this.$message.info('功能开发中...')
    }
  }
}
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 20px;
  background: #f0f2f5;
  min-height: calc(100vh - 84px);

  // 欢迎区域
  .welcome-section {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    padding: 40px;
    margin-bottom: 20px;
    color: #fff;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

    .welcome-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;

      .welcome-text {
        .welcome-title {
          font-size: 32px;
          font-weight: 600;
          margin: 0 0 10px 0;
        }

        .welcome-subtitle {
          font-size: 18px;
          opacity: 0.9;
          margin: 0 0 5px 0;
        }

        .welcome-time {
          font-size: 14px;
          opacity: 0.8;
          margin: 0;
        }
      }

      .quick-actions {
        display: flex;
        gap: 12px;

        .el-button {
          background: rgba(255, 255, 255, 0.2);
          border: 1px solid rgba(255, 255, 255, 0.3);
          color: #fff;
          padding: 12px 24px;
          font-size: 14px;

          &:hover {
            background: rgba(255, 255, 255, 0.3);
            border-color: rgba(255, 255, 255, 0.5);
          }
        }
      }
    }
  }

  // 统计卡片
  .stats-section {
    margin-bottom: 20px;

    .stat-card {
      background: #fff;
      border-radius: 12px;
      padding: 24px;
      display: flex;
      align-items: center;
      cursor: pointer;
      transition: all 0.3s;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
      }

      .stat-icon {
        width: 64px;
        height: 64px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 20px;
        font-size: 28px;
        color: #fff;

        &.archive-icon {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        &.package-icon {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        &.storage-icon {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        &.report-icon {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
      }

      .stat-content {
        flex: 1;

        .stat-value {
          font-size: 28px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 5px;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-bottom: 8px;
        }

        .stat-trend {
          font-size: 12px;
          font-weight: 500;

          &.trend-up {
            color: #67c23a;
          }

          &.trend-down {
            color: #f56c6c;
          }
        }

        .stat-progress {
          margin-top: 8px;
        }

        .stat-info {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }

  // 图表区域
  .chart-section {
    margin-bottom: 20px;
  }

  .chart-card,
  .list-card {
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    overflow: hidden;

    .card-header {
      padding: 20px 24px;
      border-bottom: 1px solid #f0f2f5;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .card-title {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        display: flex;
        align-items: center;
        gap: 8px;

        i {
          color: #409eff;
        }
      }
    }

    .card-body {
      padding: 24px;
    }
  }

  // 最近操作
  .activity-item {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
    color: #606266;

    i {
      font-size: 16px;
    }
  }

  // 待办事项
  .todo-list {
    .todo-item {
      display: flex;
      align-items: center;
      padding: 16px;
      margin-bottom: 12px;
      border-radius: 8px;
      background: #f5f7fa;
      transition: all 0.3s;

      &:hover {
        background: #ecf5ff;
      }

      &:last-child {
        margin-bottom: 0;
      }

      .todo-icon {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;
        font-size: 18px;
      }

      &.todo-warning .todo-icon {
        background: #fef0f0;
        color: #f56c6c;
      }

      &.todo-info .todo-icon {
        background: #f4f4f5;
        color: #909399;
      }

      .todo-content {
        flex: 1;

        .todo-title {
          font-size: 14px;
          color: #303133;
          font-weight: 500;
          margin-bottom: 4px;
        }

        .todo-desc {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }

  // 空状态
  .empty-state {
    text-align: center;
    padding: 40px 20px;
    color: #909399;

    i {
      font-size: 48px;
      margin-bottom: 12px;
      opacity: 0.5;
    }

    p {
      margin: 0;
      font-size: 14px;
    }
  }

  // 响应式
  @media (max-width: 768px) {
    .welcome-section {
      padding: 24px;

      .welcome-content {
        flex-direction: column;
        gap: 20px;

        .welcome-text .welcome-title {
          font-size: 24px;
        }

        .quick-actions {
          width: 100%;

          .el-button {
            flex: 1;
          }
        }
      }
    }

    .stat-card {
      margin-bottom: 12px;
    }
  }
}
</style>

