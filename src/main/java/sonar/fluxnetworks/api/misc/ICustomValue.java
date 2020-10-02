package sonar.fluxnetworks.api.misc;

public interface ICustomValue<T> {

    T getValue();

    void setValue(T set);
}
