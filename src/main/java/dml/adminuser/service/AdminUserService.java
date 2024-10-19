package dml.adminuser.service;

import dml.adminuser.entity.*;
import dml.adminuser.repository.AdminUserCurrentSessionRepository;
import dml.adminuser.repository.AdminUserRepository;
import dml.adminuser.repository.AdminUserSessionIDGeneratorRepository;
import dml.adminuser.repository.AdminUserSessionRepository;
import dml.adminuser.service.repositoryset.AdminUserServiceRepositorySet;
import dml.keepalive.repository.AliveKeeperRepository;
import dml.keepalive.service.KeepAliveService;
import dml.keepalive.service.repositoryset.AliveKeeperServiceRepositorySet;

public class AdminUserService {

    public static AddAdminUserResult addAdminUser(AdminUserServiceRepositorySet repositorySet,
                                                  String account, String password, AdminUser newAdminUser) {
        AdminUserRepository<AdminUser> adminUserRepository = repositorySet.getAdminUserRepository();

        AddAdminUserResult addAdminUserResult = new AddAdminUserResult();
        newAdminUser.setAccount(account);
        newAdminUser.setPassword(password);
        AdminUser existAdminUser = adminUserRepository.putIfAbsent(newAdminUser);
        if (existAdminUser != null) {
            addAdminUserResult.setExistAdminUser(existAdminUser);
            return addAdminUserResult;
        }
        addAdminUserResult.setSuccess(true);
        addAdminUserResult.setNewAdminUser(newAdminUser);
        return addAdminUserResult;
    }

    public static LoginResult login(AdminUserServiceRepositorySet repositorySet,
                                    String account, String password, AdminUserSession newAdminUserSession,
                                    long currentTime) {
        AdminUserRepository<AdminUser> adminUserRepository = repositorySet.getAdminUserRepository();
        AdminUserCurrentSessionRepository<AdminUserCurrentSession> adminUserCurrentSessionRepository = repositorySet.getAdminUserCurrentSessionRepository();

        LoginResult loginResult = new LoginResult();
        AdminUser adminUser = adminUserRepository.find(account);
        if (adminUser == null) {
            loginResult.setNoAccount(true);
            return loginResult;
        }
        if (adminUser.verifyPassword(password)) {
            if (adminUser.isBanned()) {
                loginResult.setBanned(true);
                return loginResult;
            }
            AdminUserCurrentSession adminUserCurrentSession = new AdminUserCurrentSession();
            adminUserCurrentSession.setAccount(account);
            adminUserCurrentSession = adminUserCurrentSessionRepository.takeOrPutIfAbsent(account, adminUserCurrentSession);
            if (adminUserCurrentSession.getCurrentSessionId() != null) {
                AdminUserSession removedSession = removeSessionAndAliveKeeper(repositorySet, adminUserCurrentSession.getCurrentSessionId());
                adminUserCurrentSession.setCurrentSessionId(null);
                loginResult.setRemovedSession(removedSession);
            }
            AdminUserSession newSession = createSessionAndAliveKeeper(repositorySet, account, newAdminUserSession, currentTime);
            adminUserCurrentSession.setCurrentSessionId(newSession.getId());
            loginResult.setNewSession(newSession);
            loginResult.setSuccess(true);
        } else {
            loginResult.setWrongPassword(true);
        }
        return loginResult;
    }

    private static AdminUserSession createSessionAndAliveKeeper(AdminUserServiceRepositorySet repositorySet,
                                                                String account, AdminUserSession newAdminUserSession,
                                                                long currentTime) {
        AdminUserSessionRepository<AdminUserSession> adminUserSessionRepository = repositorySet.getAdminUserSessionRepository();
        AdminUserSessionIDGeneratorRepository adminUserSessionIDGeneratorRepository = repositorySet.getAdminUserSessionIDGeneratorRepository();

        newAdminUserSession.setId(adminUserSessionIDGeneratorRepository.take().generateId());
        newAdminUserSession.setAccount(account);
        adminUserSessionRepository.put(newAdminUserSession);

        KeepAliveService.createAliveKeeper(getAliveKeeperServiceRepositorySet(repositorySet),
                newAdminUserSession.getId(), currentTime, new AdminUserSessionAliveKeeper());
        return newAdminUserSession;
    }

