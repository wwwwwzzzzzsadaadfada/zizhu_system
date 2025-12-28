<template>
  <div class="app-container base-config-container">
    <!-- 页面标题 -->
    <el-card class="page-header-card" shadow="never">
      <div class="header-content">
        <div class="header-left">
          <i class="el-icon-setting header-icon"></i>
          <span class="header-title">基础配置管理</span>
          <span class="header-subtitle">学制、年级、班级、学年学期等基础数据配置</span>
        </div>
      </div>
    </el-card>

    <!-- Tab卡片 -->
    <el-card class="tab-card" shadow="never">
      <el-tabs v-model="activeName" type="card" class="custom-tabs">
        <!-- 学制配置 -->
        <el-tab-pane name="schoolPlan">
          <span slot="label">
            <i class="el-icon-school"></i> 学制配置
          </span>
          <div class="tab-content">
            <div class="toolbar">
              <el-button type="primary" icon="el-icon-plus" size="small" @click="handleAddSchoolPlan">新增学制</el-button>
              <div class="toolbar-right">
                <el-tag type="info" effect="plain">共 {{ schoolPlanList.length }} 条记录</el-tag>
              </div>
            </div>

            <el-table
              v-loading="schoolPlanLoading"
              :data="schoolPlanList"
              stripe
              class="data-table"
              :header-cell-style="{background:'#f5f7fa',color:'#606266'}"
            >
              <el-table-column label="序号" type="index" width="60" align="center" />
              <el-table-column label="学制名称" align="center" prop="name" min-width="150">
                <template slot-scope="scope">
                  <el-tag type="success" effect="plain">{{ scope.row.name }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="学制年数" align="center" prop="years" width="120">
                <template slot-scope="scope">
                  <el-tag type="warning">{{ scope.row.years }} 年</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="180" fixed="right">
                <template slot-scope="scope">
                  <el-button size="mini" type="primary" icon="el-icon-edit" @click="handleEditSchoolPlan(scope.row)">编辑</el-button>
                  <el-button size="mini" type="danger" icon="el-icon-delete" @click="handleDeleteSchoolPlan(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 年级配置 -->
        <el-tab-pane name="grade">
          <span slot="label">
            <i class="el-icon-reading"></i> 年级配置
          </span>
          <div class="tab-content">
            <div class="toolbar">
              <el-button type="primary" icon="el-icon-plus" size="small" @click="handleAddGrade">新增年级</el-button>
              <div class="toolbar-right">
                <el-tag type="info" effect="plain">共 {{ gradeList.length }} 条记录</el-tag>
              </div>
            </div>

            <el-table
              v-loading="gradeLoading"
              :data="gradeList"
              stripe
              class="data-table"
              :header-cell-style="{background:'#f5f7fa',color:'#606266'}"
            >
              <el-table-column label="序号" type="index" width="60" align="center" />
              <el-table-column label="年级名称" align="center" prop="name" min-width="150">
                <template slot-scope="scope">
                  <el-tag type="success" effect="plain">{{ scope.row.name }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="所属学制" align="center" prop="schoolingPlanName" min-width="150">
                <template slot-scope="scope">
                  <el-tag>{{ scope.row.schoolingPlanName }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="180" fixed="right">
                <template slot-scope="scope">
                  <el-button size="mini" type="primary" icon="el-icon-edit" @click="handleEditGrade(scope.row)">编辑</el-button>
                  <el-button size="mini" type="danger" icon="el-icon-delete" @click="handleDeleteGrade(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 班级配置 -->
        <el-tab-pane name="class">
          <span slot="label">
            <i class="el-icon-office-building"></i> 班级配置
          </span>
          <div class="tab-content">
            <div class="toolbar">
              <el-button type="primary" icon="el-icon-plus" size="small" @click="handleAddClass">新增班级</el-button>
              <div class="toolbar-right">
                <el-tag type="info" effect="plain">共 {{ classList.length }} 条记录</el-tag>
              </div>
            </div>

            <el-table
              v-loading="classLoading"
              :data="classList"
              stripe
              class="data-table"
              :header-cell-style="{background:'#f5f7fa',color:'#606266'}"
            >
              <el-table-column label="序号" type="index" width="60" align="center" />
              <el-table-column label="班级名称" align="center" prop="className" min-width="120">
                <template slot-scope="scope">
                  <el-tag type="success" effect="plain">{{ scope.row.className }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="所属年级" align="center" prop="gradeName" min-width="150">
                <template slot-scope="scope">
                  <el-tag>{{ scope.row.gradeName }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="班主任" align="center" prop="headTeacher" min-width="120">
                <template slot-scope="scope">
                  <span v-if="scope.row.headTeacher">
                    <i class="el-icon-user" style="color: #409EFF;"></i> {{ scope.row.headTeacher }}
                  </span>
                  <span v-else style="color: #909399;">暂无</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="180" fixed="right">
                <template slot-scope="scope">
                  <el-button size="mini" type="primary" icon="el-icon-edit" @click="handleEditClass(scope.row)">编辑</el-button>
                  <el-button size="mini" type="danger" icon="el-icon-delete" @click="handleDeleteClass(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 学年学期配置 -->
        <el-tab-pane name="yearSemester">
          <span slot="label">
            <i class="el-icon-date"></i> 学年学期配置
          </span>
          <div class="tab-content">
            <div class="toolbar">
              <el-button type="primary" icon="el-icon-plus" size="small" @click="handleAddYearSemester">新增学年学期</el-button>
              <div class="toolbar-right">
                <el-tag type="info" effect="plain">共 {{ yearSemesterList.length }} 条记录</el-tag>
              </div>
            </div>

            <el-table
              v-loading="yearSemesterLoading"
              :data="yearSemesterList"
              stripe
              class="data-table"
              :header-cell-style="{background:'#f5f7fa',color:'#606266'}"
            >
              <el-table-column label="序号" type="index" width="60" align="center" />
              <el-table-column label="学年" align="center" prop="schoolYear" min-width="120" />
              <el-table-column label="学期" align="center" prop="semesterLabel" min-width="120" />
              <el-table-column label="开始日期" align="center" prop="startDate" min-width="120">
                <template slot-scope="scope">
                  <i class="el-icon-calendar" style="color: #409EFF;"></i> {{ scope.row.startDate }}
                </template>
              </el-table-column>
              <el-table-column label="结束日期" align="center" prop="endDate" min-width="120">
                <template slot-scope="scope">
                  <i class="el-icon-calendar" style="color: #F56C6C;"></i> {{ scope.row.endDate }}
                </template>
              </el-table-column>
              <el-table-column label="状态" align="center" prop="status" min-width="90">
                <template slot-scope="scope">
                  <el-tag v-if="scope.row.status === 0" type="info">未开始</el-tag>
                  <el-tag v-else-if="scope.row.status === 1" type="success">进行中</el-tag>
                  <el-tag v-else type="danger">已结束</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="当前学期" align="center" prop="isCurrent" min-width="90">
                <template slot-scope="scope">
                  <el-tag v-if="scope.row.isCurrent === 1" type="success" effect="dark">
                    <i class="el-icon-star-on"></i> 当前
                  </el-tag>
                  <span v-else style="color: #909399;">-</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="260" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-button
                    v-if="scope.row.isCurrent !== 1"
                    size="mini"
                    type="text"
                    icon="el-icon-star-off"
                    @click="handleSetCurrent(scope.row)"
                  >设为当前</el-button>
                  <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEditYearSemester(scope.row)">编辑</el-button>
                  <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDeleteYearSemester(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 学制表单对话框 -->
    <el-dialog
      :title="schoolPlanTitle"
      :visible.sync="schoolPlanOpen"
      width="550px"
      append-to-body
      :close-on-click-modal="false"
      class="custom-dialog"
    >
      <el-form ref="schoolPlanForm" :model="schoolPlanForm" :rules="schoolPlanRules" label-width="100px">
        <el-form-item label="学制名称" prop="name">
          <el-input
            v-model="schoolPlanForm.name"
            placeholder="请输入学制名称，如：三年普通高中"
            maxlength="20"
            prefix-icon="el-icon-edit"
          />
        </el-form-item>
        <el-form-item label="学制年数" prop="years">
          <el-input-number
            v-model="schoolPlanForm.years"
            :min="1"
            :max="20"
            placeholder="请输入学制年数"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelSchoolPlan">取 消</el-button>
        <el-button type="primary" @click="submitSchoolPlanForm" icon="el-icon-check">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 年级表单对话框 -->
    <el-dialog
      :title="gradeTitle"
      :visible.sync="gradeOpen"
      width="550px"
      append-to-body
      :close-on-click-modal="false"
      class="custom-dialog"
    >
      <el-form ref="gradeForm" :model="gradeForm" :rules="gradeRules" label-width="100px">
        <el-form-item label="年级名称" prop="name">
          <el-input
            v-model="gradeForm.name"
            placeholder="请输入年级名称，如：高一、初一"
            maxlength="20"
            prefix-icon="el-icon-edit"
          />
        </el-form-item>
        <el-form-item label="所属学制" prop="schoolingPlanId">
          <el-select
            v-model="gradeForm.schoolingPlanId"
            placeholder="请选择所属学制"
            style="width: 100%"
            prefix-icon="el-icon-school"
          >
            <el-option
              v-for="item in schoolPlanList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <span style="float: left">{{ item.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ item.years }}年</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelGrade">取 消</el-button>
        <el-button type="primary" @click="submitGradeForm" icon="el-icon-check">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 班级表单对话框 -->
    <el-dialog
      :title="classTitle"
      :visible.sync="classOpen"
      width="550px"
      append-to-body
      :close-on-click-modal="false"
      class="custom-dialog"
    >
      <el-form ref="classForm" :model="classForm" :rules="classRules" label-width="100px">
        <el-form-item label="所属年级" prop="gradeId">
          <el-select
            v-model="classForm.gradeId"
            placeholder="请选择所属年级"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="item in allGradeList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <span style="float: left">{{ item.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ item.schoolingPlanName }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="班级名称" prop="className">
          <el-input
            v-model="classForm.className"
            placeholder="请输入班级名称，如：1班、2班"
            maxlength="50"
            prefix-icon="el-icon-edit"
            @blur="$refs.classForm.validateField('className')"
          />
        </el-form-item>
        <el-form-item label="班主任" prop="headTeacher">
          <el-input
            v-model="classForm.headTeacher"
            placeholder="请输入班主任姓名"
            maxlength="50"
            prefix-icon="el-icon-user"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelClass">取 消</el-button>
        <el-button type="primary" @click="submitClassForm" icon="el-icon-check">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 学年学期表单对话框 -->
    <el-dialog
      :title="yearSemesterTitle"
      :visible.sync="yearSemesterOpen"
      width="600px"
      append-to-body
      :close-on-click-modal="false"
      class="custom-dialog"
    >
      <el-form ref="yearSemesterForm" :model="yearSemesterForm" :rules="yearSemesterRules" label-width="100px">
        <el-form-item label="学年" prop="schoolYear">
          <el-input
            v-model="yearSemesterForm.schoolYear"
            placeholder="请输入学年，如：2024-2025"
            maxlength="20"
            prefix-icon="el-icon-edit"
          />
          <span style="color: #909399; font-size: 12px;">示例：2024-2025、2025-2026</span>
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-radio-group v-model="yearSemesterForm.semester">
            <el-radio :label="1">秋季学期</el-radio>
            <el-radio :label="2">春季学期</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="yearSemesterForm.startDate"
            type="date"
            placeholder="选择开始日期"
            value-format="yyyy-MM-dd"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker
            v-model="yearSemesterForm.endDate"
            type="date"
            placeholder="选择结束日期"
            value-format="yyyy-MM-dd"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="yearSemesterForm.status">
            <el-radio :label="0">未开始</el-radio>
            <el-radio :label="1">进行中</el-radio>
            <el-radio :label="2">已结束</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="当前学期" prop="isCurrent">
          <el-switch
            v-model="yearSemesterForm.isCurrent"
            :active-value="1"
            :inactive-value="0"
            active-text="是"
            inactive-text="否"
          />
          <span style="color: #F56C6C; font-size: 12px; margin-left: 10px;">注：设置为当前学期后，其他学期将自动取消</span>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelYearSemester">取 消</el-button>
        <el-button type="primary" @click="submitYearSemesterForm" icon="el-icon-check">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listSchoolPlans, getSchoolPlan, addSchoolPlan, updateSchoolPlan, delSchoolPlan,
  listGrades, getGrade, addGrade, updateGrade, delGrade,
  listClasses, getClass, addClass, updateClass, delClass,
  listYearSemesters, getYearSemester, addYearSemester, updateYearSemester, delYearSemester, setCurrentYearSemester
} from "@/api/system/baseconfig"

export default {
  name: "BaseConfig",
  data() {
    return {
      // 当前激活的tab
      activeName: 'schoolPlan',
      // 学制数据
      schoolPlanLoading: false,
      schoolPlanList: [],
      schoolPlanOpen: false,
      schoolPlanTitle: '',
      schoolPlanForm: {},
      schoolPlanRules: {
        name: [{ required: true, message: "学制名称不能为空", trigger: "blur" }],
        years: [{ required: true, message: "学制年数不能为空", trigger: "blur" }]
      },
      // 年级数据
      gradeLoading: false,
      gradeList: [],
      allGradeList: [],
      gradeOpen: false,
      gradeTitle: '',
      gradeForm: {},
      gradeRules: {
        name: [{ required: true, message: "年级名称不能为空", trigger: "blur" }],
        schoolingPlanId: [{ required: true, message: "所属学制不能为空", trigger: "change" }]
      },
      // 班级数据
      classLoading: false,
      classList: [],
      classOpen: false,
      classTitle: '',
      classForm: {},
      classRules: {
        gradeId: [{ required: true, message: "所属年级不能为空", trigger: "change" }],
        className: [
          { required: true, message: "班级名称不能为空", trigger: "blur" },
          { validator: this.validateClassName, trigger: "blur" }
        ]
      },
      // 学年学期数据
      yearSemesterLoading: false,
      yearSemesterList: [],
      yearSemesterOpen: false,
      yearSemesterTitle: '',
      yearSemesterForm: {},
      yearSemesterRules: {
        schoolYear: [{ required: true, message: "学年不能为空", trigger: "blur" }],
        semester: [{ required: true, message: "学期不能为空", trigger: "change" }],
        startDate: [{ required: true, message: "开始日期不能为空", trigger: "change" }],
        endDate: [{ required: true, message: "结束日期不能为空", trigger: "change" }],
        status: [{ required: true, message: "状态不能为空", trigger: "change" }]
      }
    }
  },
  created() {
    this.getSchoolPlanList();
    this.getGradeList();
    this.getClassList();
    this.getYearSemesterList();
  },
  methods: {
    /** 查询学制列表 */
    getSchoolPlanList() {
      this.schoolPlanLoading = true;
      listSchoolPlans().then(response => {
        this.schoolPlanList = response.data;
        this.schoolPlanLoading = false;
      });
    },
    /** 新增学制 */
    handleAddSchoolPlan() {
      this.reset();
      this.schoolPlanOpen = true;
      this.schoolPlanTitle = "添加学制";
    },
    /** 修改学制 */
    handleEditSchoolPlan(row) {
      this.reset();
      this.schoolPlanForm = Object.assign({}, row);
      this.schoolPlanOpen = true;
      this.schoolPlanTitle = "修改学制";
    },
    /** 删除学制 */
    handleDeleteSchoolPlan(row) {
      this.$modal.confirm('是否确认删除学制"' + row.name + '"？').then(() => {
        return delSchoolPlan(row.id);
      }).then(() => {
        this.getSchoolPlanList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 提交学制表单 */
    submitSchoolPlanForm() {
      this.$refs["schoolPlanForm"].validate(valid => {
        if (valid) {
          if (this.schoolPlanForm.id != null) {
            updateSchoolPlan(this.schoolPlanForm).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.schoolPlanOpen = false;
              this.getSchoolPlanList();
            });
          } else {
            addSchoolPlan(this.schoolPlanForm).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.schoolPlanOpen = false;
              this.getSchoolPlanList();
            });
          }
        }
      });
    },
    /** 取消学制 */
    cancelSchoolPlan() {
      this.schoolPlanOpen = false;
      this.reset();
    },

    /** 查询年级列表 */
    getGradeList() {
      this.gradeLoading = true;
      listGrades().then(response => {
        this.gradeList = response.data;
        this.allGradeList = response.data;
        this.gradeLoading = false;
      });
    },
    /** 新增年级 */
    handleAddGrade() {
      this.reset();
      this.gradeOpen = true;
      this.gradeTitle = "添加年级";
    },
    /** 修改年级 */
    handleEditGrade(row) {
      this.reset();
      this.gradeForm = Object.assign({}, row);
      this.gradeOpen = true;
      this.gradeTitle = "修改年级";
    },
    /** 删除年级 */
    handleDeleteGrade(row) {
      this.$modal.confirm('是否确认删除年级"' + row.name + '"？').then(() => {
        return delGrade(row.id);
      }).then(() => {
        this.getGradeList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 提交年级表单 */
    submitGradeForm() {
      this.$refs["gradeForm"].validate(valid => {
        if (valid) {
          if (this.gradeForm.id != null) {
            updateGrade(this.gradeForm).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.gradeOpen = false;
              this.getGradeList();
            });
          } else {
            addGrade(this.gradeForm).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.gradeOpen = false;
              this.getGradeList();
            });
          }
        }
      });
    },
    /** 取消年级 */
    cancelGrade() {
      this.gradeOpen = false;
      this.reset();
    },

    /** 查询班级列表 */
    getClassList() {
      this.classLoading = true;
      listClasses().then(response => {
        this.classList = response.data;
        this.classLoading = false;
      });
    },
    /** 新增班级 */
    handleAddClass() {
      this.reset();
      this.classOpen = true;
      this.classTitle = "添加班级";
    },
    /** 修改班级 */
    handleEditClass(row) {
      this.reset();
      this.classForm = Object.assign({}, row);
      this.classOpen = true;
      this.classTitle = "修改班级";
    },
    /** 删除班级 */
    handleDeleteClass(row) {
      this.$modal.confirm('是否确认删除班级"' + row.className + '"？').then(() => {
        return delClass(row.classId);
      }).then(() => {
        this.getClassList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 验证班级名称是否重复 */
    validateClassName(rule, value, callback) {
      if (!value || !this.classForm.gradeId) {
        callback();
        return;
      }

      // 查找当前年级下是否已存在相同班级名称
      const existClass = this.classList.find(item =>
        item.gradeId === this.classForm.gradeId &&
        item.className === value &&
        item.classId !== this.classForm.classId // 排除当前编辑的班级
      );

      if (existClass) {
        callback(new Error('该班级已分配，请修改班级名称'));
      } else {
        callback();
      }
    },
    /** 提交班级表单 */
    submitClassForm() {
      this.$refs["classForm"].validate(valid => {
        if (valid) {
          if (this.classForm.classId != null) {
            updateClass(this.classForm).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.classOpen = false;
              this.getClassList();
            });
          } else {
            addClass(this.classForm).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.classOpen = false;
              this.getClassList();
            });
          }
        }
      });
    },
    /** 取消班级 */
    cancelClass() {
      this.classOpen = false;
      this.reset();
    },

    /** 表单重置 */
    reset() {
      this.schoolPlanForm = {
        id: null,
        name: null,
        years: null
      };
      this.gradeForm = {
        id: null,
        name: null,
        schoolingPlanId: null
      };
      this.classForm = {
        classId: null,
        gradeId: null,
        className: null,
        headTeacher: null
      };
      this.yearSemesterForm = {
        id: null,
        schoolYear: null,
        semester: 1,
        startDate: null,
        endDate: null,
        status: 0,
        isCurrent: 0
      };
      this.resetForm("schoolPlanForm");
      this.resetForm("gradeForm");
      this.resetForm("classForm");
      this.resetForm("yearSemesterForm");
    },

    /** 查询学年学期列表 */
    getYearSemesterList() {
      this.yearSemesterLoading = true;
      listYearSemesters().then(response => {
        this.yearSemesterList = response.rows || response.data || [];
        this.yearSemesterLoading = false;
      });
    },
    /** 新增学年学期 */
    handleAddYearSemester() {
      this.reset();
      this.yearSemesterOpen = true;
      this.yearSemesterTitle = "添加学年学期";
    },
    /** 修改学年学期 */
    handleEditYearSemester(row) {
      this.reset();
      this.yearSemesterForm = Object.assign({}, row);
      this.yearSemesterOpen = true;
      this.yearSemesterTitle = "修改学年学期";
    },
    /** 删除学年学期 */
    handleDeleteYearSemester(row) {
      this.$modal.confirm('是否确认删除学年学期"' + row.schoolYear + ' 第' + (row.semester === 1 ? '一' : '二') + '学期"?').then(() => {
        return delYearSemester(row.id);
      }).then(() => {
        this.getYearSemesterList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 设置当前学期 */
    handleSetCurrent(row) {
      this.$modal.confirm('是否设置"' + row.schoolYear + ' 第' + (row.semester === 1 ? '一' : '二') + '学期"为当前学期?').then(() => {
        return setCurrentYearSemester(row.id);
      }).then(() => {
        this.getYearSemesterList();
        this.$modal.msgSuccess("设置成功");
      }).catch(() => {});
    },
    /** 提交学年学期表单 */
    submitYearSemesterForm() {
      this.$refs["yearSemesterForm"].validate(valid => {
        if (valid) {
          if (this.yearSemesterForm.id != null) {
            updateYearSemester(this.yearSemesterForm).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.yearSemesterOpen = false;
              this.getYearSemesterList();
            });
          } else {
            addYearSemester(this.yearSemesterForm).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.yearSemesterOpen = false;
              this.getYearSemesterList();
            });
          }
        }
      });
    },
    /** 取消学年学期 */
    cancelYearSemester() {
      this.yearSemesterOpen = false;
      this.reset();
    }
  }
}
</script>

<style scoped lang="scss">
.base-config-container {
  padding: 20px;
  background: #f0f2f5;
  min-height: calc(100vh - 84px);
}

// 页面头部卡片
.page-header-card {
  margin-bottom: 20px;
  border-radius: 8px;

  ::v-deep .el-card__body {
    padding: 20px 24px;
  }
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  font-size: 24px;
  color: #409EFF;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-subtitle {
  font-size: 14px;
  color: #909399;
  margin-left: 8px;
}

// Tab卡片
.tab-card {
  border-radius: 8px;

  ::v-deep .el-card__body {
    padding: 0;
  }
}

// 自定义Tab样式
.custom-tabs {
  ::v-deep .el-tabs__header {
    margin: 0;
    background: #fff;
    border-radius: 8px 8px 0 0;
  }

  ::v-deep .el-tabs__nav {
    border: none;
  }

  ::v-deep .el-tabs__item {
    height: 50px;
    line-height: 50px;
    font-size: 15px;
    color: #606266;
    border: none;
    border-bottom: 3px solid transparent;

    i {
      margin-right: 6px;
      font-size: 16px;
    }

    &.is-active {
      color: #409EFF;
      border-bottom-color: #409EFF;
      font-weight: 500;
    }

    &:hover {
      color: #409EFF;
    }
  }
}

// Tab内容
.tab-content {
  padding: 20px;
  background: #fff;
}

// 工具栏
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #EBEEF5;
}

