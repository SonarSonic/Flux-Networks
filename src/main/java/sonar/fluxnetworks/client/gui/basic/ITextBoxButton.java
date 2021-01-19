package sonar.fluxnetworks.client.gui.basic;

import sonar.fluxnetworks.client.gui.button.TextboxButton;

public interface ITextBoxButton {

    default void onTextBoxChanged(TextboxButton text) {}
}
