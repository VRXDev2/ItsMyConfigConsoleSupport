package me.vrxdev.itsMyConfigConsoleSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import to.itsme.itsmyconfig.placeholder.PlaceholderManager;

public final class IMCFilterInstaller {

    private static IMCFilter filter;

    public static synchronized void install(PlaceholderManager manager) {
        if (filter != null) return;

        filter = new IMCFilter(manager);
        filter.start();

        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        LoggerConfig rootLoggerConfig = config.getRootLogger();
        rootLoggerConfig.addFilter(filter);

        ctx.updateLoggers();
    }

    // this exists only to add proper reload safety
    public static synchronized void uninstall() {
        if (filter == null) return;

        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        LoggerConfig rootLoggerConfig = config.getRootLogger();
        rootLoggerConfig.removeFilter(filter);

        try {
            filter.stop();
        } finally {
            filter = null;
        }

        ctx.updateLoggers();
    }
}
