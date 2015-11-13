/**
 * MySQLManager
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.Properties;

import org.jibble.pircbot.Colors;
import org.mind.prebot.main.Main;
import org.mind.prebot.utils.Utils;

/**
 * Manage a {@link MySQLManager}
 * @author Sh1fT
 *
 */
public class MySQLManager extends Thread {
	private Main parent;
	private Boolean stop;
	private Connection mySQLConnection;
	private Statement mySQLStatement;

	/**
	 * Create a new {@link MySQLManager} instance
	 * @param parent
	 */
	public MySQLManager(Main parent) {
		this.setParent(parent);
		this.setStop(false);
		this.setMySQLConnection(null);
		this.setMySQLStatement(null);
		this.init();
	}

	public Main getParent() {
		return parent;
	}

	public void setParent(Main parent) {
		this.parent = parent;
	}

	public Boolean getStop() {
		return stop;
	}

	public void setStop(Boolean stop) {
		this.stop = stop;
	}

	public Connection getMySQLConnection() {
		return mySQLConnection;
	}

	public void setMySQLConnection(Connection mySQLConnection) {
		this.mySQLConnection = mySQLConnection;
	}

	public Statement getMySQLStatement() {
		return mySQLStatement;
	}

	public void setMySQLStatement(Statement mySQLStatement) {
		this.mySQLStatement = mySQLStatement;
	}

	/** 
     * Initialize the connection with the MySQL database
     */
	public void init() {
		String url = "jdbc:mysql://" + this.getParent().getMySQLHost() + ":" +
				this.getParent().getMySQLPort() + "/" + this.getParent().getMySQLDatabase();
		Properties p = new Properties();
		p.setProperty("user", this.getParent().getMySQLUsername());
		p.setProperty("password", this.getParent().getMySQLPassword());
		p.setProperty("autoReconnect", "true");
        try {
        	Class.forName("com.mysql.jdbc.Driver");
            this.setMySQLConnection(DriverManager.getConnection(url, p));
            this.getMySQLConnection().setAutoCommit(true);
            this.setMySQLStatement(this.getMySQLConnection().createStatement(
            		ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE));
        } catch (SQLException | ClassNotFoundException ex) {
        	System.out.printf("Error: " + ex.getLocalizedMessage());
            System.exit(1);
        }
     }

