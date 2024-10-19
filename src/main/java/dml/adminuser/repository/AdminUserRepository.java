package dml.adminuser.repository;

import dml.adminuser.entity.AdminUser;
import dml.common.repository.CommonRepository;

public interface AdminUserRepository<E extends AdminUser> extends CommonRepository<E, String> {
}
