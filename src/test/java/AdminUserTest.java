import dml.adminuser.entity.AdminUserSession;
import dml.adminuser.repository.*;
import dml.adminuser.service.AdminUserService;
import dml.adminuser.service.repositoryset.AdminUserServiceRepositorySet;
import dml.adminuser.service.result.LoginResult;
import dml.common.repository.TestCommonRepository;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class AdminUserTest {

    @Test
    public void test() {
        //添加一个管理员
        String account1 = "admin1";
        String password1 = "admin";
        AdminUserService.addAdminUser(adminUserServiceRepositorySet,
                account1, password1, new TestAdminUser());

        //管理员登录
        long currentTime = System.currentTimeMillis();
        LoginResult loginResult1 = AdminUserService.login(adminUserServiceRepositorySet,
                account1, password1, new TestAdminUserSession(UUID.randomUUID().toString()), currentTime);
        assertTrue(loginResult1.isSuccess());

        //修改管理员密码
        String password2 = "admin2";
        AdminUserService.changePassword(adminUserServiceRepositorySet,
                account1, password2);
        //原密码登录失败
        LoginResult loginResult2 = AdminUserService.login(adminUserServiceRepositorySet,
                account1, password1, new TestAdminUserSession(UUID.randomUUID().toString()), currentTime);
        assertFalse(loginResult2.isSuccess());
        assertTrue(loginResult2.isWrongPassword());
        //新密码登录成功
        LoginResult loginResult3 = AdminUserService.login(adminUserServiceRepositorySet,
                account1, password2, new TestAdminUserSession(UUID.randomUUID().toString()), currentTime);
        assertTrue(loginResult3.isSuccess());
        //封禁管理员
        AdminUserService.banAdminUser(adminUserServiceRepositorySet,
                account1);
        //登录失败
        LoginResult loginResult4 = AdminUserService.login(adminUserServiceRepositorySet,
                account1, password2, new TestAdminUserSession(UUID.randomUUID().toString()), currentTime);
        assertFalse(loginResult4.isSuccess());
        assertTrue(loginResult4.isBanned());
        //解封管理员
        AdminUserService.unbanAdminUser(adminUserServiceRepositorySet,
                account1);
        //登录成功
        LoginResult loginResult5 = AdminUserService.login(adminUserServiceRepositorySet,
                account1, password2, new TestAdminUserSession(UUID.randomUUID().toString()), currentTime);
        assertTrue(loginResult5.isSuccess());
        //删除管理员
        AdminUserService.removeAdminUser(adminUserServiceRepositorySet,
                account1);
        //登录失败
        LoginResult loginResult6 = AdminUserService.login(adminUserServiceRepositorySet,
                account1, password2, new TestAdminUserSession(UUID.randomUUID().toString()), currentTime);
        assertFalse(loginResult6.isSuccess());
        assertTrue(loginResult6.isNoAccount());

        //再添加一个管理员
        String account2 = "admin2";
        String password3 = "admin";
        AdminUserService.addAdminUser(adminUserServiceRepositorySet,
                account2, password3, new TestAdminUser());
        //管理员登录
        LoginResult loginResult7 = AdminUserService.login(adminUserServiceRepositorySet,
                account2, password3, new TestAdminUserSession(UUID.randomUUID().toString()), currentTime);
        assertTrue(loginResult7.isSuccess());
        //通过sessionId验证身份
        String authedAccount1 = AdminUserService.auth(adminUserServiceRepositorySet,
                loginResult7.getNewSession().getId());
        assertEquals(account2, authedAccount1);
        //同个管理员再次登录
        LoginResult loginResult8 = AdminUserService.login(adminUserServiceRepositorySet,
                account2, password3, new TestAdminUserSession(UUID.randomUUID().toString()), currentTime);
        assertTrue(loginResult8.isSuccess());
        assertEquals(loginResult7.getNewSession().getId(), loginResult8.getRemovedSession().getId());
        //通过之前的sessionId验证身份失败（被踢）
        String authedAccount2 = AdminUserService.auth(adminUserServiceRepositorySet,
                loginResult7.getNewSession().getId());
        assertNull(authedAccount2);

        long sessionAliveTime = 1000;
        //时间流逝
        currentTime += 500;
        //在session过期之前保活一下
        AdminUserService.keepSessionAlive(adminUserServiceRepositorySet,
                loginResult8.getNewSession().getId(), currentTime);
        //时间流逝
        currentTime += 600;
        //检查并过期session，未过期
        AdminUserSession removedSession1 = AdminUserService.checkSessionDeadAndRemove(adminUserServiceRepositorySet,
                loginResult8.getNewSession().getId(), currentTime, sessionAliveTime);
        assertNull(removedSession1);
        //时间流逝
        currentTime += 1500;
        //检查并过期session，已过期
        AdminUserSession removedSession2 = AdminUserService.checkSessionDeadAndRemove(adminUserServiceRepositorySet,
                loginResult8.getNewSession().getId(), currentTime, sessionAliveTime);
        assertNotNull(removedSession2);
        assertEquals(loginResult8.getNewSession().getId(), removedSession2.getId());
        //通过sessionId验证身份失败
        String authedAccount3 = AdminUserService.auth(adminUserServiceRepositorySet,
                loginResult8.getNewSession().getId());
        assertNull(authedAccount3);

        //再次登录
        LoginResult loginResult9 = AdminUserService.login(adminUserServiceRepositorySet,
                account2, password3, new TestAdminUserSession(UUID.randomUUID().toString()), currentTime);
        assertTrue(loginResult9.isSuccess());
        //登出
        AdminUserSession removedSession3 = AdminUserService.logout(adminUserServiceRepositorySet,
                loginResult9.getNewSession().getId());
        //通过sessionId验证身份失败
        String authedAccount4 = AdminUserService.auth(adminUserServiceRepositorySet,
                loginResult9.getNewSession().getId());
        assertNull(authedAccount4);
        //再次登录
        LoginResult loginResult10 = AdminUserService.login(adminUserServiceRepositorySet,
                account2, password3, new TestAdminUserSession(UUID.randomUUID().toString()), currentTime);
        assertTrue(loginResult10.isSuccess());
        assertNull(loginResult10.getRemovedSession());
        //通过sessionId验证身份
        String authedAccount5 = AdminUserService.auth(adminUserServiceRepositorySet,
                loginResult10.getNewSession().getId());
        assertEquals(account2, authedAccount5);
    }

    AdminUserServiceRepositorySet adminUserServiceRepositorySet = new AdminUserServiceRepositorySet() {

        @Override
        public AdminUserRepository getAdminUserRepository() {
            return adminUserRepository;
        }

        @Override
        public AdminUserSessionRepository getAdminUserSessionRepository() {
            return adminUserSessionRepository;
        }

        @Override
        public AdminUserCurrentSessionRepository getAdminUserCurrentSessionRepository() {
            return adminUserCurrentSessionRepository;
        }

        @Override
        public AdminUserSessionAliveKeeperRepository getAdminUserSessionAliveKeeperRepository() {
            return adminUserSessionAliveKeeperRepository;
        }

        @Override
        public ClearSessionTaskRepository getClearSessionTaskRepository() {
            return null;
        }

        @Override
        public ClearSessionTaskSegmentRepository getClearSessionTaskSegmentRepository() {
            return null;
        }

    };

    AdminUserRepository adminUserRepository = TestCommonRepository.instance(AdminUserRepository.class);
    AdminUserSessionRepository adminUserSessionRepository = TestCommonRepository.instance(AdminUserSessionRepository.class);
    AdminUserCurrentSessionRepository adminUserCurrentSessionRepository = TestCommonRepository.instance(AdminUserCurrentSessionRepository.class);
    AdminUserSessionAliveKeeperRepository adminUserSessionAliveKeeperRepository = TestCommonRepository.instance(AdminUserSessionAliveKeeperRepository.class);

}
