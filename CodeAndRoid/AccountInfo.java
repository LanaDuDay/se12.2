package com.example.myapplication;

import java.util.List;

public class AccountInfo {
    private String accountType;
    private List<BalanceInfo> balances;

    // Thêm các trường thông tin tài khoản khác nếu cần

    public AccountInfo(String accountType, List<BalanceInfo> balances) {
        this.accountType = accountType;
        this.balances = balances;
    }

    // Thêm getter và setter nếu cần
    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public List<BalanceInfo> getBalances() {
        return balances;
    }

    public void setBalances(List<BalanceInfo> balances) {
        this.balances = balances;
    }

    // Tạo một lớp mới để đại diện cho thông tin số dư
    public static class BalanceInfo {
        private String asset;
        private String free;
        private String locked;

        // Thêm getter và setter nếu cần
        public String getAsset() {
            return asset;
        }

        public void setAsset(String asset) {
            this.asset = asset;
        }

        public String getFree() {
            return free;
        }

        public void setFree(String free) {
            this.free = free;
        }

        public String getLocked() {
            return locked;
        }

        public void setLocked(String locked) {
            this.locked = locked;
        }
    }
}
