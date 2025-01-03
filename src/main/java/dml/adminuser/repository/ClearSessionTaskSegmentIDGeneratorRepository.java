package dml.adminuser.repository;

import dml.common.repository.CommonSingletonRepository;
import dml.id.entity.IdGenerator;

public interface ClearSessionTaskSegmentIDGeneratorRepository extends CommonSingletonRepository<IdGenerator<String>> {
}
