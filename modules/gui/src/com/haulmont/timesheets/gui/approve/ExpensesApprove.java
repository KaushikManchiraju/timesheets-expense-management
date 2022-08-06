/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui.approve;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.global.*;
import com.haulmont.timesheets.gui.commandline.CommandLineFrameController;
import com.haulmont.timesheets.gui.expenses.ExpensesBrowse;
import com.haulmont.timesheets.gui.rejection.RejectionReason;
import com.haulmont.timesheets.gui.util.ScreensHelper;
import com.haulmont.timesheets.gui.util.SecurityAssistant;
import com.haulmont.timesheets.gui.util.TimeEntryOvertimeAggregation;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.Calendar;

@UiController("ts_ExpensesApprove")
@UiDescriptor("expenses-approve.xml")
public class ExpensesApprove extends Screen {

    @Inject
    protected UserSession userSession;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected Dialogs dialogs;
    @Inject
    protected WorkdaysTools workdaysTools;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected WorkTimeConfigBean workTimeConfigBean;
    @Inject
    protected PopupButton approve;
    @Inject
    protected PopupButton reject;
    @Inject
    protected LookupField user;
    @Inject
    protected LookupField status;
    @Inject
    protected CollectionLoader<Group> groupsDl;
    @Inject
    protected CollectionLoader<Project> projectsDl;
    @Inject
    protected LookupField<Group> group;
    @Inject
    protected LookupField<Project> project;
    @Inject
    protected CollectionLoader<Task> tasksDl;
    @Inject
    protected LookupField<Task> task;
    @Inject
    protected DateField<Date> dateFrom;
    @Inject
    protected DateField<Date> dateTo;
    @Inject
    protected GroupTable<Expenses> expensesesTable;

    @Inject
    protected CollectionContainer<Expenses> expensesesDc;

    @Inject
    protected SecurityAssistant securityAssistant;

    @Inject
    protected CollectionLoader<Expenses> expensesesDl;

    protected String rejectReason;


    @Subscribe("expensesesTable.refresh")
    protected void onExpensesesTableRefresh(Action.ActionPerformedEvent event) {
        refresh();
    }

    @Subscribe(id = "projectsDc", target = Target.DATA_CONTAINER)
    private void onProjectsDcItemChange(InstanceContainer.ItemChangeEvent<Project> e) {
        tasksDl.setParameter("project", e.getItem());
        tasksDl.load();
    }

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
    //@Override
    public void onInit(InitEvent event) {
        //super.onInit(event);

        if (securityAssistant.isSuperUser()) {
            expensesesDl.setQuery("select e from ts_Expenses e");
        } else {
            expensesesDl.setParameter("user", userSession.getUser());
        }

        /*timeEntriesTable.getColumn("overtime").setAggregation(
                ScreensHelper.createAggregationInfo(
                        projectsService.getEntityMetaPropertyPath(TimeEntry.class, "overtime"),
                        new TimeEntryOvertimeAggregation()));*/

        Date previousMonth = DateUtils.addMonths(timeSource.currentTimestamp(), -1);
        dateFrom.setValue(DateUtils.truncate(previousMonth, java.util.Calendar.MONTH));
        dateTo.setValue(DateUtils.addDays(DateUtils.truncate(timeSource.currentTimestamp(), Calendar.MONTH), -1));

        approve.addAction(new BaseAction("approveSelected")
                .withHandler(ae -> {
                    setStatus(expensesesTable.getSelected(), ExpensesStatus.APPROVED);
                })
                .withCaption(messageBundle.getMessage("approveSelected")));

        approve.addAction(new BaseAction("approveAll")
                .withHandler(ae -> {
                    setStatus(expensesesDc.getMutableItems(), ExpensesStatus.APPROVED);
                })
                .withCaption(messageBundle.getMessage("approveAll")));

        reject.addAction(new BaseAction("rejectSelected")
                .withHandler(ae -> {
                    setStatus(expensesesTable.getSelected(), ExpensesStatus.REJECTED);
                })
                .withCaption(messageBundle.getMessage("rejectSelected")));

        reject.addAction(new BaseAction("rejectAll")
                .withHandler(ae -> {
                    setStatus(expensesesDc.getMutableItems(), ExpensesStatus.REJECTED);
                })
                .withCaption(messageBundle.getMessage("rejectAll")));

        status.setOptionsList(Arrays.asList(TimeEntryStatus.values()));
        user.setOptionsList(projectsService.getManagedUsers(userSession.getCurrentOrSubstitutedUser(), View.MINIMAL));
    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        if (securityAssistant.isSuperUser()) {
            expensesesDl.setQuery("select e from ts_Expenses e " +
                    "where e.date >= :dateFrom and e.date <= :dateTo");
            projectsDl.setParameter("superUser", true);
        } else {
            expensesesDl.setParameter("sessionUser", userSession.getCurrentOrSubstitutedUser());
            projectsDl.setParameter("superUser", false);
        }
        expensesesDl.setParameter("dateFrom", dateFrom.getValue());
        expensesesDl.setParameter("dateTo", dateTo.getValue());
        expensesesDl.load();
        projectsDl.setParameter("user", userSession.getCurrentOrSubstitutedUser());
        projectsDl.load();
        groupsDl.load();
    }

