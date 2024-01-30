<template>
  <div class="app-container">
    <el-alert :closable="false">
      失去焦点自动保存
    </el-alert>
    <el-table
      :data="pageInfo.rows"
      border
      highlight-current-row
      :cell-style="cellStyleSmall()"
      :header-cell-style="headCellStyleSmall()"
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
          <el-input v-model="scope.row.baseType" placeholder="基本类型" size="mini" maxlength="20" show-word-limit @blur="onTableUpdate(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column
        prop="boxType"
        label="装箱类型"
        width="200"
      >
        <template slot-scope="scope">
          <el-input v-model="scope.row.boxType" placeholder="装箱类型" size="mini" maxlength="20" show-word-limit @blur="onTableUpdate(scope.row)" />
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      pageInfo: {
        rows: [],
        total: 0
      }
    }
  },
  created() {
    this.loadTable()
  },
  methods: {
    loadTable() {
      this.post('/type/list', {}, resp => {
        this.pageInfo.rows = resp.data
      })
    },
    onTableUpdate(row) {
      this.post('/type/update', row, () => {

      })
    }
  }
}
</script>
