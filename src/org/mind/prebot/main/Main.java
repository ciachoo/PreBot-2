/**
 * Main
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

package org.mind.prebot.main;

import java.util.Properties;

import org.mind.prebot.database.MySQLManager;
import org.mind.prebot.robot.PreBot;
import org.mind.prebot.utils.PropertiesLauncher;

/**
 * Manage a {@link Main}
 * @author Sh1fT
 *
 */
public class Main {
	private PropertiesLauncher propertiesLauncher;
	private MySQLManager mySQLManager;
	private PreBot prebot;

	/**
	 * Create a new {@link Main} instance
	 */
	public Main() {
		this.setPropertiesLauncher(new PropertiesLauncher("PreBot.properties"));
		this.setMySQLManager(new MySQLManager(this));
		this.setPrebot(new PreBot(this));
		this.getPrebot().setVerbose(true);
	}

	public PropertiesLauncher getPropertiesLauncher() {
		return propertiesLauncher;
	}

	public void setPropertiesLauncher(PropertiesLauncher propertiesLauncher) {
		this.propertiesLauncher = propertiesLauncher;
	}

	public MySQLManager getMySQLManager() {
		return mySQLManager;
	}

	public void setMySQLManager(MySQLManager mySQLManager) {
		this.mySQLManager = mySQLManager;
	}

	public PreBot getPrebot() {
		return prebot;
	}

	public void setPrebot(PreBot prebot) {
		this.prebot = prebot;
	}

	public Properties getProperties() {
		return this.getPropertiesLauncher().getProperties();
	}

	public String getMySQLHost() {
		return this.getProperties().getProperty("MySQLHost");
	}

	public Integer getMySQLPort() {
		return Integer.parseInt(this.getProperties().getProperty("MySQLPort"));
	}

	public String getMySQLDatabase() {
		return this.getProperties().getProperty("MySQLDatabase");
	}

	public String getMySQLTable() {
		return this.getProperties().getProperty("MySQLTable");
	}

	public String getMySQLUsername() {
		return this.getProperties().getProperty("MySQLUsername");
	}

	public String getMySQLPassword() {
		return this.getProperties().getProperty("MySQLPassword");
	}

	public String getIRCHost() {
		return this.getProperties().getProperty("IRCHost");
	}

	public Integer getIRCPort() {
		return Integer.parseInt(this.getProperties().getProperty("IRCPort"));
	}
	
	public String getIRCPaswword() {
		return this.getProperties().getProperty("IRCPassword");
	}

	public String getIRCMindChannel() {
		return this.getProperties().getProperty("IRCMindChannel");
	}

	public String getIRCMindChannelKey() {
		return this.getProperties().getProperty("IRCMindChannelKey");
	}

	public String getIRCMindPreChannel() {
		return this.getProperties().getProperty("IRCMindPreChannel");
	}
	
	public String getIRCMindPreSearchChannel() {
		return this.getProperties().getProperty("IRCMindPreSearchChannel");
	}
	
	public String getIRCMindSpamChannel() {
		return this.getProperties().getProperty("IRCMindSpamChannel");
	}
	
	public String getIRCPreChannel() {
		return this.getProperties().getProperty("IRCPreChannel");
	}
	
	public String getIRCGksChannel() {
		return this.getProperties().getProperty("IRCGksChannel");
	}

	public String getIRCBotName() {
		return this.getProperties().getProperty("IRCBotName");
	}

	public String getIRCBotPassword() {
		return this.getProperties().getProperty("IRCBotPassword");
	}

	public String getIRCFishKey() {
		return this.getProperties().getProperty("IRCFishKey");
	}

	/**
	 * Launch the {@link Main}
	 */
	public void launch() {
		this.getMySQLManager().start();
	}

	public static void main(String[] args) {
       new Main().launch();
    }
}