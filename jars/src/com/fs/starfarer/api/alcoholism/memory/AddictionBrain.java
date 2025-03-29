package com.fs.starfarer.api.alcoholism.memory;

import com.fs.starfarer.api.Global;

/**
 * literally 5head
 * Devmode adds 10 days of addiction each day, check AlcoholConsumptionManager
 */

public class AddictionBrain {
    public static float getBaseConsumptionPerCrewPerDay() {
        return (1 / SettingsHandler.getInstance().daysPerMonth) / SettingsHandler.getInstance().crewPerUnitPerMonth;
    }

    //root function increase - this is the basis that everything is calculated off
    public static float getAddictionForDays(float mult, float days) {
        return (float) (mult * Math.sqrt((SettingsHandler.getInstance().baseAddictionGainPerDay * Math.max(0f, days)) / 2));
    }

    //above root function, solved for "days"
    public static float getDaysAddicted(float mult, float addiction) {
        return (float) ((2f * Math.pow(addiction, 2)) / (Math.pow(mult, 2) * SettingsHandler.getInstance().baseAddictionGainPerDay));
    }

    //consumption per day per crew member, for a certain addiction level
    public static float getConsumptionPerCrewPerDayForAddiction(float addiction) {
        return (1 + addiction) * getBaseConsumptionPerCrewPerDay();
    }

    //difference between current addiction and the addiction in X days
    public static float getAddictionIncrease(float mult, float currentAddiction, float days){
        float daysAddictet = getDaysAddicted(mult, currentAddiction);
        float addictionInDays = getAddictionForDays(mult, days + daysAddictet);
        return addictionInDays - currentAddiction;
    }
}
