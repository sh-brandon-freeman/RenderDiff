package renderdiff.service.event;

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
