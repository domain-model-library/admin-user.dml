package dml.adminuser.repository;

import dml.common.repository.CommonSingletonRepository;
import dml.id.entity.IdGenerator;

/**
 * @author zheng chengdong
 */
public interface AdminUserSessionIDGeneratorRepository extends CommonSingletonRepository<IdGenerator<String>> {
}
