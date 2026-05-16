<template>
  <div>
    <h2 style="margin-bottom: 20px">减免审批</h2>
    
    <el-card style="margin-bottom: 20px">
      <template #header>
        <span>业主申请减免</span>
      </template>
      <el-form :inline="true" label-width="80px">
        <el-form-item label="选择业主">
          <el-select v-model="applyForm.ownerId" placeholder="选择业主" style="width: 150px" @change="onOwnerChange">
            <el-option v-for="o in owners" :key="o.id" :label="o.name" :value="o.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择账单">
          <el-select v-model="applyForm.billId" placeholder="选择账单" style="width: 250px" :disabled="!applyForm.ownerId">
            <el-option v-for="b in ownerBills" :key="b.id" :label="getFeeTypeName(b.feeType) + ' - ' + b.unpaidAmount + '元'" :value="b.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="减免金额">
          <el-input-number v-model="applyForm.amount" :min="0.01" :precision="2" :max="maxDiscountAmount"></el-input-number>
        </el-form-item>
        <el-form-item label="原因">
          <el-input v-model="applyForm.reason" placeholder="减免原因" style="width: 150px"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="applyDiscount">提交申请</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <template #header>
        <span>审批列表</span>
      </template>
      <el-table :data="discounts" border stripe>
        <el-table-column prop="id" label="申请ID" width="180"></el-table-column>
        <el-table-column label="业主" width="100">
          <template #default="scope">
            {{ getOwnerName(scope.row.ownerId) }}
          </template>
        </el-table-column>
        <el-table-column prop="billId" label="账单ID" width="180"></el-table-column>
        <el-table-column prop="discountAmount" label="减免金额" width="100"></el-table-column>
        <el-table-column prop="reason" label="减免原因" width="150"></el-table-column>
        <el-table-column prop="approver" label="审批人" width="100"></el-table-column>
        <el-table-column prop="approvalRemark" label="审批意见" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'PENDING'" type="warning">待审批</el-tag>
            <el-tag v-else-if="scope.row.status === 'APPROVED'" type="success">已通过</el-tag>
            <el-tag v-else-if="scope.row.status === 'REJECTED'" type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="scope">
            <template v-if="scope.row.status === 'PENDING'">
              <el-input v-model="scope.row.remark" placeholder="审批意见" size="small" style="width: 100px; margin-right: 5px"></el-input>
              <el-button size="small" type="success" @click="approve(scope.row, true)">通过</el-button>
              <el-button size="small" type="danger" @click="approve(scope.row, false)">拒绝</el-button>
            </template>
            <span v-else class="text-muted">-</span>
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
      owners: [],
      bills: [],
      discounts: [],
      applyForm: { ownerId: '', billId: '', amount: 0, reason: '' }
    }
  },
  computed: {
    ownerBills() {
      return this.bills.filter(b => b.ownerId === this.applyForm.ownerId && b.status !== 'PAID')
    },
    maxDiscountAmount() {
      const bill = this.bills.find(b => b.id === this.applyForm.billId)
      return bill ? bill.unpaidAmount : 999999
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      const [owners, bills, discounts] = await Promise.all([
        axios.get('/api/owners'),
        axios.get('/api/bills'),
        axios.get('/api/discounts')
      ])
      this.owners = owners.data
      this.bills = bills.data
      this.discounts = discounts.data.map(d => ({ ...d, remark: d.approvalRemark || '' }))
    },
    onOwnerChange() {
      this.applyForm.billId = ''
      this.applyForm.amount = 0
    },
    getOwnerName(ownerId) {
      const owner = this.owners.find(o => o.id === ownerId)
      return owner ? owner.name : ownerId
    },
    getFeeTypeName(feeType) {
      const types = {
        'PROPERTY_FEE': '物业费',
        'WATER_FEE': '水费',
        'ELECTRICITY_FEE': '电费',
        'PARKING_FEE': '停车费'
      }
      return types[feeType] || feeType
    },
    async applyDiscount() {
      if (!this.applyForm.ownerId || !this.applyForm.billId || !this.applyForm.reason) {
        this.$message.warning('请填写完整信息')
        return
      }
      if (this.applyForm.amount <= 0) {
        this.$message.warning('请输入减免金额')
        return
      }
      await axios.post(`/api/discounts/apply?ownerId=${this.applyForm.ownerId}&billId=${this.applyForm.billId}&amount=${this.applyForm.amount}&reason=${this.applyForm.reason}`)
      this.$message.success('申请提交成功')
      this.applyForm = { ownerId: '', billId: '', amount: 0, reason: '' }
      this.loadData()
    },
    async approve(row, approved) {
      await axios.post(`/api/discounts/${row.id}/approve?approver=管理员&remark=${row.remark || ''}&approved=${approved}`)
      this.$message.success(approved ? '已通过' : '已拒绝')
      this.loadData()
    }
  }
}
</script>
