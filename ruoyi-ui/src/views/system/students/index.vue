<template>
  <div class="app-container students-page">
    <div class="query-table-wrapper">
      <div class="query-form-header">
        <el-button
          type="text"
          size="mini"
          :icon="queryFormCollapsed ? 'el-icon-arrow-down' : 'el-icon-arrow-up'"
          @click="queryFormCollapsed = !queryFormCollapsed"
          class="collapse-btn"
        >
          {{ queryFormCollapsed ? '展开' : '收起' }}
        </el-button>
      </div>
      <el-collapse-transition>
        <el-form
          v-show="!queryFormCollapsed && showSearch"
          :model="queryParams"
          ref="queryForm"
          size="small"
          :inline="true"
          label-width="90px"
          class="query-form"
        >
      <el-form-item label="学年学期" prop="yearSemesterId">
        <el-select v-model="queryParams.yearSemesterId" placeholder="请选择学年学期" clearable style="width: 200px">
          <el-option
            v-for="item in yearSemesterOptions"
            :key="item.id"
            :label="`${item.schoolYear || ''} ${item.semesterLabel || ''}`"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCardNo">
        <el-input
          v-model="queryParams.idCardNo"
          placeholder="请输入身份证号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="民族" prop="ethnicity">
        <el-select v-model="queryParams.ethnicity" placeholder="请选择民族" clearable>
          <el-option
            v-for="dict in dict.type.sys_student_ethnicity"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="所属年级" prop="gradeId">
        <el-select v-model="queryParams.gradeId" placeholder="请选择年级" clearable>
          <el-option
            v-for="item in allGradeOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="困难类型" prop="difficultyTypeId">
        <el-select v-model="queryParams.difficultyTypeId" placeholder="请选择困难类型" clearable>
          <el-option
            v-for="dict in dict.type.sys_difficulty_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="脱贫户" prop="isPovertyReliefFamily">
        <el-select v-model="queryParams.isPovertyReliefFamily" placeholder="是否脱贫户" clearable>
          <el-option label="是" value="1" />
          <el-option label="否" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
      </el-collapse-transition>

    <el-row :gutter="10" class="mb8 action-row">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:students:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:students:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-dropdown @command="handleCommand" v-hasPermi="['system:students:import']">
          <el-button
            type="success"
            plain
            icon="el-icon-upload2"
            size="mini">
            导入 <i class="el-icon-arrow-down el-icon--right"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="import" icon="el-icon-upload2">导入数据</el-dropdown-item>
            <el-dropdown-item command="template" icon="el-icon-download">下载模板</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:students:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-edit-outline"
          size="mini"
          @click="handleBatchDifficulty"
          v-hasPermi="['system:students:edit']"
        >批量认定</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-section">
    <el-table v-loading="loading" :data="studentsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="姓名" align="center" prop="name" width="120">
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" @click="openStudentDetail(scope.row)">
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="身份证号" align="center" prop="idCardNo" width="160" show-overflow-tooltip />
      <el-table-column label="性别" align="center" prop="gender" width="70">
        <template slot-scope="scope">
          <span v-if="scope.row.gender === '1'">男</span>
          <span v-else-if="scope.row.gender === '0'">女</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="民族" align="center" prop="ethnicity" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_student_ethnicity" :value="scope.row.ethnicity"/>
        </template>
      </el-table-column>
      <el-table-column label="联系电话" align="center" prop="phone" width="120" />
      <el-table-column label="户籍所在地" align="center" prop="domicile" width="180" show-overflow-tooltip>
        <template slot-scope="scope">
          {{ formatDomicile(scope.row.domicile) }}
        </template>
      </el-table-column>
      <el-table-column label="学籍号" align="center" prop="studentNo" width="140" show-overflow-tooltip />
      <el-table-column label="入学时间" align="center" prop="enrollmentDate" width="110" />
      <el-table-column label="政治面貌" align="center" prop="politicalStatus" width="100" />
      <el-table-column label="民族班" align="center" prop="isEthnicClass" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isEthnicClass === '1' ? 'success' : 'info'" size="small">
            {{ scope.row.isEthnicClass === '1' ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="当前学年学期" align="center" width="150">
        <template slot-scope="scope">
          <span v-if="scope.row.currentSchoolYear">
            {{ scope.row.currentSchoolYear || '' }} {{ scope.row.currentSemesterLabel || '' }}
          </span>
          <span v-else style="color: #909399;">未设置</span>
        </template>
      </el-table-column>
      <el-table-column label="所属学制" align="center" prop="schoolingYears" width="100">
        <template slot-scope="scope">
          {{ scope.row.schoolingYears }}
        </template>
      </el-table-column>
      <el-table-column label="年级/班级" align="center" width="180">
        <template slot-scope="scope">
          {{ formatGradeAndClass(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column label="就读状态" align="center" prop="studyStatus" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_study_status" :value="scope.row.studyStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="困难类型" align="center" prop="difficultyTypeId" width="220">
        <template slot-scope="scope">
          <span>{{ formatSupportInfo(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="困难等级" align="center" prop="difficultyLevelId" width="120">
        <template slot-scope="scope">
          <span v-if="scope.row.difficultyLevelId" class="difficulty-pill">
            {{ formatDifficultyLevel(scope.row.difficultyLevelId) }}
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="memo" width="150" show-overflow-tooltip />
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:students:edit']"
          >维护</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:students:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    </div>

    </div>

    <!-- 批量认定困难类型对话框 -->
    <el-dialog
      :visible.sync="batchDifficultyOpen"
      width="1000px"
      append-to-body
      @open="loadUnrecognizedStudents"
      :close-on-click-modal="false"
    >
      <div slot="title" style="display: flex; align-items: center;">
        <span style="font-size: 18px; font-weight: 600;">批量认定困难类型</span>
        <el-popover
          placement="right"
          width="300"
          trigger="click"
        >
          <div style="line-height: 1.6; color: #606266;">
            <i class="" style="color: #409EFF; margin-right: 4px;"></i>
            只能认定还没有认定困难类型的学生，请从左侧列表中选择需要认定的学生。
          </div>
          <i
            slot="reference"
            class="el-icon-info"
            style="margin-left: 8px; font-size: 16px; color: #409EFF; cursor: pointer;"
          ></i>
        </el-popover>
      </div>

      <!-- 对话框主体内容 -->
      <div>
        <el-row :gutter="20">
        <!-- 左侧：筛选和穿梭框 -->
        <el-col :span="17">
          <!-- 筛选条件 -->
          <div style="margin-bottom: 16px; padding: 12px 16px; background: #f5f7fa; border-radius: 4px;">
            <div style="display: flex; align-items: center;">
              <span style="white-space: nowrap; margin-right: 12px; font-weight: 500; color: #606266;">学段筛选：</span>
              <el-checkbox-group v-model="transferFilter.schoolStage" @change="handleTransferFilter" size="small">
                <el-checkbox v-for="item in schoolStageOptions" :key="item.value" :label="item.value" border style="margin-right: 8px;">
                  {{ item.label }}
                </el-checkbox>
              </el-checkbox-group>
            </div>
          </div>

          <!-- 穿梭框 -->
          <div style="display: flex; justify-content: center;">
            <el-transfer
              v-model="transferValue"
              v-loading="transferLoading"
              :data="transferData"
              :titles="['未认定学生', '已选择学生']"
              :button-texts="['移除', '选择']"
              :props="{
                key: 'id',
                label: 'label'
              }"
              filterable
              :filter-placeholder="'搜索学生姓名'"
            >
              <template slot-scope="{ option }">
                <div>
                  <span style="font-weight: 500;">{{ option.label }}</span>
                  <span style="margin-left: 8px; color: #909399; font-size: 12px;">{{ option.gradeName }}</span>
                </div>
              </template>
            </el-transfer>
          </div>

          <div style="margin-top: 12px; color: #909399; font-size: 13px; text-align: center;">
            <i class="el-icon-info"></i>
            已选择 <span style="color: #409EFF; font-weight: 500;">{{ transferValue.length }}</span> 名学生
          </div>
        </el-col>

        <!-- 右侧：认定信息表单 -->
        <el-col :span="7">
          <div style="padding: 16px; background: #fafafa; border-radius: 4px; border: 1px solid #e4e7ed;">
            <div style="font-size: 14px; font-weight: 600; color: #303133; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 2px solid #409EFF;">
              <i class="el-icon-edit-outline"></i> 认定信息
            </div>

            <el-form ref="batchDifficultyForm" :model="batchDifficultyForm" label-width="90px" label-position="left" size="small">
              <el-form-item label="困难类型" prop="difficultyTypeId" :rules="[{ required: true, message: '请选择困难类型', trigger: 'change' }]">
                <el-select v-model="batchDifficultyForm.difficultyTypeId" placeholder="请选择" style="width: 100%" @change="handleBatchDifficultyTypeChange">
                  <el-option
                    v-for="dict in dict.type.sys_difficulty_type"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </el-select>
              </el-form-item>

              <el-form-item label="困难等级" prop="difficultyLevelId" :rules="[{ required: true, message: '请选择困难等级', trigger: 'change' }]">
                <el-select v-model="batchDifficultyForm.difficultyLevelId" placeholder="请选择" style="width: 100%">
                  <el-option
                    v-for="dict in dict.type.sys_difficulty_level"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </el-select>
              </el-form-item>

              <el-form-item label="是否脱贫户" prop="isPovertyReliefFamily" v-if="batchDifficultyForm.isPovertyReliefFamily">
                <el-radio-group v-model="batchDifficultyForm.isPovertyReliefFamily" disabled size="small">
                  <el-radio label="1">是</el-radio>
                  <el-radio label="0">否</el-radio>
                </el-radio-group>
                <div style="margin-top: 4px; color: #909399; font-size: 12px; line-height: 1.5;">
                  <i class="el-icon-info"></i> 根据困难类型自动设置
                </div>
              </el-form-item>

              <el-form-item label="脱贫年份" prop="povertyReliefYear" v-if="batchDifficultyForm.isPovertyReliefFamily === '1'" :rules="[{ required: true, message: '请选择脱贫年份', trigger: 'change' }]">
                <el-date-picker
                  v-model="batchDifficultyForm.povertyReliefYear"
                  type="year"
                  placeholder="请选择脱贫年份"
                  value-format="yyyy"
                  style="width: 100%"
                />
              </el-form-item>
            </el-form>
          </div>
        </el-col>
      </el-row>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelBatchDifficulty" size="medium">取 消</el-button>
        <el-button type="primary" @click="submitBatchDifficulty" :disabled="transferValue.length === 0" size="medium">
          <i class="el-icon-check"></i> 确定认定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listStudents, getStudents, delStudents, addStudents, updateStudents,
  getSchoolPlanList, getGradeList, getClassList, batchUpdateDifficulty,
  importStudents
} from "@/api/system/students";
import {
  listStudentRecords, getStudentRecord, delStudentRecords, addStudentRecord, updateStudentRecord,
  listStudentsBase, getStudentsBase, addStudentsBase, updateStudentsBase
} from "@/api/system/studentRecord";
import { listYearSemesters } from "@/api/system/baseconfig";
import { getRegionTree } from "@/api/system/region";
import iFrame from "@/components/iFrame/index";

export default {
  name: "Students",
  components: {
    iFrame
  },
  dicts: ['sys_student_gender', 'sys_student_ethnicity', 'sys_study_status', 'sys_difficulty_type', 'sys_difficulty_level', 'sys_health_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 查询表单折叠
      queryFormCollapsed: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 困难学生基础信息表格数据
      studentsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 学年学期选项
      yearSemesterOptions: [],
      // 学制选项
      schoolPlanOptions: [],
      // 年级选项
      gradeOptions: [],
      // 所有年级选项（用于查询条件）
      allGradeOptions: [],
      // 班级选项
      classOptions: [],
      // 广西行政区划数据（从后端获取）
      guangxiRegions: [],
      // 是否正在编辑地址
      isEditingAddress: false,
      // 是否显示脱贫年份字段
      showPovertyReliefYear: false,
      currentYearSemesterId: null,
      // 批量认定对话框
      batchDifficultyOpen: false,
      selectedStudentNames: [],
      batchDifficultyForm: {
        difficultyTypeId: null,
        difficultyLevelId: null,
        isPovertyReliefFamily: null,
        povertyReliefYear: null
      },
      // 穿梭框相关数据
      transferData: [], // 未认定学生列表
      transferValue: [], // 选中的学生ID列表
      transferLoading: false,
      // 筛选条件
      transferFilter: {
        schoolStage: [], // 学段：小学、初中、高中
        name: '' // 姓名搜索
      },
      // 学段选项
      schoolStageOptions: [
        { label: '小学', value: 'primary' },
        { label: '初中', value: 'junior' },
        { label: '高中', value: 'senior' }
      ],
      // 家庭成员
      relationOptions: [
        { value: '父亲', label: '父亲' },
        { value: '母亲', label: '母亲' },
        { value: '祖父母', label: '祖父母' },
        { value: '兄弟姐妹', label: '兄弟姐妹' },
        { value: '监护人', label: '监护人' },
        { value: '其他', label: '其他' }
      ],
      familyMembers: [],
      // 银行卡信息
      bankCards: [],
      bankCardRules: {
        bankAccountNo: [{ required: true, message: '银行卡号不能为空', trigger: 'blur' }],
        bankName: [{ required: true, message: '开户行不能为空', trigger: 'blur' }],
        accountHolder: [{ required: true, message: '开卡人不能为空', trigger: 'blur' }]
      },
      detailMode: false,
      // 报表相关
      reportUrl: '',
      reportId: '1159821341794144256',
      reportLoading: false,
      reportError: null,
      reportTimeout: null, // 超时定时器
      sectionAnchors: [
        { id: 'semester', label: '学期信息', icon: 'el-icon-date', ref: 'semesterSection' },
        { id: 'basic', label: '基本信息', icon: 'el-icon-user', ref: 'basicSection' },
        { id: 'domicile', label: '户籍信息', icon: 'el-icon-location', ref: 'domicileSection' },
        { id: 'academic', label: '学籍信息', icon: 'el-icon-reading', ref: 'academicSection' },
        { id: 'difficulty', label: '困难认定', icon: 'el-icon-warning-outline', ref: 'difficultySection' }
      ],
      activeTab: 'basic',
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        yearSemesterId: null,
        name: null,
        idCardNo: null,
        ethnicity: null,
        gradeId: null,
        difficultyTypeId: null,
        isPovertyReliefFamily: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        yearSemesterId: [
          { required: true, message: "学年学期不能为空", trigger: "change" }
        ],
        name: [
          { required: true, message: "姓名不能为空", trigger: "blur" }
        ],
        idCardNo: [
          { required: true, message: "身份证号不能为空", trigger: "blur" },
          { validator: this.validateIdCardNo, trigger: "blur" }
        ],
        gender: [
          { required: true, message: "性别不能为空", trigger: "blur" }
        ],
        ethnicity: [
          { required: true, message: "民族不能为空", trigger: "change" }
        ],
        domicile: [
          { required: true, message: "户籍所在地不能为空", trigger: "blur" }
        ],
        studentNo: [
          { required: true, message: "学籍号不能为空", trigger: "blur" },
          { validator: this.validateStudentNo, trigger: "blur" }
        ],
        schoolingPlanId: [
          { required: true, message: "所属学制不能为空", trigger: "blur" }
        ],
        gradeId: [
          { required: true, message: "所属年级不能为空", trigger: "blur" }
        ],
        classId: [
          { required: true, message: "所属班级不能为空", trigger: "blur" }
        ],
        studyStatus: [
          { required: true, message: "就读状态不能为空", trigger: "change" }
        ],
        difficultyLevelId: [
          { required: true, message: "困难等级不能为空", trigger: "change" }
        ],
        isPovertyReliefFamily: [
          { required: true, message: "请选择是否脱贫户", trigger: "change" }
        ],
        povertyReliefYear: [
          { validator: this.validatePovertyYear, trigger: "change" }
        ]
      }
    }
  },
  computed: {
    /** 对话框宽度：报表tab时使用更大宽度 */
    dialogWidth() {
      // 报表tab时使用95%宽度，其他tab使用1000px
      return this.activeTab === 'report' ? '95%' : '1000px'
    },
    /** 脱贫年份字符串（用于 el-date-picker，确保始终是字符串类型） */
    povertyReliefYearString: {
      get() {
        if (!this.form || !this.form.povertyReliefYear) {
          return null
        }
        // 确保返回字符串类型
        const value = this.form.povertyReliefYear
        return typeof value === 'string' ? value : String(value)
      },
      set(value) {
        // el-date-picker 返回的是字符串，直接赋值
        if (this.form) {
          this.form.povertyReliefYear = value
        }
      }
    }
  },
  watch: {
    // 监听Tab切换，切换到报表tab时自动生成URL
    activeTab(newVal) {
      if (newVal === 'report' && this.form && this.form.id && !this.reportUrl) {
        this.generateReportUrl(this.form.id)
      }
    }
  },
  created() {
    // 检查是否有刷新标记（从表单页面保存后返回）
    if (this.$route.query.refresh) {
      console.log('[Students List] 检测到刷新标记，将刷新列表');
    }

    this.loadRegionData(); // 加载地区数据
    this.getList();
    this.getYearSemesterList();
    this.getSchoolPlanList();
    this.getAllGradeList();
    if (!this.reportId) {
      // 可以从环境变量或系统配置中获取
      // this.reportId = process.env.VUE_APP_REPORT_ID || ''
    }
  },
  mounted() {
    // 监听学生信息保存事件，刷新列表
    this.$EventBus.$on('refreshStudentList', this.handleRefreshList);
  },
  beforeDestroy() {
    // 移除事件监听，防止内存泄漏
    this.$EventBus.$off('refreshStudentList', this.handleRefreshList);
  },
  methods: {
    /** 查询学年学期列表 */
    getYearSemesterList() {
      listYearSemesters({ status: 1 }).then(response => {
        this.yearSemesterOptions = response.rows || response.data || [];
        // 默认选中当前学期
        const currentSemester = this.yearSemesterOptions.find(item => item.isCurrent === 1);
        if (currentSemester) {
          this.currentYearSemesterId = currentSemester.id;
          if (!this.queryParams.yearSemesterId) {
            this.queryParams.yearSemesterId = currentSemester.id;
          }
          this.applyCurrentSemesterToForm();
        }
      });
    },
    /** 验证身份证号唯一性 */
    validateIdCardNo(rule, value, callback) {
      if (!value) {
        callback();
        return;
      }

      // 如果是编辑模式，且身份证号没有变化，则不验证
      if (this.form.id) {
        callback();
        return;
      }

      // 检查身份证号是否已存在
      listStudentsBase({ idCardNo: value }).then(response => {
        if (response.rows && response.rows.length > 0) {
          callback(new Error('该身份证号已存在，请检查'));
        } else {
          callback();
        }
      }).catch(() => {
        callback();
      });
    },
    /** 验证学籍号唯一性 */
    validateStudentNo(rule, value, callback) {
      if (!value) {
        callback();
        return;
      }

      // 如果是编辑模式，且学籍号没有变化，则不验证
      if (this.form.id) {
        callback();
        return;
      }

      // 检查学籍号是否已存在
      listStudentsBase({ studentNo: value }).then(response => {
        if (response.rows && response.rows.length > 0) {
          callback(new Error('该学籍号已存在，请检查'));
        } else {
          callback();
        }
      }).catch(() => {
        callback();
      });
    },
    validatePovertyYear(rule, value, callback) {
      // 如果困难类型是脱贫户类型，则脱贫年份必填
      // 使用计算属性的值进行验证
      const povertyYear = this.povertyReliefYearString
      if (this.form.difficultyTypeId && this.isPovertyReliefDifficultyType(this.form.difficultyTypeId) && !povertyYear) {
        callback(new Error('脱贫年份不能为空'));
      } else {
        callback();
      }
    },
    /** 加载地区数据（从后端获取） */
    loadRegionData() {
      getRegionTree().then(response => {
        if (response.code === 200 && response.data) {
          this.guangxiRegions = response.data;
          console.log('[Students Index] 地区数据加载成功，共', response.data.length, '个顶级节点');
        } else {
          console.error('[Students Index] 地区数据加载失败:', response.msg);
          this.$modal.msgError('加载地区数据失败：' + (response.msg || '未知错误'));
        }
      }).catch(error => {
        console.error('[Students Index] 地区数据加载异常:', error);
        this.$modal.msgError('加载地区数据异常');
      });
    },
    /** 查询困难学生基础信息列表 */
    getList() {
      this.loading = true
      listStudents(this.queryParams).then(response => {
        this.studentsList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    /** 处理列表刷新事件 */
    handleRefreshList() {
      console.log('[Students Index] 接收到刷新事件，刷新学生列表');
      this.getList();
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.detailMode = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        yearSemesterId: this.currentYearSemesterId,
        currentYearSemesterId: this.currentYearSemesterId,
        studentBaseId: null,
        name: null,
        idCardNo: null,
        gender: null,
        ethnicity: null,
        birthday: null,
        phone: null,
        politicalStatus: null,
        enrollmentDate: null,
        isEthnicClass: '0',
        domicile: null,
        regionCodes: [],
        village: null,
        hamlet: null,
        studentNo: null,
        schoolingPlanId: null,
        gradeId: null,
        classId: null,
        studyStatus: null,
        lastGradeUpdate: null,
        difficultyTypeId: null,
        difficultyLevelId: null,
        isPovertyReliefFamily: '0',
        povertyReliefYear: null,
        memo: null
      }
      this.isEditingAddress = false
      this.showPovertyReliefYear = false
      this.familyMembers = []
      this.bankCards = []
      // 重置报表相关状态
      if (this.reportTimeout) {
        clearTimeout(this.reportTimeout)
        this.reportTimeout = null
      }
      this.reportUrl = ''
      this.reportError = null
      // 重置时，如果困难类型是脱贫户，需要显示脱贫年份
      this.$nextTick(() => {
        if (this.form.difficultyTypeId) {
          const isPovertyReliefType = this.isPovertyReliefDifficultyType(this.form.difficultyTypeId)
          this.showPovertyReliefYear = isPovertyReliefType
        }
      })
      this.applyCurrentSemesterToForm()
    },
    applyCurrentSemesterToForm() {
      if (!this.form) {
        this.form = {}
      }
      if (this.currentYearSemesterId && !this.form.yearSemesterId) {
        this.$set(this.form, 'yearSemesterId', this.currentYearSemesterId)
      }
      if (this.currentYearSemesterId && !this.form.currentYearSemesterId) {
        this.$set(this.form, 'currentYearSemesterId', this.currentYearSemesterId)
      }
    },
    formatSupportInfo(row) {
      if (!row) {
        return ''
      }
      const difficultyLabel = this.selectDictLabel(this.dict.type.sys_difficulty_type, row.difficultyTypeId)
      if (!difficultyLabel || difficultyLabel === '无') {
        return ''
      }
      // 如果困难类型是脱贫户类型，且存在脱贫年份，则在前面加上年份
      if (row.difficultyTypeId && this.isPovertyReliefDifficultyType(row.difficultyTypeId) && row.povertyReliefYear) {
        return `${row.povertyReliefYear}年${difficultyLabel}`
      }
      // 否则直接显示困难类型标签
      return difficultyLabel
    },
    formatGradeAndClass(row) {
      if (!row) {
        return '-'
      }
      const grade = row.gradeName || ''
      const clazz = row.className || ''
      const combined = `${grade}${clazz}`
      return combined || '-'
    },
    formatDifficultyLevel(value) {
      return this.selectDictLabel(this.dict.type.sys_difficulty_level, value) || value || '-'
    },
    scrollToSection(refName) {
      this.$nextTick(() => {
        const scrollWrapper = this.$refs.formScroll
        const target = this.$refs[refName]
        if (!scrollWrapper || !target) {
          return
        }
        const wrapperEl = scrollWrapper instanceof HTMLElement ? scrollWrapper : scrollWrapper.$el
        const targetEl = target.$el || target
        if (!wrapperEl || !targetEl) return
        const offset = targetEl.offsetTop - wrapperEl.offsetTop
        wrapperEl.scrollTo({
          top: offset,
          behavior: 'smooth'
        })
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.$router.push({
        path: '/system/students-form/index',
        query: {}
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.$router.push({
        path: '/system/students-form/index',
        query: { id: row.id }
      });
    },
    /** 姓名列点击：打开详情页（只读模式） */
    openStudentDetail(row) {
      this.$router.push({
        path: '/system/students-form/index',
        query: {
          id: row.id,
          mode: 'view'  // 添加 mode 参数标识为详情查看模式
        }
      });
    },
    /** 原有的详情逻辑（已废弃，保留注释）
    openStudentDetailOld(row) {
      this.detailMode = true
      this.reset()
      const id = row.id || this.ids
      getStudents(id).then(response => {
        this.form = response.data
        const resolvedSemesterId = this.form.currentYearSemesterId || this.form.yearSemesterId || this.currentYearSemesterId
        this.$set(this.form, 'yearSemesterId', resolvedSemesterId)
        this.$set(this.form, 'currentYearSemesterId', resolvedSemesterId || null)
        if (this.form.povertyReliefYear !== null && this.form.povertyReliefYear !== undefined) {
          this.form.povertyReliefYear = String(this.form.povertyReliefYear)
        }
        if (this.form.difficultyTypeId) {
          const isPovertyReliefType = this.isPovertyReliefDifficultyType(this.form.difficultyTypeId)
          this.showPovertyReliefYear = isPovertyReliefType
          if (isPovertyReliefType) {
            this.form.isPovertyReliefFamily = '1'
          } else {
            this.form.isPovertyReliefFamily = this.form.isPovertyReliefFamily || '0'
          }
        } else {
          this.form.isPovertyReliefFamily = this.form.isPovertyReliefFamily || '0'
          this.showPovertyReliefYear = false
        }
        if (this.form.schoolingPlanId) {
          getGradeList(this.form.schoolingPlanId).then(res => {
            this.gradeOptions = res.data || []
          }).catch(error => {
            console.error('获取年级列表失败:', error)
            this.gradeOptions = []
          })
        }
        if (this.form.gradeId) {
          getClassList(this.form.gradeId).then(res => {
            this.classOptions = res.data || []
          }).catch(error => {
            console.error('获取班级列表失败:', error)
            this.classOptions = []
          })
        }
        this.familyMembers = (response.data && response.data.familyMembers) ? response.data.familyMembers : []
        this.bankCards = (response.data && response.data.bankCards) ? response.data.bankCards : []
        // 生成报表预览URL
        this.generateReportUrl(id)
        this.open = true
        this.title = "学生详情（只读）"
      }).catch(error => {
        console.error('获取学生记录失败:', error)
        this.$modal.msgError('获取学生记录失败')
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.currentYearSemesterId = this.form.yearSemesterId || null
          // 根据困难类型自动设置 isPovertyReliefFamily
          if (this.form.difficultyTypeId) {
            const isPovertyReliefType = this.isPovertyReliefDifficultyType(this.form.difficultyTypeId)
            this.form.isPovertyReliefFamily = isPovertyReliefType ? '1' : '0'
            // 如果不是脱贫户类型，清空脱贫年份
            if (!isPovertyReliefType) {
              this.form.povertyReliefYear = null
            } else if (this.form.povertyReliefYear) {
              // 如果是脱贫户类型且有脱贫年份，将字符串转换为数字（后端需要 Integer 类型）
              this.form.povertyReliefYear = parseInt(this.form.povertyReliefYear)
            }
          } else if (this.form.povertyReliefYear) {
            // 如果没有困难类型但有脱贫年份，也转换为数字
            this.form.povertyReliefYear = parseInt(this.form.povertyReliefYear)
          }
          // 只有在新增或修改地址时才拼接完整的户籍地址
          if (!this.form.id || this.isEditingAddress) {
            if (this.form.domicile) {
              let fullAddress = this.form.domicile;
              if (this.form.village) {
                fullAddress += this.form.village;
              }
              if (this.form.hamlet) {
                fullAddress += this.form.hamlet;
              }
              this.form.domicile = fullAddress;
            }
          }

          // 绑定家庭成员并进行简单校验
          if (this.familyMembers && this.familyMembers.length > 0) {
            const invalid = this.familyMembers.find(item => !item.name || !item.relation)
            if (invalid) {
              this.$modal.msgError('家庭成员姓名和关系不能为空')
              return
            }
          }
          this.form.familyMembers = this.familyMembers

          // 绑定银行卡并进行简单校验
          if (this.bankCards && this.bankCards.length > 0) {
            const invalidCard = this.bankCards.find(item => !item.bankAccountNo || !item.bankName || !item.accountHolder)
            if (invalidCard) {
              this.$modal.msgError('银行卡号、开户行、开卡人均不能为空')
              return
            }
          }
          this.form.bankCards = this.bankCards

          if (this.form.id != null) {
            updateStudents(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addStudents(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 添加家庭成员行 */
    addFamilyMember() {
      this.familyMembers.push({
        name: '',
        age: null,
        relation: '',
        employer: '',
        occupation: '',
        healthStatus: ''
      })
    },
    /** 移除家庭成员行 */
    removeFamilyMember(index) {
      this.familyMembers.splice(index, 1)
    },
    addBankCard() {
      this.bankCards.push({
        bankAccountNo: '',
        bankType: '信用社',
        bankName: '',
        branchName: '',
        accountHolder: this.form.name || '',
        isPrimary: this.bankCards.length === 0 ? 1 : 0,
        status: 0
      })
    },
    handlePrimaryChange(index, val) {
      if (val === 1) {
        this.bankCards = this.bankCards.map((item, idx) => ({
          ...item,
          isPrimary: idx === index ? 1 : 0
        }))
      }
    },
    removeBankCard(index) {
      this.bankCards.splice(index, 1)
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除困难学生基础信息编号为"' + ids + '"的数据项？').then(function() {
        return delStudents(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/students/export', {
        ...this.queryParams
      }, `students_${new Date().getTime()}.xlsx`)
    },
    /** 获取学制列表 */
    getSchoolPlanList() {
      getSchoolPlanList().then(response => {
        this.schoolPlanOptions = response.data;
      });
    },
    /** 获取所有年级列表（用于查询条件） */
    getAllGradeList() {
      // 调用基础配置的年级接口获取所有年级
      import('@/api/system/baseconfig').then(module => {
        module.listGrades().then(response => {
          this.allGradeOptions = response.data;
        });
      });
    },
    /** 学制改变时触发 */
    handleSchoolPlanChange(value) {
      // 清空年级和班级选择
      this.form.gradeId = null;
      this.form.classId = null;
      this.gradeOptions = [];
      this.classOptions = [];

      // 如果选择了学制，则加载对应的年级列表
      if (value) {
        getGradeList(value).then(response => {
          this.gradeOptions = response.data || [];
        }).catch(error => {
          console.error('获取年级列表失败:', error);
          this.$modal.msgError('获取年级列表失败');
        });
      }
    },
    /** 年级改变时触发 */
    handleGradeChange(value) {
      this.form.classId = null;
      this.classOptions = [];
      if (value) {
        getClassList(value).then(response => {
          this.classOptions = response.data;
        });
      }
    },
    /** 行政区划改变时触发 */
    handleRegionChange(value) {
      // value 是数组，如 ['450100', '450102', '450102001']
      if (value && value.length > 0) {
        // 获取选中的各级名称
        const labels = this.getRegionLabels(value);
        // 拼接完整地址：广西壮族自治区 + 市 + 县 + 乡镇
        const baseAddress = '广西壮族自治区' + labels.join('');
        // 如果有村和屯，会在提交时拼接
        this.form.domicile = baseAddress;
      } else {
        this.form.domicile = null;
      }
    },
    /** 获取区划名称 */
    getRegionLabels(codes) {
      const labels = [];
      let currentLevel = this.guangxiRegions;

      for (let i = 0; i < codes.length; i++) {
        const code = codes[i];
        const item = currentLevel.find(region => region.value === code);
        if (item) {
          labels.push(item.label);
          currentLevel = item.children || [];
        }
      }

      return labels;
    },
    /** 困难类型变化 */
    handleDifficultyTypeChange(value) {
      // 检查选择的困难类型是否是脱贫户类型
      const isPovertyReliefType = this.isPovertyReliefDifficultyType(value)
      if (isPovertyReliefType) {
        // 如果是脱贫户类型，自动设置 isPovertyReliefFamily 为 '1'
        this.form.isPovertyReliefFamily = '1'
        this.showPovertyReliefYear = true
      } else {
        // 如果不是脱贫户类型，清空脱贫相关字段
        this.form.isPovertyReliefFamily = '0'
        this.showPovertyReliefYear = false
        this.form.povertyReliefYear = null
      }
    },
    /** 判断困难类型是否是脱贫户类型 */
    isPovertyReliefDifficultyType(difficultyTypeId) {
      if (!difficultyTypeId) {
        return false
      }
      // 从字典中查找对应的困难类型
      const difficultyType = this.dict.type.sys_difficulty_type.find(dict => dict.value === difficultyTypeId)
      if (!difficultyType) {
        return false
      }
      // 判断标签或值中是否包含"脱贫"关键字
      const label = difficultyType.label || ''
      const value = difficultyType.value || ''
      return label.includes('脱贫') || value.includes('poverty') || value.includes('脱贫')
    },
    /** 格式化户籍所在地，隐藏"自治区"等字样 */
    formatDomicile(domicile) {
      if (!domicile) return ''
      // 移除常见的行政区划后缀和民族名称
      return domicile
        .replace(/壮族/g, '')
        .replace(/回族/g, '')
        .replace(/维吾尔/g, '')
        .replace(/自治区/g, '')
        .replace(/省/g, '')
        .replace(/特别行政区/g, '')
    },
    /** 批量认定困难类型 */
    handleBatchDifficulty() {
      // 不再需要选择学生，直接打开对话框
      this.batchDifficultyForm = {
        difficultyTypeId: null,
        difficultyLevelId: null,
        isPovertyReliefFamily: null,
        povertyReliefYear: null
      };
      this.transferValue = [];
      this.transferFilter = {
        schoolStage: [],
        name: ''
      };
      this.batchDifficultyOpen = true;
    },
    /** 加载未认定学生列表 */
    loadUnrecognizedStudents() {
      this.transferLoading = true;
      // 调用后端接口获取未认定学生列表
      listStudents({
        unrecognized: true,  // 标记只查询未认定的学生
        pageNum: 1,
        pageSize: 10000 // 获取所有
      }).then(response => {
        let students = response.rows || [];

        // 前端过滤：根据学段和姓名筛选
        if (this.transferFilter.schoolStage.length > 0) {
          students = students.filter(student => {
            const stage = this.getSchoolStage(student.schoolingPlanName, student.schoolingYears);
            return this.transferFilter.schoolStage.includes(stage);
          });
        }

        if (this.transferFilter.name) {
          const searchName = this.transferFilter.name.toLowerCase();
          students = students.filter(student => {
            return student.name && student.name.toLowerCase().includes(searchName);
          });
        }

        this.transferData = students.map(student => ({
          id: student.id,
          label: student.name,
          gradeName: `${student.gradeName || ''} ${student.className || ''}`.trim(),
          schoolStage: this.getSchoolStage(student.schoolingPlanName, student.schoolingYears)
        }));
        this.transferLoading = false;
      }).catch(() => {
        this.$message.error('获取学生列表失败');
        this.transferLoading = false;
      });
    },
    /** 根据学制名称或学制年限判断学段 */
    getSchoolStage(schoolingPlanName, schoolingYears) {
      // 优先根据学制名称判断（更准确）
      if (schoolingPlanName) {
        const name = schoolingPlanName.toLowerCase();
        if (name.includes('小学')) return 'primary';
        if (name.includes('初中')) return 'junior';
        if (name.includes('高中')) return 'senior';
      }

      // 降级到根据学制年限判断
      if (schoolingYears) {
        const years = parseInt(schoolingYears);
        if (years === 6) return 'primary';  // 小学通常6年
        if (years === 3) return 'junior';    // 初中通常3年
        if (years === 4) return 'senior';    // 高中通常3-4年
      }

      return 'unknown';
    },
    /** 处理穿梭框筛选 */
    handleTransferFilter() {
      // 重新加载并筛选数据
      this.loadUnrecognizedStudents();
    },
    /** 处理批量认定困难类型变化 */
    handleBatchDifficultyTypeChange(value) {
      // 前端仅UI展示，不做业务判断，业务逻辑由后端处理
      if (!value) {
        this.batchDifficultyForm.isPovertyReliefFamily = null;
        return;
      }

      // 前端只用于显示预览，根据字典标签判断
      const difficultyType = this.dict.type.sys_difficulty_type.find(dict => dict.value === value);
      if (!difficultyType) {
        this.batchDifficultyForm.isPovertyReliefFamily = null;
        return;
      }

      const label = difficultyType.label || '';
      const isPovertyReliefType = label.includes('脱贫');
      // 仅用于UI显示，实际值由后端计算
      this.batchDifficultyForm.isPovertyReliefFamily = isPovertyReliefType ? '1' : '0';

      // 如果不是脱贫户，清空脱贫年份
      if (!isPovertyReliefType) {
        this.batchDifficultyForm.povertyReliefYear = null;
      }
    },
    /** 提交批量认定 */
    submitBatchDifficulty() {
      if (this.transferValue.length === 0) {
        this.$message.warning('请选择需要认定的学生');
        return;
      }

      this.$refs["batchDifficultyForm"].validate(valid => {
        if (valid) {
          // 注意：不传递 isPovertyReliefFamily，由后端根据 difficultyTypeId 自动判断
          // 后端会根据困难类型字典标签自动设置 isPovertyReliefFamily
          const params = {
            studentIds: this.transferValue,  // 使用 studentIds 字段名，与后端 DTO 一致
            difficultyTypeId: this.batchDifficultyForm.difficultyTypeId,
            difficultyLevelId: this.batchDifficultyForm.difficultyLevelId,
            povertyReliefYear: this.batchDifficultyForm.povertyReliefYear ? parseInt(this.batchDifficultyForm.povertyReliefYear) : null
            // 不传递 isPovertyReliefFamily，由后端根据 difficultyTypeId 自动计算
          };

          batchUpdateDifficulty(params).then(response => {
            this.$modal.msgSuccess(response.msg || `批量认定成功，共 ${this.transferValue.length} 名学生`);
            this.batchDifficultyOpen = false;
            this.getList();
          }).catch(() => {
            this.$modal.msgError('批量认定失败');
          });
        }
      });
    },
    /** 取消批量认定 */
    cancelBatchDifficulty() {
      this.batchDifficultyOpen = false;
      this.transferValue = [];
      this.transferData = [];
      this.transferFilter = {
        schoolStage: [],
        name: ''
      };
      this.batchDifficultyForm = {
        difficultyTypeId: null,
        difficultyLevelId: null,
        isPovertyReliefFamily: null,
        povertyReliefYear: null
      };
    },
    /** 生成报表预览URL */
    generateReportUrl(studentId) {
      if (!studentId) {
        this.reportUrl = ''
        this.reportError = null
        return
      }

      const reportId = this.reportId

      if (!reportId) {
        this.reportError = '报表ID未配置，请联系管理员配置报表ID'
        this.reportUrl = ''
        return
      }

      try {
        const baseUrl = process.env.NODE_ENV === 'development'
          ? 'http://localhost:8080'
          : (process.env.VUE_APP_BASE_API || window.location.origin)

        this.reportUrl = `${baseUrl}/jmreport/view/${reportId}?studentId=${studentId}`
        this.reportError = null

        if (this.reportTimeout) {
          clearTimeout(this.reportTimeout)
          this.reportTimeout = null
        }
      } catch (error) {
        console.error('生成报表URL失败:', error)
        this.reportError = '生成报表URL失败: ' + (error.message || '未知错误')
        this.reportUrl = ''
      }
    },
    /** iframe加载完成事件 */
    onReportIframeLoad() {
      if (this.reportTimeout) {
        clearTimeout(this.reportTimeout)
        this.reportTimeout = null
      }
      this.reportError = null
    },
    /** 重试加载报表 */
    retryLoadReport() {
      if (this.form && this.form.id) {
        this.reportError = null
        this.generateReportUrl(this.form.id)
      }
    },
    /** 新窗口打开报表 */
    openReportInNewWindow() {
      if (this.reportUrl) {
        window.open(this.reportUrl, '_blank', 'width=1200,height=800,scrollbars=yes')
      } else {
        this.$modal.msgWarning('报表URL未生成，请稍后再试')
      }
    },
    /** 打印报表 */
    printReport() {
      // 由于iframe跨域限制，建议在新窗口中打印
      if (this.reportUrl) {
        const printWindow = window.open(this.reportUrl, '_blank', 'width=1200,height=800')
        if (printWindow) {
          printWindow.onload = () => {
            setTimeout(() => {
              printWindow.print()
            }, 500)
          }
        } else {
          this.$modal.msgWarning('无法打开新窗口，请检查浏览器弹窗设置')
        }
      } else {
        this.$modal.msgWarning('报表URL未生成，请稍后再试')
      }
    },
    /** 导出PDF */
    exportReport() {
      if (!this.form || !this.form.id) {
        this.$modal.msgWarning('学生信息不存在')
        return
      }

      // 方式1：如果积木报表支持导出，可以直接调用
      if (this.reportUrl) {
        // 在URL后添加导出参数
        const exportUrl = this.reportUrl + '&export=pdf'
        window.open(exportUrl, '_blank')
      } else {
        this.$modal.msgWarning('报表URL未生成，请稍后再试')
      }

      // 方式2：如果需要后端接口导出（推荐）
      // this.download(`/system/report/export/${this.reportId}`, {
      //   studentId: this.form.id,
      //   format: 'pdf'
      // }, `助学金申请表_${this.form.name}_${new Date().getTime()}.pdf`)
    },
    /** 导入按钮下拉菜单操作 */
    handleCommand(command) {
      switch (command) {
        case 'import':
          this.handleImportData();
          break;
        case 'template':
          this.downloadTemplate();
          break;
        default:
          break;
      }
    },
    /** 导入数据 */
    handleImportData() {
      // 先让用户选择导入模式
      this.$confirm('请选择导入模式：\n\n• 更新已存在：如果学生已存在，将更新其信息\n• 跳过已存在：如果学生已存在，将跳过该条数据', '导入数据', {
        confirmButtonText: '更新已存在',
        cancelButtonText: '跳过已存在',
        distinguishCancelAndClose: true,
        type: 'info',
        closeOnClickModal: false
      }).then(() => {
        // 用户选择"更新已存在"
        this.selectAndImportFile(true);
      }).catch((action) => {
        if (action === 'cancel') {
          // 用户选择"跳过已存在"
          this.selectAndImportFile(false);
        }
        // 如果action是'close'，用户点击了关闭按钮，不做任何操作
      });
    },
    /** 选择文件并导入 */
    selectAndImportFile(isUpdateSupport) {
      // 创建隐藏的文件输入元素
      const input = document.createElement('input');
      input.type = 'file';
      input.accept = '.xlsx,.xls'; // 仅用于文件选择器提示，实际验证在后端

      input.onchange = (e) => {
        const file = e.target.files[0];
        if (!file) return;

        // 创建FormData对象
        // 文件类型和大小验证由后端统一处理，前端仅负责文件选择和数据提交
        const formData = new FormData();
        formData.append('file', file);

        // 执行导入
        this.doImport(formData, isUpdateSupport);
      };

      input.click(); // 触发文件选择
    },
    /** 执行导入操作 */
    doImport(formData, isUpdateSupport) {
      // 显示加载提示
      const loading = this.$loading({
        lock: true,
        text: '正在导入数据，请稍候...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });

      importStudents(formData, isUpdateSupport).then(response => {
        loading.close();
        if (response.code === 200) {
          // 后端返回的消息包含详细的导入结果
          const message = response.msg || '导入成功';
          this.$alert(message, '导入结果', {
            dangerouslyUseHTMLString: true,
            type: 'success',
            showClose: true,
            confirmButtonText: '确定',
            customClass: 'import-result-dialog'
          });
          this.getList(); // 重新加载数据
        } else {
          // 处理错误响应
          this.handleImportError(response.msg || '导入失败');
        }
      }).catch(error => {
        loading.close();
        console.error('导入失败:', error);
        // 从错误对象中提取错误信息
        let errorMessage = '导入失败，请检查文件格式和数据';
        if (error.msg) {
          errorMessage = error.msg;
        } else if (error.message) {
          errorMessage = error.message;
        } else if (error.response && error.response.data && error.response.data.msg) {
          errorMessage = error.response.data.msg;
        }
        this.handleImportError(errorMessage);
      });
    },
    /** 处理导入错误 */
    handleImportError(errorMessage) {
      // 如果错误信息包含HTML标签，使用alert显示（后端返回的错误信息通常包含<br/>）
      if (errorMessage && (errorMessage.includes('<br/>') || errorMessage.includes('<br>'))) {
        this.$alert(errorMessage, '导入失败', {
          dangerouslyUseHTMLString: true,
          type: 'error',
          showClose: true,
          confirmButtonText: '确定',
          customClass: 'import-result-dialog'
        });
      } else {
        this.$modal.msgError(errorMessage || '导入失败');
      }
    },
    /** 下载导入模板 */
    downloadTemplate() {
      this.download('/system/students/importTemplate', {}, `学生导入模板.xlsx`);
    }
  }
}
</script>

<style scoped lang="scss">
/* 整个页面使用思源黑体 */
* {
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
}

.query-table-wrapper {
  margin-bottom: 12px;
  padding: 16px 20px 20px;
  border-radius: 16px;
  background-color: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.14);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.table-section {
  margin-top: 12px;
}

.query-form-header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(228, 231, 237, 0.5);
}

.collapse-btn {
  padding: 0;
  color: #606266;
  font-size: 13px;
}

.collapse-btn:hover {
  color: #409EFF;
}

.query-table-wrapper .query-form {
  margin-top: 0;
}

.query-table-wrapper .action-row {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(228, 231, 237, 0.5);
}

/* 搜索按钮使用蓝色背景 */
.students-page ::v-deep .query-form .el-button--primary {
  background-color: #409EFF !important;
  border-color: #409EFF !important;
  color: #ffffff !important;
}

.students-page ::v-deep .query-form .el-button--primary:hover {
  background-color: #66b1ff !important;
  border-color: #66b1ff !important;
  color: #ffffff !important;
}

.students-page ::v-deep .query-form .el-button--primary:active {
  background-color: #3a8ee6 !important;
  border-color: #3a8ee6 !important;
  color: #ffffff !important;
}

/* 操作栏中的 primary 按钮保持白色背景 */
.students-page ::v-deep .action-row .el-button--primary {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.students-page ::v-deep .action-row .el-button--primary:hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.students-page ::v-deep .action-row .el-button--primary:active {
  background-color: #e8e8e8 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

/* 统一功能按钮：白底、浅灰边、黑字（排除搜索、文本、折叠） */
.students-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary) {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.students-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.students-page ::v-deep .el-button:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):active {
  background-color: #e8e8e8 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.students-page ::v-deep .el-button.is-plain:not(.collapse-btn):not(.el-button--text):not(.el-button--primary) {
  background-color: #ffffff !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

.students-page ::v-deep .el-button.is-plain:not(.collapse-btn):not(.el-button--text):not(.el-button--primary):hover {
  background-color: #f5f5f5 !important;
  border-color: #dcdfe6 !important;
  color: #000000 !important;
}

// 表单卡片样式
.form-card {
  margin-bottom: 20px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;

  ::v-deep .el-card__header {
    padding: 12px 20px;
    background: #f5f7fa;
    border-bottom: 1px solid #e4e7ed;
  }

  ::v-deep .el-card__body {
    padding: 20px;
  }

  &:last-of-type {
    margin-bottom: 0;
  }
}

.section-tabs {
  margin-bottom: 14px;
  ::v-deep .el-tabs__header {
    border-bottom: 1px solid #e4e7ed;
    margin: 0 0 4px 0;
  }
  ::v-deep .el-tabs__item {
    border: none;
    border-bottom: 2px solid transparent;
    border-radius: 0;
    margin-right: 18px;
    padding: 10px 6px;
    background: transparent;
    color: #606266;
    font-weight: 500;
    transition: color 0.2s ease, border-color 0.2s ease;
  }
  ::v-deep .el-tabs__item.is-active {
    border-bottom-color: #409EFF;
    color: #409EFF;
    font-weight: 600;
  }
  ::v-deep .el-tabs__item:hover {
    color: #409EFF;
  }
}

.form-tip {
  margin-bottom: 15px;
}

.section-nav {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
  background: #f5f7fa;
  padding: 8px 12px;
  border-radius: 6px;
}

.section-nav .el-button {
  color: #1890ff;
  padding: 0 8px;
  font-size: 13px;
}

.form-scroll {
  max-height: 520px;
  overflow-y: auto;
  padding-right: 8px;
}

.domicile-preview,
.domicile-inputs {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.difficulty-pill {
  display: inline-block;
  padding: 0 10px;
  line-height: 22px;
  border: 1px solid #1890ff;
  border-radius: 6px;
  color: #1890ff;
  background: #ecf5ff;
  font-size: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: #303133;

  i {
    margin-right: 8px;
    font-size: 16px;
    color: #409EFF;
  }
}

// 对话框样式优化
::v-deep .el-dialog__body {
  padding: 20px;
  max-height: 65vh;
  overflow-y: auto;
}

::v-deep .el-dialog__footer {
  padding: 15px 20px;
  text-align: right;
  border-top: 1px solid #e4e7ed;
}

// 报表对话框特殊样式
::v-deep .report-dialog {
  .el-dialog__body {
    max-height: 85vh; /* 报表对话框使用更大的高度，与报表容器高度匹配 */
    padding: 15px;
  }

  .el-dialog {
    margin-top: 3vh !important; /* 减小顶部边距，增大可用空间 */
  }
}

// 表单项间距
::v-deep .el-form-item {
  margin-bottom: 18px;
}

// 响应式设计
@media (max-width: 768px) {
  ::v-deep .el-dialog {
    width: 95% !important;
  }
}

// 报表预览相关样式
.report-card {
  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}

.report-preview-container {
  width: 100%;
  min-height: 85vh; /* 增加高度到85vh */
  height: 85vh;
  position: relative;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: auto; /* 改为auto，允许横向滚动 */
  background: #f5f7fa;

  .report-iframe-wrapper {
    width: 100%;
    max-width: 100%; /* 限制最大宽度，不超过容器 */
    height: 100%;
    min-height: 600px;
    border: none;
    background: #fff;

    ::v-deep > div {
      width: 100%;
      max-width: 100%;
      height: 100%;
    }

    ::v-deep iframe {
      width: 100%;
      max-width: 100%; /* 不再强制最小宽度，让报表自适应 */
      height: 100%;
      min-height: 600px;
    }
  }

  .report-error {
    padding: 20px;
    height: 100%;
    min-height: 400px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .report-empty {
    height: 100%;
    min-height: 400px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  @media (max-width: 768px) {
    height: 400px;
    min-height: 400px;

    .report-iframe-wrapper {
      min-width: 100%;

      ::v-deep iframe {
        min-width: 100%;
      }
    }
  }

  @media (max-width: 768px) {
    height: 400px;
  }
}

// 横线标题样式
.section-title {
  position: relative;
  margin: 24px 0 16px 0;
  padding-bottom: 10px;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 12px;

  &::before {
    content: '';
    display: inline-block;
    width: 4px;
    height: 16px;
    background: linear-gradient(180deg, #409EFF 0%, #66B1FF 100%);
    border-radius: 2px;
  }

  &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: linear-gradient(90deg, #D9D9D9 0%, transparent 100%);
    margin-left: 8px;
  }
}

// 表单布局优化
.form-layout {
  ::v-deep .el-form-item {
    margin-bottom: 16px;

    .el-form-item__label {
      padding: 0 8px 0 0;
      font-weight: 500;
      font-size: 13px;
      color: #303133;
      line-height: 32px;
      white-space: nowrap;
    }

    .el-form-item__content {
      line-height: 32px;
    }
  }

  .el-row {
    margin-bottom: 8px;
  }
}

// Tab选项卡样式
.section-tabs {
  ::v-deep .el-tabs__nav {
    background: transparent;
    border: none;
  }

  ::v-deep .el-tabs__item {
    padding: 0 12px !important;
    height: 40px;
    line-height: 40px;
    margin: 0 4px 0 0;
    border: 1px solid #e4e7ed !important;
    border-radius: 4px 4px 0 0;
    background: #f5f7fa;
    font-size: 13px;

    &:hover {
      color: #409EFF;
      background: #f0f5ff;
    }
  }

  ::v-deep .is-active {
    background: #fff !important;
    border-color: #409EFF !important;
  }
}

.tab-icon {
  width: 20px;
  height: 20px;
  object-fit: contain;
  display: inline-block;
  margin-right: 6px;
  vertical-align: middle;
}

// Tab样式优化
::v-deep .el-drawer {
  display: flex;
  flex-direction: column;

  .el-drawer__header {
    padding: 16px 24px;
    border-bottom: 1px solid #f0f0f0;
  }

  .el-drawer__body {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 0;
    overflow: hidden;
  }

  .el-alert {
    margin-bottom: 16px;
    margin: 0;
  }

  .section-nav {
    padding: 12px 24px 0 24px;
    background: #fff;
    border-bottom: 1px solid #f0f0f0;
    flex-shrink: 0;
  }

  .form-scroll {
    flex: 1;
    overflow-y: auto;
    overflow-x: hidden;
    padding: 16px 24px;
    max-height: none;

    // 自定义滑块
    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-track {
      background: transparent;
    }

    &::-webkit-scrollbar-thumb {
      background: #ccc;
      border-radius: 3px;

      &:hover {
        background: #999;
      }
    }
  }
}

// 导入结果对话框样式优化
::v-deep .import-result-dialog {
  .el-message-box__message {
    max-height: 400px;
    overflow-y: auto;
    padding: 10px 0;
    line-height: 1.6;
    word-break: break-word;

    // 自定义滚动条样式
    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-track {
      background: #f5f5f5;
      border-radius: 3px;
    }

    &::-webkit-scrollbar-thumb {
      background: #c0c4cc;
      border-radius: 3px;

      &:hover {
        background: #909399;
      }
    }
  }

  // 成功消息样式
  &.el-message-box--success .el-message-box__message {
    color: #67c23a;
  }

  // 错误消息样式
  &.el-message-box--error .el-message-box__message {
    color: #f56c6c;
  }
}
</style>
