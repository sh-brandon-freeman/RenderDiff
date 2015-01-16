package org.priorityhealth.stab.pdiff.controller.stacked;

import org.priorityhealth.stab.pdiff.controller.parent.AbstractParentController;

abstract public class AbstractStackedController implements StackedControllerInterface {

    protected AbstractParentController parentController;
    public static final String CONTROLLER_VIEW = "";

    @Override
    public void setParentController(AbstractParentController parentController) {
        this.parentController = parentController;
    }
}
