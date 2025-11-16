package me.vrxdev.itsMyConfigConsoleSupport;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.bukkit.OfflinePlayer;
import to.itsme.itsmyconfig.placeholder.Placeholder;
import to.itsme.itsmyconfig.placeholder.PlaceholderDependancy;
import to.itsme.itsmyconfig.placeholder.PlaceholderManager;

public class IMCFilter extends AbstractFilter {
    private final PlaceholderManager placeholderManager;

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final ANSIComponentSerializer ANSI_SERIALIZER = ANSIComponentSerializer.ansi();

    public IMCFilter(PlaceholderManager placeholderManager) {
        this.placeholderManager = placeholderManager;
    }

    @Override
    public Result filter(LogEvent event) {
        String original = event.getMessage().getFormattedMessage();
        String modified = replacePlaceholdersAndColors(original);

        LogEventMutator.setMessage(event, modified);
        return Result.NEUTRAL;
    }

    private String replacePlaceholdersAndColors(String message) {
        if (message == null || !message.contains("<p:")) return message;

        boolean requires = message.startsWith("$");
        if (requires) message = message.substring(1);

        for (var entry : placeholderManager.getPlaceholdersMap().entrySet()) {
            String key = entry.getKey();
            Placeholder ph = entry.getValue();

            if (!ph.hasDependency(PlaceholderDependancy.NONE)) continue;

            if (!message.contains("<p:" + key + ">")) continue;

            try {
                String value = ph.getResult((OfflinePlayer)null, new String[0]);
                if (value != null) {
                    value = value.replaceAll("#([a-fA-F0-9]{6})", "<#$1>");
                    message = message.replace("<p:" + key + ">", value);
                }
            } catch (Exception ex) {
                System.out.println("[IMC DEBUG] Failed placeholder: key=" + key + ", type=" + ph.getType() + " -> " + ex.getMessage());
            }
        }

        Component component = MINI_MESSAGE.deserialize(message);
        return ANSI_SERIALIZER.serialize(component);
    }
}
