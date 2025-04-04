package com.fs.starfarer.api.alcoholism.memory;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;

import java.util.ArrayList;
import java.util.List;

public class FactionAlcoholHandler {
    //alcohol bound to faction, not faction to alcohol
    //need to specify import/export profiles in the alcohol repo

    public static final String FACTION_ALCOHOL_KEY = "$alcoholism_faction_alcohol_types";

    public static void assignFactionAlcohols() {
        setFactionAlcoholTypes(Factions.HEGEMONY, AlcoholRepo.STOUT);
        setFactionAlcoholTypes(Factions.TRITACHYON, AlcoholRepo.ABSYNTH);
        setFactionAlcoholTypes(Factions.LUDDIC_CHURCH, AlcoholRepo.TEARS);
        setFactionAlcoholTypes(Factions.LUDDIC_PATH, AlcoholRepo.FUEL);
        setFactionAlcoholTypes(Factions.PIRATES, AlcoholRepo.BLOOD);
        setFactionAlcoholTypes(Factions.DIKTAT, AlcoholRepo.SUNSHINE);
        setFactionAlcoholTypes(Factions.PERSEAN, AlcoholRepo.KING);
        setFactionAlcoholTypes(Factions.INDEPENDENT, AlcoholRepo.FREEDOM, AlcoholRepo.TEA);
    }

    public static void setFactionAlcoholTypes(String factionID, String... alcoholIDs) {
        MemoryAPI mem = Global.getSector().getFaction(factionID).getMemoryWithoutUpdate();
        mem.set(FACTION_ALCOHOL_KEY, alcoholIDs);
    }

    public static List<Alcohol> getFactionAlcoholTypes(String factionID) {
        MemoryAPI mem = Global.getSector().getFaction(factionID).getMemoryWithoutUpdate();
        List<Alcohol> alcoholList = new ArrayList<>();

        if(mem.contains(FACTION_ALCOHOL_KEY)){
            for (String id : (String[]) mem.get(FACTION_ALCOHOL_KEY)) {
                alcoholList.add(AlcoholRepo.get(id));
            }
        }

        return alcoholList;
    }
}
