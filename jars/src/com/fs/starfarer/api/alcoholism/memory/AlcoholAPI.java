package com.fs.starfarer.api.alcoholism.memory;

import com.fs.starfarer.api.ui.TooltipMakerAPI;

public interface AlcoholAPI {
    String getId();
    AddictionStatus getAddictionStatus();
    void addEffectTooltip(TooltipMakerAPI tt, boolean forHullmod);
    void addStatusTooltip(TooltipMakerAPI tt);
}
