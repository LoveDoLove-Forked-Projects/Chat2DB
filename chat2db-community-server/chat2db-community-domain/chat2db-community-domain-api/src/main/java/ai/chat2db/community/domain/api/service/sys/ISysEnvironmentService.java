package ai.chat2db.community.domain.api.service.sys;

import ai.chat2db.community.domain.api.config.Environment;

import java.util.List;

/**
 * Provides environment metadata for datasource organization.
 */
public interface ISysEnvironmentService {

    List<Environment> listAll();
}
