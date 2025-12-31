<template>
  <div class="app-container student-form-page">
    <!-- 顶部标题栏 -->
    <div class="form-header">
      <div class="form-title">{{ title }}</div>
      <div v-if="!isNewStudent && form.name" class="student-name">
        <img src="@/assets/jb_xx/student.png" alt="" class="student-icon" />
        <span class="name">{{ form.name }}</span>
      </div>
      <el-button @click="handleCancel" plain size="small">关 闭</el-button>
    </div>

    <!-- 表单内容容器 -->
    <div class="form-container">
      <!-- 选项卡 -->
      <div class="section-nav">
        <el-tabs v-model="activeTab" type="card" class="section-tabs" @tab-click="handleTabClick">
          <el-tab-pane name="basic">
            <template slot="label">
              <img src="@/assets/jb_xx/1.png" alt="" class="tab-icon" />
              <span>基本信息</span>
            </template>
          </el-tab-pane>
          <el-tab-pane name="family" :disabled="isTabDisabled">
            <template slot="label">
              <span :class="{'disabled-tab': isTabDisabled}">
                <img src="@/assets/jb_xx/2.png" alt="" class="tab-icon" />
                <span>家庭成员</span>
                <el-tooltip v-if="isTabDisabled" content="请先保存学生基本信息" placement="top">
                  <i class="el-icon-warning-outline" style="margin-left: 5px; color: #E6A23C;"></i>
                </el-tooltip>
              </span>
            </template>
          </el-tab-pane>
          <el-tab-pane name="bank" :disabled="isTabDisabled">
            <template slot="label">
              <span :class="{'disabled-tab': isTabDisabled}">
                <img src="@/assets/jb_xx/3.png" alt="" class="tab-icon" />
                <span>银行卡</span>
                <el-tooltip v-if="isTabDisabled" content="请先保存学生基本信息" placement="top">
                  <i class="el-icon-warning-outline" style="margin-left: 5px; color: #E6A23C;"></i>
                </el-tooltip>
              </span>
            </template>
          </el-tab-pane>
          <el-tab-pane name="report" :disabled="isTabDisabled">
            <template slot="label">
              <span :class="{'disabled-tab': isTabDisabled}">
                <img src="@/assets/jb_xx/4.png" alt="" class="tab-icon" />
                <span>助学金申请表</span>
                <el-tooltip v-if="isTabDisabled" content="请先保存学生基本信息" placement="top">
                  <i class="el-icon-warning-outline" style="margin-left: 5px; color: #E6A23C;"></i>
                </el-tooltip>
              </span>
            </template>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 表单内容 -->
      <div class="form-content">
        <el-form ref="form" :model="form" :rules="rules" label-position="top" class="form-layout compact-form" :disabled="isViewMode">
          <!-- 基本信息标签页 -->
          <div v-show="activeTab === 'basic'">
            <!-- 学期信息 -->
            <div class="section-title">学期信息</div>
            <el-row :gutter="12">
              <el-col :span="8">
                <el-form-item label="学年学期" prop="yearSemesterId">
                  <el-select v-model="form.yearSemesterId" placeholder="请选择学年学期" style="width: 100%">
                    <el-option
                      v-for="item in yearSemesterOptions"
                      :key="item.id"
                      :label="`${item.schoolYear || ''} ${item.semesterLabel || ''}`"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 基本信息 -->
            <div class="section-title">基本信息</div>
            <el-row :gutter="12">
              <el-col :span="8">
                <el-form-item label="姓名" prop="name">
                  <el-input v-model="form.name" placeholder="请输入姓名" maxlength="50" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="性别" prop="gender">
                  <el-select v-model="form.gender" placeholder="请选择性别" style="width: 100%">
                    <el-option label="男" value="1" />
                    <el-option label="女" value="0" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="民族" prop="ethnicity">
                  <el-select v-model="form.ethnicity" placeholder="请选择民族" filterable style="width: 100%">
                    <el-option
                      v-for="dict in dict.type.sys_student_ethnicity"
                      :key="dict.value"
                      :label="dict.label"
                      :value="dict.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="12">
              <el-col :span="8">
                <el-form-item label="出生日期" prop="birthday">
                  <el-date-picker
                    v-model="form.birthday"
                    type="date"
                    placeholder="请选择出生日期"
                    value-format="yyyy-MM-dd"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="政治面貌" prop="politicalStatus">
                  <el-select v-model="form.politicalStatus" placeholder="请选择政治面貌" style="width: 100%">
                    <el-option label="群众" value="群众" />
                    <el-option label="团员" value="团员" />
                    <el-option label="预备党员" value="预备党员" />
                    <el-option label="党员" value="党员" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="联系电话" prop="phone">
                  <el-input v-model="form.phone" placeholder="请输入11位手机号" maxlength="11" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="身份证号" prop="idCardNo">
                  <el-input v-model="form.idCardNo" placeholder="请输入18位身份证号" maxlength="18" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="学籍号" prop="studentNo">
                  <el-input v-model="form.studentNo" placeholder="请输入学籍号" maxlength="32" />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 户籍信息 -->
            <div class="section-title">户籍信息</div>
            <el-row :gutter="12">
              <el-col :span="24">
                <el-form-item label="户籍所在地" prop="domicile">
                  <div class="domicile-inputs">
                    <el-cascader
                      v-model="form.regionCodes"
                      :options="guangxiRegions"
                      :props="{
                        value: 'value',
                        label: 'label',
                        children: 'children',
                        checkStrictly: true,
                        expandTrigger: 'hover'
                      }"
                      placeholder="请选择市/县/乡镇"
                      clearable
                      filterable
                      style="width: 40%"
                      @change="handleRegionChange"
                    />
                    <el-input
                      v-model="form.village"
                      placeholder="请输入村/社区"
                      maxlength="50"
                      style="width: 28%; margin-left: 10px"
                    />
                    <el-input
                      v-model="form.hamlet"
                      placeholder="请输入屯/组"
                      maxlength="50"
                      style="width: 28%; margin-left: 10px"
                    />
                  </div>
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 学籍信息 -->
            <div class="section-title">学籍信息</div>
            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="所属学制" prop="schoolingPlanId">
                  <el-select v-model="form.schoolingPlanId" placeholder="请选择学制" @change="handleSchoolPlanChange" style="width: 100%">
                    <el-option
                      v-for="item in schoolPlanOptions"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="所属年级" prop="gradeId">
                  <el-select v-model="form.gradeId" placeholder="请选择年级" @change="handleGradeChange" style="width: 100%" :disabled="!form.schoolingPlanId">
                    <el-option
                      v-for="item in gradeOptions"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="所属班级" prop="classId">
                  <el-select v-model="form.classId" placeholder="请选择班级" style="width: 100%" :disabled="!form.gradeId">
                    <el-option
                      v-for="item in classOptions"
                      :key="item.classId"
                      :label="item.className"
                      :value="item.classId"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="入学时间" prop="enrollmentDate">
                  <el-date-picker
                    v-model="form.enrollmentDate"
                    type="date"
                    placeholder="请选择入学时间"
                    value-format="yyyy-MM-dd"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="就读状态" prop="studyStatus">
                  <el-select v-model="form.studyStatus" placeholder="请选择就读状态" style="width: 100%">
                    <el-option
                      v-for="dict in dict.type.sys_study_status"
                      :key="dict.value"
                      :label="dict.label"
                      :value="dict.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="民族高中班" prop="isEthnicClass">
                  <el-radio-group v-model="form.isEthnicClass">
                    <el-radio label="1">是</el-radio>
                    <el-radio label="0">否</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 困难认定 -->
            <div class="section-title">困难认定</div>
            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="困难类型" prop="difficultyTypeId">
                  <el-select v-model="form.difficultyTypeId" placeholder="请选择困难类型" style="width: 100%" @change="handleDifficultyTypeChange">
                    <el-option
                      v-for="dict in dict.type.sys_difficulty_type"
                      :key="dict.value"
                      :label="dict.label"
                      :value="dict.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="困难等级" prop="difficultyLevelId">
                  <el-select v-model="form.difficultyLevelId" placeholder="请选择困难等级" style="width: 100%">
                    <el-option
                      v-for="dict in dict.type.sys_difficulty_level"
                      :key="dict.value"
                      :label="dict.label"
                      :value="dict.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="12" v-if="showPovertyReliefYear">
              <el-col :span="12">
                <el-form-item label="是否脱贫户" prop="isPovertyReliefFamily">
                  <el-radio-group v-model="form.isPovertyReliefFamily" disabled>
                    <el-radio label="1">是</el-radio>
                    <el-radio label="0">否</el-radio>
                  </el-radio-group>
                  <span style="margin-left: 10px; color: #909399; font-size: 12px;">（根据困难类型自动设置）</span>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="脱贫年份" prop="povertyReliefYear">
                  <el-date-picker
                    v-model="form.povertyReliefYear"
                    type="year"
                    placeholder="请选择脱贫年份"
                    value-format="yyyy"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="12">
              <el-col :span="24">
                <el-form-item label="备注" prop="memo">
                  <el-input
                    v-model="form.memo"
                    type="textarea"
                    :rows="3"
                    placeholder="请输入备注信息"
                    maxlength="500"
                    show-word-limit
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16" class="form-buttons">
              <el-col :span="24">
                <el-button v-if="!isViewMode" type="primary" @click="submitForm">保 存</el-button>
                <el-button @click="handleCancel">返 回</el-button>
              </el-col>
            </el-row>
          </div>

          <!-- 家庭成员标签页 -->
          <div v-show="activeTab === 'family'">
            <div class="section-title">家庭成员信息</div>
            <div style="margin-bottom: 12px;">
              <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="addFamilyMember">添加成员</el-button>
            </div>
            <el-table :data="familyMembers" border size="small" style="width: 100%;">
              <el-table-column label="姓名" align="center" width="140">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.name" placeholder="姓名" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="年龄" align="center" width="100">
                <template slot-scope="scope">
                  <el-input-number v-model="scope.row.age" :min="0" :max="150" size="small" controls-position="right" style="width: 100%;" />
                </template>
              </el-table-column>
              <el-table-column label="关系" align="center" width="130">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.relation" placeholder="关系" size="small" style="width: 100%;">
                    <el-option v-for="item in relationOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="工作单位" align="center" min-width="160">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.employer" placeholder="工作单位" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="职业" align="center" min-width="130">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.occupation" placeholder="职业" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="健康状态" align="center" width="130">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.healthStatus" placeholder="健康状态" size="small" style="width: 100%;">
                    <el-option
                      v-for="dict in dict.type.sys_health_status || []"
                      :key="dict.value"
                      :label="dict.label"
                      :value="dict.value"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="100" fixed="right">
                <template slot-scope="scope">
                  <el-button type="text" size="mini" @click="removeFamilyMember(scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="familyMembers.length === 0" style="margin-top: 8px; color: #909399; font-size: 13px;">
              暂无家庭成员，请点击"添加成员"。
            </div>
            <el-row :gutter="16" class="form-buttons">
              <el-col :span="24">
                <el-button v-if="!isViewMode" type="primary" icon="el-icon-user-solid" @click="addFamilyMember">添加家庭成员</el-button>
                <el-button v-if="!isViewMode" type="primary" @click="submitForm">保 存</el-button>
                <el-button @click="handleCancel">返 回</el-button>
              </el-col>
            </el-row>
          </div>

          <!-- 银行卡标签页 -->
          <div v-show="activeTab === 'bank'">
            <div class="section-title">银行卡信息</div>
            <div style="margin-bottom: 12px;">
              <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="addBankCard">添加银行卡</el-button>
            </div>
            <el-table :data="bankCards" border size="small" style="width: 100%;">
              <el-table-column label="银行卡号" align="center" min-width="180">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.bankAccountNo" placeholder="请输入19位银行卡号" size="small" maxlength="19" />
                </template>
              </el-table-column>
              <el-table-column label="所属银行" align="center" min-width="140">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.bankType" placeholder="请选择银行" size="small" style="width: 100%;">
                    <el-option label="信用社" value="信用社" />
                    <el-option label="中幸银行" value="中幸银行" />
                    <el-option label="工商银行" value="工商银行" />
                    <el-option label="中国银行" value="中国银行" />
                    <el-option label="农业银行" value="农业银行" />
                    <el-option label="建设银行" value="建设银行" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="开户行" align="center" min-width="160">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.bankName" placeholder="请输入开户行" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="支行/网点" align="center" min-width="140">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.branchName" placeholder="可选" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="开卡人" align="center" min-width="120">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.accountHolder" placeholder="请输入开卡人" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="主卡" align="center" width="90">
                <template slot-scope="scope">
                  <el-switch
                    v-model="scope.row.isPrimary"
                    :active-value="1"
                    :inactive-value="0"
                    active-color="#13ce66"
                    inactive-color="#dcdfe6"
                    @change="val => handlePrimaryChange(scope.$index, val)"
                    size="small"
                  />
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="100" fixed="right">
                <template slot-scope="scope">
                  <el-button type="text" size="mini" @click="removeBankCard(scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="bankCards.length === 0" style="margin-top: 8px; color: #909399; font-size: 13px;">
              暂无银行卡信息，请点击"添加银行卡"。
            </div>
            <el-row :gutter="16" class="form-buttons">
              <el-col :span="24">
                <el-button v-if="!isViewMode" type="primary" icon="el-icon-wallet" @click="addBankCard">添加银行卡</el-button>
                <el-button v-if="!isViewMode" type="primary" @click="submitForm">保 存</el-button>
                <el-button @click="handleCancel">返 回</el-button>
              </el-col>
            </el-row>
          </div>

          <!-- 助学金申请表标签页 -->
          <div v-show="activeTab === 'report'">
            <div class="section-title">普通高中国家助学金申请表</div>
            <div style="padding: 40px; text-align: center; color: #909399;">
              <p>需要先保存学生信息后，才能查看申请表。</p>
            </div>
            <el-row :gutter="16" class="form-buttons">
              <el-col :span="24">
                <el-button @click="handleCancel">返 回</el-button>
              </el-col>
            </el-row>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import {
  getStudents, addStudents, updateStudents,
  getSchoolPlanList, getGradeList, getClassList
} from "@/api/system/students";
import { listYearSemesters } from "@/api/system/baseconfig";
import guangxiRegions from "@/assets/data/guangxi-region.js";

