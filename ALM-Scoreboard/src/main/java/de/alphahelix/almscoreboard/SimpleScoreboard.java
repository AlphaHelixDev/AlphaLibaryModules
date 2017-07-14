/*
 *     Copyright (C) <2016>  <AlphaHelixDev>
 *
 *     This program is free software: you can redistribute it under the
 *     terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.alphahelix.almscoreboard;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.gson.annotations.Expose;
import de.alphahelix.almcore.ALMCore;
import de.alphahelix.almutils.Util;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.Serializable;
import java.util.*;

public class SimpleScoreboard implements Serializable {

    @Expose
    private transient static final List<ChatColor> colors = Arrays.asList(ChatColor.values());

    private static WeakHashMap<String, SimpleScoreboard> scoreboards = new WeakHashMap<>();

    private final WeakHashMap<String, List<StringLine>> stringLines = new WeakHashMap<>();
    private final List<BoardLine> boardLines = new ArrayList<>();
    private final String owner;
    private Scoreboard scoreboard = null;
    private Objective objective = null;

    /**
     * Creates a new {@link SimpleScoreboard}
     *
     * @param displayName the title of the {@link Scoreboard}
     * @param lines       the amount of lines the {@link Scoreboard} should have (Highest = 16)
     */
    public SimpleScoreboard(Player p, String displayName, StringLine... lines) {
        Validate.isTrue(lines.length < colors.size(), "Too many lines!");

        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective(Util.generateRandomString(15), "dummy");
        this.owner = (p == null ? "ALL" : p.getName());

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(displayName);

        for (int i = 0; i < colors.size(); i++) {
            final ChatColor color = colors.get(i);
            final Team team = scoreboard.registerNewTeam("line" + i);

            team.addEntry(color.toString());
            boardLines.add(new BoardLine(color, i, team));
        }

        for (StringLine line : lines) {
            setValue(line);
            addStringLine(line);
        }

        if (p == null) {
            scoreboards.put("ALL", this);
        } else {
            scoreboards.put(p.getName(), this);
        }
    }

    /**
     * @param key ALL for everybody or a player name
     * @return
     */
    public static SimpleScoreboard getScoreboard(String key) {
        if (scoreboards.containsKey(key))
            return scoreboards.get(key);
        return null;
    }

    private static String getFirstColors(String input) {
        StringBuilder result = new StringBuilder();
        int length = input.length();

        for (int index = 0; index < length; index++) {
            char section = input.charAt(index);

            if (section == ChatColor.COLOR_CHAR && index < length - 1) {
                char c = input.charAt(index + 1);
                ChatColor color = ChatColor.getByChar(c);

                if (color != null) {
                    result.insert(0, color.toString());

                    if (color.isColor() || color.equals(ChatColor.RESET)) {
                        break;
                    }
                }
            }
        }

        return result.toString();
    }

    private void addStringLine(StringLine line) {
        List<StringLine> list = new ArrayList<>();

        if (stringLines.containsKey(owner))
            list = stringLines.get(owner);


        if (!list.contains(line)) {
            list.add(line);
            stringLines.put(owner, list);
        }
    }

    private BoardLine getBoardLine(int line) {
        for (BoardLine lines : boardLines) {
            if (lines.getLine() == line) {
                return lines;
            }
        }
        return null;
    }

    public StringLine getStringLine(int line) {
        for (StringLine lines : stringLines.get(owner)) {
            if (lines.getLine() == line) return lines;
        }
        return null;
    }

    public List<StringLine> getLines() {
        return stringLines.get(owner);
    }

    /**
     * Add a new line to the {@link Scoreboard}
     *
     * @param value the text at this line
     */
    public void setValue(StringLine value) {
        addStringLine(value);
        if (!value.getValue().contains(value.getSplitter())) {
            Iterable<String> res = Splitter.fixedLength(value.getValue().length() / 2).split(value.getValue());
            String[] text = Iterables.toArray(res, String.class);
            BoardLine bl = getBoardLine(value.getLine());
            String cc = ChatColor.getLastColors(text[0]);

            assert bl != null;
            objective.getScore(bl.getColor().toString()).setScore(value.getLine());

            bl.getTeam().setPrefix(text[0]);
            bl.getTeam().setSuffix(text[1].startsWith("?") ? text[1] : cc + text[1]);
        } else {
            String[] text = value.getValue().split(value.getSplitter());
            BoardLine bl = getBoardLine(value.getLine());

            String firstLeftColors = getFirstColors(text[0]);
            String firstRightColors = getFirstColors(text[1]);

            String leftText = text[0].replace(firstLeftColors, "");
            String rightText = text[1].replace(firstRightColors, "");

            if (objective == null) return;
            if (bl == null) return;
            if (objective.getScore(bl.getColor().toString()) == null) return;
            objective.getScore(bl.getColor().toString()).setScore(value.getLine());

            int lefTextLenght = leftText.length();
            int rightTextLengt = (value.getSplitter() + rightText).length();

            if (lefTextLenght > 12) {
                if (rightTextLengt > 12) {

                    Scroller left = new Scroller(leftText, 10, 3, '?');
                    Scroller right = new Scroller(rightText, 8, 3, '?');

                    if ((leftText.length() % 2) == 0) {
                        left = new Scroller(leftText, 10, 2, '?');
                    }

                    if ((rightText.length() % 2) == 0) {
                        right = new Scroller(rightText, 8, 2, '?');
                    }

                    bl.getTeam().setPrefix(firstLeftColors + left.next());
                    bl.getTeam().setSuffix(value.getSplitter() + firstRightColors + right.next());

                    Scroller finalLeft = left;
                    Scroller finalRight = right;

                    value.setMoving(new BukkitRunnable() {
                        public void run() {
                            bl.getTeam().setPrefix(firstLeftColors + finalLeft.next());
                            bl.getTeam().setSuffix(value.getSplitter() + firstRightColors + finalRight.next());
                        }
                    }.runTaskTimerAsynchronously(ALMCore.getInstance(), 0, 10));
                }

                // When only left is too long
                else {
                    Scroller left = new Scroller(leftText, 10, 3, '?');

                    if ((leftText.length() % 2) == 0) {
                        left = new Scroller(leftText, 10, 2, '?');
                    }

                    bl.getTeam().setPrefix(firstLeftColors + left.next());
                    bl.getTeam().setSuffix(value.getSplitter() + firstRightColors + rightText);

                    Scroller finalLeft = left;

                    value.setMoving(new BukkitRunnable() {
                        public void run() {
                            bl.getTeam().setPrefix(firstLeftColors + finalLeft.next());
                        }
                    }.runTaskTimerAsynchronously(ALMCore.getInstance(), 0, 10));
                }
            }

            // When left is correct
            else {
                if (rightTextLengt > 12) {
                    Scroller right = new Scroller(rightText, 8, 3, '?');

                    if ((rightText.length() % 2) == 0) {
                        right = new Scroller(rightText, 8, 2, '?');
                    }

                    bl.getTeam().setPrefix(firstLeftColors + leftText);
                    bl.getTeam().setSuffix(value.getSplitter() + firstRightColors + right.next());

                    Scroller finalRight = right;

                    value.setMoving(new BukkitRunnable() {
                        public void run() {
                            bl.getTeam().setSuffix(value.getSplitter() + firstRightColors + finalRight.next());
                        }
                    }.runTaskTimerAsynchronously(ALMCore.getInstance(), 0, 10));
                }

                // When everything is perfect
                else {
                    bl.getTeam().setPrefix(firstLeftColors + text[0]);
                    bl.getTeam().setSuffix(value.getSplitter() + firstRightColors + text[1]);
                }
            }
        }
    }

    /**
     * Updates a line inside the {@link Scoreboard}
     *
     * @param value the new text of this line
     */
    public void updateValue(StringLine value) {
        if (!value.getValue().contains(value.getSplitter()))
            return;

        String[] text = value.getValue().split(value.getSplitter());
        BoardLine bl = getBoardLine(value.getLine());

        if (bl == null) return;

        String firstLeftColors = getFirstColors(text[0]);
        String lastRightColors = getFirstColors(text[1]);

        String leftText = text[0].replace(firstLeftColors, "");
        String rightText = text[1].replace(lastRightColors, "");

        int lefTextLenght = leftText.length();
        int rightTextLengt = (value.getSplitter() + rightText).length();

        if (lefTextLenght >= 12) {
            if (rightTextLengt >= 12) {

                Scroller left = new Scroller(leftText, 10, 3, '?');
                Scroller right = new Scroller(rightText, 8, 3, '?');

                if ((leftText.length() % 2) == 0) {
                    left = new Scroller(leftText, 10, 2, '?');
                }

                if ((rightText.length() % 2) == 0) {
                    right = new Scroller(rightText, 8, 2, '?');
                }

                bl.getTeam().setPrefix(firstLeftColors + left.next());
                bl.getTeam().setSuffix(value.getSplitter() + lastRightColors + right.next());

                Scroller finalLeft = left;
                Scroller finalRight = right;

                if (value.getMoving() != null) value.getMoving().cancel();

                value.setMoving(new BukkitRunnable() {
                    public void run() {
                        bl.getTeam().setPrefix(firstLeftColors + finalLeft.next());
                        bl.getTeam().setSuffix(value.getSplitter() + lastRightColors + finalRight.next());
                    }
                }.runTaskTimerAsynchronously(ALMCore.getInstance(), 0, 10));
            }

            // When only left is too long
            else {
                Scroller left = new Scroller(leftText, 10, 3, '?');

                if ((leftText.length() % 2) == 0) {
                    left = new Scroller(leftText, 10, 2, '?');
                }

                bl.getTeam().setPrefix(firstLeftColors + left.next());
                bl.getTeam().setSuffix(value.getSplitter() + lastRightColors + rightText);

                Scroller finalLeft = left;

                if (value.getMoving() != null) value.getMoving().cancel();

                value.setMoving(new BukkitRunnable() {
                    public void run() {
                        bl.getTeam().setPrefix(firstLeftColors + finalLeft.next());
                    }
                }.runTaskTimerAsynchronously(ALMCore.getInstance(), 0, 10));
            }
        }

        // When left is correct
        else {
            if (rightTextLengt >= 12) {
                Scroller right = new Scroller(rightText, 8, 3, '?');

                if ((rightText.length() % 2) == 0) {
                    right = new Scroller(rightText, 8, 2, '?');
                }

                bl.getTeam().setPrefix(firstLeftColors + leftText);
                bl.getTeam().setSuffix(value.getSplitter() + lastRightColors + right.next());

                Scroller finalRight = right;

                if (value.getMoving() != null) value.getMoving().cancel();

                value.setMoving(new BukkitRunnable() {
                    public void run() {
                        bl.getTeam().setSuffix(value.getSplitter() + lastRightColors + finalRight.next());
                    }
                }.runTaskTimerAsynchronously(ALMCore.getInstance(), 0, 10));
            }

            // When everything is perfect
            else {
                if (value.getMoving() != null) value.getMoving().cancel();

                bl.getTeam().setPrefix(firstLeftColors + text[0]);
                bl.getTeam().setSuffix(value.getSplitter() + lastRightColors + text[1]);
            }
        }
    }

    public void removeLine(int line) {
        final BoardLine boardLine = getBoardLine(line);
        Validate.notNull(boardLine, "Unable to find BoardLine with index of " + line + "");

        scoreboard.resetScores(boardLine.getColor().toString());
    }

    public Scoreboard buildScoreboard() {
        return scoreboard;
    }

    @Override
    public String toString() {
        return "SimpleScoreboard{" +
                "boardLines=" + boardLines +
                ", scoreboard=" + scoreboard +
                ", objective=" + objective +
                '}';
    }

    public static class StringLine {

        private final String startValue, startSplitter;
        private int line;
        private String value, splitter;
        @Expose
        private transient BukkitTask moving;

        public StringLine(int line, String value, String splitter) {
            setLine(line);

            this.startValue = value;
            setValue(value);

            this.startSplitter = splitter;
            setSplitter(splitter);
        }

        public String getStartValue() {
            return startValue;
        }

        public String getStartSplitter() {
            return startSplitter;
        }

        public int getLine() {
            return line;
        }

        public StringLine setLine(int line) {
            this.line = line;
            return this;
        }

        public String getValue() {
            if (value == null) value = startValue;
            return value;
        }

        public StringLine setValue(String value) {
            this.value = value;
            return this;
        }

        public String getSplitter() {
            if (splitter == null) splitter = startSplitter;
            return (splitter == null ? UUID.randomUUID().toString() : splitter.replace("$", "§"));
        }

        public StringLine setSplitter(String splitter) {
            this.splitter = (splitter == null ? UUID.randomUUID().toString() : splitter);
            return this;
        }

        public BukkitTask getMoving() {
            return moving;
        }

        public StringLine setMoving(BukkitTask moving) {
            this.moving = moving;
            return this;
        }

        @Override
        public String toString() {
            return "StringLine{" +
                    "line=" + line +
                    ", startValue='" + startValue + '\'' +
                    ", startSplitter='" + startSplitter + '\'' +
                    ", value='" + value + '\'' +
                    ", splitter='" + splitter + '\'' +
                    '}';
        }
    }
}

class BoardLine {

    private final ChatColor color;
    private final int line;
    private final Team team;

    public BoardLine(ChatColor color, int line, Team team) {
        this.color = color;
        this.line = line;
        this.team = team;
    }

    public ChatColor getColor() {
        return color;
    }

    public int getLine() {
        return line;
    }

    public Team getTeam() {
        return team;
    }

    @Override
    public String toString() {
        return "BoardLine{" +
                "color=" + color +
                ", line=" + line +
                ", team=" + team +
                '}';
    }
}


