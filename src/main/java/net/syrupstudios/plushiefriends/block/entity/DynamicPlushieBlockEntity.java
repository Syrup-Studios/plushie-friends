package net.syrupstudios.plushiefriends.block.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.syrupstudios.plushiefriends.util.PlushieNbtHelper;
import net.syrupstudios.plushiefriends.util.PlushieProfileManager;
import org.jetbrains.annotations.Nullable;

public class DynamicPlushieBlockEntity extends BlockEntity {
    public static BlockEntityType<DynamicPlushieBlockEntity> TYPE;

    private GameProfile owner = null;
    private ListTag lore = new ListTag();
    private boolean isResolving = false;
    private final long creationTime = System.currentTimeMillis();

    public boolean isSafeToForceRender() {
        return (System.currentTimeMillis() - this.creationTime) > 250;
    }

    public DynamicPlushieBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    public void setOwner(@Nullable GameProfile profile) {
        this.owner = profile;
        this.setChanged();
    }

    @Nullable
    public GameProfile getOwner() {
        return this.owner;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.owner = PlushieNbtHelper.getOwnerFromBlockEntityTag(tag);

        if (tag.contains(PlushieNbtHelper.PLUSHIE_LORE, PlushieNbtHelper.TAG_LIST)) {
            this.lore = tag.getList(PlushieNbtHelper.PLUSHIE_LORE, PlushieNbtHelper.TAG_STRING);
        } else {
            this.lore = new ListTag();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.owner != null) {
            PlushieNbtHelper.writeOwnerToBlockEntityTag(tag, this.owner);
        }
        if (this.lore != null && !this.lore.isEmpty()) {
            tag.put(PlushieNbtHelper.PLUSHIE_LORE, this.lore);
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DynamicPlushieBlockEntity blockEntity) {
        if (blockEntity.owner == null || blockEntity.owner.getProperties().containsKey("textures")) {
            return;
        }

        GameProfile cachedProfile = PlushieProfileManager.getCachedProfile(blockEntity.owner.getName());
        if (cachedProfile != null && cachedProfile.getProperties().containsKey("textures")) {
            blockEntity.owner = cachedProfile;
            blockEntity.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
            return;
        }

        if (!blockEntity.isResolving && PlushieProfileManager.shouldAttemptResolution(blockEntity.owner.getName())) {
            blockEntity.isResolving = true;

            PlushieProfileManager.resolveProfileAsync(blockEntity.owner.getName(), profile -> {
                Runnable applyResult = () -> {
                    if (profile != null) {
                        blockEntity.owner = profile;
                        blockEntity.setChanged();
                        level.sendBlockUpdated(pos, state, state, 3);
                    }
                    blockEntity.isResolving = false;
                };
                if (level.getServer() != null) {
                    level.getServer().execute(applyResult);
                } else {
                    applyResult.run();
                }
            });
        }
    }
}
