package com.cisco.xmp.jsonxsl;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonXSLT {

	private JsonObject dataJsonObj;
	private JsonObject rulesJsonObj;
	private JsonObject paramsJsonObj;

	private static final String MAIN_TAG = "main";
	private static final String RULES_TAG = "rules";
	private static final String CONDITION_TAG = "condition";

	private Gson gson;

	public JsonXSLT(String data, String rules, Map<String, String> parameters) {
		if (rules == null) {
			throw new IllegalArgumentException("Rules Json is null");
		}

		initDataRules(data, rules);
		initParameters(parameters);
	}

	public JsonXSLT(Reader data, Reader rules, Map<String, String> parameters) {
		if (rules == null) {
			throw new IllegalArgumentException("Rules Json is null");
		}

		initDataRules(data, rules);
		initParameters(parameters);
	}

	public JsonXSLT(JsonElement data, JsonElement rules,
			Map<String, String> parameters) {
		if (rules == null) {
			throw new IllegalArgumentException("Rules Json is null");
		}

		initDataRules(data, rules);
		initParameters(parameters);
	}
	
	private void initDataRules(String data, String rules) {
		dataJsonObj = new JsonParser().parse(data).getAsJsonObject();

		JsonElement ruleEle = new JsonParser().parse(rules).getAsJsonObject()
				.get(RULES_TAG);
		if (ruleEle == null || ruleEle.isJsonNull()) {
			throw new IllegalArgumentException(
					"Rules Json does not have rules property");
		}

		rulesJsonObj = ruleEle.getAsJsonObject();
	}

	private void initDataRules(Reader data, Reader rules) {
		dataJsonObj = new JsonParser().parse(data).getAsJsonObject();

		JsonElement ruleEle = new JsonParser().parse(rules).getAsJsonObject()
				.get(RULES_TAG);
		if (ruleEle == null || ruleEle.isJsonNull()) {
			throw new IllegalArgumentException(
					"Rules Json does not have rules property");
		}

		rulesJsonObj = ruleEle.getAsJsonObject();
	}

	private void initDataRules(JsonElement data, JsonElement rules) {
		dataJsonObj = data.getAsJsonObject();

		JsonElement ruleEle = rules.getAsJsonObject().get(RULES_TAG);
		if (ruleEle == null || ruleEle.isJsonNull()) {
			throw new IllegalArgumentException(
					"Rules Json does not have rules property");
		}

		rulesJsonObj = ruleEle.getAsJsonObject();
	}

	private void initParameters(Map<String, String> parameters) {
		if (parameters == null) {
			parameters = new HashMap<String, String>();
		}

		this.gson = new Gson();
		this.paramsJsonObj = this.gson.toJsonTree(parameters).getAsJsonObject();
	}

	public String apply() {
		String main = rulesJsonObj.get(MAIN_TAG).getAsString();
		return this.apply(dataJsonObj, rulesJsonObj, main);
	}

	private String apply(JsonElement inputDataJsonEle,
			JsonElement inputRulesJsonEle, String input) {

		input = this.revomeDoubleQuotesForStartEnd(input);

		if (input.startsWith("function")) {
			Object flag = executeFunction(inputDataJsonEle, input);
			if (flag != null) {
				return flag.toString();
			}
		} else {
			// text in curly brackets including {}
			final Pattern textInCurlyBracesPattern = Pattern
					.compile("(\\{)(.*?)(\\})");
			final Matcher matcher = textInCurlyBracesPattern.matcher(input);

			while (matcher.find()) {
				String textInCurlyBraces = matcher.group();

				String processedVariable = this.processVariable(
						inputDataJsonEle, inputRulesJsonEle, textInCurlyBraces);

				if (processedVariable != null) {
					input = input.replace(textInCurlyBraces, processedVariable);
				}
			}
		}

		return input;
	}

	private String revomeDoubleQuotesForStartEnd(String str) {
		if (str.startsWith("\"")) {
			str = str.substring(1);
		}

		if (str.endsWith("\"")) {
			str = str.substring(0, str.length() - 1);
		}

		return str;
	}

	private String processVariable(JsonElement inputDataJsonEle,
			JsonElement inputRulesJsonEle, String variable) {
		if (inputDataJsonEle == null || inputDataJsonEle.isJsonNull()) {
			return "";
		}

		if (variable.startsWith("{$$")) { // Get value from parameter.
			String variableName = variable.replace("{$$", "").replace("}", "");
			String variableValue = paramsJsonObj.get(variableName)
					.getAsString();

			if (variableValue == null) {
				variableValue = "";
			}
			return variableValue;
		} else if (variable.startsWith("{$")) { // Get value from current Json.

			if (variable.startsWith("{$xParent")) {

			} else {
				String variableName = variable.replace("{$", "").replace("}",
						"");

				if (variableName.equals("")) {
					return this.revomeDoubleQuotesForStartEnd(inputDataJsonEle
							.toString());
					// return inputDataJsonEle.toString().replace("\"", "");
				} else {
					if (inputDataJsonEle.isJsonObject()) {
						JsonElement ele = inputDataJsonEle.getAsJsonObject()
								.get(variableName);
						if (ele != null && !ele.isJsonNull()) {
							return ele.getAsString();
						}
					}
				}
			}
		} else if (variable.startsWith("{")) { // Get value from next key from
												// rules Json.
			String result = processRuleVariable(inputDataJsonEle,
					inputRulesJsonEle, variable);

			return result;
		} else {
			return variable;
		}
		return null;
	}

	private String processRuleVariable(JsonElement inputDataJsonEle,
			JsonElement inputRulesJsonEle, String variable) {

		String variableName = variable.replace("{", "").replace("}", "");

		if (inputRulesJsonEle.isJsonPrimitive()) {
			return this.apply(inputDataJsonEle, inputRulesJsonEle, variable);
		} else if (inputRulesJsonEle.isJsonArray()) {
			// TODO:
		} else if (inputRulesJsonEle.isJsonObject()) {
			JsonElement ruleJsonEle = inputRulesJsonEle.getAsJsonObject().get(
					variableName);

			if (ruleJsonEle.isJsonObject()) {
				String main = ruleJsonEle.getAsJsonObject().get(MAIN_TAG)
						.getAsString();

				JsonElement conditionJsonEle = ruleJsonEle.getAsJsonObject()
						.get(CONDITION_TAG);

				if (conditionJsonEle != null && !conditionJsonEle.isJsonNull()) {
					String condition = conditionJsonEle.getAsString();
					String result = this.processData(inputDataJsonEle,
							ruleJsonEle, main, condition, variableName);
					return result;
				} else {
					String result = this.processData(inputDataJsonEle,
							ruleJsonEle, main, null, variableName);
					return result;
				}
			} else if (ruleJsonEle.isJsonPrimitive()) {
				String result = this.apply(inputDataJsonEle, inputRulesJsonEle,
						ruleJsonEle.toString());
				return result;
			}
		}
		return "";
	}

	private String processData(JsonElement inputDataJsonEle,
			JsonElement inputRulesJsonEle, String data, String condition,
			String variableName) {

		JsonElement dataJsonEle = inputDataJsonEle.getAsJsonObject().get(
				variableName);

		if (dataJsonEle != null && !dataJsonEle.isJsonNull()) {
			if (dataJsonEle.isJsonArray()) {
				JsonArray dataJsonArr = dataJsonEle.getAsJsonArray();
				StringBuilder sb = new StringBuilder();

				for (int i = 0; i < dataJsonArr.size(); i++) {
					JsonElement dataJsonElement = dataJsonArr.get(i);

					if (this.applyCondition(dataJsonElement, inputRulesJsonEle,
							condition)) {
						sb.append(this.apply(dataJsonElement,
								inputRulesJsonEle, data));
					}
				}
				return sb.toString();
			} else {
				if (this.applyCondition(dataJsonEle, inputRulesJsonEle,
						condition)) {
					return this.apply(dataJsonEle, inputRulesJsonEle, data);
				}
			}
		}

		return "";
	}

	private boolean applyCondition(JsonElement inputDataJsonEle,
			JsonElement inputRulesJsonObj, String condition) {

		if (condition == null) {
			return true;
		}

		if (condition.startsWith("notEq")) {
			condition = condition.replace("\\s", "");
			Pattern pattern = Pattern.compile("(?<=notEq\\()(.*)(?=\\))");
			Matcher matcher = pattern.matcher(condition);
			if (matcher.find()) {
				String jj = matcher.group();

				String[] strs = jj.split(",");

				String first = strs[0];
				String second = strs[1];

				first = this.processVariable(
						inputDataJsonEle.getAsJsonObject(), inputRulesJsonObj,
						first);
				second = this.processVariable(
						inputDataJsonEle.getAsJsonObject(), inputRulesJsonObj,
						second);

				first = first.replace("'", "");
				second = second.replace("'", "");

				if (!first.equals(second)) {
					return true;
				}
			}
		} else if (condition.startsWith("eq")) {
			condition = condition.replace("\\s", "");
			Pattern pattern = Pattern.compile("(?<=eq\\()(.*)(?=\\))");
			Matcher matcher = pattern.matcher(condition);

			if (matcher.find()) {
				String jj = matcher.group();

				String[] strs = jj.split(",");

				String first = strs[0];
				String second = strs[1];

				first = this.processVariable(
						inputDataJsonEle.getAsJsonObject(), inputRulesJsonObj,
						first);
				second = this.processVariable(
						inputDataJsonEle.getAsJsonObject(), inputRulesJsonObj,
						second);

				first = first.replace("'", "");
				second = second.replace("'", "");

				if (first.equals(second)) {
					return true;
				}
			}
		} else if (condition.startsWith("function")) {
			Object flag = executeFunction(inputDataJsonEle.getAsJsonObject(),
					condition);
			if (flag instanceof Boolean) {
				return (Boolean) flag;
			}
		}
		return false;
	}

	public Object executeFunction(JsonElement dataElement, String funcDefinition) {
		Pattern pattern = Pattern.compile("(?i)(?<=function)(.+?)(?=\\()");
		Matcher matcher = pattern.matcher(funcDefinition);

		if (matcher.find()) {
			String funcName = matcher.group().trim();

			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("JavaScript");

			try {
				engine.eval(funcDefinition);
				Invocable inv1 = (Invocable) engine;

				return inv1.invokeFunction(funcName, dataElement, this.paramsJsonObj);
			} catch (ScriptException e) {
				System.out.println(e);
			} catch (NoSuchMethodException e) {
				System.out.println(e);
			}
		}
		return null;
	}
}
