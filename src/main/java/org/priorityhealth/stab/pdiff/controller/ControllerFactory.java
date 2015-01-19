package org.priorityhealth.stab.pdiff.controller;

import javafx.util.Callback;
import org.priorityhealth.stab.pdiff.persistence.repository.RepositoryFactory;
import org.priorityhealth.stab.pdiff.service.LogService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

public class ControllerFactory implements Callback<Class<?>, Object> {

    protected RepositoryFactory repositoryFactory;

    public ControllerFactory(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    @Override
    public Object call(Class<?> param) {
        String className = param.getName();
        String methodName = "get" + className.substring(className.lastIndexOf('.') + 1);

        try {
            Method method = this.getClass().getDeclaredMethod(methodName);
            LogService.Info(this, "Calling: " + method.getName());
            return method.invoke(this);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            return null;
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ProfileController getProfileController() {
        try {
            return new ProfileController(
                    repositoryFactory.getAssetRepository(),
                    repositoryFactory.getNodeRepository(),
                    repositoryFactory.getProfileRepository(),
                    repositoryFactory.getStateRepository()
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public TestController getTestController() {
        try {
            return new TestController(
                    repositoryFactory.getAssetRepository(),
                    repositoryFactory.getProfileRepository(),
                    repositoryFactory.getStateRepository()
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public MainController getMainController() {
        LogService.Info(this, "Creating MainController");
        return new MainController(this);
    }
}
