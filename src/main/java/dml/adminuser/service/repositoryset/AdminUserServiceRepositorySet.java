package dml.adminuser.service.repositoryset;

import dml.adminuser.repository.*;

public interface AdminUserServiceRepositorySet {
    AdminUserRepository getAdminUserRepository();

    AdminUserSessionRepository getAdminUserSessionRepository();

    AdminUserCurrentSessionRepository getAdminUserCurrentSessionRepository();

    AdminUserSessionAliveKeeperRepository getAdminUserSessionAliveKeeperRepository();

    ClearSessionTaskRepository getClearSessionTaskRepository();

    ClearSessionTaskSegmentRepository getClearSessionTaskSegmentRepository();
}
