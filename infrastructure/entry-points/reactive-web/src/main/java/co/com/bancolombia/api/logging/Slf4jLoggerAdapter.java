package co.com.bancolombia.api.logging;

import co.com.bancolombia.model.common.LoggerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Slf4jLoggerAdapter implements LoggerPort {

    private final Logger logger = LoggerFactory.getLogger(Slf4jLoggerAdapter.class);



    @Override
    public void info(String msg, Object... args) { logger.info(msg, args); }

    @Override
    public void warn(String msg, Object... args) { logger.warn(msg, args); }

    @Override
    public void debug(String msg, Object... args) { logger.debug(msg, args); }

    @Override
    public void error(String msg, Object... args) { logger.error(msg, args); }

    @Override
    public void error(String msg, Throwable t) { logger.error(msg, t); }
}

