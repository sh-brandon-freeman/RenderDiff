package renderdiff.controller.stacked;

import renderdiff.controller.parent.ParentControllerInterface;

/**
 * Created by bra50311 on 1/10/15.
 */
abstract public class AbstractStackedController implements StackedControllerInterface {

    protected ParentControllerInterface parentController;

    @Override
    public void setParentController(ParentControllerInterface parentController) {
        this.parentController = parentController;
    }
}