export default {
  name: "StudentForm",
  dicts: ['sys_student_gender', 'sys_student_ethnicity', 'sys_study_status', 'sys_difficulty_type', 'sys_difficulty_level', 'sys_health_status'],
  data() {
    return {
      title: "新增学生",
      activeTab: "basic",
      loading: false,
      isViewMode: false, // 标记是否为查看模式（只读）
      isNewStudent: true, // 标记是否为新增模式，初始为true
      hasUnsavedChanges: false, // 标记是否有未保存的修改
      hasSavedData: false, // 标记是否保存过数据，用于通知列表刷新
      originalFormData: null, // 保存原始表单数据用于对比
      isLoadingData: false, // 标记是否正在加载数据，加载时不检测变化
      showPovertyReliefYear: false, // 是否显示脱贫年份字段
      yearSemesterOptions: [],
      schoolPlanOptions: [],
      gradeOptions: [],
      classOptions: [],
      guangxiRegions: guangxiRegions,
      relationOptions: [
        { value: '父亲', label: '父亲' },
        { value: '母亲', label: '母亲' },
        { value: '祖父母', label: '祖父母' },
        { value: '兄弟姐妹', label: '兄弟姐妹' },
        { value: '监护人', label: '监护人' },
        { value: '其他', label: '其他' }
      ],
      familyMembers: [],
      bankCards: [],
      form: {},
      rules: {
        yearSemesterId: [
          { required: true, message: "学年学期不能为空", trigger: "change" }
        ],
        name: [
          { required: true, message: "姓名不能为空", trigger: "blur" }
        ],
        idCardNo: [
          { required: true, message: "身份证号不能为空", trigger: "blur" }
        ],
        gender: [
          { required: true, message: "性别不能为空", trigger: "change" }
        ],
        ethnicity: [
          { required: true, message: "民族不能为空", trigger: "change" }
        ],
        domicile: [
          { required: true, message: "户籍所在地不能为空", trigger: "blur" }
        ],
        studentNo: [
          { required: true, message: "学籍号不能为空", trigger: "blur" }
        ],
        schoolingPlanId: [
          { required: true, message: "所属学制不能为空", trigger: "change" }
        ],
        gradeId: [
          { required: true, message: "所属年级不能为空", trigger: "change" }
        ],
        classId: [
          { required: true, message: "所属班级不能为空", trigger: "change" }
        ],
        studyStatus: [
          { required: true, message: "就读状态不能为空", trigger: "change" }
        ]
      }
    };
  },
  created() {
    this.loadData();
  },
  watch: {
    // 监听路由变化，当路由参数变化时重新加载数据
    '$route'(to, from) {
      // 如果是同一个页面，但参数变了，重新加载
      if (to.path === from.path) {
        console.log('[Form.vue] 路由参数变化，重新加载数据');
        this.loadData();
      }
    },
    // 监听表单数据变化
    form: {
      handler() {
        this.checkFormChanges();
      },
      deep: true
    },
    // 监听家庭成员变化
    familyMembers: {
      handler() {
        this.checkFormChanges();
      },
      deep: true
    },
    // 监听银行卡变化
    bankCards: {
      handler() {
        this.checkFormChanges();
      },
      deep: true
    }
  },
  computed: {
    /**
     * 计算属性：判断标签页是否应该被禁用
     * 逻辑：
     * 1. 如果是新增模式（isNewStudent = true）且没有学生ID，则禁用关联标签页
     * 2. 如果是编辑模式（isNewStudent = false），则允许访问所有标签页
     * 3. 如果新增后保存成功并获得ID，也允许访问其他标签页
     * 4. 这确保了必须先保存学生基本信息，再添加关联信息
     */
    isTabDisabled() {
      // 编辑模式：不禁用
      if (!this.isNewStudent) {
        return false;
      }
      // 新增模式：如果没有学生ID，则禁用其他标签页
      return !this.form.id;
    }
  },
  methods: {
    loadData() {
      this.getYearSemesters();
      this.getSchoolPlans();
      
      const studentId = this.$route.query.id;
      const mode = this.$route.query.mode; // 获取模式参数
      console.log('[Form.vue] 路由参数 id:', studentId, 'mode:', mode);
      
      // 设置查看模式：只有明确传入 mode='view' 才是查看模式
      this.isViewMode = (mode === 'view');
      console.log('[Form.vue] isViewMode 设置为:', this.isViewMode);
      
      if (studentId) {
        // 编辑或查看模式
        this.title = this.isViewMode ? "学生详情" : "维护学生";
        this.isNewStudent = false; // 设置为编辑模式
        this.getStudentDetail(studentId);
      } else {
        // 新增模式
        this.title = "新增学生";
        this.isNewStudent = true; // 设置为新增模式
        this.isViewMode = false; // 新增不能是查看模式
        this.initForm();
      }
    },
    initForm() {
      // 设置加载标记，禁用变化检测
      this.isLoadingData = true;
      
      this.form = {
        yearSemesterId: null,
        name: null,
        gender: null,
        ethnicity: null,
        birthday: null,
        politicalStatus: null,
        phone: null,
        idCardNo: null,
        studentNo: null,
        schoolingPlanId: null,
        gradeId: null,
        classId: null,
        enrollmentDate: null,
        studyStatus: null,
        isEthnicClass: '0',
        domicile: null,
        regionCodes: [],
        village: null,
        hamlet: null,
        difficultyTypeId: null,
        difficultyLevelId: null,
        isPovertyReliefFamily: '0',
        povertyReliefYear: null,
        memo: null
      };
      this.familyMembers = [];
      this.bankCards = [];
      
      // 保存原始数据
      this.saveOriginalData();
      
      // 数据初始化完成，启用变化检测
      this.$nextTick(() => {
        this.isLoadingData = false;
      });
    },
    getYearSemesters() {
      console.log('[Form.vue] 开始加载学年学期列表...');
      listYearSemesters({ status: 1 }).then(res => {
        console.log('[Form.vue] 学年学期数据:', res);
        this.yearSemesterOptions = res.rows || res.data || [];
        console.log('[Form.vue] yearSemesterOptions:', this.yearSemesterOptions);
        
        // 如果是新增，默认选中当前学年学期
        if (!this.form.id && this.yearSemesterOptions.length > 0) {
          const currentSemester = this.yearSemesterOptions.find(item => item.isCurrent === 1);
          if (currentSemester) {
            this.$set(this.form, 'yearSemesterId', currentSemester.id);
            console.log('[Form.vue] 默认选中当前学期:', currentSemester);
          }
        }
      }).catch(error => {
        console.error('[Form.vue] 获取学年学期列表失败:', error);
        this.$message.error('获取学年学期列表失败');
      });
    },
    getSchoolPlans() {
      getSchoolPlanList().then(res => {
        this.schoolPlanOptions = res.data || [];
      });
    },
    handleSchoolPlanChange() {
      this.form.gradeId = null;
      this.form.classId = null;
      this.classOptions = [];
      if (this.form.schoolingPlanId) {
        getGradeList(this.form.schoolingPlanId).then(res => {
          this.gradeOptions = res.data || [];
        });
      } else {
        this.gradeOptions = [];
      }
    },
    handleGradeChange() {
      this.form.classId = null;
      if (this.form.gradeId) {
        getClassList(this.form.gradeId).then(res => {
          this.classOptions = res.data || [];
        });
      } else {
        this.classOptions = [];
      }
    },
    getStudentDetail(id) {
      console.log('[Form.vue] 开始加载学生信息, id:', id);
      
      // 设置加载标记，禁用变化检测
      this.isLoadingData = true;
      
      const loading = this.$loading({
        lock: true,
        text: "加载中...",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)"
      });

     getStudents(id).then(res => {
        console.log('[Form.vue] 获取学生信息成功:', res.data);
        
        this.form = res.data || {};
        
        // 处理户籍地址 - 将完整地址解析为级联选择器的值
        if (this.form.domicile) {
          console.log('[Form.vue] 原始户籍地址:', this.form.domicile);
          this.parseDomicileAddress(this.form.domicile);
        }
        
        // 处理学年学期
        const resolvedSemesterId = this.form.currentYearSemesterId || this.form.yearSemesterId;
        this.$set(this.form, 'yearSemesterId', resolvedSemesterId);
        this.$set(this.form, 'currentYearSemesterId', resolvedSemesterId || null);
        
        // 处理脱贫年份
        if (this.form.povertyReliefYear !== null && this.form.povertyReliefYear !== undefined) {
          this.form.povertyReliefYear = String(this.form.povertyReliefYear);
        }
        
        // 根据后端返回的 isPovertyReliefFamily 值控制是否显示脱贫年份字段
        // 后端已经根据困难类型设置好了 isPovertyReliefFamily 的值
        this.showPovertyReliefYear = (this.form.isPovertyReliefFamily === '1');
        
        // 加载学制对应的年级
        if (this.form.schoolingPlanId) {
          getGradeList(this.form.schoolingPlanId).then(gradeRes => {
            this.gradeOptions = gradeRes.data || [];
          }).catch(error => {
            console.error('获取年级列表失败:', error);
            this.gradeOptions = [];
          });
        }
        
        // 加载年级对应的班级
        if (this.form.gradeId) {
          getClassList(this.form.gradeId).then(classRes => {
            this.classOptions = classRes.data || [];
          }).catch(error => {
            console.error('获取班级列表失败:', error);
            this.classOptions = [];
          });
        }
        
        // 加载家庭成员和银行卡信息
        this.familyMembers = (res.data && res.data.familyMembers) ? res.data.familyMembers : [];
        this.bankCards = (res.data && res.data.bankCards) ? res.data.bankCards : [];
        
        console.log('[Form.vue] 最终 form 数据:', this.form);
        
        // 保存原始数据用于对比
        this.saveOriginalData();
        
        // 数据加载完成，启用变化检测
        this.$nextTick(() => {
          this.isLoadingData = false;
        });
        
        loading.close();
      }).catch(error => {
        this.isLoadingData = false; // 加载失败也要重置标记
        loading.close();
        console.error('[Form.vue] 获取学生信息失败:', error);
        this.$message.error('获取学生信息失败，请稍后重试');
        this.$router.go(-1);
      });
    },
    handleRegionChange(value) {
      // 处理地区级联变化
      if (value && value.length > 0) {
        // 获取选中的各级名称
        const labels = this.getRegionLabels(value);
        // 拼接基础地址：广西壮族自治区 + 市 + 县 + 乡镇
        const baseAddress = '广西壮族自治区' + labels.join('');
        // 如果有村和屯，会在提交时拼接
        this.form.domicile = baseAddress;
      } else {
        this.form.domicile = null;
      }
    },
    getRegionLabels(codes) {
      // 根据区划代码获取名称
      if (!codes || codes.length === 0) return [];
      
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
    parseDomicileAddress(fullAddress) {
      // 将完整地址解析为级联选择器的值和村/屯
      if (!fullAddress) return;
      
      // 移除“广西壮族自治区”前缀
      let address = fullAddress.replace('广西壮族自治区', '');
      
      // 尝试匹配市/县/乡镇级联结构
      const regionCodes = [];
      let currentLevel = this.guangxiRegions;
      let remainingAddress = address;
      
      // 最多匹配3级（市/县/乡镇）
      for (let level = 0; level < 3 && currentLevel && currentLevel.length > 0; level++) {
        let matched = false;
        
        for (const region of currentLevel) {
          if (remainingAddress.startsWith(region.label)) {
            regionCodes.push(region.value);
            remainingAddress = remainingAddress.substring(region.label.length);
            currentLevel = region.children || [];
            matched = true;
            break;
          }
        }
        
        if (!matched) break;
      }
      
      // 设置级联选择器的值
      this.$set(this.form, 'regionCodes', regionCodes);
      
      // 剩余部分尝试解析为村/屯
      // 假设格式为：村/社区 + 屯/组
      if (remainingAddress) {
        // 简单处理：尝试找到最后一个常见的屯/组分隔符
        const hamletKeywords = ['屯', '组', '队', '小组'];
        let village = remainingAddress;
        let hamlet = '';
        
        for (const keyword of hamletKeywords) {
          const lastIndex = remainingAddress.lastIndexOf(keyword);
          if (lastIndex > 0) {
            // 找到分界点，分隔村和屯
            village = remainingAddress.substring(0, lastIndex);
            hamlet = remainingAddress.substring(lastIndex);
            break;
          }
        }
        
        this.$set(this.form, 'village', village);
        this.$set(this.form, 'hamlet', hamlet);
      } else {
        this.$set(this.form, 'village', '');
        this.$set(this.form, 'hamlet', '');
      }
      
      console.log('[Form.vue] 解析结果:', {
        regionCodes: this.form.regionCodes,
        village: this.form.village,
        hamlet: this.form.hamlet
      });
    },
    // 注释：前端根据字典标签控制UI显示，业务逻辑判断和数据设置由后端处理
    handleDifficultyTypeChange(value) {
      // 前端仅控制UI显示：根据困难类型字典标签判断是否显示脱贫年份字段
      // 这是UI交互逻辑，不是业务逻辑
      if (!value) {
        this.showPovertyReliefYear = false;
        return;
      }
      
      // 从字典中查找对应的困难类型标签
      const difficultyType = this.dict.type.sys_difficulty_type.find(dict => dict.value === value);
      if (!difficultyType) {
        this.showPovertyReliefYear = false;
        return;
      }
      
      // 根据标签判断是否显示脱贫年份字段（UI控制）
      const label = difficultyType.label || '';
      const isPovertyReliefType = label.includes('脱贫');
      this.showPovertyReliefYear = isPovertyReliefType;
      
      // 注意：不在前端设置 isPovertyReliefFamily 和 povertyReliefYear 的值
      // 这些值由后端在保存时自动设置
    },
    addFamilyMember() {
      this.familyMembers.push({
        name: null,
        age: null,
        relation: null,
        employer: null,
        occupation: null,
        healthStatus: null
      });
    },
    removeFamilyMember(index) {
      this.familyMembers.splice(index, 1);
    },
    addBankCard() {
      this.bankCards.push({
        bankAccountNo: null,
        bankType: null,
        bankName: null,
        branchName: null,
        accountHolder: null,
        isPrimary: 0
      });
    },
    removeBankCard(index) {
      this.bankCards.splice(index, 1);
    },
    // 注释：主卡唯一性控制已移至后端，前端不再做业务逻辑判断
    handlePrimaryChange(index, val) {
      // 前端仅更新UI状态，实际校验由后端处理
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return;
        
        // 处理学年学期
        this.form.currentYearSemesterId = this.form.yearSemesterId || null;
        
        // 处理脱贫年份（转换为数字）
        if (this.form.povertyReliefYear) {
          this.form.povertyReliefYear = parseInt(this.form.povertyReliefYear);
        }
        
        // 处理户籍地址（拼接完整地址）
        if (this.form.regionCodes && this.form.regionCodes.length > 0) {
          // 获取选中的各级名称
          const labels = this.getRegionLabels(this.form.regionCodes);
          let fullAddress = '广西壮族自治区' + labels.join('');
          
          if (this.form.village) {
            fullAddress += this.form.village;
          }
          if (this.form.hamlet) {
            fullAddress += this.form.hamlet;
          }
          
          this.form.domicile = fullAddress;
        }
        
        // 注释：数据验证已移至后端，前端不再做业务逻辑验证
        
        let submitData = {
          ...this.form,
          familyMembers: this.familyMembers,
          bankCards: this.bankCards
        };

        const loading = this.$loading({
          lock: true,
          text: "保存中...",
          spinner: "el-icon-loading",
          background: "rgba(0, 0, 0, 0.7)"
        });

        const request = this.form.id ? updateStudents(submitData) : addStudents(submitData);
        request.then((res) => {
          loading.close();
          
          // 展示后端返回的提示信息
          this.$message.success(res.msg || '保存成功');
          
          // 标记数据已保存，用于关闭时通知列表刷新
          this.hasSavedData = true;
          
          // 禁用变化检测，避免更新数据时触发
          this.isLoadingData = true;
          
          // 如果是新增模式且后端返回了学生ID，设置到 form 中
          if (this.isNewStudent && res.data && res.data.id) {
            console.log('[Form.vue] 新增学生成功，后端返回学生ID:', res.data.id);
            
            // 设置学生ID，解锁标签页
            this.$set(this.form, 'id', res.data.id);
            this.title = '编辑学生';
            
            // 同步后端返回的脱贫户相关字段（后端已经处理完毕）
            if (res.data.isPovertyReliefFamily !== undefined) {
              this.$set(this.form, 'isPovertyReliefFamily', res.data.isPovertyReliefFamily);
              this.showPovertyReliefYear = (res.data.isPovertyReliefFamily === '1');
            }
            if (res.data.povertyReliefYear !== undefined && res.data.povertyReliefYear !== null) {
              this.$set(this.form, 'povertyReliefYear', String(res.data.povertyReliefYear));
            }
            
            // 新增模式：不跳转，留在当前页面继续添加关联信息
            // 保存成功后更新原始数据，重置修改标记
            this.saveOriginalData();
            
            // 启用变化检测
            this.$nextTick(() => {
              this.isLoadingData = false;
            });
            return;
          }
          
          // 编辑模式：也不跳转，留在当前页面
          // 同步后端返回的数据（如果有）
          if (res.data) {
            if (res.data.isPovertyReliefFamily !== undefined) {
              this.$set(this.form, 'isPovertyReliefFamily', res.data.isPovertyReliefFamily);
              this.showPovertyReliefYear = (res.data.isPovertyReliefFamily === '1');
            }
            if (res.data.povertyReliefYear !== undefined && res.data.povertyReliefYear !== null) {
              this.$set(this.form, 'povertyReliefYear', String(res.data.povertyReliefYear));
            }
          }
          
          // 保存成功后更新原始数据，重置修改标记
          this.saveOriginalData();
          
          // 启用变化检测
          this.$nextTick(() => {
            this.isLoadingData = false;
          });
          // this.$router.go(-1);
        }).catch(error => {
          loading.close();
          console.error('保存失败:', error);
          // 错误信息由后端返回，前端自动展示
        });
      });
    },
    /**
     * 处理标签页点击事件
     * 如果尝试点击被禁用的标签页，给出提示
     */
    handleTabClick(tab) {
      if (this.isTabDisabled && (tab.name === 'family' || tab.name === 'bank' || tab.name === 'report')) {
        this.$message({
          type: 'warning',
          message: '请先保存学生基本信息，再添加关联信息',
          duration: 2000
        });
        // 切换回基本信息标签页
        this.activeTab = 'basic';
      }
    },
    /**
     * 保存原始表单数据用于对比
     * 使用 JSON 深拷贝避免引用问题
     */
    saveOriginalData() {
      this.originalFormData = {
        form: JSON.parse(JSON.stringify(this.form)),
        familyMembers: JSON.parse(JSON.stringify(this.familyMembers)),
        bankCards: JSON.parse(JSON.stringify(this.bankCards))
      };
      this.hasUnsavedChanges = false;
      console.log('[Form.vue] 保存原始数据:', this.originalFormData);
    },
    /**
     * 检测表单是否有修改
     * 对比当前数据与原始数据
     */
    checkFormChanges() {
      // 如果正在加载数据，不检测变化
      if (this.isLoadingData) {
        return;
      }
      
      if (!this.originalFormData) {
        this.hasUnsavedChanges = false;
        return;
      }
      
      const currentData = {
        form: JSON.parse(JSON.stringify(this.form)),
        familyMembers: JSON.parse(JSON.stringify(this.familyMembers)),
        bankCards: JSON.parse(JSON.stringify(this.bankCards))
      };
      
      // 对比当前数据与原始数据
      const hasChanges = JSON.stringify(currentData) !== JSON.stringify(this.originalFormData);
      this.hasUnsavedChanges = hasChanges;
    },
    /**
     * 处理取消/关闭按钮
     * 如果有未保存的修改，弹出确认对话框
     */
    handleCancel() {
      if (this.hasUnsavedChanges) {
        this.$confirm('您有未保存的修改，是否保存？', '提示', {
          confirmButtonText: '保存',
          cancelButtonText: '不保存',
          distinguishCancelAndClose: true,
          type: 'warning'
        }).then(() => {
          // 点击“保存”按钮，触发保存操作
          this.submitForm();
          // 注意：保存成功后不会跳转，需要用户再次点击关闭
        }).catch((action) => {
          // 点击“不保存”按钮或关闭对话框
          if (action === 'cancel') {
            // 点击“不保存”，直接返回
            this.goBackAndRefresh();
          }
          // 如果是点击关闭按钮或ESC，不做任何操作
        });
      } else {
        // 没有修改，直接返回
        this.goBackAndRefresh();
      }
    },
    /**
     * 返回列表并根据需要刷新
     * 如果保存过数据，通知列表页刷新
     */
    goBackAndRefresh() {
      if (this.hasSavedData) {
        // 如果保存过数据，使用事件总线通知列表页刷新
        this.$EventBus.$emit('refreshStudentList');
      }
      // 返回上一页
      this.$router.go(-1);
    }
  }
};
</script>

