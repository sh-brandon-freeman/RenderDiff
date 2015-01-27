package org.priorityhealth.stab.pdiff.controller.factory;

import javafx.util.Callback;
import org.priorityhealth.stab.pdiff.controller.*;
import org.priorityhealth.stab.pdiff.domain.service.comparator.factory.ComparatorServiceFactory;
import org.priorityhealth.stab.pdiff.persistence.repository.factory.RepositoryFactory;
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
                    repositoryFactory.getStateRepository(),
                    ComparatorServiceFactory.getProfilerService()
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
                    repositoryFactory.getStateRepository(),
                    repositoryFactory.getResultRepository(),
                    repositoryFactory.getTestRepository(),
                    ComparatorServiceFactory.getStateCompareService()
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ComparatorController getComparatorController() {
        try {
            return new ComparatorController(
                    repositoryFactory.getAssetRepository(),
                    repositoryFactory.getNodeRepository(),
                    repositoryFactory.getProfileRepository(),
                    repositoryFactory.getResultRepository(),
                    repositoryFactory.getStateRepository(),
                    repositoryFactory.getTestRepository(),
                    ComparatorServiceFactory.getComparatorService()
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public AssetController getAssetController() {
        try {
            return new AssetController(
                    repositoryFactory.getAssetRepository(),
                    repositoryFactory.getNodeRepository()
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
