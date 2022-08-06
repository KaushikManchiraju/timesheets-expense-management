/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ExpensesStatus implements EnumClass<String> {

    NEW("new"),
    APPROVED("approved"),
    REJECTED("rejected"),
    CLOSED("closed");

    private String id;

    ExpensesStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ExpensesStatus fromId(String id) {
        for (ExpensesStatus at : ExpensesStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}