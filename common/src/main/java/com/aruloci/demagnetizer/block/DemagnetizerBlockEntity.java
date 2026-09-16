package com.aruloci.demagnetizer.block;

import com.aruloci.demagnetizer.DemagnetizerMod;
import com.aruloci.demagnetizer.zone.DemagnetizerZones;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DemagnetizerBlockEntity extends BlockEntity {
    public static final int MIN_RANGE = 1;
    public static final int MAX_RANGE = 64;
    public static final int DEFAULT_RANGE = 8;
    private static final String RANGE_TAG = "Range";

    private int range = DEFAULT_RANGE;

    public DemagnetizerBlockEntity(BlockPos pos, BlockState state) {
        super(DemagnetizerMod.BLOCK_ENTITY.get(), pos, state);
    }

    public int getRange() {
        return range;
    }

    public void setRange(int newRange) {
        newRange = Mth.clamp(newRange, MIN_RANGE, MAX_RANGE);
        if (newRange == range) {
            return;
        }
        range = newRange;
        syncZone();
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    public void syncZone() {
        if (level == null || level.isClientSide()) {
            return;
        }
        if (DemagnetizerBlock.isActive(getBlockState())) {
            DemagnetizerZones.put(level, worldPosition, range);
        } else {
            DemagnetizerZones.remove(level, worldPosition);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(RANGE_TAG, range);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        range = tag.contains(RANGE_TAG, Tag.TAG_INT) ? Mth.clamp(tag.getInt(RANGE_TAG), MIN_RANGE, MAX_RANGE) : DEFAULT_RANGE;
        if (!isRemoved()) {
            syncZone();
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        syncZone();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level != null && !level.isClientSide()) {
            DemagnetizerZones.remove(level, worldPosition);
        }
    }
}
