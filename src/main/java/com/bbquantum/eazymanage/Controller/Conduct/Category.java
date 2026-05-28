package com.bbquantum.eazymanage.Controller.Conduct;

import com.bbquantum.eazymanage.DTO.Academics.StudentConductRecord;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class Category {

    private final String label;

    private final Function<StudentConductRecord, String> getter;

    private final BiConsumer<StudentConductRecord, String> setter;

    public Category(String label,
                    Function<StudentConductRecord, String> getter,
                    BiConsumer<StudentConductRecord, String> setter) {
        this.label = label;
        this.getter = getter;
        this.setter = setter;
    }

    public String getLabel() {
        return label;
    }

    public Function<StudentConductRecord, String> getGetter() {
        return getter;
    }

    public BiConsumer<StudentConductRecord, String> getSetter() {
        return setter;
    }
}
