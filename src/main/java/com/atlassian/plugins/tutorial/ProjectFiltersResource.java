package com.atlassian.plugins.tutorial;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;

import com.acme.jiracharts.core.domain.filter.Filter;
import com.acme.jiracharts.core.domain.filter.FilterRepository;
import com.acme.jiracharts.jira.JiraFilterRepository;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.filter.SearchRequestService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.bc.project.ProjectService.GetProjectResult;
import com.atlassian.jira.bc.project.version.VersionService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.user.UserManager;

/**
 * REST resource that provides a list of projects in JSON format.
 */
@Path("/projectfilters")
public class ProjectFiltersResource {
	private UserManager userManager;
	private PermissionManager permissionManager;
	private UserUtil userUtil;
	private SearchService searchService;
	private ProjectService projectService;
	private VersionService versionService;
	private FilterRepository issueRepository;
	private SearchRequestService searchRequestService;
	private JiraFilterRepository filterRepository;

	/**
	 * Constructor.
	 * 
	 * @param userManager
	 *            a SAL object used to find remote usernames in Atlassian
	 *            products
	 * @param userUtil
	 *            a JIRA object to resolve usernames to JIRA's internal
	 *            {@code com.opensymphony.os.User} objects
	 * @param permissionManager
	 *            the JIRA object which manages permissions for users and
	 *            projects
	 */
	public ProjectFiltersResource(ProjectService projectService,
			VersionService versionService, SearchService searchService,
			UserManager userManager, UserUtil userUtil,
			PermissionManager permissionManager,
			SearchRequestService searchRequestService) {
		this.projectService = projectService;
		this.versionService = versionService;
		this.searchService = searchService;
		this.userManager = userManager;
		this.userUtil = userUtil;
		this.permissionManager = permissionManager;
		this.searchRequestService = searchRequestService;
		this.filterRepository = new JiraFilterRepository(searchRequestService);

	}

	/**
	 * Returns the list of projects browsable by the user in the specified
	 * request.
	 * 
	 * @param request
	 *            the context-injected {@code HttpServletRequest}
	 * @return a {@code Response} with the marshalled projects
	 */
	@GET
	@AnonymousAllowed
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getBurndown(@Context HttpServletRequest request,
			@QueryParam("projectId") String projectIdString) {
		try {
			Long projectId = Long.valueOf(projectIdString.substring("project-"
					.length()));

			String username = userManager.getRemoteUsername(request);
			User user = userUtil.getUser(username);
			GetProjectResult projectByKey = projectService.getProjectById(user,
					projectId);

			List<Filter> filters = filterRepository.filtersSharedByProject(
					user, projectId);
			for (Filter filter : filters) {
				System.out.println(filter.getName());
			}

			ProjectFilters projectFilters = ProjectFilters.fromFilters(filters);

			return Response.ok(projectFilters).build();
		} catch (RuntimeException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);

			return Response.ok(sw.getBuffer()).build();
		}
	}

	private List<Issue> issuesForUser(List<Issue> issues, User aUser) {
		List<Issue> result = new ArrayList<Issue>();
		for (Issue issue : issues) {
			if (issue.getAssignee() == null) {
				if (aUser == null) {
					result.add(issue);
				}
			} else if (issue.getAssignee().equals(aUser)) {
				result.add(issue);
			}
		}
		return result;
	}

	private List<String> toString(List<DateTime> dates) {
		List<String> stringDates = new ArrayList<String>();
		for (DateTime aDate : dates) {
			stringDates.add(aDate.toString("dd/MM/yyyy"));
		}
		return stringDates;
	}

}