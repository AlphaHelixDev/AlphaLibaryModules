package de.alphahelix.almteams;

import com.google.gson.annotations.Expose;
import de.alphahelix.almteams.events.TeamJoinEvent;
import de.alphahelix.almteams.events.TeamLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.WeakHashMap;

public class GameTeam {

    @Expose
    private static final transient WeakHashMap<String, GameTeam> TEAMS = new WeakHashMap<>();

    private String teamName, rawTeamName;
    private ChatColor color;
    private Location spawn;
    private int maximumPlayers;
    private boolean friendlyFire = true;
    private ArrayList<String> members = new ArrayList<>();

    public GameTeam(String teamName, ChatColor color, Location spawn, int maximumPlayers) {
        this.teamName = teamName;
        this.rawTeamName = ChatColor.stripColor(teamName).toLowerCase();
        this.color = color;
        this.spawn = spawn;
        this.maximumPlayers = maximumPlayers;
    }

    public GameTeam(String teamName, ChatColor color, Location spawn, int maximumPlayers, boolean friendlyFire) {
        this.teamName = teamName;
        this.rawTeamName = ChatColor.stripColor(teamName).toLowerCase();
        this.color = color;
        this.spawn = spawn;
        this.maximumPlayers = maximumPlayers;
        this.friendlyFire = friendlyFire;
    }

    public static void initTeam(GameTeam team) {
        TEAMS.put(team.getRawTeamName(), team);
    }

    public static GameTeam getTeamByName(String rawTeamName) {
        if (TEAMS.containsKey(rawTeamName))
            return TEAMS.get(rawTeamName);
        return null;
    }

    public static GameTeam getTeamByPlayer(Player p) {
        for (GameTeam gt : TEAMS.values()) {
            if (gt.containsPlayer(p)) return gt;
        }
        return null;
    }

    public static GameTeam getTeamByColor(ChatColor chatColor) {
        for (GameTeam gt : TEAMS.values()) {
            if (gt.getColor() == chatColor) return gt;
        }

        return null;
    }

    public static GameTeam getTeamWithLowestAmountOfMembers() {
        int lowest = 0;
        for (GameTeam gt : TEAMS.values()) {
            if (lowest < gt.getMembers().size())
                lowest = gt.getMembers().size();
        }

        for (GameTeam gt : TEAMS.values()) {
            if (lowest == gt.getMembers().size()) return gt;
        }
        return null;
    }

    public GameTeam addPlayer(Player p, boolean updateTab) {
        members.add(p.getName());

        if (updateTab) {
            if (p.getScoreboard() == null) {
                Scoreboard s = Bukkit.getScoreboardManager().getMainScoreboard();

                if (s.getTeam(this.getTeamName()) == null) {
                    Team team = s.registerNewTeam(this.getRawTeamName());

                    team.addEntry(p.getName());

                    team.setPrefix(this.getColor() + "");

                    team.setAllowFriendlyFire(this.isFriendlyFire());
                } else {
                    Team team = s.getTeam(this.getRawTeamName());

                    team.addEntry(p.getName());
                }
            } else {
                Scoreboard s = p.getScoreboard();

                if (s.getTeam(this.getTeamName()) == null) {
                    Team team = s.registerNewTeam(this.getRawTeamName());

                    team.addEntry(p.getName());

                    team.setPrefix(this.getColor() + "");

                    team.setAllowFriendlyFire(this.isFriendlyFire());
                } else {
                    Team team = s.getTeam(this.getRawTeamName());

                    team.addEntry(p.getName());
                }
            }
        }
        Bukkit.getPluginManager().callEvent(new TeamJoinEvent(p, this));
        return this;
    }

    public GameTeam removePlayer(Player p, boolean updateTab) {
        if (members.contains(p.getName())) {

            if (updateTab) {
                if (p.getScoreboard() != null) {

                    Scoreboard s = p.getScoreboard();

                    if (s.getTeam(this.getTeamName()) != null) {
                        Team team = s.getTeam(this.getRawTeamName());

                        team.removeEntry(p.getName());
                    }
                }
            }

            members.remove(p.getName());
        }
        Bukkit.getPluginManager().callEvent(new TeamLeaveEvent(p, this));
        return this;
    }

    public boolean containsPlayer(Player p) {
        return members.contains(p.getName());
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
        this.rawTeamName = ChatColor.stripColor(teamName).toLowerCase();
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public int getMaximumPlayers() {
        return maximumPlayers;
    }

    public void setMaximumPlayers(int maximumPlayers) {
        this.maximumPlayers = maximumPlayers;
    }

    public String getRawTeamName() {
        return rawTeamName;
    }

    @Override
    public String toString() {
        return "GameTeam{" +
                "teamName='" + teamName + '\'' +
                ", rawTeamName='" + rawTeamName + '\'' +
                ", color=" + color +
                ", spawn=" + spawn +
                ", maximumPlayers=" + maximumPlayers +
                ", friendlyFire=" + friendlyFire +
                ", members=" + members +
                '}';
    }
}
