package com.zrp200.rkpd2.utils;

import java.util.HashSet;

public class FunctionalStuff {
    public interface Func<T>{
        T ret();
    }

    public interface ConditionFunc<T>{
        boolean check(T type);
    }

    public static<E> HashSet<E> extract(HashSet<E> set, ConditionFunc<E> condition){
        HashSet<E> newSet = new HashSet<>();
        for (E element: set){
            if (condition.check(element)){
                newSet.add(element);
            }
        }
        return newSet;
    }
}
