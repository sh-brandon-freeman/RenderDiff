package org.priorityhealth.stab.pdiff.domain.service.comparator.factory;

import org.priorityhealth.stab.pdiff.controller.ComparatorController;
import org.priorityhealth.stab.pdiff.domain.service.comparator.ComparatorService;
import org.priorityhealth.stab.pdiff.domain.service.comparator.ProfilerService;
import org.priorityhealth.stab.pdiff.domain.service.comparator.StateCompareService;
import org.priorityhealth.stab.pdiff.persistence.repository.factory.RepositoryFactory;
import org.priorityhealth.stab.pdiff.service.StorageService;

import java.sql.SQLException;

public class ComparatorServiceFactory {

    protected static RepositoryFactory repositoryFactory;

    public static void setRepositoryFactory(RepositoryFactory repositoryFactory) {
        ComparatorServiceFactory.repositoryFactory = repositoryFactory;
    }

    public static ProfilerService getProfilerService() {
        try {
            return new ProfilerService(
                    repositoryFactory.getAssetRepository(),
                    repositoryFactory.getNodeRepository(),
                    repositoryFactory.getStateRepository(),
                    repositoryFactory.getProfileRepository(),
                    StorageService.getStoragePath()
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static StateCompareService getStateCompareService() {
        try {
            return new StateCompareService(
                    repositoryFactory.getResultRepository(),
                    repositoryFactory.getTestRepository(),
                    StorageService.getStoragePath()
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ComparatorService getComparatorService() {
        return new ComparatorService(
                getProfilerService(),
                getStateCompareService()
        );
    }
}
