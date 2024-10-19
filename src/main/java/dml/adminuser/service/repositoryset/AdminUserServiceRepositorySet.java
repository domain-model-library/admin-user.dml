package dml.adminuser.service.repositoryset;

import dml.adminuser.repository.*;

public interface AdminUserServiceRepositorySet {
    AdminUserRepository getAdminUserRepository();

    AdminUserSessionRepository getAdminUserSessionRepository();

    AdminUserSessionIDGeneratorRepository getAdminUserSessionIDGeneratorRepository();

    AdminUserCurrentSessionRepository getAdminUserCurrentSessionRepository();

    AdminUserSessionAliveKeeperRepository getAdminUserSessionAliveKeeperRepository();
}
