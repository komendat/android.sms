package cz.vsb.dp.kom134.interpreter.command;

import cz.vsb.dp.kom134.interpreter.Script;
import cz.vsb.dp.kom134.interpreter.expression.Expression;
import cz.vsb.dp.kom134.interpreter.expression.ExpressionParse;
import cz.vsb.dp.kom134.interpreter.model.RuntimeError;
import cz.vsb.dp.kom134.interpreter.model.SyntaxError;
import cz.vsb.dp.kom134.interpreter.model.Token;
import cz.vsb.dp.kom134.interpreter.model.Tokenizer;
import cz.vsb.dp.kom134.interpreter.model.Variable;

public class CommandLet extends Command
{
	private Variable	  variable;
	private Expression	expression;

	public CommandLet(Script script, Tokenizer tokenizer) throws SyntaxError
	{
		super(CommandType.LET);
		parse(script, tokenizer);
	}

	public CommandLet()
	{
		super(CommandType.LET);
	}

	@Override
	void init(Script script, Tokenizer tokenizer) throws SyntaxError
	{
		parse(script, tokenizer);
	}

	@Override
	void parse(Script script, Tokenizer tokenizer) throws SyntaxError
	{
		Token token = tokenizer.nextToken();
		if (token.getTokenType() != Token.TokenType.VARIABLE)
			throw new SyntaxError("Error in LET command variable.");
		Variable variable = (Variable) token;
		this.variable = variable;
		token = tokenizer.nextToken();
		if (!token.isOperator(Expression.OperatorType.BIND))
			throw new SyntaxError("Error in LET command - missing =.");
		this.expression = ExpressionParse.expression(tokenizer);
		this.variable.setValueType(expression.getValueType());
		token = tokenizer.nextToken();
		if (token.isSymbol(')'))
			throw new SyntaxError("Error in LET command - () mishmash.");
		else tokenizer.dropToken();
		script.setVariable(variable);
		token = tokenizer.nextToken();
		if (token.getTokenType() == Token.TokenType.EOL)
			return;
		throw new SyntaxError("Error in Let command.");
	}

	@Override
	Command doit(Script script) throws RuntimeError
	{
		if (expression.isBoolean())
			script.setVariable(variable, expression.getBool());
		if (expression.isDouble())
			script.setVariable(variable, expression.getDouble());
		if (expression.isString())
			script.setVariable(variable, expression.getString());

		return script.nextCommand(this);
	}
}
