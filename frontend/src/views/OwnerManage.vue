<template>
  <div>
    <h2 style="margin-bottom: 20px">业主管理</h2>
    
    <el-card style="margin-bottom: 20px">
      <el-form :inline="true" :model="ownerForm" label-width="80px">
        <el-form-item label="业主姓名">
          <el-input v-model="ownerForm.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="ownerForm.phone" placeholder="请输入电话"></el-input>
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="ownerForm.idCard" placeholder="请输入身份证号"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="addOwner">添加业主</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="owners" border stripe>
        <el-table-column prop="id" label="业主ID" width="150"></el-table-column>
        <el-table-column prop="name" label="姓名" width="100"></el-table-column>
        <el-table-column prop="phone" label="联系电话" width="130"></el-table-column>
        <el-table-column prop="idCard" label="身份证号" width="180"></el-table-column>
        <el-table-column label="黑名单状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.blacklisted ? 'danger' : 'success'">
              {{ scope.row.blacklisted ? '黑名单' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="限制服务" min-width="200">
          <template #default="scope">
            <el-tag v-for="s in scope.row.restrictedServices" :key="s" size="small" style="margin: 2px">
              {{ s }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" :type="scope.row.blacklisted ? 'success' : 'warning'" 
              @click="toggleBlacklist(scope.row)">
              {{ scope.row.blacklisted ? '移出' : '加入' }}黑名单
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <h2 style="margin: 30px 0 20px">房屋管理</h2>
    <el-card style="margin-bottom: 20px">
      <el-form :inline="true" :model="houseForm" label-width="80px">
        <el-form-item label="楼栋">
          <el-input v-model="houseForm.buildingNo" placeholder="楼栋号"></el-input>
        </el-form-item>
        <el-form-item label="单元">
          <el-input v-model="houseForm.unitNo" placeholder="单元号"></el-input>
        </el-form-item>
        <el-form-item label="房间">
          <el-input v-model="houseForm.roomNo" placeholder="房间号"></el-input>
        </el-form-item>
        <el-form-item label="面积">
          <el-input-number v-model="houseForm.area" :min="0" :step="0.1"></el-input-number>
        </el-form-item>
        <el-form-item label="车位">
          <el-checkbox v-model="houseForm.hasParkingSpace">有车位</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="addHouse">添加房屋</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="houses" border stripe>
        <el-table-column prop="id" label="房屋ID" width="150"></el-table-column>
        <el-table-column prop="buildingNo" label="楼栋" width="80"></el-table-column>
        <el-table-column prop="unitNo" label="单元" width="80"></el-table-column>
        <el-table-column prop="roomNo" label="房间" width="80"></el-table-column>
        <el-table-column prop="area" label="面积(㎡)" width="100"></el-table-column>
        <el-table-column label="车位" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.hasParkingSpace ? 'success' : 'info'">
              {{ scope.row.hasParkingSpace ? '有' : '无' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="绑定业主" width="150">
          <template #default="scope">
            <el-select v-model="scope.row.ownerId" placeholder="选择业主" 
              @change="bindOwner(scope.row.id, scope.row.ownerId)" size="small">
              <el-option v-for="o in owners" :key="o.id" :label="o.name" :value="o.id"></el-option>
            </el-select>
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
      houses: [],
      ownerForm: { name: '', phone: '', idCard: '' },
      houseForm: { buildingNo: '', unitNo: '', roomNo: '', area: 0, hasParkingSpace: false }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      const [owners, houses] = await Promise.all([
        axios.get('/api/owners'),
        axios.get('/api/houses')
      ])
      this.owners = owners.data
      this.houses = houses.data
    },
    async addOwner() {
      if (!this.ownerForm.name) {
        this.$message.warning('请输入业主姓名')
        return
      }
      await axios.post('/api/owners', this.ownerForm)
      this.$message.success('添加业主成功')
      this.ownerForm = { name: '', phone: '', idCard: '' }
      this.loadData()
    },
    async addHouse() {
      if (!this.houseForm.buildingNo) {
        this.$message.warning('请输入楼栋号')
        return
      }
      await axios.post('/api/houses', this.houseForm)
      this.$message.success('添加房屋成功')
      this.houseForm = { buildingNo: '', unitNo: '', roomNo: '', area: 0, hasParkingSpace: false }
      this.loadData()
    },
    async bindOwner(houseId, ownerId) {
      await axios.post(`/api/houses/${houseId}/bind-owner?ownerId=${ownerId}`)
      this.$message.success('绑定业主成功')
    },
    async toggleBlacklist(owner) {
      await axios.post(`/api/owners/${owner.id}/blacklist?blacklisted=${!owner.blacklisted}`)
      this.$message.success(owner.blacklisted ? '已移出黑名单' : '已加入黑名单')
      this.loadData()
    }
  }
}
</script>
