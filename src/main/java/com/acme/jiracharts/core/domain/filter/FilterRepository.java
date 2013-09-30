package com.acme.jiracharts.core.domain.filter;

import java.util.List;

import com.atlassian.crowd.embedded.api.User;

public interface FilterRepository {

	List<Filter> filtersSharedByProject(User user, Long projectId);

}
