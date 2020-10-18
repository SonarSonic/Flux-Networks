package sonar.fluxnetworks.api.network;

import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class NetworkSecurity {

    private SecurityType type = SecurityType.PUBLIC;

    @Nonnull
    private String password = "";

    public NetworkSecurity() {

    }

    public void set(SecurityType type, @Nonnull String password) {
        this.type = type;
        this.password = password;
    }

    public SecurityType getType() {
        return type;
    }

    public void setType(SecurityType type) {
        this.type = type;
    }

    @Nonnull
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nonnull String password) {
        this.password = password;
    }

    public boolean isEncrypted() {
        return type != SecurityType.PUBLIC;
    }

    public void writeNBT(@Nonnull CompoundNBT nbt, boolean writePassword) {
        CompoundNBT tag = new CompoundNBT();
        tag.putByte("type", (byte) type.ordinal());
        if (writePassword)
            tag.putString("password", password);
        nbt.put("security", tag);
    }

    public void readNBT(@Nonnull CompoundNBT nbt) {
        CompoundNBT tag = nbt.getCompound("security");
        type = SecurityType.values()[tag.getByte("type")];
        password = tag.getString("password");
    }
}
