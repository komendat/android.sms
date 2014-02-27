package cz.vsb.dp.kom134.interpreter.command;

import cz.vsb.dp.kom134.interpreter.Script;
import cz.vsb.dp.kom134.interpreter.model.RuntimeError;
import cz.vsb.dp.kom134.interpreter.model.SyntaxError;
import cz.vsb.dp.kom134.interpreter.model.Tokenizer;

public abstract class Command
{

	public static enum CommandType
	{
		REM, FOR, NEXT, IF, ELSE, ENDIF, LET, LOG, GOTO, LABEL, CALL, WAIT, MKDIR, SEND, SMS, POPUP, NOTIFY, SOUND, TOAST, EXIT
	}

	public static enum KeywordType
	{
		TO, STEP, THEN, DROP, COPY, MOVE, FWD, RE, DELETE, SETSEEN, SETUNSEEN, OPEN, RING, RINGWAIT, VIBRATE, STOP, VOLUME, MUTE, UNMUTE, SPEAK, ALL
	}

	final static Class<?>			COMMANDS[]	= { CommandComment.class, CommandFor.class, CommandNext.class, CommandIf.class, CommandElse.class,
			CommandEndif.class, CommandLet.class, CommandLog.class, CommandGoto.class, CommandLabel.class, CommandCall.class, CommandWait.class,
			CommandMkDir.class, CommandSend.class, CommandSMS.class, CommandPopup.class, CommandNotify.class, CommandSound.class, CommandToast.class,
			CommandExit.class					};

	protected static CommandType	commandType;
	protected int					line;

	private String					lineString;

	protected Command(CommandType commandType)
	{
		this.commandType = commandType;
	}

	abstract void init(Script script, Tokenizer tokenizer) throws SyntaxError;

	abstract void parse(Script script, Tokenizer tokenizer) throws SyntaxError;

	public Command execute(Script script) throws RuntimeError
	{
		Command nextOne = null;
		try
		{
			nextOne = doit(script);
		}
		catch (RuntimeError e)
		{
			throw new RuntimeError(this, e.getMessage());
		}
		return nextOne;
	}

	abstract Command doit(Script script) throws RuntimeError;

	@Override
	public String toString()
	{
		return "[" + line + "] " + lineString;
	}

	public void setLine(int line)
	{
		this.line = line;
	}

	public int getLine()
	{
		return line;
	}

	public void setLineString(String string)
	{
		lineString = string;
	}

}