<style scoped lang="scss">
.student-form-page {
  background: #f5f7fa;
  padding: 16px;
  max-width: 1400px;
  margin: 0 auto;
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  background: #fff;
  padding: 12px 20px;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  position: relative;
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
}

.form-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
}

// 学生姓名样式：居中、红色、字体比标题小1px、加粗
.student-name {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-size: 15px;  // 比标题(16px)小1px
  color: #F56C6C;  // 红色
  font-weight: bold;  // 加粗
  white-space: nowrap;  // 防止换行
  display: flex;
  align-items: center;
  gap: 6px;  // 图标和姓名之间的间距
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
  
  .student-icon {
    width: 16px;
    height: 16px;
    vertical-align: middle;
  }
  
  .name {
    font-weight: bold;  // 姓名加粗
    font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
  }
}

.form-container {
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
}

.section-nav {
  padding: 12px 20px 0;
  border-bottom: 1px solid #ebeef5;
}

.section-tabs {
  ::v-deep .el-tabs__header {
    margin: 0 !important;
    border-bottom: none;
  }
  
  ::v-deep .el-tabs__nav {
    border: none;
  }
  
  ::v-deep .el-tabs__item {
    padding: 0 16px;
    height: 36px;
    line-height: 36px;
    font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
  }
  
  // 禁用标签页样式
  ::v-deep .el-tabs__item.is-disabled {
    cursor: not-allowed;
    opacity: 0.5;
    
    &:hover {
      color: #C0C4CC;
    }
  }
}

