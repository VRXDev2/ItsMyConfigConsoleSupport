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
import to.itsme.itsmyconfig.placeholder.type.ColorPlaceholder;

import static me.vrxdev.itsMyConfigConsoleSupport.ItsMyConfigConsoleSupport.cachedPlaceholders;

public class IMCFilter extends AbstractFilter {
    private final PlaceholderManager placeholderManager;

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final ANSIComponentSerializer ANSI_SERIALIZER = ANSIComponentSerializer.ansi();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    public IMCFilter(PlaceholderManager placeholderManager) {
        this.placeholderManager = placeholderManager;
    }

    @Override
    public Result filter(LogEvent event) {
        String original = event.getMessage().getFormattedMessage();
        if (!original.startsWith("$")) return Result.NEUTRAL;

        String modified = replacePlaceholdersAndColors(original);

        LogEventMutator.setMessage(event, modified);
        return Result.NEUTRAL;
    }

    private String replacePlaceholdersAndColors(String message) {
        if (message == null) return "";

        message = replaceAllPlaceholders(message);

        Component component = MINI_MESSAGE.deserialize(message);
        String legacyString = LEGACY_SERIALIZER.serialize(component);
        component = LEGACY_SERIALIZER.deserialize(legacyString);
        return ANSI_SERIALIZER.serialize(component);
    }

    private String replaceAllPlaceholders(String message) {
        message = message.substring(1);
        message = message.replaceAll("</p>", "");

        boolean replaced;
        do {
            replaced = false;

            for (var entry : placeholderManager.getPlaceholdersMap().entrySet()) {
                String key = entry.getKey();
                Placeholder ph = entry.getValue();

                if (!ph.hasDependency(PlaceholderDependancy.NONE)) continue;

                String placeholderTag = "<p:" + key + ">";
                if (!message.contains(placeholderTag)) continue;

                try {
                    String value = cachedPlaceholders.get(key);
                    if (value == null) {
                        value = ph.getResult((OfflinePlayer)null, new String[0]);
                    }

                    if (value != null) {
                        if (ph instanceof ColorPlaceholder) {
                            value = "<" + value + ">";
                        }
                        message = message.replace(placeholderTag, value);
                        replaced = true;
                    }
                } catch (Exception ex) {
                    System.out.println("[IMCCS DEBUG] Failed placeholder: key=" + key + ", type=" + ph.getType() + " -> " + ex.getMessage());
                }
            }

        } while (replaced);

        return message;
    }


}
