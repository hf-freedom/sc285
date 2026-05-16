package com.property.billing.controller;

import com.property.billing.model.*;
import com.property.billing.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3006")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @GetMapping("/owners")
    public List<Owner> getOwners() {
        return new ArrayList<>(DataStorage.OWNERS.values());
    }

    @PostMapping("/owners")
    public Owner createOwner(@RequestBody Owner owner) {
        owner.setId("O" + System.currentTimeMillis());
        owner.setCreateTime(System.currentTimeMillis());
        DataStorage.OWNERS.put(owner.getId(), owner);
        return owner;
    }

    @GetMapping("/houses")
    public List<House> getHouses() {
        return new ArrayList<>(DataStorage.HOUSES.values());
    }

    @PostMapping("/houses")
    public House createHouse(@RequestBody House house) {
        house.setId("H" + System.currentTimeMillis());
        house.setCreateTime(System.currentTimeMillis());
        DataStorage.HOUSES.put(house.getId(), house);
        return house;
    }

    @PostMapping("/houses/{houseId}/bind-owner")
    public House bindOwner(@PathVariable String houseId, @RequestParam String ownerId) {
        House house = DataStorage.HOUSES.get(houseId);
        if (house == null) throw new RuntimeException("房屋不存在");
        house.setOwnerId(ownerId);
        DataStorage.HOUSES.put(houseId, house);
        return house;
    }

    @PostMapping("/bills/generate")
    public List<Bill> generateBills(@RequestParam String houseId, @RequestParam int year, @RequestParam int month) {
        return billingService.generateMonthlyBills(houseId, year, month);
    }

    @GetMapping("/bills")
    public List<Bill> getBills(@RequestParam(required = false) String ownerId) {
        if (ownerId != null) {
            return billingService.getOwnerBills(ownerId);
        }
        return new ArrayList<>(DataStorage.BILLS.values());
    }

    @PostMapping("/payments")
    public Payment makePayment(@RequestParam String ownerId, @RequestParam BigDecimal amount) {
        return billingService.processPayment(ownerId, amount);
    }

    @GetMapping("/payments")
    public List<Payment> getPayments(@RequestParam(required = false) String ownerId) {
        if (ownerId != null) {
            return billingService.getOwnerPayments(ownerId);
        }
        return new ArrayList<>(DataStorage.PAYMENTS.values());
    }

    @PostMapping("/discounts/apply")
    public DiscountApplication applyDiscount(@RequestParam String ownerId, @RequestParam String billId,
                                             @RequestParam BigDecimal amount, @RequestParam String reason) {
        return billingService.applyDiscount(ownerId, billId, amount, reason);
    }

    @PostMapping("/discounts/{appId}/approve")
    public DiscountApplication approveDiscount(@PathVariable String appId, @RequestParam String approver,
                                                @RequestParam String remark, @RequestParam boolean approved) {
        return billingService.approveDiscount(appId, approver, remark, approved);
    }

    @GetMapping("/discounts")
    public List<DiscountApplication> getDiscounts(@RequestParam(required = false) String ownerId) {
        if (ownerId != null) {
            return billingService.getOwnerDiscounts(ownerId);
        }
        return new ArrayList<>(DataStorage.DISCOUNT_APPLICATIONS.values());
    }

    @PostMapping("/owners/{ownerId}/blacklist")
    public void setBlacklist(@PathVariable String ownerId, @RequestParam boolean blacklisted) {
        billingService.setBlacklist(ownerId, blacklisted);
    }

    @PostMapping("/calculate-late-fees")
    public void calculateLateFees() {
        billingService.calculateLateFees();
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("ownerCount", DataStorage.OWNERS.size());
        stats.put("houseCount", DataStorage.HOUSES.size());
        stats.put("billCount", DataStorage.BILLS.size());
        stats.put("paymentCount", DataStorage.PAYMENTS.size());
        stats.put("unpaidBillCount", DataStorage.BILLS.values().stream()
                .filter(b -> b.getStatus() != BillStatus.PAID).count());
        return stats;
    }
}
