package dml.adminuser.service.repositoryset;

import dml.adminuser.repository.*;
import dml.largescaletaskmanagement.repository.LargeScaleTaskSegmentIDGeneratorRepository;

public interface AdminUserServiceRepositorySet {
    AdminUserRepository getAdminUserRepository();

    AdminUserSessionRepository getAdminUserSessionRepository();

    AdminUserSessionIDGeneratorRepository getAdminUserSessionIDGeneratorRepository();

    AdminUserCurrentSessionRepository getAdminUserCurrentSessionRepository();

    AdminUserSessionAliveKeeperRepository getAdminUserSessionAliveKeeperRepository();

    ClearSessionTaskRepository getClearSessionTaskRepository();

    ClearSessionTaskSegmentRepository getClearSessionTaskSegmentRepository();

    LargeScaleTaskSegmentIDGeneratorRepository getClearSessionTaskSegmentIDGeneratorRepository();

}
