package sonar.fluxnetworks.common.test;

@Deprecated
public interface ICustomValue<T> {

    T getValue();

    void setValue(T set);
}
