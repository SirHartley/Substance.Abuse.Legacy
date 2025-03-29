package com.fs.starfarer.api.alcoholism.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.alcoholism.ModPlugin;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.campaign.listeners.PlayerColonizationListener;

public class ResourceConditionApplicator implements PlayerColonizationListener, EconomyTickListener {

    public static final String COND_RESSOURCES = "alcohol_resourceCondition";

    @Override
    public void reportPlayerColonizedPlanet(PlanetAPI planetAPI) {
        MarketAPI m = planetAPI.getMarket();
        applyRessourceCond(m);
    }

    @Override
    public void reportPlayerAbandonedColony(MarketAPI marketAPI) {

    }

    @Override
    public void reportEconomyTick(int i) {
        applyRessourceCondToAllMarkets();
    }

    @Override
    public void reportEconomyMonthEnd() {

    }

    private void applyRessourceCondToAllMarkets() {
        for (MarketAPI m : Global.getSector().getEconomy().getMarketsCopy()) {
            applyRessourceCond(m);
        }
    }

    private void applyRessourceCond(MarketAPI m) {
        if (m.isInEconomy() && !m.hasCondition(COND_RESSOURCES)) m.addCondition(COND_RESSOURCES);
    }

    //transient
    public static void register() {
        ListenerManagerAPI manager = Global.getSector().getListenerManager();
        if(!manager.hasListenerOfClass(ResourceConditionApplicator.class)) {
            ModPlugin.log("creating ResourceConditionApplicator instance");

            ResourceConditionApplicator listener = new ResourceConditionApplicator();
            manager.addListener(listener, true);
            listener.applyRessourceCondToAllMarkets();
        }
    }
}
