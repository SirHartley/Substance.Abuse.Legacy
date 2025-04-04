package com.fs.starfarer.api.alcoholism.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.alcoholism.ModPlugin;
import com.fs.starfarer.api.alcoholism.TooltipHelper;
import com.fs.starfarer.api.alcoholism.memory.*;
import com.fs.starfarer.api.alcoholism.scripts.BlackoutScript;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Does not drink responsibly
 */

public class AlcoholConsumptionManager implements NewDayListener, RefitTabListener {

    public float unitsConsumed = 0f;

    public Map<String, Integer> warningMap = new HashMap<>();

    //transient
    public static AlcoholConsumptionManager getInstanceOrRegister() {

        for (AlcoholConsumptionManager manager : Global.getSector().getListenerManager().getListeners(AlcoholConsumptionManager.class)) {
            ModPlugin.log("AlcoholConsumptionManager is present");
            return manager;
        }

        ModPlugin.log("creating new AlcoholConsumptionManager instance");

        AlcoholConsumptionManager manager = new AlcoholConsumptionManager();
        Global.getSector().getListenerManager().addListener(manager, true);
        return manager;
    }

    private void devActions(){
        AddictionMemory memory = AddictionMemory.getInstanceOrRegister();
        for (Alcohol alcohol : AlcoholRepo.ALCOHOL_MAP.values()){
            ModPlugin.log(alcohol.getName() + " " + memory.getStatusForId(alcohol.getId()).getAddictionValue());
        }

        ModPlugin.log("total addiction gain " + memory.getTotaladdictionGainLastMonth());
        ModPlugin.log("current crew " + getPlayerSkeletonCrewAmount());
    }

    @Override
    public void onNewDay() {

        boolean isDevmode = Global.getSettings().isDevMode();
        if(isDevmode) devActions();

        CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
        CargoAPI cargo = fleet.getCargo();
        AddictionMemory memory = AddictionMemory.getInstanceOrRegister();

        applyActiveAlcoholHullmods(fleet);
        incrementWarningMapOneDay();

        float days = isDevmode ? 10f: 1f;
        float totalAddictionGain = 0f;

        for (Alcohol alcohol : AlcoholRepo.ALCOHOL_MAP.values()){

            AddictionStatus addictionStatus = alcohol.getAddictionStatus();
            String id = alcohol.getId();
            String commodityID = alcohol.getCommodityId();

            boolean isWater = id.equals(AlcoholRepo.WATER);
            float mult = 1f;

            if(addictionStatus.isConsuming()){
                float toConsume = Math.max(1f, getCurrentRequiredUnitsPerDay(alcohol));

                unitsConsumed += toConsume;
                ModPlugin.log(alcohol.getName() + " - consuming " + toConsume + " | total: " + unitsConsumed + " in " + AddictionBrain.getDaysAddicted(alcohol.getMult(), addictionStatus.getAddictionValue()) + " days");

                if(cargo.getCommodityQuantity(commodityID) >= toConsume){
                    if(!isWater && isConsumingWater()) mult -= getWaterMult();

                    cargo.removeCommodity(commodityID, toConsume);
                    totalAddictionGain += alcohol.incrementAddiction(days * mult); //this increments the addiction

                    if(!warningMap.containsKey(id) && cargo.getCommodityQuantity(commodityID) < TooltipHelper.getPredictedAmountRequiredForOneMonth(id)){
                        Global.getSector().getCampaignUI().addMessage("You will run out of %s within the next %s",
                                Misc.getTextColor(),
                                alcohol.getName(),
                                TooltipHelper.getAmountWillLastDays(alcohol, cargo.getCommodityQuantity(commodityID)) + " days.",
                                alcohol.getFaction().getColor(),
                                Misc.getNegativeHighlightColor());

                        warningMap.put(id, 14);
                    }

                } else {
                    addictionStatus.setConsuming(false);
                    Global.getSector().getCampaignUI().addMessage("You have run out of %s and are now %s", Misc.getTextColor(), alcohol.getName(), "in withdrawal.", alcohol.getFaction().getColor(), Misc.getNegativeHighlightColor());
                }

            } else if (addictionStatus.isWithdrawal()){
                if(!isWater && isConsumingWater()) mult += getWaterMult();
                alcohol.incrementAddiction(-days * mult);

                if(!addictionStatus.isAddicted()) Global.getSector().getCampaignUI().addMessage("You are no longer addicted to %s.", Misc.getTextColor(), alcohol.getName(), null, alcohol.getFaction().getColor(), null);
            }
        }

        memory.addTotalDailyAddictionGainToMemory(totalAddictionGain);
        reapply();

        //if all alcohols are consumed at once, this will take less than three days.
        if(memory.getTotaladdictionGainLastMonth() >= SettingsHandler.getInstance().monthlyAddictionGainLimitBeforeBlackout){
            BlackoutScript.register();
            AddictionMemory.getInstanceOrRegister().resetAddictionMap();
        }
    }

