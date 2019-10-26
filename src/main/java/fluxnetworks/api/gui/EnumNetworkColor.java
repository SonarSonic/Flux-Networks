package fluxnetworks.api.gui;

public enum EnumNetworkColor {
    flux1(0x295e8a), flux2(0x343477), flux3(0x582a72), flux4(0x882d60), flux5(0xaa3939), flux6(0xaa6f39),
    flux7(0xc6b900), flux8(0x609732), lightBlue(0x87cefa), lilac(0x86608e), lightCoral(0xf08080), pink(0xffc0cb),
    peach(0xffdab9), flax(0xeedc82);

    public int color;

    EnumNetworkColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
