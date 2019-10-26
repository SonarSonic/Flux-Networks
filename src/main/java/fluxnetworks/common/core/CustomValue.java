package fluxnetworks.common.core;

import fluxnetworks.api.utils.ICustomValue;

public class CustomValue<T> implements ICustomValue<T> {

    public T value;

    public CustomValue(T value){
        setValue(value);
    }

    public CustomValue() {

    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T set) {
        if(value == null || (set != null && !set.equals(value))) {
            value = set;
        }
    }
}
