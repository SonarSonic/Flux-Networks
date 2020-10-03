package sonar.fluxnetworks.api.misc;

@Deprecated
public interface ICustomValue<T> {

    T getValue();

    void setValue(T set);
}
