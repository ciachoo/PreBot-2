/**
 * Release
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

/**
 * Manage a {@link Release}
 * @author Sh1fT
 *
 */
public class Release {
	private Integer results;
	private String date;
	private String category;
	private String name;
	private String size;
	private String priority;
	private String checked;
	private String nuked;
	private String reason;
	private Integer diffDate;

	/**
	 * Create a new {@link Release} instance
	 */
	public Release() {
		this.setResults(0);
		this.setDate(null);
		this.setCategory(null);
		this.setName(null);
		this.setSize(null);
		this.setPriority(null);
		this.setChecked(null);
		this.setNuked(null);
		this.setReason(null);
		this.setDiffDate(null);
	}

	/**
	 * Create a new {@link Release} instance
	 * @param results
	 * @param date
	 * @param category
	 * @param name
	 * @param size
	 * @param priority
	 * @param checked
	 * @param nuked
	 * @param reason
	 * @param diffDate
	 */
	public Release(Integer results, String date, String category, String name,
			String size, String priority, String checked, String nuked, String reason, Integer diffDate) {
		this.setResults(results);
		this.setDate(date);
		this.setCategory(category);
		this.setName(name);
		this.setSize(size);
		this.setPriority(priority);
		this.setChecked(checked);
		this.setNuked(nuked);
		this.setReason(reason);
		this.setDiffDate(diffDate);
	}

	/**
	 * Create a new {@link Release} instance
	 * @param release
	 */
	public Release(Release release) {
		this.setResults(release.getResults());
		this.setDate(release.getDate());
		this.setCategory(release.getCategory());
		this.setName(release.getName());
		this.setSize(release.getSize());
		this.setPriority(release.getPriority());
		this.setChecked(release.getChecked());
		this.setNuked(release.getNuked());
		this.setReason(release.getReason());
		this.setDiffDate(release.getDiffDate());
	}

	public Integer getResults() {
		return results;
	}

	public void setResults(Integer results) {
		this.results = results;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String getNuked() {
		return nuked;
	}

	public void setNuked(String nuked) {
		this.nuked = nuked;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Integer getDiffDate() {
		return diffDate;
	}

	public void setDiffDate(Integer diffDate) {
		this.diffDate = diffDate;
	}
}