	/** 
     * Close the connection with the MySQL database
     */
	public void close() {
        try {
            this.getMySQLStatement().close();
            this.getMySQLConnection().close();
        } catch (SQLException ex) {
        	System.out.printf("Error: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }

	/**
	 * Search for a PRED {@link Release}
	 * @param releaseName
	 * @return
	 */
    public Release pre(String releaseName) {
    	try {
    		String query = "SELECT COUNT(*) AS Results, Date, Categorie, Nom, Taille, Checked, Nuked, Reason, " +
    				"TIMESTAMPDIFF(SECOND, Date, NOW()) AS DiffDate FROM " +
					"(SELECT * FROM releases WHERE Nom LIKE ?";
    		String[] releasesName = releaseName.split(" ");
    		for (Integer i=1; i < releasesName.length; i++)
    			query += " AND Nom LIKE ?";
    		query += " ORDER BY Date DESC) AS tab LIMIT 1;";
			PreparedStatement ps = this.getMySQLConnection().prepareStatement(query);
			for (Integer i=0; i < releasesName.length; i++)
    			ps.setString(i+1, "%" + releasesName[i] + "%");
			ResultSet rs = ps.executeQuery();
			Release release = new Release();
			if (rs.next()) {
				release.setResults(rs.getInt("Results"));
				release.setDate(rs.getString("Date"));
				release.setCategory(rs.getString("Categorie"));
				release.setName(rs.getString("Nom"));
				release.setSize(rs.getString("Taille"));
				release.setChecked(rs.getString("Checked"));
				release.setNuked(rs.getString("Nuked"));
				release.setReason(rs.getString("Reason"));
				release.setDiffDate(rs.getInt("DiffDate"));
			}
			rs.close();
			ps.close();
			return release;
		} catch (SQLException ex) {
			System.out.printf("Error: " + ex.getLocalizedMessage());
			this.close();
            System.exit(1);
		}
		return null;
    }

    /**
     * Search for a DUPE {@link Release}
     * @param releaseName
     * @param limit
     * @return
     */
    public LinkedList<Release> dupe(String releaseName, String limit) {
    	try {
    		PreparedStatement ps = null;
    		String query = "SELECT Date, Categorie, Nom, Taille, Checked, Nuked, Reason, " +
    				"TIMESTAMPDIFF(SECOND, Date, NOW()) AS DiffDate FROM " +
    				"(SELECT * FROM releases WHERE Nom LIKE ?";
    		String[] releasesName = releaseName.split(" ");
    		if (releasesName.length > 0) {
	    		for (Integer i=1; i < releasesName.length; i++)
	    			query += " AND Nom LIKE ?";
	    		query += " ORDER BY Date DESC) AS tab LIMIT ?;";
	    		ps = this.getMySQLConnection().prepareStatement(query);
	    		for (Integer i=0; i < releasesName.length; i++)
	    			ps.setString(i+1, "%" + releasesName[i] + "%");
	    		try {
	    			ps.setInt(releasesName.length+1, Integer.parseInt(limit) > 0 ? Integer.parseInt(limit) : 10);
	    		} catch (NumberFormatException ex) {
	    			ps.setInt(releasesName.length+1, 10);
	    		}
    		} else {
    			query = "SELECT Date, Categorie, Nom, Taille, Checked, Nuked, Reason, " +
        				"TIMESTAMPDIFF(SECOND, Date, NOW()) AS DiffDate " +
    					"FROM releases ORDER BY Date DESC LIMIT ?;";
    			ps = this.getMySQLConnection().prepareStatement(query);
    			try {
    				ps.setInt(1, Integer.parseInt(limit) > 0 ? Integer.parseInt(limit) : 10);
    			} catch (NumberFormatException ex) {
    				ps.setInt(1, 10);
    			}
    		}
			ResultSet rs = ps.executeQuery();
			LinkedList<Release> releases = new LinkedList<Release>();
			Integer results = 0;
			while (rs.next()) {
				Release release = new Release();
				release.setDate(rs.getString("Date"));
				release.setCategory(rs.getString("Categorie"));
				release.setName(rs.getString("Nom"));
				release.setSize(rs.getString("Taille"));
				release.setChecked(rs.getString("Checked"));
				release.setNuked(rs.getString("Nuked"));
				release.setReason(rs.getString("Reason"));
				release.setDiffDate(rs.getInt("DiffDate"));
				releases.add(release);
				results++;
			}
			if (!releases.isEmpty())
				releases.get(0).setResults(results);
			rs.close();
			ps.close();
			return releases;
		} catch (SQLException ex) {
			System.out.printf("Error: " + ex.getLocalizedMessage());
			this.close();
            System.exit(1);
		}
		return null;
	}

    /**
     * Search for a DUPE NUKED {@link Release}
     * @param releaseName
     * @param limit
     * @return
     */
    public LinkedList<Release> dupenuke(String releaseName, String limit) {
    	try {
    		PreparedStatement ps = null;
    		String query = "SELECT Date, Categorie, Nom, Taille, Checked, Nuked, Reason, " +
    				"TIMESTAMPDIFF(SECOND, Date, NOW()) AS DiffDate FROM " +
    				"(SELECT * FROM releases WHERE Nom LIKE ? AND Nuked IS TRUE AND Checked IS FALSE";
    		String[] releasesName = releaseName.split(" ");
    		if (releasesName.length > 0) {
	    		for (Integer i=1; i < releasesName.length; i++)
	    			query += " AND Nom LIKE ?";
	    		query += " ORDER BY Date DESC) AS tab LIMIT ?;";
	    		ps = this.getMySQLConnection().prepareStatement(query);
	    		for (Integer i=0; i < releasesName.length; i++)
	    			ps.setString(i+1, "%" + releasesName[i] + "%");
	    		try {
	    			ps.setInt(releasesName.length+1, Integer.parseInt(limit) > 0 ? Integer.parseInt(limit) : 10);
	    		} catch (NumberFormatException ex) {
	    			ps.setInt(releasesName.length+1, 10);
	    		}
    		} else {
    			query = "SELECT Date, Categorie, Nom, Taille, Checked, Nuked, Reason, " +
        				"TIMESTAMPDIFF(SECOND, Date, NOW()) AS DiffDate " +
    					"FROM releases WHERE Nuked IS TRUE AND Checked IS FALSE ORDER BY Date DESC LIMIT ?;";
    			ps = this.getMySQLConnection().prepareStatement(query);
    			try {
    				ps.setInt(1, Integer.parseInt(limit) > 0 ? Integer.parseInt(limit) : 10);
    			} catch (NumberFormatException ex) {
    				ps.setInt(1, 10);
    			}
    		}
			ResultSet rs = ps.executeQuery();
			LinkedList<Release> releases = new LinkedList<Release>();
			Integer results = 0;
			while (rs.next()) {
				Release release = new Release();
				release.setDate(rs.getString("Date"));
				release.setCategory(rs.getString("Categorie"));
				release.setName(rs.getString("Nom"));
				release.setSize(rs.getString("Taille"));
				release.setChecked(rs.getString("Checked"));
				release.setNuked(rs.getString("Nuked"));
				release.setReason(rs.getString("Reason"));
				release.setDiffDate(rs.getInt("DiffDate"));
				releases.add(release);
				results++;
			}
			if (!releases.isEmpty())
				releases.get(0).setResults(results);
			rs.close();
			ps.close();
			return releases;
		} catch (SQLException ex) {
			System.out.printf("Error: " + ex.getLocalizedMessage());
			this.close();
            System.exit(1);
		}
		return null;
	}

    /**
     * NUKE a {@link Release}
     * @param releaseName
     * @param reason
     * @return
     */
    public Integer nuke(String releaseName, String reason) {
    	try {
    		String query =
    				"UPDATE releases SET Nuked = TRUE, Checked = FALSE, Reason = ?, Announced = FALSE WHERE Nom LIKE ?;";
    		PreparedStatement ps = this.getMySQLConnection().prepareStatement(query);
			ps.setString(1, reason);
			ps.setString(2, releaseName);
			Integer rs = ps.executeUpdate();
			ps.close();
			return rs;
		} catch (SQLException ex) {
			System.out.printf("Error: " + ex.getLocalizedMessage());
			this.close();
            System.exit(1);
		}
		return null;
    }

    /**
     * UNNUKE a {@link Release}
     * @param releaseName
     * @param reason
     * @return
     */
    public Integer unnuke(String releaseName, String reason) {
    	try {
    		String query = "UPDATE releases SET Checked = TRUE, Reason = ?, Announced = FALSE WHERE Nom LIKE ?;";
    		PreparedStatement ps = this.getMySQLConnection().prepareStatement(query);
			ps.setString(1, reason);
			ps.setString(2, releaseName);
			Integer rs = ps.executeUpdate();
			ps.close();
			return rs;
		} catch (SQLException ex) {
			System.out.printf("Error: " + ex.getLocalizedMessage());
			this.close();
            System.exit(1);
		}
		return null;
    }

    /**
     * ADDPRE a {@link Release}
     * @param releaseName
     * @param releaseCategory
     * @param releaseSize
     * @return
     */
    public Integer addpre(String releaseName, String releaseCategory, String releaseSize) {
    	try {
    		String query =
    				"INSERT INTO releases(Date, Categorie, Nom, Taille, Nuked, Reason, Checked) VALUES (NOW(), ?, ?, ?, 0, '', 0);";
    		PreparedStatement ps = this.getMySQLConnection().prepareStatement(query);
			ps.setString(1, releaseCategory);
			ps.setString(2, releaseName);
			ps.setString(3, releaseSize);
			Integer rs = ps.executeUpdate();
			ps.close();
			return rs;
		} catch (SQLException ex) {
			System.out.printf("Error: " + ex.getLocalizedMessage());
			// avoid duplicate primary key exception
			if (!(ex.getErrorCode() == 1062)) {
				this.close();
				System.exit(1);
			}
		}
		return 0;
    }
    
    /**
     * DELPRE a {@link Release}
     * @param releaseName
     * @param releaseCategory
     * @param releaseSize
     * @return
     */
    public Integer delpre(String releaseName) {
    	try {
    		String query = "DELETE FROM releases WHERE Nom LIKE ?;";
    		PreparedStatement ps = this.getMySQLConnection().prepareStatement(query);
			ps.setString(1, releaseName);
			Integer rs = ps.executeUpdate();
			ps.close();
			return rs;
		} catch (SQLException ex) {
			System.out.printf("Error: " + ex.getLocalizedMessage());
			this.close();
            System.exit(1);
		}
		return 0;
    }

    /**
     * Do the login for a user
     * @param username
     * @param password
     * @return
     */
    public Boolean login(String username, String password) {
    	try {
    		String query = "SELECT * FROM users WHERE name LIKE ? AND password LIKE ?";
    		PreparedStatement ps = this.getMySQLConnection().prepareStatement(query);
    		ps.setString(1, username);
    		ps.setString(2, Utils.encryptPassword(password));
    		ResultSet rs = ps.executeQuery();
    		if (rs.next())
    			return true;
    		return false;
    	} catch (SQLException ex) {
			System.out.printf("Error: " + ex.getLocalizedMessage());
			this.close();
            System.exit(1);
		}
		return null;
    }

    /**
	 * Search for a {@link Request}
	 * @param releaseName
	 * @return
	 */
    public Request showrequest(String requestName) {
    	try {
    		String query = "SELECT COUNT(*) AS Results, request, requestBy, requestDate, filled, filledBy, " +
    				"TIMESTAMPDIFF(SECOND, requestDate, NOW()) AS DiffDate FROM " +
					"(SELECT * FROM requests WHERE request LIKE ?";
    		String[] requestsName = requestName.split(" ");
    		for (Integer i=1; i < requestsName.length; i++)
    			query += " AND request LIKE ?";
    		query += " ORDER BY requestDate DESC) AS tab LIMIT 1;";
			PreparedStatement ps = this.getMySQLConnection().prepareStatement(query);
			for (Integer i=0; i < requestsName.length; i++)
    			ps.setString(i+1, "%" + requestsName[i] + "%");
			ResultSet rs = ps.executeQuery();
			Request request = new Request();
			if (rs.next()) {
				request.setResults(rs.getInt("Results"));
				request.setRequest(rs.getString("request"));
				request.setRequestBy(rs.getString("requestBy"));
				request.setRequestDate(rs.getDate("requestDate"));
				request.setFilled(rs.getBoolean("filled"));
				request.setFilledBy(rs.getString("filledBy"));
				request.setDiffDate(rs.getInt("DiffDate"));
			}
			rs.close();
			ps.close();
			return request;
		} catch (SQLException ex) {
			System.out.printf("Error: " + ex.getLocalizedMessage());
			this.close();
            System.exit(1);
		}
		return null;
    }

    /**
     * Add a {@link Request}
     * @param request
     * @return
     */
    public Integer addrequest(Request request) {
    	try {
    		String query = "INSERT INTO requests VALUES(?, ?, NOW(), ?, ?);";
    		PreparedStatement ps = this.getMySQLConnection().prepareStatement(query);
    		ps.setString(1, request.getRequest());
    		ps.setString(2, request.getRequestBy());
    		ps.setBoolean(3, request.getFilled());
    		ps.setString(4, request.getFilledBy());
    		Integer rs = ps.executeUpdate();
			ps.close();
			return rs;
		} catch (SQLException ex) {
			System.out.printf("Error: " + ex.getLocalizedMessage());
			// avoid duplicate primary key exception
			if (!(ex.getErrorCode() == 1062)) {
				this.close();
				System.exit(1);
			}
		}
		return 0;
	}

    /**
     * Fill a {@link Request}
     * @param request
     * @param filledBy
     * @return
     */
    public Integer fillrequest(String request, String filledBy) {
    	try {
    		String query = "UPDATE requests SET filled = TRUE, filledBy = ? WHERE request LIKE ?;";
    		PreparedStatement ps = this.getMySQLConnection().prepareStatement(query);
    		ps.setString(1, filledBy);
    		ps.setString(2, request);
    		Integer rs = ps.executeUpdate();
			ps.close();
			return rs;
		} catch (SQLException ex) {
			System.out.printf("Error: " + ex.getLocalizedMessage());
			this.close();
            System.exit(1);
		}
		return null;
	}

    /**
     * Search for a DUPE {@link Request}
     * @param requestName
     * @param limit
     * @return
     */
    public LinkedList<Request> duperequest(String requestName, String limit) {
    	try {
    		PreparedStatement ps = null;
    		String query = "SELECT request, requestBy, requestDate, filled, filledBy, " +
    				"TIMESTAMPDIFF(SECOND, requestDate, NOW()) AS DiffDate FROM " +
    				"(SELECT * FROM requests WHERE request LIKE ?";
    		String[] requestsName = requestName.split(" ");
    		if (requestsName.length > 0) {
	    		for (Integer i=1; i < requestsName.length; i++)
	    			query += " AND request LIKE ?";
	    		query += " ORDER BY requestDate DESC) AS tab LIMIT ?;";
	    		ps = this.getMySQLConnection().prepareStatement(query);
	    		for (Integer i=0; i < requestsName.length; i++)
	    			ps.setString(i+1, "%" + requestsName[i] + "%");
	    		try {
	    			ps.setInt(requestsName.length+1, Integer.parseInt(limit) > 0 ? Integer.parseInt(limit) : 10);
	    		} catch (NumberFormatException ex) {
	    			ps.setInt(requestsName.length+1, 10);
	    		}
    		} else {
    			query = "SELECT request, requestBy, requestDate, filled, filledBy, " +
        				"TIMESTAMPDIFF(SECOND, requestDate, NOW()) AS DiffDate " +
    					"FROM requests ORDER BY requestDate DESC LIMIT ?;";
    			ps = this.getMySQLConnection().prepareStatement(query);
    			try {
    				ps.setInt(1, Integer.parseInt(limit) > 0 ? Integer.parseInt(limit) : 10);
    			} catch (NumberFormatException ex) {
    				ps.setInt(1, 10);
    			}
    		}
			ResultSet rs = ps.executeQuery();
			LinkedList<Request> requests = new LinkedList<Request>();
			Integer results = 0;
			while (rs.next()) {
				Request request = new Request();
				request.setRequest(rs.getString("request"));
				request.setRequestBy(rs.getString("requestBy"));
				request.setRequestDate(rs.getDate("requestDate"));
				request.setFilled(rs.getBoolean("filled"));
				request.setFilledBy(rs.getString("filledBy"));
				request.setDiffDate(rs.getInt("DiffDate"));
				requests.add(request);
				results++;
			}
			if (!requests.isEmpty())
				requests.get(0).setResults(results);
			rs.close();
			ps.close();
			return requests;
		} catch (SQLException ex) {
			System.out.printf("Error: " + ex.getLocalizedMessage());
			this.close();
            System.exit(1);
		}
		return null;
	}

    /**
     * Show group info
     * @return
     */
    public Group group() {
    	try {
    		Group group = new Group();
    		ResultSet rs = this.getMySQLStatement().executeQuery("SELECT COUNT(*) AS Results FROM releases;");
    		if (rs.next())
    			group.setTotalReleases(rs.getInt("Results"));
    		rs.close();
    		rs = this.getMySQLStatement().executeQuery(
    				"SELECT COUNT(*) AS Results FROM releases WHERE Nuked IS TRUE AND Checked IS FALSE;");
    		if (rs.next())
    			group.setTotalNukes(rs.getInt("Results"));
    		rs.close();
    		rs = this.getMySQLStatement().executeQuery(
    				"SELECT COUNT(*) AS Results FROM releases WHERE Nuked IS TRUE AND Checked IS TRUE;");
    		if (rs.next())
    			group.setTotalUnnukes(rs.getInt("Results"));
    		rs.close();
    		rs = this.getMySQLStatement().executeQuery(
    				"SELECT Nom, Categorie, Date FROM releases GROUP BY Date ASC LIMIT 1;");
    		if (rs.next()) {
    			group.setCategoryFirstPre(rs.getString("Categorie"));
    			group.setFirstPre(rs.getString("Nom"));
    			group.setDateFirstPre(rs.getString("Date"));
    		}
    		rs.close();
    		rs = this.getMySQLStatement().executeQuery(
    				"SELECT Nom, Categorie, Date FROM releases GROUP BY Date DESC LIMIT 1;");
    		if (rs.next()) {
    			group.setCategoryLastPre(rs.getString("Categorie"));
    			group.setLastPre(rs.getString("Nom"));
    			group.setDateLastPre(rs.getString("Date"));
    		}
    		rs.close();
    		return group;
    	} catch (SQLException ex) {
			System.out.printf("Error: " + ex.getLocalizedMessage());
			this.close();
            System.exit(1);
		}
		return null;
    }

    @Override
    public void run() {
    	while (!this.getStop()) {
    		try {
    			ResultSet rs = this.getMySQLStatement().
    					executeQuery("SELECT Announced, Categorie, Nom, Taille, Reason, Nuked, Checked FROM releases " + 
    					"WHERE Announced IS FALSE;");
				while(rs.next()) {
					if (!rs.getBoolean("Announced")) {
						String category = rs.getString("Categorie");
						String name = rs.getString("Nom");
						String size = rs.getString("Taille");
						String reason = rs.getString("Reason");
						if (rs.getString("Nuked").equals("0")) {
							String cmd = Colors.TEAL + "[" + Colors.YELLOW + " PRE " + Colors.TEAL + "][ " +
									Utils.getCategoryCode(category) + category + Colors.TEAL + " ][ " +
									Colors.LIGHT_GRAY + name + Colors.TEAL + " ][ " + Colors.OLIVE + size + Colors.TEAL +
									" ]";
							this.getParent().getPrebot().sendMessage(this.getParent().getIRCMindPreChannel(),
									this.getParent().getPrebot().encryptData(cmd));
						} else if (rs.getString("Nuked").equals("1") && rs.getString("Checked").equals("0")) {
							String cmd = Colors.TEAL + "[" + Colors.RED + " NUKED " + Colors.TEAL + "][ " +
									Utils.getCategoryCode(category) + category + Colors.TEAL + " ][ " +
									Colors.LIGHT_GRAY + name + Colors.TEAL + " ][ " + Colors.RED + reason + Colors.TEAL +
									" ][ " + Colors.OLIVE + size + Colors.TEAL + " ]";
							this.getParent().getPrebot().sendMessage(this.getParent().getIRCMindPreChannel(),
									this.getParent().getPrebot().encryptData(cmd));
						} else if (rs.getString("Nuked").equals("1") && rs.getString("Checked").equals("1")) {
							String cmd = Colors.TEAL + "[" + Colors.DARK_GREEN + " UNNUKED " + Colors.TEAL + "][ " +
									Utils.getCategoryCode(category) + category + Colors.TEAL + " ][ " +
									Colors.LIGHT_GRAY + name + Colors.TEAL + " ]";
							if (!reason.isEmpty())
								cmd += Colors.TEAL + "[ " + Colors.DARK_GREEN + reason + Colors.TEAL + " ]";
							cmd += Colors.TEAL + "[ " + Colors.OLIVE + size + Colors.TEAL + " ]";
							this.getParent().getPrebot().sendMessage(this.getParent().getIRCMindPreChannel(),
									this.getParent().getPrebot().encryptData(cmd));
						}
						rs.updateBoolean("Announced", true);
						rs.updateRow();
					}
				}
				rs.close();
				rs = this.getMySQLStatement().executeQuery(
						"SELECT soundtrack, capper, format, completed, announced, size, speed FROM soundtracks " +
						"WHERE announced IS FALSE;");
				while(rs.next()) {
					if (!rs.getBoolean("announced")) {
						String soundtrack = rs.getString("soundtrack");
						String capper = rs.getString("capper");
						String format = rs.getString("format");
						Boolean completed = rs.getBoolean("completed");
						Long size = rs.getLong("size");
						Integer speed = rs.getInt("speed");
						DecimalFormat df = new DecimalFormat(); 
						DecimalFormatSymbols dfs = new DecimalFormatSymbols();
						dfs.setDecimalSeparator('.');
						df.setDecimalFormatSymbols(dfs);
						df.setMaximumFractionDigits(2);
						Double properSize = Double.parseDouble(df.format(new Double(size/1024d/1024d)));
						String cmd = null;
						if (!completed)
							cmd = Colors.TEAL + "[" + Colors.YELLOW + " BS " + Colors.TEAL + "][" + Colors.DARK_GREEN +
								" Capper: " + Colors.OLIVE + capper + Colors.TEAL + " ][" + Colors.DARK_GREEN +
								" SoundTrack: " + Colors.OLIVE + soundtrack + Colors.TEAL + " ][" + Colors.DARK_GREEN +
								" Format: " + Colors.OLIVE + format + Colors.TEAL + " ][" + Colors.DARK_GREEN +
								" State: " + Colors.OLIVE + "In progress " + Colors.TEAL + "]";
						else
							cmd = Colors.TEAL + "[" + Colors.YELLOW + " BS " + Colors.TEAL + "][" + Colors.DARK_GREEN +
								" Capper: " + Colors.OLIVE + capper + Colors.TEAL + " ][" + Colors.DARK_GREEN +
								" SoundTrack: " + Colors.OLIVE + soundtrack + Colors.TEAL + " ][" + Colors.DARK_GREEN +
								" Format: " + Colors.OLIVE + format + Colors.TEAL + " ][" + Colors.DARK_GREEN +
								" Size: " + Colors.OLIVE + properSize + " MiB " + Colors.TEAL + "][" +
								Colors.DARK_GREEN + " Speed: " + Colors.OLIVE + "~ " + speed + " Kb/s " + Colors.TEAL +
								"][" + Colors.DARK_GREEN + " State: " + Colors.OLIVE + "Completed " + Colors.TEAL + "]";
						this.getParent().getPrebot().sendMessage(this.getParent().getIRCMindSpamChannel(),
								this.getParent().getPrebot().encryptData(cmd));
						rs.updateBoolean("announced", true);
						rs.updateRow();
					}
				}
				rs.close();
				sleep(1000);
			} catch (SQLException ex) {
				System.out.printf("Error: " + ex.getLocalizedMessage());
				this.close();
			    System.exit(1);
			} catch (InterruptedException ex) {
				this.setStop(true);
				this.close();
			}
    	}
    }
}