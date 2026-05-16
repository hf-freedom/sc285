package com.property.billing.service;

import com.property.billing.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BillingService {

    public Bill generateBill(String houseId, FeeType feeType, int year, int month) {
        House house = DataStorage.HOUSES.get(houseId);
        if (house == null || house.getOwnerId() == null) {
            throw new RuntimeException("房屋不存在或未绑定业主");
        }

        String billId = "B" + System.currentTimeMillis() + new Random().nextInt(1000);
        Bill bill = new Bill();
        bill.setId(billId);
        bill.setHouseId(houseId);
        bill.setOwnerId(house.getOwnerId());
        bill.setFeeType(feeType);
        bill.setBillingYear(year);
        bill.setBillingMonth(month);

        BigDecimal amount = calculateAmount(house, feeType);
        bill.setTotalAmount(amount);
        bill.setUnpaidAmount(amount);
        bill.setStatus(BillStatus.UNPAID);

        LocalDate dueDate = LocalDate.of(year, month, 1).plusMonths(1).withDayOfMonth(15);
        bill.setDueDate(dueDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        bill.setCreateTime(System.currentTimeMillis());

        DataStorage.BILLS.put(billId, bill);
        return bill;
    }

    public List<Bill> generateMonthlyBills(String houseId, int year, int month) {
        List<Bill> bills = new ArrayList<>();
        House house = DataStorage.HOUSES.get(houseId);
        if (house == null || house.getOwnerId() == null) {
            return bills;
        }

        bills.add(generateBill(houseId, FeeType.PROPERTY_FEE, year, month));
        bills.add(generateBill(houseId, FeeType.WATER_FEE, year, month));
        bills.add(generateBill(houseId, FeeType.ELECTRICITY_FEE, year, month));
        if (house.isHasParkingSpace()) {
            bills.add(generateBill(houseId, FeeType.PARKING_FEE, year, month));
        }
        return bills;
    }

    private BigDecimal calculateAmount(House house, FeeType feeType) {
        Double rate = DataStorage.FEE_RATES.get(feeType.name());
        if (rate == null) return BigDecimal.ZERO;

        switch (feeType) {
            case PROPERTY_FEE:
                return BigDecimal.valueOf(house.getArea()).multiply(BigDecimal.valueOf(rate))
                        .setScale(2, RoundingMode.HALF_UP);
            case WATER_FEE:
                return BigDecimal.valueOf(10).multiply(BigDecimal.valueOf(rate))
                        .setScale(2, RoundingMode.HALF_UP);
            case ELECTRICITY_FEE:
                return BigDecimal.valueOf(200).multiply(BigDecimal.valueOf(rate))
                        .setScale(2, RoundingMode.HALF_UP);
            case PARKING_FEE:
                return BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_UP);
            default:
                return BigDecimal.ZERO;
        }
    }

    public Payment processPayment(String ownerId, BigDecimal amount) {
        Owner owner = DataStorage.OWNERS.get(ownerId);
        if (owner == null) {
            throw new RuntimeException("业主不存在");
        }

        List<Bill> unpaidBills = DataStorage.BILLS.values().stream()
                .filter(b -> b.getOwnerId().equals(ownerId))
                .filter(b -> b.getStatus() != BillStatus.PAID)
                .sorted(Comparator.comparingInt(b -> b.getFeeType().getPriority()))
                .collect(Collectors.toList());

        BigDecimal remaining = amount;
        List<String> paidBillIds = new ArrayList<>();

        for (Bill bill : unpaidBills) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal toPay = remaining.min(bill.getUnpaidAmount());
            bill.setPaidAmount(bill.getPaidAmount().add(toPay));
            bill.setUnpaidAmount(bill.getUnpaidAmount().subtract(toPay));
            remaining = remaining.subtract(toPay);

            if (bill.getUnpaidAmount().compareTo(BigDecimal.ZERO) == 0) {
                bill.setStatus(BillStatus.PAID);
            } else {
                bill.setStatus(BillStatus.PARTIAL_PAID);
            }

            paidBillIds.add(bill.getId());
            DataStorage.BILLS.put(bill.getId(), bill);
        }

        String paymentId = "P" + System.currentTimeMillis() + new Random().nextInt(1000);
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setOwnerId(ownerId);
        payment.setTotalAmount(amount.subtract(remaining));
        payment.setBillIds(paidBillIds);
        payment.setPayTime(System.currentTimeMillis());

        DataStorage.PAYMENTS.put(paymentId, payment);
        return payment;
    }

    public void calculateLateFees() {
        long now = System.currentTimeMillis();
        for (Bill bill : DataStorage.BILLS.values()) {
            if (bill.getStatus() == BillStatus.PAID || bill.getDueDate() == null) {
                continue;
            }

            if (now > bill.getDueDate()) {
                long daysOverdue = (now - bill.getDueDate()) / (1000 * 60 * 60 * 24);
                if (daysOverdue > 0) {
                    BigDecimal lateFee = bill.getUnpaidAmount()
                            .multiply(BigDecimal.valueOf(0.005))
                            .multiply(BigDecimal.valueOf(daysOverdue))
                            .setScale(2, RoundingMode.HALF_UP);
                    bill.setLateFee(lateFee);
                    bill.setStatus(BillStatus.OVERDUE);
                    DataStorage.BILLS.put(bill.getId(), bill);
                }
            }
        }
    }

    public DiscountApplication applyDiscount(String ownerId, String billId, BigDecimal amount, String reason) {
        Owner owner = DataStorage.OWNERS.get(ownerId);
        Bill bill = DataStorage.BILLS.get(billId);
        if (owner == null || bill == null) {
            throw new RuntimeException("业主或账单不存在");
        }
        if (!bill.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("账单不属于该业主");
        }

        String appId = "D" + System.currentTimeMillis() + new Random().nextInt(1000);
        DiscountApplication app = new DiscountApplication();
        app.setId(appId);
        app.setOwnerId(ownerId);
        app.setBillId(billId);
        app.setDiscountAmount(amount);
        app.setReason(reason);
        app.setStatus(ApprovalStatus.PENDING);
        app.setCreateTime(System.currentTimeMillis());

        DataStorage.DISCOUNT_APPLICATIONS.put(appId, app);
        return app;
    }

    public DiscountApplication approveDiscount(String appId, String approver, String remark, boolean approved) {
        DiscountApplication app = DataStorage.DISCOUNT_APPLICATIONS.get(appId);
        if (app == null) {
            throw new RuntimeException("申请不存在");
        }

        app.setStatus(approved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED);
        app.setApprover(approver);
        app.setApprovalRemark(remark);
        app.setApprovalTime(System.currentTimeMillis());

        if (approved) {
            Bill bill = DataStorage.BILLS.get(app.getBillId());
            if (bill != null) {
                bill.setTotalAmount(bill.getTotalAmount().subtract(app.getDiscountAmount()));
                bill.setUnpaidAmount(bill.getUnpaidAmount().subtract(app.getDiscountAmount()));
                if (bill.getUnpaidAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    bill.setStatus(BillStatus.PAID);
                }
                DataStorage.BILLS.put(bill.getId(), bill);
            }
        }

        DataStorage.DISCOUNT_APPLICATIONS.put(appId, app);
        return app;
    }

    public void setBlacklist(String ownerId, boolean blacklisted) {
        Owner owner = DataStorage.OWNERS.get(ownerId);
        if (owner == null) {
            throw new RuntimeException("业主不存在");
        }
        owner.setBlacklisted(blacklisted);
        if (blacklisted) {
            owner.setRestrictedServices(Arrays.asList("装修申请", "停车延期", "报修服务"));
        } else {
            owner.setRestrictedServices(new ArrayList<>());
        }
        DataStorage.OWNERS.put(ownerId, owner);
    }

    public List<Bill> getOwnerBills(String ownerId) {
        return DataStorage.BILLS.values().stream()
                .filter(b -> b.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public List<Payment> getOwnerPayments(String ownerId) {
        return DataStorage.PAYMENTS.values().stream()
                .filter(p -> p.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public List<DiscountApplication> getOwnerDiscounts(String ownerId) {
        return DataStorage.DISCOUNT_APPLICATIONS.values().stream()
                .filter(d -> d.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }
}
