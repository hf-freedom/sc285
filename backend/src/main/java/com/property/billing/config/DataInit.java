package com.property.billing.config;

import com.property.billing.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

@Component
public class DataInit implements CommandLineRunner {

    @Autowired
    private com.property.billing.service.BillingService billingService;

    @Override
    public void run(String... args) throws Exception {
        Owner owner1 = new Owner();
        owner1.setId("O001");
        owner1.setName("张三");
        owner1.setPhone("13800138001");
        owner1.setIdCard("110101199001011234");
        owner1.setBlacklisted(false);
        owner1.setCreateTime(System.currentTimeMillis());
        DataStorage.OWNERS.put(owner1.getId(), owner1);

        Owner owner2 = new Owner();
        owner2.setId("O002");
        owner2.setName("李四");
        owner2.setPhone("13800138002");
        owner2.setIdCard("110101199002021234");
        owner2.setBlacklisted(true);
        owner2.setRestrictedServices(java.util.Arrays.asList("装修申请", "停车延期"));
        owner2.setCreateTime(System.currentTimeMillis());
        DataStorage.OWNERS.put(owner2.getId(), owner2);

        House house1 = new House();
        house1.setId("H001");
        house1.setBuildingNo("1栋");
        house1.setUnitNo("2单元");
        house1.setRoomNo("301");
        house1.setArea(100.5);
        house1.setOwnerId("O001");
        house1.setHasParkingSpace(true);
        house1.setCreateTime(System.currentTimeMillis());
        DataStorage.HOUSES.put(house1.getId(), house1);

        House house2 = new House();
        house2.setId("H002");
        house2.setBuildingNo("1栋");
        house2.setUnitNo("2单元");
        house2.setRoomNo("302");
        house2.setArea(88.0);
        house2.setOwnerId("O002");
        house2.setHasParkingSpace(false);
        house2.setCreateTime(System.currentTimeMillis());
        DataStorage.HOUSES.put(house2.getId(), house2);

        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        billingService.generateMonthlyBills("H001", year, month);
        billingService.generateMonthlyBills("H002", year, month);

        System.out.println("数据初始化完成");
    }
}
