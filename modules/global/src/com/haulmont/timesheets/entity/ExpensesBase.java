/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.entity;

import java.util.Set;

public interface ExpensesBase {

    Task getTask();
    ActivityType getActivityType();
    Set<Tag> getTags();



}
