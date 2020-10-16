package sonar.fluxnetworks.api.network;

import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.api.text.FluxTranslate;

import javax.annotation.Nonnull;

public class NetworkSecurity {

    private Type type = Type.PUBLIC;

    @Nonnull
    private String password = "";

    public NetworkSecurity() {

    }

    public void set(Type type, @Nonnull String password) {
        this.type = type;
        this.password = password;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
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
        return type != Type.PUBLIC;
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
        type = Type.values()[tag.getByte("type")];
        password = tag.getString("password");
    }

    public enum Type {
        PUBLIC(FluxTranslate.PUBLIC),
        ENCRYPTED(FluxTranslate.ENCRYPTED),
        PRIVATE(FluxTranslate.PRIVATE);

        private final FluxTranslate localization;

        Type(FluxTranslate localization) {
            this.localization = localization;
        }

        @Nonnull
        public String getName() {
            return localization.t();
        }
    }
}
