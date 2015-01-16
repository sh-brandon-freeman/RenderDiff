package org.priorityhealth.stab.pdiff.domain.service.event;

/**
 *
 */
public interface BrowserStateChangeHandlerInterface {
    /**
     * Triggered on Page Load Success
     */
    public void onPageLoadSuccess();

    /**
     * Handle browser load stall
     */
    public void handleStall();
}
