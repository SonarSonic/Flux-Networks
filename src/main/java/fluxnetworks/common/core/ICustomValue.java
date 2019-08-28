package fluxnetworks.common.core;

public interface ICustomValue<T> {

    T getValue();

    void setValue(T set);
}
