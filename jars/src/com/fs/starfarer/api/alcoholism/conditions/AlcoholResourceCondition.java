package com.fs.starfarer.api.alcoholism.conditions;

import com.fs.starfarer.api.alcoholism.industry.Brewery;
import com.fs.starfarer.api.alcoholism.memory.Alcohol;
import com.fs.starfarer.api.alcoholism.memory.AlcoholRepo;
import com.fs.starfarer.api.alcoholism.memory.FactionAlcoholHandler;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class AlcoholResourceCondition extends BaseMarketConditionPlugin {

    @Override
    public void apply(String id) {
        super.apply(id);

        if (market.isPlayerOwned()) {
            applyPlayerAlcohol();
            return;
        }

        applyAllNPCAlcohol();
    }

    @Override
    public void unapply(String id) {
        super.unapply(id);

        unapplyPlayer();
        unapplyNPC();
    }

    private void unapplyPlayer() {
        if (!market.isPlayerOwned()) return;

        Industry ind = market.getIndustry(Industries.POPULATION);

        for (Alcohol alcohol : AlcoholRepo.ALCOHOL_MAP.values()) {
            if(ind instanceof BaseIndustry){
                ((BaseIndustry) ind).demand(getModId(), alcohol.getCommodityId(), 0, "");
            }
        }
    }

    private void unapplyNPC() {
        if (market.isPlayerOwned()) return;

        Industry popInfra = market.getIndustry(Industries.POPULATION);
        Industry lightIndustry = market.getIndustry(Industries.LIGHTINDUSTRY);

        if (market.hasIndustry(Industries.POPULATION)) for (Alcohol alcohol : AlcoholRepo.ALCOHOL_MAP.values()) {
            ((BaseIndustry) popInfra).demand(getModId(), alcohol.getCommodityId(), 0, "");
        }

        if (market.hasIndustry(Industries.LIGHTINDUSTRY)) {
            lightIndustry.supply(getModId(), AlcoholRepo.convertID(AlcoholRepo.WATER), 0, "");

            for (Alcohol alcohol : getAlcoholForMarket()) {
                lightIndustry.supply(getModId(), alcohol.getCommodityId(), 0, "");

                for (String s : alcohol.getDemandsForProduction()) {
                    ((BaseIndustry) lightIndustry).demand(getModId(), s, 0, "");
                }
            }
        }
    }

    private void applyPlayerAlcohol() {
        List<String> exports = getPlayerBreweryExports();

        for (String s : exports) {
            applyDemand(Industries.POPULATION, -2, s);
        }

        if (!exports.isEmpty())
            applyDemand(Industries.POPULATION, -4, AlcoholRepo.get(AlcoholRepo.WATER).getCommodityId());
    }

    private List<String> getPlayerBreweryExports() {
        List<String> commodityList = new ArrayList<>();

        for (MarketAPI m : Misc.getPlayerMarkets(true)) {
            if (m.getId().equals(market.getId())) continue;
            if (m.hasIndustry(Brewery.INDUSTRY_ID)) {
                for (MutableCommodityQuantity q : m.getIndustry(Brewery.INDUSTRY_ID).getAllSupply()) {
                    commodityList.add(q.getCommodityId());
                }
            }
        }

        return commodityList;
    }

    private void applyAllNPCAlcohol() {
        for (Alcohol alcohol : getAlcoholForMarket()) {
            applyNPCAlcohol(alcohol);
        }

        if (market.getFactionId().equals(Factions.INDEPENDENT))
            applyNPCAlcohol(AlcoholRepo.get(AlcoholRepo.WATER)); //everyone loves heroin
        if (market.getSize() > 5)
            applyDemand(Industries.POPULATION, AlcoholRepo.get(AlcoholRepo.TEA).getPopulationImportMod(), AlcoholRepo.get(AlcoholRepo.TEA).getCommodityId()); //tea demand to large worlds
    }

    private void applyNPCAlcohol(Alcohol alcohol) {
        String commodityID = alcohol.getCommodityId();

        if (!market.hasIndustry(Brewery.INDUSTRY_ID)) {
            applyDemand(Industries.LIGHTINDUSTRY, alcohol.getLightIndustryMod(), alcohol.getDemandsForProduction());
            applySupply(Industries.LIGHTINDUSTRY, alcohol.getLightIndustryMod(), commodityID);
        }

        applyDemand(Industries.POPULATION, alcohol.getPopulationImportMod(), commodityID);
    }

    private void applyDemand(String industryID, int marketSizeMod, String... commodityIDs) {
        if (market.hasIndustry(industryID)) {
            BaseIndustry ind = (BaseIndustry) market.getIndustry(industryID);
            int amt = Math.max(0, market.getSize() + marketSizeMod);

            for (String s : commodityIDs) {
                //don't demand anyhing that the industry supplies in higher numbers
                if (ind.getSupply(s).getQuantity().getModifiedInt() >= amt) continue;
                applyDemand(ind, s, amt);
            }
        }
    }

    private void applySupply(String industryID, int marketSizeMod, String... commodityIDs) {
        if (market.hasIndustry(industryID)) {
            BaseIndustry ind = (BaseIndustry) market.getIndustry(industryID);
            int amt = Math.max(0, market.getSize() + marketSizeMod);

            for (String s : commodityIDs) {
                applySupply(ind, s, amt);
            }
        }
    }

    public static void applyDemand(BaseIndustry ind, String id, int amt) {
        if (amt == 0) return;
        ind.demand(id, amt);
    }

    public static void applySupply(BaseIndustry ind, String commodityId, int amt) {
        if (amt == 0) return;
        ind.supply(commodityId, amt);

        Pair<String, Integer> result = new Pair<String, Integer>();
        result.two = 0;

        for (MutableCommodityQuantity q : ind.getAllDemand()) {
            String id = q.getCommodityId();
            int demand = (int) ind.getDemand(id).getQuantity().getModifiedValue();
            CommodityOnMarketAPI com = ind.getMarket().getCommodityData(id);
            int available = com.getAvailable();

            int deficit = Math.max(demand - available, 0);
            if (deficit > result.two) {
                result.one = id;
                result.two = deficit;
            }
        }

        applyDeficitToProduction(ind, 3, result,
                commodityId);
    }

    private static void applyDeficitToProduction(Industry ind, int index, Pair<String, Integer> deficit, String... commodities) {
        String[] var7 = commodities;
        int var6 = commodities.length;

        for (int var5 = 0; var5 < var6; ++var5) {
            String commodity = var7[var5];
            if (!ind.getSupply(commodity).getQuantity().isUnmodified()) {
                ind.supply(String.valueOf(index), commodity, -(Integer) deficit.two, BaseIndustry.getDeficitText((String) deficit.one));
            }
        }
    }

    private List<Alcohol> getAlcoholForMarket() {
        String factionID = market.getFaction().getId();
        return FactionAlcoholHandler.getFactionAlcoholTypes(factionID);
    }

    @Override
    public boolean showIcon() {
        return false;
    }

    @Override
    public String getModId() {
        return condition.getId();
    }
}