    public void reapply(){
        List<Alcohol> alcoholList = new ArrayList<>(AlcoholRepo.ALCOHOL_MAP.values());
        for(FleetMemberAPI m : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
            ShipVariantAPI variant = m.getVariant();

            for (Alcohol alcohol : alcoholList){
                String hullmodID = alcohol.getEffectHullmodId();

                if(variant.hasHullMod(hullmodID)) {

                    List<String> modules = variant.getModuleSlots();
                    for (String s : modules) {
                        variant.getModuleVariant(s).removeMod(hullmodID);
                        variant.getModuleVariant(s).removePermaMod(hullmodID);

                        if(alcohol.getAddictionStatus().isAddicted() || alcohol.getAddictionStatus().isConsuming()) variant.getModuleVariant(s).addPermaMod(hullmodID);
                    }

                    variant.removeMod(hullmodID);
                    variant.removePermaMod(hullmodID);

                    if(alcohol.getAddictionStatus().isAddicted() || alcohol.getAddictionStatus().isConsuming()) variant.addPermaMod(hullmodID);
                }
            }

            m.updateStats();
        }
    }

    public void incrementWarningMapOneDay(){
        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, Integer> e : warningMap.entrySet()){
            if(e.getValue() < 1) toRemove.add(e.getKey());
            e.setValue(e.getValue() - 1);
        }

        for (String s : toRemove) warningMap.remove(s);
    }

    public float getWaterMult(){
        return AlcoholRepo.get(AlcoholRepo.WATER).getAddictionStatus().getAddictionValue() * SettingsHandler.getInstance().waterMult;
    }

    public boolean isConsumingWater(){
        return AlcoholRepo.get(AlcoholRepo.WATER).getAddictionStatus().isConsuming();
    }

    public static float getPlayerSkeletonCrewAmount(){
        CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();

        float minCrew = fleet.getFleetData().getMinCrew();
        float crew = fleet.getCargo().getCrew();

        return Math.min(minCrew, crew);
    }

    public void applHullmodToFleet(String id) {
        for (FleetMemberAPI m : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
            ShipVariantAPI variant = m.getVariant();
            String hullmodID = AlcoholRepo.get(id).getEffectHullmodId();

            m.getVariant().addPermaMod(hullmodID);

            List<String> modules = variant.getModuleSlots();
            for (String s : modules) {
                variant.getModuleVariant(s).addPermaMod(hullmodID);
            }
        }
    }

    public void unapplyHullmodFromFleet(String id) {
        for (FleetMemberAPI m : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
            String hullmodID = AlcoholRepo.get(id).getEffectHullmodId();
            ShipVariantAPI variant = m.getVariant();

            if(!variant.getHullMods().contains(hullmodID)) continue;

            List<String> modules = variant.getModuleSlots();
            for (String s : modules) {
                variant.getModuleVariant(s).removeMod(hullmodID);
                variant.getModuleVariant(s).removePermaMod(hullmodID);
            }

            variant.removeMod(hullmodID);
            variant.removePermaMod(hullmodID);
        }
    }

    public static float getCurrentRequiredUnitsPerDay(Alcohol alcohol) {
        return AddictionBrain.getConsumptionPerCrewPerDayForAddiction(alcohol.getAddictionStatus().getAddictionValue()) * getPlayerSkeletonCrewAmount();
    }

    public void applyActiveAlcoholHullmods(CampaignFleetAPI fleet){
        for (AlcoholAPI alcoholAPI : AlcoholRepo.ALCOHOL_MAP.values()) {
            Alcohol alcohol = (Alcohol) alcoholAPI;
            if(!alcohol.getAddictionStatus().isAddicted()) continue;

            String hullmodID = alcohol.getEffectHullmodId();

            for (FleetMemberAPI m : fleet.getFleetData().getMembersListCopy()) {
                if (!m.getVariant().hasHullMod(hullmodID)) {
                    m.getVariant().addPermaMod(hullmodID);
                    m.updateStats();
                }
            }
        }
    }

    @Override
    public void reportRefitOpened(CampaignFleetAPI fleet) {
       reapply();
    }

    @Override
    public void reportRefitClosed(CampaignFleetAPI fleet) {

    }
}
