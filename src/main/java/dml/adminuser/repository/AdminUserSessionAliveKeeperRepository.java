package dml.adminuser.repository;

import dml.adminuser.entity.AdminUserSessionAliveKeeper;
import dml.keepalive.repository.AliveKeeperRepository;

public interface AdminUserSessionAliveKeeperRepository extends AliveKeeperRepository<AdminUserSessionAliveKeeper, String> {
}
