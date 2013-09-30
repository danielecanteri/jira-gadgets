package com.acme.jiracharts.jira;

import java.util.ArrayList;
import java.util.List;

import com.acme.jiracharts.core.domain.filter.Filter;
import com.acme.jiracharts.core.domain.filter.FilterRepository;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.filter.SearchRequestService;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.sharing.search.ProjectShareTypeSearchParameter;
import com.atlassian.jira.sharing.search.SharedEntitySearchParametersBuilder;
import com.atlassian.jira.sharing.search.SharedEntitySearchResult;

public class JiraFilterRepository implements FilterRepository {

	private SearchRequestService searchRequestService;

	public JiraFilterRepository(SearchRequestService searchRequestService) {
		this.searchRequestService = searchRequestService;
	}

	@Override
	public List<Filter> filtersSharedByProject(User user, Long projectId) {
		SharedEntitySearchParametersBuilder b = new SharedEntitySearchParametersBuilder();
		b.setShareTypeParameter(new ProjectShareTypeSearchParameter(projectId));
		SharedEntitySearchResult<SearchRequest> search = searchRequestService
				.search(new JiraServiceContextImpl(user),
						b.toSearchParameters(), 0, 100);
		List<Filter> result = new ArrayList<Filter>();
		for (SearchRequest searchRequest : search.getResults()) {
			Filter filter = new Filter();
			filter.setId(searchRequest.getId());
			filter.setName(searchRequest.getName());
			result.add(filter);
		}
		return result;
	}
}
