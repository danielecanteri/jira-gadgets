package com.atlassian.plugins.tutorial;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.acme.jiracharts.core.domain.filter.Filter;

/**
 * JAXB representation of a project's information. This can be marshalled as
 * either JSON or XML, depending on what the client asks for.
 */
// @Immutable
@SuppressWarnings("UnusedDeclaration")
@XmlRootElement
public class FilterRepresentation {
	@XmlElement
	private Long id;

	@XmlElement
	private String name;

	// This private constructor isn't used by any code, but JAXB requires any
	// representation class to have a no-args constructor.
	private FilterRepresentation() {
	}

	public static FilterRepresentation fromFilter(Filter filter) {
		FilterRepresentation filterRepresentation = new FilterRepresentation();
		filterRepresentation.id = filter.getId();
		filterRepresentation.name = filter.getName();
		return filterRepresentation;
	}

}