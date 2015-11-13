/**
 * PreBot
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

package org.mind.prebot.robot;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jibble.pircbot.Colors;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.TrustingSSLSocketFactory;
import org.mind.prebot.database.Group;
import org.mind.prebot.database.MySQLManager;
import org.mind.prebot.database.Release;
import org.mind.prebot.database.Request;
import org.mind.prebot.main.Main;
import org.mind.prebot.utils.Utils;

import fr.k2r.blowfish.BlowfishCBC;

/**
 * Manage a {@link PreBot}
 * @author Sh1fT
 *
 */
public class PreBot extends PircBot {
	private Main parent;
	private MySQLManager mySQLManager;
	private Integer slaps;
	private Integer slapsRandom;
	private BlowfishCBC fish;
	private List<String> ignoredList;
	private List<String> nukerList;

	/**
	 * Create a new {@link PreBot} instance
	 * @param parent
	 */
	public PreBot(Main parent) {
		this.setParent(parent);
		this.setMySQLManager(this.getParent().getMySQLManager());
		this.setSlaps(0);
		this.setSlapsRandom(new Random().nextInt(26));
		this.setFish(new BlowfishCBC(this.getParent().getIRCFishKey()));
		this.setIgnoredList(new LinkedList<String>());
		this.setNukerList(new LinkedList<String>());
		this.init();
	}

	public Main getParent() {
		return parent;
	}

	public void setParent(Main parent) {
		this.parent = parent;
	}

	public MySQLManager getMySQLManager() {
		return mySQLManager;
	}

	public void setMySQLManager(MySQLManager mySQLManager) {
		this.mySQLManager = mySQLManager;
	}

	public Integer getSlaps() {
		return slaps;
	}

	public void setSlaps(Integer slaps) {
		this.slaps = slaps;
	}

	public Integer getSlapsRandom() {
		return slapsRandom;
	}

	public void setSlapsRandom(Integer slapsRandom) {
		this.slapsRandom = slapsRandom;
	}

	public BlowfishCBC getFish() {
		return fish;
	}

	public void setFish(BlowfishCBC fish) {
		this.fish = fish;
	}
	
	public List<String> getIgnoredList() {
		return ignoredList;
	}
	
	public void setIgnoredList(List<String> ignoredList) {
		this.ignoredList = ignoredList;
	}

	public List<String> getNukerList() {
		return nukerList;
	}

	public void setNukerList(List<String> nukerList) {
		this.nukerList = nukerList;
	}

	/**
	 * Encrypt data using Blowfish algorithm
	 * @param data
	 * @return
	 */
	public String encryptData(String data) {
		return Utils.encryptData(this.getFish(), data);
	}

	/**
	 * Decrypt data using Blowfish algorithm
	 * @param data
	 * @param forceEncryption
	 * @return
	 */
	public String decryptData(String data, Boolean forceEncryption) {
		return Utils.decryptData(this.getFish(), data, forceEncryption);
	}