.toolbar-right {
  display: flex;
  gap: 12px;
  align-items: center;
}

// 数据表格
.data-table {
  ::v-deep .el-table__header {
    th {
      font-weight: 600;
    }
  }

  ::v-deep .el-table__row {
    &:hover {
      background: #f5f7fa;
    }
  }
}

// 自定义对话框
.custom-dialog {
  ::v-deep .el-dialog__header {
    padding: 20px 24px;
    background: #f5f7fa;
    border-bottom: 1px solid #e4e7ed;
    border-radius: 8px 8px 0 0;

    .el-dialog__title {
      color: #303133;
      font-size: 16px;
      font-weight: 600;
    }

    .el-dialog__headerbtn {
      .el-dialog__close {
        color: #909399;
        font-size: 18px;

        &:hover {
          color: #606266;
        }
      }
    }
  }

  ::v-deep .el-dialog__body {
    padding: 24px;
  }

  ::v-deep .el-dialog__footer {
    padding: 16px 24px;
    background: #f5f7fa;
    border-radius: 0 0 8px 8px;
  }
}

// 按钮组
::v-deep .el-button--mini {
  padding: 7px 12px;
}

// 响应式设计
@media (max-width: 768px) {
  .base-config-container {
    padding: 10px;
  }

  .header-title {
    font-size: 16px;
  }

  .header-subtitle {
    display: none;
  }

  .tab-content {
    padding: 12px;
  }

  .toolbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .toolbar-right {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
