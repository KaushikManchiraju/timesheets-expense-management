/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui.expenses;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.Expenses;
import com.haulmont.timesheets.gui.commandline.CommandLineFrameController;
import com.haulmont.timesheets.gui.util.ScreensHelper;
import com.haulmont.timesheets.gui.util.SecurityAssistant;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;

@UiController("ts_Expenses.browse")
@UiDescriptor("expenses-browse.xml")
@LookupComponent("expensesesTable")
//@LoadDataBeforeShow
public class ExpensesBrowse extends StandardLookup<Expenses> {
    @Inject
    protected SecurityAssistant securityAssistant;

    @Inject
    protected UserSession userSession;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected ScreenBuilders screenBuilders;

    @Inject
    protected CollectionContainer<Expenses> expensesesDc;
    @Inject
    protected CollectionLoader<Expenses> expensesesDl;
    @Inject
    protected GroupTable<Expenses> expensesesTable;
    @Inject
    protected CommandLineFrameController commandLine;

    @Subscribe("expensesesTable.create")
    protected void onExpensesesTableCreateActionPerformed(Action.ActionPerformedEvent e) {
        screenBuilders.editor(expensesesTable)
                .newEntity()
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show();
    }

    /*@Subscribe("expensesesTable.edit")
    protected void onExpensesesTableEditActionPerformed(Action.ActionPerformedEvent e) {
        screenBuilders.editor(expensesesTable)
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show();
    }*/




    @Install(to = "expensesesTable", subject = "styleProvider")
    protected String expensesesTableStyleProvider(Entity entity, String property) {
        if ("status".equals(property)) {
            Expenses expenses = (Expenses) entity;
            if (expenses == null) {
                return null;
            } else {
                return ScreensHelper.getExpensesStatusStyle(expenses);
            }
        }
        return null;
    }

    @Subscribe
    protected void onInit(InitEvent e) {
        commandLine.setExpensesHandler(resultExpenses -> {
            if (CollectionUtils.isNotEmpty(resultExpenses)) {
                screenBuilders.editor(expensesesTable)
                        .editEntity(resultExpenses.get(0))
                        .withScreenClass(ExpensesEdit.class)
                        .withLaunchMode(OpenMode.DIALOG)
                        .withAfterCloseListener(ace -> {
                            if ("commit".equalsIgnoreCase(((StandardCloseAction) ace.getCloseAction()).getActionId())) {
                                expensesesTable.setSelected(ace.getScreen().getEditedEntity());
                            }
                        })
                        .build()
                        .show();
            }
        });
    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        expensesesDl.setParameter("user", userSession.getUser());
        expensesesDl.load();
    }



}