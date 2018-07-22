package org.jfrog.artifactory.client.v2.impl;

import org.jfrog.artifactory.client.v2.model.permissions.Action;
import org.jfrog.artifactory.client.v2.model.permissions.ActionType;

import java.util.Set;

public class ActionImpl implements Action {

    private String name;
    private Set<ActionType> actionTypes;

    public ActionImpl() {
        super();
    }

    public ActionImpl(String name, Set<ActionType> actionTypes) {
        this.name = name;
        this.actionTypes = actionTypes;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<ActionType> getActionTypes() {
        return actionTypes;
    }

    @Override
    public boolean isAllowedTo(ActionType... actions) {
        return false;
    }
}
