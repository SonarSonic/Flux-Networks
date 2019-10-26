package fluxnetworks.api.utils;

public interface ICustomValue<T> {

    T getValue();

    void setValue(T set);
}