    private static AdminUserSession removeSessionAndAliveKeeper(AdminUserServiceRepositorySet repositorySet,
                                                                String sessionId) {
        AdminUserSessionRepository<AdminUserSession> adminUserSessionRepository = repositorySet.getAdminUserSessionRepository();

        KeepAliveService.removeAliveKeeper(getAliveKeeperServiceRepositorySet(repositorySet), sessionId);
        return adminUserSessionRepository.remove(sessionId);
    }

    private static AliveKeeperServiceRepositorySet getAliveKeeperServiceRepositorySet(AdminUserServiceRepositorySet adminUserServiceRepositorySet) {
        return new AliveKeeperServiceRepositorySet() {

            @Override
            public AliveKeeperRepository getAliveKeeperRepository() {
                return adminUserServiceRepositorySet.getAdminUserSessionAliveKeeperRepository();
            }
        };
    }

    public static void changePassword(AdminUserServiceRepositorySet repositorySet,
                                      String account, String newPassword) {
        AdminUserRepository<AdminUser> adminUserRepository = repositorySet.getAdminUserRepository();

        AdminUser adminUser = adminUserRepository.take(account);
        adminUser.setPassword(newPassword);
    }

    public static void banAdminUser(AdminUserServiceRepositorySet adminUserServiceRepositorySet,
                                    String account) {
        AdminUserRepository<AdminUser> adminUserRepository = adminUserServiceRepositorySet.getAdminUserRepository();

        AdminUser adminUser = adminUserRepository.take(account);
        adminUser.setBanned(true);
    }

    public static void unbanAdminUser(AdminUserServiceRepositorySet repositorySet, String account) {
        AdminUserRepository<AdminUser> adminUserRepository = repositorySet.getAdminUserRepository();

        AdminUser adminUser = adminUserRepository.take(account);
        adminUser.setBanned(false);
    }

    public static AdminUser removeAdminUser(AdminUserServiceRepositorySet repositorySet,
                                            String account) {
        AdminUserRepository<AdminUser> adminUserRepository = repositorySet.getAdminUserRepository();

        return adminUserRepository.remove(account);
    }

    public static String auth(AdminUserServiceRepositorySet repositorySet,
                              String sessionId) {
        AdminUserSessionRepository<AdminUserSession> adminUserSessionRepository = repositorySet.getAdminUserSessionRepository();

        AdminUserSession adminUserSession = adminUserSessionRepository.find(sessionId);
        if (adminUserSession == null) {
            return null;
        }
        return adminUserSession.getAccount();
    }

    public static void keepSessionAlive(AdminUserServiceRepositorySet repositorySet,
                                        String sessionId, long currentTime) {
        KeepAliveService.keepAlive(getAliveKeeperServiceRepositorySet(repositorySet),
                sessionId, currentTime);
    }

    public static AdminUserSession checkSessionDeadAndRemove(AdminUserServiceRepositorySet repositorySet,
                                                             String sessionId, long currentTime, long sessionAliveTime) {
        AdminUserSessionRepository<AdminUserSession> adminUserSessionRepository = repositorySet.getAdminUserSessionRepository();

        boolean alive = KeepAliveService.isAlive(getAliveKeeperServiceRepositorySet(repositorySet),
                sessionId, currentTime, sessionAliveTime);
        if (!alive) {
            AdminUserSession removedSession = adminUserSessionRepository.remove(sessionId);
            KeepAliveService.removeAliveKeeper(getAliveKeeperServiceRepositorySet(repositorySet)
                    , sessionId);
            return removedSession;
        }
        return null;
    }

    public static AdminUserSession logout(AdminUserServiceRepositorySet repositorySet,
                                          String sessionId) {
        AdminUserCurrentSessionRepository<AdminUserCurrentSession> adminUserCurrentSessionRepository = repositorySet.getAdminUserCurrentSessionRepository();
        AdminUserSessionRepository<AdminUserSession> adminUserSessionRepository = repositorySet.getAdminUserSessionRepository();

        AdminUserSession removedSession = removeSessionAndAliveKeeper(repositorySet, sessionId);
        if (removedSession == null) {
            return null;
        }
        AdminUserCurrentSession adminUserCurrentSession = adminUserCurrentSessionRepository.take(removedSession.getAccount());
        adminUserCurrentSession.setCurrentSessionId(null);
        return removedSession;
    }
}
