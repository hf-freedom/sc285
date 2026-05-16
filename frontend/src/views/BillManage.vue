<template>
  <div>
    <h2 style="margin-bottom: 20px">账单管理</h2>
    
    <el-card style="margin-bottom: 20px">
      <el-form :inline="true" label-width="80px">
        <el-form-item label="选择房屋">
          <el-select v-model="selectedHouse" placeholder="选择房屋" style="width: 200px">
            <el-option v-for="h in houses" :key="h.id" 
              :label="`${h.buildingNo}-${h.unitNo}-${h.roomNo}`" 
              :value="h.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="年份">
          <el-input-number v-model="year" :min="2020" :max="2030"></el-input-number>
        </el-form-item>
        <el-form-item label="月份">
          <el-input-number v-model="month" :min="1" :max="12"></el-input-number>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="generateBills">生成账单</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="bills" border stripe>
        <el-table-column prop="id" label="账单ID" width="180"></el-table-column>
        <el-table-column prop="feeType" label="费用类型" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.feeType === 'PROPERTY_FEE'" type="primary">物业费</el-tag>
            <el-tag v-else-if="scope.row.feeType === 'WATER_FEE'" type="info">水费</el-tag>
            <el-tag v-else-if="scope.row.feeType === 'ELECTRICITY_FEE'" type="warning">电费</el-tag>
            <el-tag v-else-if="scope.row.feeType === 'PARKING_FEE'" type="success">停车费</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="账期" width="100">
          <template #default="scope">
            {{ scope.row.billingYear }}-{{ scope.row.billingMonth }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="账单金额" width="100"></el-table-column>
        <el-table-column prop="paidAmount" label="已缴金额" width="100"></el-table-column>
        <el-table-column prop="unpaidAmount" label="未缴金额" width="100"></el-table-column>
        <el-table-column prop="lateFee" label="滞纳金" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'UNPAID'" type="danger">未缴</el-tag>
            <el-tag v-else-if="scope.row.status === 'PARTIAL_PAID'" type="warning">部分缴纳</el-tag>
            <el-tag v-else-if="scope.row.status === 'PAID'" type="success">已缴</el-tag>
            <el-tag v-else-if="scope.row.status === 'OVERDUE'" type="danger">逾期</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      bills: [],
      houses: [],
      selectedHouse: '',
      year: new Date().getFullYear(),
      month: new Date().getMonth() + 1
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      const [bills, houses] = await Promise.all([
        axios.get('/api/bills'),
        axios.get('/api/houses')
      ])
      this.bills = bills.data
      this.houses = houses.data
    },
    async generateBills() {
      if (!this.selectedHouse) {
        this.$message.warning('请先选择房屋')
        return
      }
      const house = this.houses.find(h => h.id === this.selectedHouse)
      if (!house.ownerId) {
        this.$message.error('该房屋尚未绑定业主，无法生成账单')
        return
      }
      await axios.post(`/api/bills/generate?houseId=${this.selectedHouse}&year=${this.year}&month=${this.month}`)
      this.$message.success('账单生成成功')
      this.loadData()
    }
  }
}
</script>