	/**
	 * Initialize the {@link PreBot}
	 */
	public void init() {
		try {
			this.setName(this.getParent().getIRCBotName());
			this.connect(this.getParent().getIRCHost(), this.getParent().getIRCPort(),
					this.getParent().getIRCPaswword(), new TrustingSSLSocketFactory());
			this.sendMessage("Nickserv", "identify " + this.getParent().getIRCBotPassword());
			Thread.sleep(15000);
			this.sendMessage("ChanServ", "invite " + this.getParent().getIRCMindChannel());
			this.joinChannel(this.getParent().getIRCMindChannel());
			this.joinChannel(this.getParent().getIRCMindPreChannel());
			this.joinChannel(this.getParent().getIRCMindPreSearchChannel());
			this.joinChannel(this.getParent().getIRCMindSpamChannel());
			this.joinChannel(this.getParent().getIRCPreChannel());
			this.joinChannel(this.getParent().getIRCGksChannel());
		} catch (IOException | IrcException | InterruptedException ex) {
			System.out.printf("Error: " + ex.getLocalizedMessage());
            System.exit(1);
		}
	}

	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		if (!this.getIgnoredList().contains(sender)) {
			String years_str = "", months_str = "", days_str = "", hours_str = "", minutes_str = "", seconds_str = "";
			String[] tab = this.decryptData(message, true).trim().split(" ");
			if (tab.length > 0) {
				if (channel.equalsIgnoreCase(this.getParent().getIRCMindPreSearchChannel()) &&
						(tab[0].equalsIgnoreCase("!pre") || tab[0].equalsIgnoreCase("!p"))) {
					if (tab.length > 1) {
						String names = "";
						for (Integer i=1; i < tab.length; i++)
							names += tab[i] + " ";
						Release release = this.getMySQLManager().pre(names.trim());
						if (release.getResults().equals(0))
							this.sendMessage(channel, this.encryptData("Nothing found for your search: " + names.trim()));
						else {
							if (release.getResults() > 1)
								this.sendMessage(channel, this.encryptData(Colors.TEAL + "[ " + Colors.RED +
										release.getResults() + " results found! " + Colors.TEAL + "]"));
							else
								this.sendMessage(channel, this.encryptData(Colors.TEAL + "[ " + Colors.RED +
										release.getResults() + " result found! " + Colors.TEAL + "]"));
							Integer years = release.getDiffDate() / 31536000;
							Integer yearsMod = release.getDiffDate() % 31536000;
							if (years == 1)
								years_str = years + " year ";
							else if (years > 1)
								years_str = years + " years ";
							Integer months = yearsMod / 2592000;
							Integer monthsMod = yearsMod % 2592000;
							if (months == 1)
								months_str = months + " month ";
							else if (months > 1)
								 months_str = months + " months ";
							Integer days = monthsMod / 86400;
							Integer daysMod = monthsMod % 86400;
							if (days == 1)
								days_str = days + " day ";
							else if (days > 1)
								days_str = days + " days ";
							Integer hours = daysMod / 3600;
							Integer hoursMod = daysMod % 3600;
							if (hours == 1)
								hours_str = hours + " hour ";
							else if (hours > 1)
								hours_str = hours + " hours ";
							Integer minutes = hoursMod / 60;
							if (minutes == 1)
								minutes_str = minutes + " minute ";
							else if (minutes > 1)
								minutes_str = minutes + " minutes ";
							Integer seconds = hoursMod % 60;
							if (seconds == 1)
								seconds_str = seconds + " second ";
							else
								seconds_str = seconds + " seconds ";
							if (release.getChecked().equals("1") || release.getNuked().equals("0"))
								this.sendMessage(channel, this.encryptData(Colors.TEAL + "[" + Colors.YELLOW + " PRED " +
										Colors.TEAL + "][ " + Utils.getCategoryCode(release.getCategory()) +
										release.getCategory() + Colors.TEAL + " ][ " + Colors.LIGHT_GRAY +
										release.getName() + Colors.TEAL +" ][ " + Colors.LIGHT_GRAY + years_str +
										months_str + days_str + hours_str + minutes_str + seconds_str + "ago (" +
										release.getDate() + ") " + Colors.TEAL + "][ " + Colors.OLIVE + release.getSize() +
										Colors.TEAL + " ]" +
										(release.getChecked().equals("1") ? "" : "[ " + Colors.RED + "UNCHECKED" + Colors.TEAL + " ]")));
							else
								this.sendMessage(channel, this.encryptData(Colors.TEAL + "[" + Colors.RED + " NUKED " +
										Colors.TEAL + "][ " + Utils.getCategoryCode(release.getCategory()) +
										release.getCategory() + Colors.TEAL + " ][ " + Colors.LIGHT_GRAY +
										release.getName() + Colors.TEAL + " ][ " + Colors.LIGHT_GRAY + years_str +
										months_str + days_str + hours_str + minutes_str + seconds_str + "ago (" +
										release.getDate() + ") " + Colors.TEAL + "][ " + Colors.RED + release.getReason() +
										Colors.TEAL + " ][ " + Colors.OLIVE + release.getSize() + Colors.TEAL + " ]"));
						}
					}
				} else if (channel.equalsIgnoreCase(this.getParent().getIRCMindPreSearchChannel()) &&
						(tab[0].equalsIgnoreCase("!dupenuke") || tab[0].equalsIgnoreCase("!dnu"))) {
					String names = "";
					String limit = "10";
					for (Integer i=1; i < tab.length; i++) {
						if (tab[i].contains("limit"))
							limit = tab[i].substring(tab[i].indexOf("limit:")+6, tab[i].length());
						else
							names += tab[i] + " ";
					}
					LinkedList<Release> releases = null;
					if (tab.length > 1)
						releases = this.getMySQLManager().dupenuke(names.trim(), limit);
					else
						releases = this.getMySQLManager().dupenuke("", limit);
					if (releases.isEmpty())
						this.sendMessage(channel, this.encryptData(
								"Nothing found for your search: " + (tab.length > 1 ? names.trim() : "")));
					else {
						if (releases.get(0).getResults() > 1)
							this.sendMessage(channel, this.encryptData("Sending " + Colors.OLIVE + sender +
									Colors.LIGHT_GRAY + " last " + Colors.OLIVE + releases.get(0).getResults() +
									Colors.RED + " nuked " + Colors.LIGHT_GRAY + "results."));
						else
							this.sendMessage(channel, this.encryptData("Sending " + Colors.OLIVE + sender +
									Colors.LIGHT_GRAY + " last " + Colors.OLIVE + releases.get(0).getResults() +
									Colors.RED + " nuked " + Colors.LIGHT_GRAY + "results"));
						for (Release release : releases) {
							Integer years = release.getDiffDate() / 31536000;
							Integer yearsMod = release.getDiffDate() % 31536000;
							if (years == 1)
								years_str = years + " year ";
							else if (years > 1)
								years_str = years + " years ";
							Integer months = yearsMod / 2592000;
							Integer monthsMod = yearsMod % 2592000;
							if (months == 1)
								months_str = months + " month ";
							else if (months > 1)
								 months_str = months + " months ";
							Integer days = monthsMod / 86400;
							Integer daysMod = monthsMod % 86400;
							if (days == 1)
								days_str = days + " day ";
							else if (days > 1)
								days_str = days + " days ";
							Integer hours = daysMod / 3600;
							Integer hoursMod = daysMod % 3600;
							if (hours == 1)
								hours_str = hours + " hour ";
							else if (hours > 1)
								hours_str = hours + " hours ";
							Integer minutes = hoursMod / 60;
							if (minutes == 1)
								minutes_str = minutes + " minute ";
							else if (minutes > 1)
								minutes_str = minutes + " minutes ";
							Integer seconds = hoursMod % 60;
							if (seconds == 1)
								seconds_str = seconds + " second ";
							else
								seconds_str = seconds + " seconds ";
							this.sendMessage(sender, this.encryptData(Colors.TEAL + "[" + Colors.RED +  " NUKED " +
									Colors.TEAL + "][ " + Utils.getCategoryCode(release.getCategory()) +
									release.getCategory() +  Colors.TEAL + " ][ " + Colors.LIGHT_GRAY + release.getName() +
									Colors.TEAL + " ][ " + Colors.LIGHT_GRAY + years_str + months_str + days_str +
									hours_str + minutes_str + seconds_str + "ago (" + release.getDate() + ") " +
									Colors.TEAL + "][ " + Colors.RED + release.getReason() + Colors.TEAL + " ][ " +
									Colors.OLIVE + release.getSize() + Colors.TEAL + " ]"));
						}
					}
				} else if (channel.equalsIgnoreCase(this.getParent().getIRCMindPreSearchChannel()) &&
						(tab[0].equalsIgnoreCase("!dupe") || tab[0].equalsIgnoreCase("!d"))) {
					String names = "";
					String limit = "10";
					for (Integer i=1; i < tab.length; i++) {
						if (tab[i].contains("limit"))
							limit = tab[i].substring(tab[i].indexOf("limit:")+6, tab[i].length());
						else
							names += tab[i] + " ";
					}
					LinkedList<Release> releases = null;
					if (tab.length > 1)
						releases = this.getMySQLManager().dupe(names.trim(), limit);
					else
						releases = this.getMySQLManager().dupe("", limit);
					if (releases.isEmpty())
						this.sendMessage(channel, this.encryptData(
								"Nothing found for your search: " + (tab.length > 1 ? names.trim() : "")));
					else {
						if (releases.get(0).getResults() > 1)
							this.sendMessage(channel, this.encryptData("Sending " + Colors.OLIVE + sender +
									Colors.LIGHT_GRAY + " last " + Colors.OLIVE + releases.get(0).getResults() +
									Colors.LIGHT_GRAY + " results."));
						else
							this.sendMessage(channel, this.encryptData("Sending " + Colors.OLIVE + sender +
									Colors.LIGHT_GRAY + " last " + Colors.OLIVE + releases.get(0).getResults() +
									Colors.LIGHT_GRAY + " result."));
						for (Release release : releases) {
							Integer years = release.getDiffDate() / 31536000;
							Integer yearsMod = release.getDiffDate() % 31536000;
							if (years == 1)
								years_str = years + " year ";
							else if (years > 1)
								years_str = years + " years ";
							Integer months = yearsMod / 2592000;
							Integer monthsMod = yearsMod % 2592000;
							if (months == 1)
								months_str = months + " month ";
							else if (months > 1)
								 months_str = months + " months ";
							Integer days = monthsMod / 86400;
							Integer daysMod = monthsMod % 86400;
							if (days == 1)
								days_str = days + " day ";
							else if (days > 1)
								days_str = days + " days ";
							Integer hours = daysMod / 3600;
							Integer hoursMod = daysMod % 3600;
							if (hours == 1)
								hours_str = hours + " hour ";
							else if (hours > 1)
								hours_str = hours + " hours ";
							Integer minutes = hoursMod / 60;
							if (minutes == 1)
								minutes_str = minutes + " minute ";
							else if (minutes > 1)
								minutes_str = minutes + " minutes ";
							Integer seconds = hoursMod % 60;
							if (seconds == 1)
								seconds_str = seconds + " second ";
							else
								seconds_str = seconds + " seconds ";
							if (release.getChecked().equals("1") || release.getNuked().equals("0"))
								this.sendMessage(sender, this.encryptData(Colors.TEAL + "[" + Colors.YELLOW + " PRED " +
										Colors.TEAL + "][ " + Utils.getCategoryCode(release.getCategory()) +
										release.getCategory() +  Colors.TEAL + "][ " + Colors.LIGHT_GRAY +
										release.getName() + Colors.TEAL + "][ " + Colors.LIGHT_GRAY + years_str +
										months_str + days_str + hours_str + minutes_str + seconds_str + "ago (" +
										release.getDate() + ") " + Colors.TEAL + "][ " + Colors.OLIVE + release.getSize() +
										Colors.TEAL + " ]" +
										(release.getChecked().equals("1") ? "" : "[ " + Colors.RED + "UNCHECKED" + Colors.TEAL + " ]")));
							else
								this.sendMessage(sender, this.encryptData(Colors.TEAL + "[" + Colors.RED + " NUKED " +
										Colors.TEAL + "][ " + Utils.getCategoryCode(release.getCategory()) +
										release.getCategory() +  Colors.TEAL + " ][ " + Colors.LIGHT_GRAY +
										release.getName() + Colors.TEAL + " ][ " + Colors.LIGHT_GRAY + years_str +
										months_str + days_str + hours_str + minutes_str + seconds_str + "ago (" +
										release.getDate() + ") " + Colors.TEAL + "][ " + Colors.RED + release.getReason() +
										Colors.TEAL + " ][ " + Colors.OLIVE + release.getSize() + Colors.TEAL + " ]"));
						}
					}
				} else if (channel.equalsIgnoreCase(this.getParent().getIRCMindPreSearchChannel()) &&
						(tab[0].equalsIgnoreCase("!nuke") || tab[0].equalsIgnoreCase("!nk"))) {
					if (this.getNukerList().contains(sender)) {
						String names = "";
						for (Integer i=2; i < tab.length; i++)
							names += tab[i] + " ";
						if (tab.length > 2) {
							Integer ret = this.getMySQLManager().nuke(tab[1], names.trim());
							if (ret > 0)
								this.sendMessage(channel, this.encryptData(Colors.OLIVE + tab[1] + Colors.LIGHT_GRAY +
										" has been successfully " + Colors.RED + "nuked" + Colors.LIGHT_GRAY + "!"));
							else
								this.sendMessage(channel, this.encryptData(Colors.OLIVE + tab[1] + Colors.LIGHT_GRAY +
										" has not been successfully " + Colors.RED + "nuked" + Colors.LIGHT_GRAY + "!"));
						}
					} else
						this.sendMessage(channel, this.encryptData(sender + ": You've to be a nuker to do this."));
				} else if (channel.equalsIgnoreCase(this.getParent().getIRCMindPreSearchChannel()) &&
						(tab[0].equalsIgnoreCase("!unnuke") || tab[0].equalsIgnoreCase("!un"))) {
					if (this.getNukerList().contains(sender)) {
						String names = "";
						for (Integer i=2; i < tab.length; i++)
							names += tab[i] + " ";
						if (tab.length > 2) {
							Integer ret = this.getMySQLManager().unnuke(tab[1], names.trim());
							if (ret > 0)
								this.sendMessage(channel, this.encryptData(Colors.OLIVE + tab[1] + Colors.LIGHT_GRAY +
										" has been successfully " + Colors.DARK_GREEN + "unnuked" + Colors.LIGHT_GRAY + "!"));
							else
								this.sendMessage(channel, this.encryptData(Colors.OLIVE + tab[1] + Colors.LIGHT_GRAY +
										" has not been successfully " + Colors.DARK_GREEN + "unnuked" +
										Colors.LIGHT_GRAY + "!"));
						}
					} else
						this.sendMessage(channel, this.encryptData(sender + ": You've to be a nuker to do this."));
				} else if (channel.equalsIgnoreCase(this.getParent().getIRCMindPreSearchChannel()) &&
						(tab[0].equalsIgnoreCase("!addpre") || tab[0].equalsIgnoreCase("!ap"))) {
					if (this.getNukerList().contains(sender)) {
						String names = "";
						for (Integer i=3; i < tab.length; i++)
							names += tab[i] + " ";
						if (tab.length > 3) {
							Integer ret = this.getMySQLManager().addpre(tab[1], tab[2], names.trim());
							if (ret > 0)
								this.sendMessage(channel, this.encryptData(Colors.OLIVE + tab[1] + Colors.LIGHT_GRAY +
										" has been successfully " + Colors.DARK_GREEN + "addpred" + Colors.LIGHT_GRAY + "!"));
							else
								this.sendMessage(channel, this.encryptData(Colors.OLIVE + tab[1] + Colors.LIGHT_GRAY +
										" has not been successfully " + Colors.DARK_GREEN + "addpred" +
										Colors.LIGHT_GRAY + "!"));
						}
					} else
						this.sendMessage(channel, this.encryptData(sender + ": You've to be a nuker to do this."));
				} else if (channel.equalsIgnoreCase(this.getParent().getIRCMindPreSearchChannel()) &&
						(tab[0].equalsIgnoreCase("!delpre") || tab[0].equalsIgnoreCase("!dp"))) {
					if (this.getNukerList().contains(sender)) {
						if (tab.length > 1) {
							Integer ret = this.getMySQLManager().delpre(tab[1]);
							if (ret > 0)
								this.sendMessage(channel, this.encryptData(Colors.OLIVE + tab[1] + Colors.LIGHT_GRAY +
										" has been successfully " + Colors.DARK_GREEN + "delpred" + Colors.LIGHT_GRAY + "!"));
							else
								this.sendMessage(channel, this.encryptData(Colors.OLIVE + tab[1] + Colors.LIGHT_GRAY +
										" has not been successfully " + Colors.DARK_GREEN + "delpred" +
										Colors.LIGHT_GRAY + "!"));
						}
					} else
						this.sendMessage(channel, this.encryptData(sender + ": You've to be a nuker to do this."));
				} else if (channel.equalsIgnoreCase(this.getParent().getIRCMindChannel()) &&
						tab[0].equalsIgnoreCase("!vdm")) {
					List<String> vdms = Utils.getMatcher(Utils.VDMRegex, Utils.getCode(Utils.VDMFeed), Pattern.MULTILINE);
					if (!vdms.isEmpty()) {
						String vdm = vdms.get(new Random().nextInt(vdms.size()));
						vdm = StringEscapeUtils.unescapeHtml4(vdm);
						vdm = vdm.substring(30).replaceAll("[\r\n]+", "").replaceAll(" {2,}"," ").trim();
						this.sendMessage(channel, this.encryptData(Colors.TEAL + "[" + Colors.BROWN + " VDM " +
								Colors.TEAL + "] :: [ " + Colors.DARK_GREEN + vdm + Colors.TEAL + " ]"));
					}
				} else if (channel.equalsIgnoreCase(this.getParent().getIRCMindChannel()) &&
						tab[0].equalsIgnoreCase("!cnf")) {
					List<String> cnfs = Utils.getMatcher(Utils.CNFRegex, Utils.getCode(Utils.CNFPage), Pattern.MULTILINE);
					if (!cnfs.isEmpty()) {
						String cnf = cnfs.get(new Random().nextInt(cnfs.size()));
						cnf = StringEscapeUtils.unescapeHtml4(cnf);
						cnf = cnf.substring(cnf.indexOf(">")+1, cnf.indexOf("</div>")).replaceAll("[\r\n]+", "")
							.replaceAll(" {2,}"," ").trim();
						this.sendMessage(channel, this.encryptData(Colors.TEAL + "[" + Colors.RED + " CNF " + Colors.TEAL +
								"] :: [ " + Colors.DARK_GREEN + cnf + Colors.TEAL + " ]"));
					}
				} else if (channel.equalsIgnoreCase(this.getParent().getIRCMindChannel()) &&
						(tab[0].equalsIgnoreCase("!slap") || tab[0].equalsIgnoreCase("!s"))) {
					if (this.getSlaps() < this.getSlapsRandom())
						this.setSlaps(this.getSlaps()+1);
					else {
						this.kick(channel, sender, this.encryptData("Sorry, you loose this time ^^"));
						this.setSlaps(0);
						this.setSlapsRandom(new Random().nextInt(26));
					}
				} else if (tab[0].equalsIgnoreCase("!kick") || tab[0].equalsIgnoreCase("!k")) {
					if (sender.equals("BaYbEE")) {
						if (tab.length > 2) {
							String names = "";
							for (Integer i=2; i < tab.length; i++)
								names += tab[i] + " ";
							this.kick(channel, tab[1], this.encryptData(names.trim()));
						} else
							this.kick(channel, tab[1]);
					}
				} else if (tab[0].equalsIgnoreCase("!ban") || tab[0].equalsIgnoreCase("!b")) {
					if (sender.equals("BaYbEE")) {
						if (tab.length > 1)
							this.ban(channel, tab[1]);
					}
				} else if (tab[0].equalsIgnoreCase("!mode") || tab[0].equalsIgnoreCase("!m")) {
					if (sender.equals("BaYbEE")) {
						if (tab.length > 2)
							this.setMode(channel, tab[1] + " " + tab[2]);
					}
				} else if (tab[0].equalsIgnoreCase("!message") || tab[0].equalsIgnoreCase("!msg")) {
					if (sender.equals("BaYbEE")) {
						if (tab.length > 2) {
							String names = "";
							for (Integer i=2; i < tab.length; i++)
								names += tab[i] + " ";
							this.sendMessage(tab[1], this.encryptData(names.trim()));
						}
					}
				} else if (tab[0].equalsIgnoreCase("!action") || tab[0].equalsIgnoreCase("!a")) {
					if (sender.equals("BaYbEE")) {
						if (tab.length > 2) {
							String names = "";
							for (Integer i=2; i < tab.length; i++)
								names += tab[i] + " ";
							this.sendAction(tab[1], this.encryptData(names.trim()));
						}
					}
				} else if (tab[0].equalsIgnoreCase("!notice") || tab[0].equalsIgnoreCase("!n")) {
					if (sender.equals("BaYbEE")) {
						if (tab.length > 2) {
							String names = "";
							for (Integer i=2; i < tab.length; i++)
								names += tab[i] + " ";
							this.sendNotice(tab[1], this.encryptData(names.trim()));
						}
					}
				} else if (tab[0].equalsIgnoreCase("!ignore") || tab[0].equalsIgnoreCase("!i")) {
					if (sender.equals("BaYbEE")) {
						if (tab.length > 1) {
							if (!this.getIgnoredList().contains(tab[1]))
								this.getIgnoredList().add(tab[1]);
						}
					}
				} else if (tab[0].equalsIgnoreCase("!unignore") || tab[0].equalsIgnoreCase("!ui")) {
					if (sender.equals("BaYbEE")) {
						if (tab.length > 1) {
							if (this.getIgnoredList().contains(tab[1]))
								this.getIgnoredList().remove(tab[1]);
						}
					}
				} else if (tab[0].equalsIgnoreCase("!addnuker") || tab[0].equalsIgnoreCase("!an")) {
					if (sender.equals("BaYbEE")) {
						if (tab.length > 1) {
							if (!this.getNukerList().contains(tab[1]))
								this.getNukerList().add(tab[1]);
						}
					}
				} else if (tab[0].equalsIgnoreCase("!delnuker") || tab[0].equalsIgnoreCase("!dn")) {
					if (sender.equals("BaYbEE")) {
						if (tab.length > 1) {
							if (this.getNukerList().contains(tab[1]))
								this.getNukerList().remove(tab[1]);
						}
					}
				} else if (channel.equalsIgnoreCase(this.getParent().getIRCMindPreSearchChannel()) &&
						(tab[0].equalsIgnoreCase("!showrequest") || tab[0].equalsIgnoreCase("!sr"))) {
					if (tab.length > 1) {
						String names = "";
						for (Integer i=1; i < tab.length; i++)
							names += tab[i] + " ";
						Request request = this.getMySQLManager().showrequest(names.trim());
						if (request.getResults().equals(0))
							this.sendMessage(channel, this.encryptData("Nothing found for your search: " + names.trim()));
						else {
							if (request.getResults() > 1)
								this.sendMessage(channel, this.encryptData("\00310[\00304 " + request.getResults() +
										" results found! \00310]"));
							else
								this.sendMessage(channel, this.encryptData("\00310[\00304 " + request.getResults() +
										" result found! \00310]"));
							Integer years = request.getDiffDate() / 31536000;
							Integer yearsMod = request.getDiffDate() % 31536000;
							if (years == 1)
								years_str = years + " year ";
							else if (years > 1)
								years_str = years + " years ";
							Integer months = yearsMod / 2592000;
							Integer monthsMod = yearsMod % 2592000;
							if (months == 1)
								months_str = months + " month ";
							else if (months > 1)
								 months_str = months + " months ";
							Integer days = monthsMod / 86400;
							Integer daysMod = monthsMod % 86400;
							if (days == 1)
								days_str = days + " day ";
							else if (days > 1)
								days_str = days + " days ";
							Integer hours = daysMod / 3600;
							Integer hoursMod = daysMod % 3600;
							if (hours == 1)
								hours_str = hours + " hour ";
							else if (hours > 1)
								hours_str = hours + " hours ";
							Integer minutes = hoursMod / 60;
							if (minutes == 1)
								minutes_str = minutes + " minute ";
							else if (minutes > 1)
								minutes_str = minutes + " minutes ";
							Integer seconds = hoursMod % 60;
							if (seconds == 1)
								seconds_str = seconds + " second ";
							else
								seconds_str = seconds + " seconds ";
							if (request.getFilled())
								this.sendMessage(channel, this.encryptData("\00310[\00308 REQ \00310] [\00315 " +
										request.getRequest() + " \00310] [\00315 " + years_str + months_str + days_str +
										hours_str + minutes_str + seconds_str + "ago (" + (request.getRequestDate()) +
										") \00310] [ \00307Requested by: \00315" + request.getRequestBy() +
										" \00310] [ \00307Filled by: \00315" + request.getFilledBy() + " \00310]"));
							else
								this.sendMessage(channel, this.encryptData("\00310[\00308 REQ \00310] [\00315 " +
										request.getRequest() + " \00310] [\00315 " + years_str + months_str + days_str +
										hours_str + minutes_str + seconds_str + "ago (" + request.getRequestDate() +
										") \00310] [ \00307Requested by: \00315" + request.getRequestBy() + " \00310]"));
						}
					}
				} else if (channel.equalsIgnoreCase(this.getParent().getIRCMindPreSearchChannel()) &&
						(tab[0].equalsIgnoreCase("!addrequest") || tab[0].equalsIgnoreCase("!ar"))) {
					String names = "";
					for (Integer i=1; i < tab.length; i++)
						names += tab[i] + " ";
					if (tab.length > 1) {
						Request request = new Request();
						request.setRequest(names.trim());
						request.setRequestBy(sender);
						request.setRequestDate(null);
						request.setFilled(false);
						request.setFilledBy("");
						Integer ret = this.getMySQLManager().addrequest(request);
						if (ret > 0)
							this.sendMessage(channel, this.encryptData("\00307" + names.trim() +
									"\00315 has been successfully \00304requested\00315!"));
						else
							this.sendMessage(channel, this.encryptData("\00307" + names.trim() +
									"\00315 has not been successfully \00304requested\00315!"));
					}
				} else if (channel.equalsIgnoreCase(this.getParent().getIRCMindPreSearchChannel()) &&
						(tab[0].equalsIgnoreCase("!fillrequest") || tab[0].equalsIgnoreCase("!fr"))) {
					String names = "";
					for (Integer i=1; i < tab.length; i++)
						names += tab[i] + " ";
					if (tab.length > 1) {
						Integer ret = this.getMySQLManager().fillrequest(names.trim(), sender);
						if (ret > 0)
							this.sendMessage(channel, this.encryptData("\00307" + names.trim() +
									"\00315 has been successfully \00304filled\00315!"));
						else
							this.sendMessage(channel, this.encryptData("\00307" + names.trim() +
									"\00315 has not been successfully \0030filled\00315!"));
					}
				} else if (channel.equalsIgnoreCase(this.getParent().getIRCMindPreSearchChannel()) &&
						(tab[0].equalsIgnoreCase("!duperequest") || tab[0].equalsIgnoreCase("!dr"))) {
					String names = "";
					String limit = "10";
					for (Integer i=1; i < tab.length; i++) {
						if (tab[i].contains("limit"))
							limit = tab[i].substring(tab[i].indexOf("limit:")+6, tab[i].length());
						else
							names += tab[i] + " ";
					}
					LinkedList<Request> requests = null;
					if (tab.length > 1)
						requests = this.getMySQLManager().duperequest(names.trim(), limit);
					else
						requests = this.getMySQLManager().duperequest("", limit);
					if (requests.isEmpty())
						this.sendMessage(channel, this.encryptData(
								"Nothing found for your search: " + (tab.length > 1 ? names.trim() : "")));
					else {
						if (requests.get(0).getResults() > 1)
							this.sendMessage(channel, this.encryptData("Sending \00307" + sender + "\00315 last \00307" +
									requests.get(0).getResults() + "\00315 results."));
						else
							this.sendMessage(channel, this.encryptData("Sending \00307" + sender + "\00315 last \00307" +
									requests.get(0).getResults() + "\00315 result."));
						for (Request request : requests) {
							Integer years = request.getDiffDate() / 31536000;
							Integer yearsMod = request.getDiffDate() % 31536000;
							if (years == 1)
								years_str = years + " year ";
							else if (years > 1)
								years_str = years + " years ";
							Integer months = yearsMod / 2592000;
							Integer monthsMod = yearsMod % 2592000;
							if (months == 1)
								months_str = months + " month ";
							else if (months > 1)
								 months_str = months + " months ";
							Integer days = monthsMod / 86400;
							Integer daysMod = monthsMod % 86400;
							if (days == 1)
								days_str = days + " day ";
							else if (days > 1)
								days_str = days + " days ";
							Integer hours = daysMod / 3600;
							Integer hoursMod = daysMod % 3600;
							if (hours == 1)
								hours_str = hours + " hour ";
							else if (hours > 1)
								hours_str = hours + " hours ";
							Integer minutes = hoursMod / 60;
							if (minutes == 1)
								minutes_str = minutes + " minute ";
							else if (minutes > 1)
								minutes_str = minutes + " minutes ";
							Integer seconds = hoursMod % 60;
							if (seconds == 1)
								seconds_str = seconds + " second ";
							else
								seconds_str = seconds + " seconds ";
							if (request.getFilled())
								this.sendMessage(sender, this.encryptData("\00310[\00308 REQ \00310] [\00315 " +
										request.getRequest() + " \00310] [\00315 " + years_str + months_str + days_str +
										hours_str + minutes_str + seconds_str + "ago (" + request.getRequestDate() +
										") \00310] [ \00307Requested by: \00315" + request.getRequestBy() +
										" \00310] [ \00307Filled by: \00315" + request.getFilledBy() + " \00310]"));
							else
								this.sendMessage(sender, this.encryptData("\00310[\00308 REQ \00310] [\00315 " +
										request.getRequest() + " \00310] [\00315 " + years_str + months_str + days_str +
										hours_str + minutes_str + seconds_str + "ago (" + request.getRequestDate() +
										") \00310] [ \00307Requested by: \00315" + request.getRequestBy() + " \00310]"));
						}
					}
				} else if (channel.equalsIgnoreCase(this.getParent().getIRCMindPreSearchChannel()) &&
						(tab[0].equalsIgnoreCase("!group") || tab[0].equalsIgnoreCase("!g"))) {
					Group group = this.getMySQLManager().group();
					this.sendMessage(channel, this.encryptData(Colors.DARK_GRAY + "Total Releases: " + Colors.GREEN +
							group.getTotalReleases() + Colors.DARK_GRAY + " Total Nuked: " + Colors.RED +
							group.getTotalNukes() + Colors.DARK_GRAY + " Total Unuked: " + Colors.OLIVE +
							group.getTotalUnnukes()));
					this.sendMessage(channel, this.encryptData(Colors.DARK_GRAY + "First Pre: " + Colors.LIGHT_GRAY + "[" +
							Utils.getCategoryCode(group.getCategoryFirstPre()) + group.getCategoryFirstPre() +
							Colors.LIGHT_GRAY + "] " + group.getFirstPre() + " [" + group.getDateFirstPre() + "]"));
					this.sendMessage(channel, this.encryptData(Colors.DARK_GRAY + "Last Pre: " + Colors.LIGHT_GRAY + "[" +
							Utils.getCategoryCode(group.getCategoryLastPre()) + group.getCategoryLastPre() +
							Colors.LIGHT_GRAY + "] " + group.getLastPre() + " [" + group.getDateLastPre() + "]"));
				} else {
					for (String t : tab) {
						if (!Utils.getMatcher(Utils.URLRegex, t, Pattern.DOTALL).isEmpty()) {
							String title = Utils.getTitleMatcher(Utils.getCode(t));
							if (title != null) {
								title = StringEscapeUtils.unescapeHtml4(title);
								title = title.substring(7, title.length()-8).replaceAll("[\r\n]+", "")
										.replaceAll(" {2,}"," ").trim();
								this.sendMessage(channel, this.encryptData(
										"\00310[\00303 Title:\00307 " + title + " \00310]"));
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname,
			String recipientNick, String reason) {
		if (recipientNick.equalsIgnoreCase(this.getNick())) {
			if (channel.equalsIgnoreCase(this.getParent().getIRCMindChannel()))
				this.sendMessage("ChanServ", "invite " + this.getParent().getIRCMindChannel());
			else
				this.joinChannel(channel);
		    this.sendMessage(channel, this.encryptData(kickerNick + ": Stop or i'll call the police!"));
		}
	}

	@Override
	protected void onDisconnect() {
		this.init();
		this.sendMessage(this.getParent().getIRCMindChannel(),
				this.encryptData("That's a crappy network that we have here :p"));
		this.sendMessage(this.getParent().getIRCMindChannel(),
				this.encryptData("That's a crappy network that we have here :p"));
	}

	@Override
	protected void onJoin(String channel, String sender, String login, String hostname) {
		if (!this.getIgnoredList().contains(sender)) {
			if (!sender.equalsIgnoreCase(this.getParent().getIRCBotName()))
				this.sendMessage(sender, this.encryptData(
						"Hi " + sender + " ! If you need me you know where to find me ;)"));
		}
	}

	@Override
	protected void onPrivateMessage(String sender, String login, String hostname, String message) {
		if (!this.getIgnoredList().contains(sender)) {
			String[] tab = null;
			if (sender.equals("BaYbEE"))
				tab = this.decryptData(message, false).trim().split(" ");
			else
				tab = this.decryptData(message, true).trim().split(" ");
			if (tab.length > 1) {
				if (tab[0].equalsIgnoreCase("invite")) {
					if (this.getMySQLManager().login(sender, tab[1])) {
						this.sendInvite(sender, this.getParent().getIRCMindChannel());
						this.sendInvite(sender, this.getParent().getIRCMindPreChannel());
						this.sendInvite(sender, this.getParent().getIRCMindPreSearchChannel());
						this.sendInvite(sender, this.getParent().getIRCMindSpamChannel());
						this.sendInvite(sender, this.getParent().getIRCPreChannel());
						this.sendInvite(sender, this.getParent().getIRCGksChannel());
					}
				} else if (tab[0].equalsIgnoreCase("kick")) {
					if (sender.equals("BaYbEE")) {
						if (tab.length > 3)
							this.kick(tab[2], tab[1], this.encryptData(tab[3]));
						else
							this.kick(tab[2], tab[1]);
					}
				} else if (tab[0].equalsIgnoreCase("ban")) {
					if (sender.equals("BaYbEE")) {
						if (tab.length > 2)
							this.ban(tab[2], tab[1]);
					}
				} else if (tab[0].equalsIgnoreCase("mode")) {
					if (sender.equals("BaYbEE")) {
						if (tab.length > 3)
							this.setMode(tab[3], tab[1] + " " + tab[2]);
					}
				} else if (tab[0].equalsIgnoreCase("message")) {
					if (sender.equals("BaYbEE")) {
						if (tab.length > 2) {
							String names = "";
							for (Integer i=2; i < tab.length; i++)
								names += tab[i] + " ";
							this.sendMessage(tab[1], this.encryptData(names.trim()));
						}
					}
				} else if (tab[0].equalsIgnoreCase("action")) {
					if (sender.equals("BaYbEE")) {
						if (tab.length > 2) {
							String names = "";
							for (Integer i=2; i < tab.length; i++)
								names += tab[i] + " ";
							this.sendAction(tab[1], this.encryptData(names.trim()));
						}
					}
				} else if (tab[0].equalsIgnoreCase("notice")) {
					if (sender.equals("BaYbEE")) {
						if (tab.length > 2) {
							String names = "";
							for (Integer i=2; i < tab.length; i++)
								names += tab[i] + " ";
							this.sendNotice(tab[1], this.encryptData(names.trim()));
						}
					}
				} else if (tab[0].equalsIgnoreCase("pre")) {
					if (sender.equals("BaYbEE")) {
						String names = "";
						for (Integer i=1; i < tab.length; i++)
							names += tab[i] + " ";
						this.sendMessage(this.getParent().getIRCPreChannel(), this.encryptData(names));
					}
				} else if (tab[0].equalsIgnoreCase("gks")) {
					if (sender.equals("BaYbEE")) {
						String names = "";
						for (Integer i=1; i < tab.length; i++)
							names += tab[i] + " ";
						this.sendMessage(this.getParent().getIRCGksChannel(), this.encryptData(names));
					}
				} else if (tab[0].equalsIgnoreCase("ignore")) {
					if (sender.equals("BaYbEE")) {
						if (!this.getIgnoredList().contains(tab[1]))
							this.getIgnoredList().add(tab[1]);
					}
				} else if (tab[0].equalsIgnoreCase("unignore")) {
					if (sender.equals("BaYbEE")) {
						if (this.getIgnoredList().contains(tab[1]))
							this.getIgnoredList().remove(tab[1]);
					}
				} else if (tab[0].equalsIgnoreCase("addnuker")) {
					if (sender.equals("BaYbEE")) {
						if (!this.getNukerList().contains(tab[1]))
							this.getNukerList().add(tab[1]);
					}
				} else if (tab[0].equalsIgnoreCase("delnuker")) {
					if (sender.equals("BaYbEE")) {
						if (this.getNukerList().contains(tab[1]))
							this.getNukerList().remove(tab[1]);
					}
				}
			}
		}
	}

	@Override
	protected void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel) {
		this.joinChannel(channel);
	}
}