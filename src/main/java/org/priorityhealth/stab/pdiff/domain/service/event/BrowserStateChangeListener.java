package org.priorityhealth.stab.pdiff.domain.service.event;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import org.priorityhealth.stab.pdiff.service.LogService;

public class BrowserStateChangeListener implements ChangeListener<Worker.State> {

    protected WebEngine webEngine;
    protected BrowserStateChangeHandlerInterface browserStateChangeHandler;
    protected Task<Void> stallTask;

    public BrowserStateChangeListener(WebEngine webEngine, BrowserStateChangeHandlerInterface browserStateChangeHandler) {
        this.webEngine = webEngine;
        this.browserStateChangeHandler = browserStateChangeHandler;
    }

    @Override
    public void changed(ObservableValue observable, Worker.State oldState, Worker.State newState) {
        LogService.Info(this, "Browser State: " + observable.getValue().toString());
        switch (newState) {
            case READY:
                createReadyTimer();
                break;
            case SUCCEEDED:
                if (stallTask != null) {
                    stallTask.cancel();
                }
                browserStateChangeHandler.onPageLoadSuccess();
                break;
            case FAILED:
                if (stallTask != null) {
                    stallTask.cancel();
                }
                try {
                    Throwable throwable = webEngine.getLoadWorker().getException();
                    if (throwable != null){
                        throw throwable;
                    } else {
                        throw new Throwable("Unknown error.");
                    }
                } catch (Throwable ex) {
                    LogService.Info(this, "Error: " + ex.getMessage());
                }
                break;
        }
    }

    private void createReadyTimer() {
        stallTask = new Task<Void>() {
            @Override
            public Void call() {
                LogService.Info(this, "Starting stall timer.");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException interrupted) {
                    // Do nothing.
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                LogService.Info(this, "Browser Stalled.");
                browserStateChangeHandler.handleStall();
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                LogService.Info(this, "Stall timer cancelled.");
            }

            @Override
            protected void failed() {
                super.failed();
                LogService.Info(this, "Stall timer failed.");
            }
        };

        new Thread(stallTask).start();
    }
}
