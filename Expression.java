package cz.vsb.dp.kom134.interpreter.expression;

import cz.vsb.dp.kom134.interpreter.model.RuntimeError;

public class Expression
{
	protected Expression	arg1;
	protected Expression	arg2;
	protected OperatorType	operatorType;

	public static enum ValueType
	{
		NUMBER, STRING, BOOL
	}

	public static enum OperatorType
	{
		ADD, SUB, MUL, DIV, EXP, EQ, BIND, NEQ, LEQ, LES, GRQ, GRS, NOT, AND, OR
	}

	public final static String	operators[]	= { "+", "-", "*", "/", "^", "==", "=", "!=", "<=", "<", ">=", ">", "!", "AND", "OR" };

	protected Expression()
	{
	}

	protected Expression(OperatorType type, Expression arg1, Expression arg2)
	{
		this.operatorType = type;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	protected Expression(OperatorType type, Expression arg1)
	{
		this.operatorType = type;
		this.arg1 = arg1;
	}

	public boolean isString()
	{
		return (getValueType() == ValueType.STRING);
	}

	public boolean isDouble()
	{
		return (getValueType() == ValueType.NUMBER);
	}

	public boolean isBoolean()
	{
		return (getValueType() == ValueType.BOOL);
	}

	public double getDouble() throws RuntimeError
	{
		double value1 = 0, value2 = 0;
		if (arg1 != null)
			value1 = arg1.getDouble();
		if (arg2 != null)
			value2 = arg2.getDouble();
		if (operatorType == null)
			throw new RuntimeError("Expression: missing operator.");
		switch (operatorType)
		{
			case ADD:
				return value1 + value2;
			case SUB:
				return value1 - value2;
			case MUL:
				return value1 * value2;
			case DIV:
				if (value2 == 0)
					throw new RuntimeError("Expression: divide by zero!");
				return value1 / value2;
			case EXP:
				return Math.pow(value1, value2);
			default:
				throw new RuntimeError("Expression: operator " + operators[operatorType.ordinal()] + "'s return value is not number type");
		}
	}

	public String getString() throws RuntimeError
	{
		String value1 = "", value2 = "";
		if (getValueType() == ValueType.NUMBER)
		{
			double result = getDouble();
			String resultString = double2string(result);
			return resultString;
		}
		if (getValueType() == ValueType.BOOL)
		{
			boolean result = getBool();
			String reString = Boolean.toString(result);
			return reString;
		}
		if (arg1 != null)
			value1 = arg1.getString();

		if (arg2 != null)
			value2 = arg2.getString();
		if (operatorType == null)
			throw new RuntimeError("Expression: missing operator.");
		switch (operatorType)
		{
			case ADD:
				return value1.concat(value2);

			default:
				throw new RuntimeError("Expression: operator " + operators[operatorType.ordinal()] + " is not aplicable to string type");
		}

	}

	public boolean getBool() throws RuntimeError
	{
    // strings
		if ((arg1 != null && arg1.isString()) || (arg2 != null && arg2.isString()))
		{
			String value1 = "", value2 = "";
			if (arg1 != null)
				value1 = arg1.getString();
			if (arg2 != null)
				value2 = arg2.getString();

			if (operatorType == null)
				throw new RuntimeError("Expression: missing operator.");
			switch (operatorType)
			{
				case EQ:
					return (value1.compareTo(value2) == 0);
				case NEQ:
					return (value1.compareTo(value2) != 0);
				default:
					throw new RuntimeError("Expression: operator " + operators[operatorType.ordinal()] + " is not aplicable to string type");

			}
		}
    // numbers
		if (arg1 != null && arg1.isDouble() && arg2 != null && arg2.isDouble())
		{
			double value1 = 0, value2 = 0;
			if (arg1 != null)
				value1 = arg1.getDouble();
			if (arg2 != null)
				value2 = arg2.getDouble();
			if (operatorType == null)
  			throw new RuntimeError("Expression: missing operator.");
			switch (operatorType)
			{
				case EQ:
					return (value1 == value2);
				case NEQ:
					return (value1 != value2);
				case LES:
					return (value1 < value2);
				case LEQ:
					return (value1 <= value2);
				case GRS:
					return (value1 > value2);
				case GRQ:
					return (value1 >= value2);
				default:
					throw new RuntimeError("Expression: operator " + operators[operatorType.ordinal()] + " is not aplicable to number type");
			}
		}
    // booleans
		if (arg1 != null && arg1.isBoolean() && (arg2 == null || arg2.isBoolean()))
		{
			boolean value1 = false, value2 = false;
			if (arg1 != null)
				value1 = arg1.getBool();
			if (arg2 != null)
				value2 = arg2.getBool();

			if (operatorType == null)
				throw new RuntimeError("Expression: missing operator.");
			switch (operatorType)
			{
				case AND:
					return (value1 && value2);
				case OR:
					return (value1 || value2);
				case NOT:
					return !value1;
				default:
					throw new RuntimeError("Expression: operator " + operators[operatorType.ordinal()] + " is not aplicable to bool type");
			}
		}
		throw new RuntimeError("Expression: operator " + operators[operatorType.ordinal()] + " can't return bool type result");
	}

	public ValueType getValueType()
	{
		switch (operatorType)
		{
			case AND:
			case OR:
			case NOT:
			case EQ:
			case NEQ:
			case GRQ:
			case GRS:
			case LEQ:
			case LES:
				return ValueType.BOOL;
			case DIV:
			case EXP:
			case MUL:
			case SUB:
				return ValueType.NUMBER;
			case ADD:
				if (arg1.getValueType() == ValueType.STRING || arg2.getValueType() == ValueType.STRING)
					return ValueType.STRING;
				return ValueType.NUMBER;
			default:
				return null;
		}
	}

	@Override
	public String toString()
	{
		String result = "";
		if (arg1 != null)
			result = arg1.toString();
		result += " " + operators[operatorType.ordinal()];
		if (arg2 != null)
			result += " " + arg2.toString();
		return result;
	}

	protected String double2string(double value)
	{
		long trs = (long) value;
		double value2 = trs;
		if (value == value2)
			return Long.toString(trs);
		return Double.toString(value);
	}

}
