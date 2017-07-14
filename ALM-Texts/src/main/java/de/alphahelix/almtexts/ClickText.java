package de.alphahelix.almtexts;

import de.alphahelix.almcommands.SimpleCommand;
import de.alphahelix.almcore.uuid.UUIDFetcher;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ClickText {

    public static void sendClickText(Player p, String text, TextAction action) {
        UUID id = UUIDFetcher.getUUID(p);
        new ActionTextCommand(id, action);

        TextComponent txt = new TextComponent(text);

        txt.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/atcPerform_" + id));

        p.spigot().sendMessage(txt);
    }

    public static void sendHoverClickText(Player p, String clickText, String hovertext, TextAction clickAction) {
        UUID id = UUIDFetcher.getUUID(p);
        new ActionTextCommand(id, clickAction);

        TextComponent txt = new TextComponent(clickText);

        txt.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/atcPerform_" + id));
        txt.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hovertext).create()));

        p.spigot().sendMessage(txt);
    }

    private static class ActionTextCommand extends SimpleCommand {

        private TextAction action;

        public ActionTextCommand(UUID player, TextAction action) {
            super("atcPerform_" + player.toString(), "", "");
            this.action = action;
        }

        @Override
        public boolean execute(CommandSender cs, String label, String[] args) {
            action.run((Player) cs);
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender cs, String label, String[] args) {
            return null;
        }
    }
}
