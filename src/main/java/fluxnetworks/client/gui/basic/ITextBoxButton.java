package fluxnetworks.client.gui.basic;

import fluxnetworks.client.gui.button.TextboxButton;

public interface ITextBoxButton {

    default void onTextBoxChanged(TextboxButton text) {}
}
