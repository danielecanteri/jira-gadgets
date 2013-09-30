package com.atlassian.plugins.tutorial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.acme.jiracharts.core.domain.filter.Filter;

@SuppressWarnings("UnusedDeclaration")
@XmlRootElement
public class ProjectFilters {

	@XmlElement
	private Collection<FilterRepresentation> filters;

	public ProjectFilters() {
	}

	public static ProjectFilters fromFilters(List<Filter> filters) {
		ProjectFilters projectFilters = new ProjectFilters();
		projectFilters.filters = new ArrayList<FilterRepresentation>();
		for (Filter filter : filters) {
			projectFilters.filters.add(FilterRepresentation.fromFilter(filter));
		}
		return projectFilters;
	}

}
