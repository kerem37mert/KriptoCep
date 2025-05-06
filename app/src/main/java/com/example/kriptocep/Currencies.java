package com.example.kriptocep;

import com.google.gson.annotations.SerializedName;

public class Currencies {
    @SerializedName("id")
    public int id;
    @SerializedName("symbol")
    public String symbol;
    @SerializedName("name")
    public String name;
    @SerializedName("nameid")
    public String nameid;
    @SerializedName("rank")
    public int rank;
    @SerializedName("price_usd")
    public double price_usd;
    @SerializedName("percent_change_24h")
    public double percent_change_24h;
    @SerializedName("percent_change_1h")
    public double percent_change_1h;
    @SerializedName("percent_change_7d")
    public double percent_change_7d;
    @SerializedName("price_btc")
    public double price_btc;
    @SerializedName("market_cap_usd")
    public double market_cap_usd;
    @SerializedName("volume24")
    public double volume24;
    @SerializedName("volume24a")
    public double volume24a;
    @SerializedName("csupply")
    public String csupply;
    @SerializedName("tsupply")
    public String tsupply;
    @SerializedName("msupply")
    public String msupply;
}