// 禁用标签文本样式
.disabled-tab {
  opacity: 0.6;
  cursor: not-allowed;
  
  .tab-icon {
    opacity: 0.5;
  }
}

.tab-icon {
  width: 14px;
  height: 14px;
  margin-right: 4px;
  vertical-align: -2px;
}

.form-content {
  padding: 16px 20px 20px;
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  margin: 16px 0 12px 0;
  padding-bottom: 8px;
  padding-left: 8px;
  border-left: 3px solid #409EFF;
  background: #f5f7fa;
  line-height: 1;
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
}

.form-layout {
  ::v-deep .el-form-item {
    margin-bottom: 16px;
  }
  
  ::v-deep .el-form-item__label {
    font-size: 13px;
    padding-bottom: 6px;
    line-height: 1;
    font-weight: 500;
    color: #606266;
    font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
  }
  
  ::v-deep .el-input__inner,
  ::v-deep .el-textarea__inner {
    font-size: 13px;
    font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
  }
  
  ::v-deep .el-select .el-input__inner {
    font-size: 13px;
    font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
  }
  
  ::v-deep .el-button {
    font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
  }
}

.domicile-inputs {
  display: flex;
  gap: 8px;
  width: 100%;
}

.form-buttons {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: center;
  gap: 12px;

  ::v-deep .el-col {
    text-align: center;
  }
  
  ::v-deep .el-button {
    padding: 8px 20px;
    font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Microsoft YaHei', '微软雅黑', sans-serif;
  }
}
</style>
