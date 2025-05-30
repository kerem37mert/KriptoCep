package com.example.kriptocep;

import com.google.gson.annotations.SerializedName;

public class GlobalData {

    @SerializedName("coins_count")
    public int coins_count;

    @SerializedName("active_markets")
    public int active_markets;

    @SerializedName("total_mcap")
    public double total_mcap;

    @SerializedName("total_volume")
    public double total_volume;

    @SerializedName("btc_d")
    public String btc_d;

    @SerializedName("eth_d")
    public String eth_d;

    @SerializedName("mcap_change")
    public String mcap_change;

    @SerializedName("volume_change")
    public String volume_change;

    @SerializedName("avg_change_percent")
    public String avg_change_percent;

    @SerializedName("volume_ath")
    public double volume_ath;

    @SerializedName("mcap_ath")
    public double mcap_ath;
}
