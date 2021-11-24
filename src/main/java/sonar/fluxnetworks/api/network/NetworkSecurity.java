package sonar.fluxnetworks.api.network;

import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;

public class NetworkSecurity {

    @Nonnull
    private SecurityLevel mLevel = SecurityLevel.PUBLIC;

    @Nonnull
    private String mPassword = "";

    public NetworkSecurity() {
    }

    public void set(@Nonnull SecurityLevel type, @Nonnull String password) {
        mLevel = type;
        mPassword = password;
    }

    @Nonnull
    public SecurityLevel getLevel() {
        return mLevel;
    }

    public void setLevel(@Nonnull SecurityLevel level) {
        mLevel = level;
    }

    @Nonnull
    public String getPassword() {
        return mPassword;
    }

    public void setPassword(@Nonnull String password) {
        mPassword = password;
    }

    public boolean isEncrypted() {
        return mLevel != SecurityLevel.PUBLIC;
    }

    public void writeNBT(@Nonnull CompoundTag tag, boolean writePassword) {
        tag.putByte("level", mLevel.getId());
        if (writePassword) {
            tag.putString("password", mPassword);
        }
    }

    public void readNBT(@Nonnull CompoundTag tag) {
        mLevel = SecurityLevel.fromId(tag.getByte("level"));
        mPassword = tag.getString("password");
    }
}
