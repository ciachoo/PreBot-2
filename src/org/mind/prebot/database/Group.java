/**
 * Group
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
 * Manage a {@link Group}
 * @author Sh1fT
 *
 */
public class Group {
	private Integer totalReleases;
	private Integer totalNukes;
	private Integer totalUnnukes;
	private String categoryFirstPre;
	private String firstPre;
	private String dateFirstPre;
	private String categoryLastPre;
	private String lastPre;
	private String dateLastPre;

	/**
	 * Create a new {@link Group} instance
	 */
	public Group() {
		this.setTotalReleases(null);
		this.setTotalNukes(null);
		this.setTotalUnnukes(null);
		this.setCategoryFirstPre(null);
		this.setFirstPre(null);
		this.setDateFirstPre(null);
		this.setCategoryLastPre(null);
		this.setLastPre(null);
		this.setDateLastPre(null);
	}

	/**
	 * Create a new {@link Group} instance
	 * @param totalReleases
	 * @param totalNukes
	 * @param totalUnnukes
	 * @param categoryFirstPre
	 * @param firstPre
	 * @param dateFirstPre
	 * @param categoryLastPre
	 * @param lastPre
	 * @param dateLastPre
	 */
	public Group(Integer totalReleases, Integer totalNukes,
			Integer totalUnnukes, String categoryFirstPre,
			String firstPre, String dateFirstPre, String categoryLastPre,
			String lastPre, String dateLastPre) {
		this.setTotalReleases(totalReleases);
		this.setTotalNukes(totalNukes);
		this.setTotalUnnukes(totalUnnukes);
		this.setCategoryFirstPre(categoryFirstPre);
		this.setFirstPre(firstPre);
		this.setDateFirstPre(dateFirstPre);
		this.setCategoryLastPre(categoryLastPre);
		this.setLastPre(lastPre);
		this.setDateLastPre(dateLastPre);
	}

	/**
	 * Create a new {@link Group} instance
	 * @param group
	 */
	public Group(Group group) {
		this.setTotalReleases(group.getTotalReleases());
		this.setTotalNukes(group.getTotalNukes());
		this.setTotalUnnukes(group.getTotalUnnukes());
		this.setCategoryFirstPre(group.getCategoryFirstPre());
		this.setFirstPre(group.getFirstPre());
		this.setDateFirstPre(group.getDateFirstPre());
		this.setCategoryLastPre(group.getCategoryLastPre());
		this.setLastPre(group.getLastPre());
		this.setDateLastPre(group.getDateLastPre());
	}

	public Integer getTotalReleases() {
		return totalReleases;
	}

	public void setTotalReleases(Integer totalReleases) {
		this.totalReleases = totalReleases;
	}

	public Integer getTotalNukes() {
		return totalNukes;
	}

	public void setTotalNukes(Integer totalNukes) {
		this.totalNukes = totalNukes;
	}

	public Integer getTotalUnnukes() {
		return totalUnnukes;
	}

	public void setTotalUnnukes(Integer totalUnnukes) {
		this.totalUnnukes = totalUnnukes;
	}

	public String getCategoryFirstPre() {
		return categoryFirstPre;
	}

	public void setCategoryFirstPre(String categoryFirstPre) {
		this.categoryFirstPre = categoryFirstPre;
	}

	public String getFirstPre() {
		return firstPre;
	}

	public void setFirstPre(String firstPre) {
		this.firstPre = firstPre;
	}

	public String getDateFirstPre() {
		return dateFirstPre;
	}

	public void setDateFirstPre(String dateFirstPre) {
		this.dateFirstPre = dateFirstPre;
	}

	public String getCategoryLastPre() {
		return categoryLastPre;
	}

	public void setCategoryLastPre(String categoryLastPre) {
		this.categoryLastPre = categoryLastPre;
	}

	public String getLastPre() {
		return lastPre;
	}

	public void setLastPre(String lastPre) {
		this.lastPre = lastPre;
	}

	public String getDateLastPre() {
		return dateLastPre;
	}

	public void setDateLastPre(String dateLastPre) {
		this.dateLastPre = dateLastPre;
	}
}