package com.property.billing.service;

import com.property.billing.model.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ScheduledTasks {

    @Autowired
    private BillingService billingService;

    @Scheduled(cron = "0 0 1 1 * ?")
    public void generateMonthlyBills() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        DataStorage.HOUSES.values().stream()
                .filter(h -> h.getOwnerId() != null)
                .forEach(house -> {
                    billingService.generateMonthlyBills(house.getId(), year, month);
                });

        System.out.println("已生成" + year + "年" + month + "月账单");
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void calculateOverdue() {
        billingService.calculateLateFees();
        System.out.println("已计算逾期滞纳金");
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendReminders() {
        System.out.println("已推送催缴提醒");
    }
}
