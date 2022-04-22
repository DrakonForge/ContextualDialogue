package io.github.drakonkinst.contextualdialogue.commonutil;

//https://stackoverflow.com/questions/1068760/can-i-pass-parameters-by-reference-in-java
public class Ref<T> {

    private T value;

    public Ref() {
        this.value = null;
    }
    
    public Ref(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T anotherValue) {
        value = anotherValue;
    }
    
    public boolean exists() {
        return value != null;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return value.equals(obj);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}