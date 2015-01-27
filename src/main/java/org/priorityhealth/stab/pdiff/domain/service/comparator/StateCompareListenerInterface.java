package org.priorityhealth.stab.pdiff.domain.service.comparator;

import org.priorityhealth.stab.pdiff.domain.entity.test.Result;
import org.priorityhealth.stab.pdiff.domain.entity.test.Test;

public interface StateCompareListenerInterface {
    public void onCompareComplete(Result result);
    public void onQueueComplete(Test test);
}
