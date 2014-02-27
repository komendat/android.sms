package cz.vsb.dp.kom134.interpreter.model;

import android.util.Log;
import cz.vsb.dp.kom134.interpreter.Script;
import cz.vsb.dp.kom134.interpreter.command.Command.CommandType;
import cz.vsb.dp.kom134.interpreter.command.Command.KeywordType;
import cz.vsb.dp.kom134.interpreter.expression.Expression;
import cz.vsb.dp.kom134.interpreter.expression.ExpressionFunction.FunctionType;
import cz.vsb.dp.kom134.interpreter.model.Token.TokenType;

public class Tokenizer
{

	private int					currentPos	= 0;
	private int					previousPos	= 0;
	private char				buffer[];
	public final Script	script;
	private int					lastCurrentPos;
	private int					lastReturnPos;
	private Token				lastToken;

	public Tokenizer(Script script, char data[])
	{
		this.script = script;
		buffer = data;
		currentPos = 0;
	}

	boolean hasMoreTokens()
	{
		return currentPos < buffer.length;
	}

	void reset(char buffer[])
	{
		lastCurrentPos = -1;
		this.buffer = buffer;
		currentPos = 0;
	}

	public void reset(String line)
	{
		lastCurrentPos = -1;
		line += "\n";
		buffer = line.toCharArray();

		currentPos = 0;
	}

	public Token nextToken()
	{
		if (currentPos == lastCurrentPos)
		{
			currentPos = lastReturnPos;
			return lastToken;
		}
		lastCurrentPos = currentPos;
		int length = buffer.length;
		if (currentPos >= length)
		{
			previousPos = currentPos;
			return returnToken(new Token(Token.TokenType.EOL));
		}
		previousPos = currentPos;
		while (isWhiteSpace(buffer[currentPos]))
			currentPos++;

		char c = buffer[currentPos];
		// token is symbol
    switch (c)
		{
			case '(':
			case ')':
			case '\\':
			case ',':
			case ';':
			case '?':
			case ':':
				currentPos++;
				return returnToken(new Token(c));
    // token is EOL
			case '\r':
			case '\n':
				currentPos = buffer.length;
				return returnToken(new Token(TokenType.EOL));
    // token is string
			case '"':
				StringBuffer sbBuffer = new StringBuffer();
				currentPos++;
				char lastC;
				while (true)
				{
					lastC = c;
					c = buffer[currentPos++];
					if (currentPos >= buffer.length || (c == '"' && lastC != '\\'))
						return returnToken(new Token(sbBuffer.toString().replaceAll("\\\\\"", "\"")));
					sbBuffer.append(c);
				}
    // token is comment
			case '#':
				return returnToken(new Token(CommandType.REM));
			default:
				break;
		}
    // token is number
		if (isDigit(c))
		{
			Token token = parseNumber();
			return returnToken(token);
		}
    // token is operator
		int index = currentPos;
		String toEnd = this.toString().toUpperCase();
		index = 0;
		for (String operator : Expression.operators)
		{
			if (toEnd.startsWith(operator))
			{
				currentPos += operator.length();
				return returnToken(new Token(Expression.OperatorType.values()[index]));
			}
			index++;
		}
    // token is word
		if (!isLetter(buffer[currentPos]))
		{
			Log.e("Tokenizer", "Error parsing token");
			return null;
		}
		return returnToken(parseWord());
	}

	Token parseWord()
	{
		StringBuffer sbBuffer = new StringBuffer();
		char c = buffer[currentPos];
		while (isLetter(c) || isDigit(c))
		{
			sbBuffer.append(c);
			c = buffer[++currentPos];
		}
		String word = sbBuffer.toString();
		if (word.length() < 1)
		{
			Log.e("Tokenizer", "Error parsing token - too short word.");
			return null;
		}
    // token is bool
		if (word.equalsIgnoreCase("false"))
		{
			currentPos += 5;
			return new Token(false);
		}
		if (word.equalsIgnoreCase("true"))
		{
			currentPos += 4;
			return new Token(true);
		}
    // token is command
		try
		{
			CommandType cmd = CommandType.valueOf(word.toUpperCase());
			if (cmd != null)
				return new Token(cmd);
		}
		catch (Exception e)
		{
			// do nothing.
		}
    // token is keyword
		try
		{
			KeywordType key = KeywordType.valueOf(word.toUpperCase());
			if (key != null)
				return new Token(key);
		}
		catch (Exception e)
		{
			// do nothing again.
		}
    // token is function
		try
		{
			FunctionType fnc = FunctionType.valueOf(word.toUpperCase());
			if (fnc != null)
				return new Token(fnc);
		}
		catch (Exception e)
		{
			// and for the third time...
		}
    // token is variable
		return new Variable(word);
	}

	Token parseNumber()
	{
		double result = 0;
		char c = buffer[currentPos];
		while (isDigit(c))
		{
			result = 10 * result + Double.parseDouble(Character.toString(c));
			c = buffer[++currentPos];
		}
		if (c == '.')
		{
			c = buffer[++currentPos];
			double f = 0.1;
			while (isDigit(c))
			{
				result += f * Double.parseDouble(Character.toString(c));
				f /= 10;
				c = buffer[++currentPos];
			}
		}
		return new Token(result);
	}

	public Token returnToken(Token token)
	{
		lastToken = token;
		lastReturnPos = currentPos;
		return token;
	}

	public void dropToken()
	{
		currentPos = previousPos;
	}

	static boolean isLetter(char c)
	{
		if (c == '$' || c == '.')
			return true;
		return (Character.isLetter(c));
	}

	static boolean isDigit(char c)
	{
		return (Character.isDigit(c));
	}

	static boolean isWhiteSpace(char c)
	{
		if (c == '\r' || c == '\n')
			return false;
		return (Character.isWhitespace(c));
	}

	public String showError()
	{
		int error = previousPos;
		int index = 0;
		while ((buffer[index] != '\n') && (buffer[index] != '\r'))
			index++;
		String result = new String(buffer, 0, index);

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(result + "\n");
		for (int i = 0; i < error; i++)
			stringBuffer.append('-');
		stringBuffer.append('^');
		return stringBuffer.toString();
	}

	public String asString()
	{
		int i = currentPos;
		while ((buffer.length >= i) && (buffer[i] != '\n') && (buffer[i] != '\r'))
			i++;
		String result = new String(buffer, currentPos, i - currentPos);
		previousPos = currentPos;
		currentPos = i;
		if (result.startsWith("#"))
			result = result.substring(1);
		if (result.toLowerCase().startsWith("rem"))
			result = result.substring(3);
		return (result.trim());
	}

	@Override
	public String toString()
	{
		int i = currentPos;
		while ((buffer.length >= i) && (buffer[i] != '\n') && (buffer[i] != '\r'))
			i++;
		String result = new String(buffer, currentPos, i - currentPos);
		return (result.trim());
	}

}
