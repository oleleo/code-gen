<template>
  <div class="app-container">
    <el-table
      :data="rows"
      border
      highlight-current-row
      :cell-style="cellStyleSmall()"
      :header-cell-style="headCellStyleSmall()"
      class="param-table"
    >
      <el-table-column
        prop="dbType"
        label="数据库类型"
        width="200"
      />
      <el-table-column
        prop="baseType"
        label="基本类型"
        width="200"
      >
        <template slot-scope="scope">
          <el-form :ref="`form_description_${scope.row.id}`" :model="scope.row" :rules="paramRowRule" size="mini" @submit.native.prevent>
            <el-form-item
              prop="baseType"
              label-width="0"
            >
              <el-input v-model="scope.row.baseType" placeholder="基本类型" size="mini" maxlength="20" show-word-limit />
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column
        prop="boxType"
        label="装箱类型"
        width="200"
      >
        <template slot-scope="scope">
          <el-form :ref="`form_description_${scope.row.id}`" :model="scope.row" :rules="paramRowRule" size="mini" @submit.native.prevent>
            <el-form-item
              prop="boxType"
              label-width="0"
            >
              <el-input v-model="scope.row.boxType" placeholder="装箱类型" size="mini" maxlength="20" show-word-limit />
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
    </el-table>
    <el-button type="primary" style="margin-top: 20px" @click="onSave">保存</el-button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      rows: [],
      paramRowRule: {
        baseType: [
          { required: true, message: '不能为空', trigger: ['blur'] }
        ],
        boxType: [
          { required: true, message: '不能为空', trigger: ['blur'] }
        ]
      }
    }
  },
  created() {
    this.loadTable()
  },
  methods: {
    loadTable() {
      this.post('/type/list', {}, resp => {
        this.rows = resp.data
      })
    },
    onSave() {
      this.post('/type/update', this.rows, () => {
        this.tip('保存成功')
      })
    }
  }
}
</script>
