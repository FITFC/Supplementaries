package net.mehvahdjukaar.supplementaries.common.block.tiles;

import net.mehvahdjukaar.moonlight.api.block.IOwnerProtected;
import net.mehvahdjukaar.supplementaries.common.block.blocks.SpeakerBlock;
import net.mehvahdjukaar.supplementaries.common.network.ClientBoundPlaySpeakerMessagePacket;
import net.mehvahdjukaar.supplementaries.common.network.NetworkHandler;
import net.mehvahdjukaar.supplementaries.configs.CommonConfigs;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.mehvahdjukaar.supplementaries.reg.ModTextures;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SpeakerBlockTile extends BlockEntity implements Nameable, IOwnerProtected {
    private UUID owner = null;

    private String message = "";
    private Mode mode = Mode.CHAT;
    //distance in blocks
    private double volume = CommonConfigs.Blocks.SPEAKER_RANGE.get();
    private Component customName;

    public SpeakerBlockTile(BlockPos pos, BlockState state) {
        super(ModRegistry.SPEAKER_BLOCK_TILE.get(), pos, state);
    }

    public void setCustomName(Component name) {
        this.customName = name;
    }

    @Override
    public Component getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    @Override
    public Component getCustomName() {
        return this.customName;
    }

    public Component getDefaultName() {
        return Component.translatable("block.supplementaries.speaker_block");
    }

    public double getVolume() {
        return volume;
    }

    public Mode getMode() {
        return mode;
    }

    public String getMessage() {
        return message;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (compound.contains("CustomName", 8)) {
            this.customName = Component.Serializer.fromJson(compound.getString("CustomName"));
        }

        this.message = compound.getString("Message");
        var m = Mode.values()[compound.getInt("Mode")];
        if (m == Mode.NARRATOR && !CommonConfigs.Blocks.SPEAKER_NARRATOR.get()) m = Mode.CHAT;
        this.mode = m;
        this.volume = compound.getDouble("Volume");
        this.loadOwner(compound);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if (this.customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(this.customName));
        }
        compound.putString("Message", this.message);
        compound.putInt("Mode", this.mode.ordinal());
        compound.putDouble("Volume", this.volume);
        this.saveOwner(compound);
    }

    public void sendMessage() {
        BlockState state = this.getBlockState();

        if (level instanceof ServerLevel server && !this.message.equals("")) {
            // particle
            BlockPos pos = this.getBlockPos();
            level.blockEvent(pos, this.getBlockState().getBlock(), 0, 0);

            Style style = !state.getValue(SpeakerBlock.ANTIQUE) ? Style.EMPTY.applyFormats(ChatFormatting.ITALIC) :
                    Style.EMPTY.withFont(ModTextures.ANTIQUABLE_FONT).applyFormats(ChatFormatting.ITALIC);

            String name = this.getName().getString();
            String s = "";
            if (name.isEmpty()) {
                s = "Speaker Block: ";
            } else if (!name.equals("\"\"") && !name.equals("\"")) s += name + ": ";
            Component component = Component.literal(s + this.message)
                    .withStyle(style);

            NetworkHandler.CHANNEL.sendToAllClientPlayersInRange(server, pos,
                    this.volume, new ClientBoundPlaySpeakerMessagePacket(component, this.mode));

        }
    }

    @Nullable
    @Override
    public UUID getOwner() {
        return owner;
    }

    @Override
    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    //needed since we access tile directly on client when opening gui
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public enum Mode {
        CHAT,
        STATUS_MESSAGE,
        TITLE,
        NARRATOR
    }

}