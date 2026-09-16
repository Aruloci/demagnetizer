package com.aruloci.demagnetizer.zone;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class DemagnetizerZones {
    private static final Map<Level, Map<BlockPos, Integer>> LEVELS = new WeakHashMap<>();

    private DemagnetizerZones() {
    }

    public static void put(Level level, BlockPos pos, int range) {
        LEVELS.computeIfAbsent(level, l -> new HashMap<>()).put(pos.immutable(), range);
    }

    public static void remove(Level level, BlockPos pos) {
        Map<BlockPos, Integer> zones = LEVELS.get(level);
        if (zones != null) {
            zones.remove(pos);
        }
    }

    public static boolean isDemagnetized(Level level, BlockPos pos) {
        Map<BlockPos, Integer> zones = LEVELS.get(level);
        return zones != null && contains(zones, pos);
    }

    public static boolean isDemagnetized(Level level, Vec3 pos) {
        return isDemagnetized(level, BlockPos.containing(pos));
    }

    static boolean contains(Map<BlockPos, Integer> zones, BlockPos pos) {
        for (Map.Entry<BlockPos, Integer> zone : zones.entrySet()) {
            BlockPos center = zone.getKey();
            int range = zone.getValue();
            if (Math.abs(pos.getX() - center.getX()) <= range
                    && Math.abs(pos.getY() - center.getY()) <= range
                    && Math.abs(pos.getZ() - center.getZ()) <= range) {
                return true;
            }
        }
        return false;
    }
}
