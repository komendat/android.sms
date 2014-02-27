package cz.vsb.dp.kom134.interpreter.model;

import cz.vsb.dp.kom134.interpreter.command.Command.CommandType;
import cz.vsb.dp.kom134.interpreter.command.Command.KeywordType;
import cz.vsb.dp.kom134.interpreter.expression.Expression;
import cz.vsb.dp.kom134.interpreter.expression.Expression.OperatorType;
import cz.vsb.dp.kom134.interpreter.expression.Expression.ValueType;
import cz.vsb.dp.kom134.interpreter.expression.ExpressionFunction;
import cz.vsb.dp.kom134.interpreter.expression.ExpressionFunction.FunctionType;

public class Token
{

	public static enum TokenType
	{
		SYMBOL, OPERATOR, NUMBER, STRING, BOOL, FUNCTION, COMMAND, KEYWORD, VARIABLE, EOL
	}

	protected TokenType		tokenType;
	protected double		dValue;
	protected String		sValue;
	protected boolean		bValue;
	protected char			symbol;
	protected OperatorType	operatorType;
	protected FunctionType	functionType;
	protected CommandType	commandType;
	protected KeywordType	keywordType;

	// constructors
	public Token()
	{
	}

	public Token(char symbol)
	{
		this.tokenType = TokenType.SYMBOL;
		this.symbol = symbol;
	}

	public Token(OperatorType operatorType)
	{
		this.tokenType = TokenType.OPERATOR;
		this.operatorType = operatorType;
	}

	public Token(Double numValue)
	{
		this.tokenType = TokenType.NUMBER;
		this.dValue = numValue;
	}

	public Token(String strValue)
	{
		this.tokenType = TokenType.STRING;
		this.sValue = strValue;
	}

	public Token(Boolean boolValue)
	{
		this.tokenType = TokenType.BOOL;
		this.bValue = boolValue;
	}

	public Token(FunctionType functionType)
	{
		this.tokenType = TokenType.FUNCTION;
		this.functionType = functionType;
	}

	public Token(CommandType commandType)
	{
		this.tokenType = TokenType.COMMAND;
		this.commandType = commandType;
	}

	public Token(KeywordType keywordType)
	{
		this.tokenType = TokenType.KEYWORD;
		this.keywordType = keywordType;
	}

	public Token(TokenType type)
	{
		this.tokenType = type;
	}

	// getters
	static final boolean isSymbol(Token t, char s)
	{
		return ((t != null) && (t.tokenType == TokenType.SYMBOL) && (t.symbol == s));
	}

	public final boolean isSymbol(char c)
	{
		return isSymbol(this, c);
	}

	public TokenType getTokenType()
	{
		return tokenType;
	}

	public final boolean isOperator(OperatorType op)
	{
		return ((tokenType == TokenType.OPERATOR) && (operatorType == op));
	}

	public final Expression.OperatorType getOperator()
	{
		return operatorType;
	}

	public final double getNumber()
	{
		return dValue;
	}

	public final String getString()
	{
		if (sValue != null)
			return sValue;
		else if (getValueType() == ValueType.NUMBER)
			return double2string(dValue);
		else if (getValueType() == ValueType.BOOL)
			return Boolean.toString(bValue);
		return "";
	}

	public final boolean getBool()
	{
		return bValue;
	}

	public final boolean isFunction(FunctionType func)
	{
		return ((tokenType == TokenType.FUNCTION) && (functionType == func));
	}

	public final ExpressionFunction.FunctionType getFunctionType()
	{
		return functionType;
	}

	public final CommandType getCommandType()
	{
		return commandType;
	}

	public final KeywordType getKeywordType()
	{
		return keywordType;
	}

	public ValueType getValueType()
	{
		switch (tokenType)
		{
			case BOOL:
				return ValueType.BOOL;
			case NUMBER:
				return ValueType.NUMBER;
			case STRING:
				return ValueType.STRING;
			case VARIABLE:
				return ((Variable) this).getValueType();
			default:
				return null;
		}
	}

	public char getSymbol()
	{
		return symbol;
	}

	@Override
	public String toString()
	{
		String result = tokenType.toString() + " = ";
		switch (tokenType)
		{
			case BOOL:
				return result + Boolean.toString(bValue);
			case COMMAND:
				return result + commandType.toString();
			case EOL:
				return result + "\\n";
			case FUNCTION:
				return result + functionType.toString();
			case KEYWORD:
				return result + keywordType.toString();
			case NUMBER:
				return result + dValue;
			case OPERATOR:
				return result + operatorType + "(" + Expression.operators[operatorType.ordinal()] + ")";
			case STRING:
				return result + sValue;
			case SYMBOL:
				return result + symbol;
			default:
				return result;
		}
	}

	private String double2string(double value)
	{
		long trs = (long) value;
		double value2 = trs;
		if (value == value2)
			return Long.toString(trs);
		return Double.toString(value);
	}
}
