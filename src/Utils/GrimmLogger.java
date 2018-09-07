package Utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class GrimmLogger extends Logger {

	private static GrimmLogger _logger;
	
	private ConsoleHandler _consoleHandler;
	
	protected GrimmLogger(String name) {
		super(name, null);
	}
	
	private void setup() {
		_consoleHandler = new ConsoleHandler();
		_consoleHandler.setFormatter(new GrimmLogFormatter());
		addHandler(_consoleHandler);
	}
	
	@Override
	public void setLevel(Level newLevel) throws SecurityException {
		super.setLevel(newLevel);
		_consoleHandler.setLevel(newLevel);
	}
	
	public static GrimmLogger getInstance() {
		if(_logger == null) {
			_logger = new GrimmLogger("GrimmLogger");
			_logger.setup();
		}
		return _logger;
	}
	
	private class GrimmLogFormatter extends Formatter {
		@Override
		public String format(LogRecord record) {
			return "[" + record.getLevel() + ": " +
					record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf(".") + 1, record.getSourceClassName().length()) + "." + 
					record.getSourceMethodName() + "] " +
					record.getMessage() + "\n";
		}
	}
}
