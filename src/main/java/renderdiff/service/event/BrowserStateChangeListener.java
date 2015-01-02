package renderdiff.service.event;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

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
        System.out.println("Browser State: " + observable.getValue().toString());
        switch (newState) {
            case READY:
                createReadyTimer();
                break;
            case SUCCEEDED:
                if (stallTask != null) {
                    stallTask.cancel();
                }
                browserStateChangeHandler.onChangeSuccess();
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
                    System.out.println("Error: " + ex.getMessage());
                }
                break;
        }
    }

    private void createReadyTimer() {
        stallTask = new Task<Void>() {
            @Override
            public Void call() {
                System.out.println("Starting stall timer.");
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
                System.out.println("Browser Stalled.");
                browserStateChangeHandler.handleStall();
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                System.out.println("Stall timer cancelled.");
            }

            @Override
            protected void failed() {
                super.failed();
                System.out.println("Stall timer failed.");
            }
        };

        new Thread(stallTask).start();
    }
}
