/**
 * Request
 *
 * Copyright (C) 2013 Sh1fT
 *
 * This file is part of PreBot.
 *
 * PreBot is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * PreBot is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PreBot; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package org.mind.prebot.database;

import java.sql.Date;

/**
 * Manage a {@link Request}
 * @author Sh1fT
 *
 */
public class Request {
	private Integer results;
	private String request;
	private String requestBy;
	private Date requestDate;
	private Boolean filled;
	private String filledBy;
	private Integer diffDate;

	/**
	 * Create a new {@link Request} instance
	 */
	public Request() {
		this.setResults(0);
		this.setRequest(null);
		this.setRequestBy(null);
		this.setRequestDate(null);
		this.setFilled(null);
		this.setFilledBy(null);
		this.setDiffDate(null);
	}

	/**
	 * Create a new {@link Request} instance
	 * @param results
	 * @param request
	 * @param requestBy
	 * @param requestDate
	 * @param filled
	 * @param filledBy
	 */
	public Request(Integer results, String request, String requestBy, Date requestDate,
			Boolean filled, String filledBy, Integer diffDate) {
		this.setResults(results);
		this.setRequest(request);
		this.setRequestBy(requestBy);
		this.setRequestDate(requestDate);
		this.setFilled(filled);
		this.setFilledBy(filledBy);
		this.setDiffDate(diffDate);
	}

	/**
	 * Create a new {@link Request} instance
	 * @param request
	 */
	public Request(Request request) {
		this.setResults(request.getResults());
		this.setRequest(request.getRequest());
		this.setRequestBy(request.getRequestBy());
		this.setRequestDate(request.getRequestDate());
		this.setFilled(request.getFilled());
		this.setFilledBy(request.getFilledBy());
		this.setDiffDate(request.getDiffDate());
	}

	public Integer getResults() {
		return results;
	}

	public void setResults(Integer results) {
		this.results = results;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getRequestBy() {
		return requestBy;
	}

	public void setRequestBy(String requestBy) {
		this.requestBy = requestBy;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Boolean getFilled() {
		return filled;
	}

	public void setFilled(Boolean filled) {
		this.filled = filled;
	}

	public String getFilledBy() {
		return filledBy;
	}

	public void setFilledBy(String filledBy) {
		this.filledBy = filledBy;
	}

	public Integer getDiffDate() {
		return diffDate;
	}

	public void setDiffDate(Integer diffDate) {
		this.diffDate = diffDate;
	}
}