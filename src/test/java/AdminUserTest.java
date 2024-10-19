import dml.adminuser.entity.LoginResult;
import dml.adminuser.repository.*;
import dml.adminuser.service.AdminUserService;
import dml.adminuser.service.repositoryset.AdminUserServiceRepositorySet;
import dml.common.repository.TestCommonRepository;
import dml.common.repository.TestCommonSingletonRepository;
import dml.id.entity.UUIDStyleRandomStringIdGenerator;
import org.junit.Test;

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
                account1, password1, new TestAdminUserSession(), currentTime);
        assertTrue(loginResult1.isSuccess());

        //修改管理员密码
        String password2 = "admin2";
        AdminUserService.changePassword(adminUserServiceRepositorySet,
                account1, password2);
        //原密码登录失败
        LoginResult loginResult2 = AdminUserService.login(adminUserServiceRepositorySet,
                account1, password1, new TestAdminUserSession(), currentTime);
        assertFalse(loginResult2.isSuccess());
        assertTrue(loginResult2.isWrongPassword());
        //新密码登录成功
        LoginResult loginResult3 = AdminUserService.login(adminUserServiceRepositorySet,
                account1, password2, new TestAdminUserSession(), currentTime);
        assertTrue(loginResult3.isSuccess());
        //封禁管理员
        AdminUserService.banAdminUser(adminUserServiceRepositorySet,
                account1);
        //登录失败
        LoginResult loginResult4 = AdminUserService.login(adminUserServiceRepositorySet,
                account1, password2, new TestAdminUserSession(), currentTime);
        assertFalse(loginResult4.isSuccess());
        assertTrue(loginResult4.isBanned());
        //解封管理员
        AdminUserService.unbanAdminUser(adminUserServiceRepositorySet,
                account1);
        //登录成功
        LoginResult loginResult5 = AdminUserService.login(adminUserServiceRepositorySet,
                account1, password2, new TestAdminUserSession(), currentTime);
        assertTrue(loginResult5.isSuccess());
        //删除管理员
        AdminUserService.removeAdminUser(adminUserServiceRepositorySet,
                account1);
        //登录失败
        LoginResult loginResult6 = AdminUserService.login(adminUserServiceRepositorySet,
                account1, password2, new TestAdminUserSession(), currentTime);
        assertFalse(loginResult6.isSuccess());
        assertTrue(loginResult6.isNoAccount());

        //再添加一个管理员
        String account2 = "admin2";
        String password3 = "admin";
        AdminUserService.addAdminUser(adminUserServiceRepositorySet,
                account2, password3, new TestAdminUser());
        //管理员登录
        LoginResult loginResult7 = AdminUserService.login(adminUserServiceRepositorySet,
                account2, password3, new TestAdminUserSession(), currentTime);
        assertTrue(loginResult7.isSuccess());
        //通过sessionId验证身份
        String authedAccount1 = AdminUserService.auth(adminUserServiceRepositorySet,
                loginResult7.getNewSession().getId());
        assertEquals(account2, authedAccount1);
        //同个管理员再次登录
        LoginResult loginResult8 = AdminUserService.login(adminUserServiceRepositorySet,
                account2, password3, new TestAdminUserSession(), currentTime);
        assertTrue(loginResult8.isSuccess());
        assertEquals(loginResult7.getNewSession().getId(), loginResult8.getRemovedSession().getId());
        //通过之前的sessionId验证身份失败（被踢）
        String authedAccount2 = AdminUserService.auth(adminUserServiceRepositorySet,
                loginResult7.getNewSession().getId());
        assertNull(authedAccount2);
        //时间流逝
        //在session过期之前保活一下
        //检查并过期session，未过期
        //时间流逝
        //检查并过期session，已过期
        //通过sessionId验证身份失败

        //再次登录
        //登出
        //通过sessionId验证身份失败
        //再次登录
        //通过sessionId验证身份
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
        public AdminUserSessionIDGeneratorRepository getAdminUserSessionIDGeneratorRepository() {
            return adminUserSessionIDGeneratorRepository;
        }

        @Override
        public AdminUserCurrentSessionRepository getAdminUserCurrentSessionRepository() {
            return adminUserCurrentSessionRepository;
        }

        @Override
        public AdminUserSessionAliveKeeperRepository getAdminUserSessionAliveKeeperRepository() {
            return adminUserSessionAliveKeeperRepository;
        }
    };

    AdminUserRepository adminUserRepository = TestCommonRepository.instance(AdminUserRepository.class);
    AdminUserSessionRepository adminUserSessionRepository = TestCommonRepository.instance(AdminUserSessionRepository.class);
    AdminUserSessionIDGeneratorRepository adminUserSessionIDGeneratorRepository = TestCommonSingletonRepository.instance(AdminUserSessionIDGeneratorRepository.class,
            new UUIDStyleRandomStringIdGenerator() {
            });
    AdminUserCurrentSessionRepository adminUserCurrentSessionRepository = TestCommonRepository.instance(AdminUserCurrentSessionRepository.class);
    AdminUserSessionAliveKeeperRepository adminUserSessionAliveKeeperRepository = TestCommonRepository.instance(AdminUserSessionAliveKeeperRepository.class);

}