    @Subscribe(id = "expensesesDc", target = Target.DATA_CONTAINER)
    protected void onExpensesesCollectionChange(CollectionContainer.CollectionChangeEvent<Expenses> e) {
        Multimap<Map<String, Object>, Expenses> map = ArrayListMultimap.create();
        for (Expenses item : expensesesDc.getMutableItems()) {
            Map<String, Object> key = new TreeMap<>();
            key.put("user", item.getUser());
            key.put("date", item.getDate());
            map.put(key, item);
        }

        /*for (Map.Entry<Map<String, Object>, Collection<Expenses>> entry : map.asMap().entrySet()) {
            BigDecimal thisDaysSummary = BigDecimal.ZERO;
            for (Expenses expenses : entry.getValue()) {
                thisDaysSummary = thisDaysSummary.add(new BigDecimal(expenses.getAmount()));
            }

            for (Expenses expenses : entry.getValue()) {
                BigDecimal planHoursForDay = workdaysTools.isWorkday(expenses.getDate())
                        ? workTimeConfigBean.getWorkHourForDay()
                        : BigDecimal.ZERO;
                BigDecimal overtime = thisDaysSummary.subtract(planHoursForDay);
                //timeEntry.setOvertimeInHours(overtime);
            }*/

    }

    protected void setStatus(final Collection<Expenses> expenses, final ExpensesStatus expensesStatus) {
        dialogs.createOptionDialog(Dialogs.MessageType.CONFIRMATION)
                .withContentMode(ContentMode.HTML)
                .withCaption(messageBundle.getMessage("notification.confirmation"))
                .withMessage(messageBundle.getMessage("notification.confirmationText"))
                .withActions(
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                super.actionPerform(component);
                                CommitContext commitContext = new CommitContext();
                                for (Expenses expenses1 : expenses) {
                                    expenses1.setStatus(expensesStatus);
                                    commitContext.addInstanceToCommit(expenses1);
                                }

                                dataManager.commit(commitContext);
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                )
                .show();
    }


    public void refresh() {
        if (user.getValue() != null)
            expensesesDl.setParameter("user", user.getValue());
        else
            expensesesDl.removeParameter("user");

        if (task.getValue() != null)
            expensesesDl.setParameter("task", task.getValue());
        else
            expensesesDl.removeParameter("task");

        if (project.getValue() != null)
            expensesesDl.setParameter("project", project.getValue());
        else
            expensesesDl.removeParameter("project");

        if (status.getValue() != null)
            expensesesDl.setParameter("status", status.getValue());
        else
            expensesesDl.removeParameter("status");

        if (group.getValue() != null)
            expensesesDl.setParameter("group", group.getValue());
        else
            expensesesDl.removeParameter("group");

        expensesesDl.setParameter("dateFrom", dateFrom.getValue());
        expensesesDl.setParameter("dateTo", dateTo.getValue());

        expensesesDl.load();
    }

}