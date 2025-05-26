package com.example.kriptocep;

public class WalletCoinItem {
    public int coinID;
    public String coinName;
    public String coinSymbol;
    public double netAmount;
    public double currentPrice;
    public double currentValue;
    public double profit;
    //public double profitPercentage;

    public WalletCoinItem(int coinID, String coinName, String coinSymbol, double netAmount, double currentPrice, double currentValue, double profit) {
        this.coinID = coinID;
        this.coinName = coinName;
        this.coinSymbol = coinSymbol;
        this.netAmount = netAmount;
        this.currentPrice = currentPrice;
        this.currentValue = currentValue;
        this.profit = profit;
        //this.profitPercentage = profitPercentage;
    }
}
