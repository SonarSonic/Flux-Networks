package sonar.fluxnetworks.api.gui;

public enum EnumNetworkColor {
    blue(0x295e8a), indigo(0x343477), purple(0x582a72), pink(0x882d60), red(0xaa3939), brown(0xaa6f39),
    yellow(0xc6b900), green(0x609732), lightBlue(0x87cefa), lilac(0x86608e), lightCoral(0xf08080), lightPink(0xffc0cb),
    peach(0xffdab9), flax(0xeedc82);

    public int color;

    EnumNetworkColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
