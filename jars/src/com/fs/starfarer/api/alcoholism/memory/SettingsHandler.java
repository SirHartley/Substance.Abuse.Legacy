package com.fs.starfarer.api.alcoholism.memory;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ModPlugin;
import lunalib.lunaSettings.LunaSettings;
import lunalib.lunaSettings.LunaSettingsListener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SettingsHandler implements LunaSettingsListener {
    public static final String KEY = "alcoholism_settings";

    public float daysPerMonth = 30f;
    public float crewPerUnitPerMonth;
    public float baseAddictionGainPerDay;
    public float monthlyAddictionGainLimitBeforeBlackout;
    public float waterMult;
    public boolean adjustOP;

    public SettingsHandler (){
        settingsChanged("alcoholism");
    }

    public static SettingsHandler getInstance(){
        SettingsHandler handler;
        Map<String, Object> persData = Global.getSector().getPersistentData();

        if (persData.containsKey(KEY)) handler = (SettingsHandler) persData.get(KEY);
        else {
            handler = new SettingsHandler();
            persData.put(KEY, handler);
        }

        if (!LunaSettings.hasSettingsListenerOfClass(SettingsHandler.class)) LunaSettings.addSettingsListener(handler);

        return handler;
    }

    @Override
    public void settingsChanged(@NotNull String s) {
        if (s.equals("alcoholism")){
            crewPerUnitPerMonth = LunaSettings.getFloat("alcoholism", "alcoholism_CREW_PER_UNIT_PER_MONTH");
            baseAddictionGainPerDay = LunaSettings.getFloat("alcoholism", "alcoholism_BASE_ADDICTION_GAIN_PER_DAY");
            monthlyAddictionGainLimitBeforeBlackout = LunaSettings.getFloat("alcoholism", "alcoholism_MONTHLY_ADDICTION_GAIN_LIMIT_BEFORE_BLACKOUT");
            waterMult = LunaSettings.getFloat("alcoholism", "alcoholism_WATER_MULT");
            adjustOP = LunaSettings.getBoolean("alcoholism", "alcoholism_ADJUST_OP_WHEN_OVER_LIMIT");

            Global.getLogger(SettingsHandler.class).info("crewPerUnitPerMonth " + crewPerUnitPerMonth + " / " +
                    "baseAddictionGainPerDay " + baseAddictionGainPerDay + " / " +
                    "monthlyAddictionGainLimitBeforeBlackout " + monthlyAddictionGainLimitBeforeBlackout + " / " +
                    "waterMult " + waterMult + " / " +
                    "adjustOP " + adjustOP);
        }
    }
}
