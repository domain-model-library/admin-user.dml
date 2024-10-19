package dml.adminuser.repository;

import dml.adminuser.entity.AdminUserSession;
import dml.common.repository.CommonRepository;

public interface AdminUserSessionRepository<E extends AdminUserSession> extends CommonRepository<E, String> {
}
