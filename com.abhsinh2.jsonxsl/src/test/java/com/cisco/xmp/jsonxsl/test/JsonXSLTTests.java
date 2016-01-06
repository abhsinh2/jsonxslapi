package com.cisco.xmp.jsonxsl.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.cisco.xmp.jsonxsl.JsonXSLT;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class JsonXSLTTests {

	public JsonXSLTTests() {
	}

	@BeforeClass
	public static void beforeClass() {

	}

	@AfterClass
	public static void afterClass() throws IOException {

	}

	@BeforeTest
	public void beforeTest() {

	}

	@AfterTest
	public void afterTest() {

	}

	// passed
	@Test
	public void testSimple() throws JsonIOException, JsonSyntaxException,
			FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/data/data_simple.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/rules/rules_simple.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<root><name>test1</name><age>20</age></root>";
		Assert.assertEquals(actualResult, expectedResult);
	}

	// passed
	@Test
	public void testSimpleArray() throws JsonIOException, JsonSyntaxException,
			FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/data/data_simpleArray.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/rules/rules_simpleArray.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<names><name>test1</name><name>test2</name></names>";
		Assert.assertEquals(actualResult, expectedResult);
	}

	// passed
	@Test
	public void testSimpleObject() throws JsonIOException, JsonSyntaxException,
			FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/data/data_simpleObject.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/rules/rules_simpleObject.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<root><name>test1</name><age>20</age></root>";
		Assert.assertEquals(actualResult, expectedResult);
	}

	// passed
	@Test
	public void testObjectWithObject() throws JsonIOException,
			JsonSyntaxException, FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/data/data_objectWithObject.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/rules/rules_objectWithObject.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<root><name>test1</name><address><city>Bangalore</city><zip>560001</zip></address></root>";
		Assert.assertEquals(actualResult, expectedResult);
	}

	// passed
	@Test
	public void testObjectWithSimpleArray() throws JsonIOException,
			JsonSyntaxException, FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/data/data_objectWithSimpleArray.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/rules/rules_objectWithSimpleArray.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<root><name>test1</name><cities><city>Bangalore</city><city>Delhi</city></cities></root>";
		Assert.assertEquals(actualResult, expectedResult);
	}

	// passed
	@Test
	public void testObjectWithArrayObject() throws JsonIOException,
			JsonSyntaxException, FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/data/data_objectWithArrayObject.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/rules/rules_objectWithArrayObject.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<root><name>test1</name><addresses>"
				+ "<address><city>Bangalore</city><zip>560001</zip></address>"
				+ "<address><city>Delhi</city><zip>110001</zip></address>"
				+ "</addresses></root>";
		Assert.assertEquals(actualResult, expectedResult);
	}

	// passed
	@Test
	public void testObjectWithObjectWithInbuiltCondition()
			throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/data/data_objectWithObject.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/rules/rules_objectWithObjectWithInbuiltCondition.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<root><name>test1</name><address><city>Bangalore</city><zip>560001</zip></address></root>";
		Assert.assertEquals(actualResult, expectedResult);
	}

	// passed
	@Test
	public void testObjectWithObjectWithInbuiltCondition_Negative()
			throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/data/data_objectWithObject.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/rules/rules_objectWithObjectWithInbuiltCondition_negative.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<root></root>";
		Assert.assertEquals(actualResult, expectedResult);
	}

	// passed
	@Test
	public void testObjectWithObjectWithJSFunctionCondition()
			throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/data/data_objectWithObject.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/rules/rules_objectWithObjectWithJSFunctionCondition.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<root><name>test1</name><address><city>Bangalore</city><zip>560001</zip></address></root>";
		Assert.assertEquals(actualResult, expectedResult);
	}

	// passed
	@Test
	public void testObjectWithObjectWithJSFunctionCondition_Negative()
			throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/data/data_objectWithObject.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/rules/rules_objectWithObjectWithJSFunctionCondition_negative.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<root></root>";
		Assert.assertEquals(actualResult, expectedResult);
	}

	// passed
	@Test
	public void testObjectWithObjectWithInbuiltConditionOnChild()
			throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/data/data_objectWithObject.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/rules/rules_objectWithObjectWithInbuiltConditionOnChild.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<root><name>test1</name><address><city>Bangalore</city><zip>560001</zip></address></root>";
		Assert.assertEquals(actualResult, expectedResult);
	}

	// passed
	@Test
	public void testObjectWithObjectWithInbuiltConditionOnChild_Negative()
			throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/data/data_objectWithObject.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/rules/rules_objectWithObjectWithInbuiltConditionOnChild_negative.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<root><name>test1</name><address></address></root>";
		Assert.assertEquals(actualResult, expectedResult);
	}

	// passed
	@Test
	public void testObjectWithObjectWithJSFunctionConditionOnChild()
			throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/data/data_objectWithObject.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/rules/rules_objectWithObjectWithJSFunctionConditionOnChild.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<root><name>test1</name><address><city>Bangalore</city><zip>560001</zip></address></root>";
		Assert.assertEquals(actualResult, expectedResult);
	}

	// passed
	@Test
	public void testObjectWithObjectWithJSFunctionConditionOnChild_Negative()
			throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/data/data_objectWithObject.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/rules/rules_objectWithObjectWithJSFunctionConditionOnChild_negative.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<root><name>test1</name><address></address></root>";
		Assert.assertEquals(actualResult, expectedResult);
	}

	// passed
	@Test
	public void testObjectWithArrayObjectWithInbuiltCondition()
			throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/data/data_objectWithArrayObject.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/rules/rules_objectWithArrayObjectWithInbuiltCondition.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<root><name>test1</name><addresses><city>Bangalore</city><zip>560001</zip></addresses></root>";
		Assert.assertEquals(actualResult, expectedResult);
	}

	@Test
	public void testObjectWithArrayObjectWithJSFunctionCondition()
			throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/data/data_objectWithArrayObject.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/rules/rules_objectWithArrayObjectWithJSFunctionCondition.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<root><name>test1</name><addresses><city>Bangalore</city><zip>560001</zip></addresses></root>";
		Assert.assertEquals(actualResult, expectedResult);
	}

	// passed
	@Test
	public void testObjectWithObjectWithInputParameters()
			throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/data/data_objectWithObject.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/rules/rules_objectWithObjectWithInputParameters.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("age", "20");

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<root><name>test1</name><age>20</age><address><city>Bangalore</city><zip>560001</zip></address></root>";
		Assert.assertEquals(actualResult, expectedResult);
	}

	// passed
	@Test
	public void testObjectWithObjectWithInputParametersInCondition()
			throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		String dataJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/data/data_objectWithObject.json";
		String rulesJsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources"
				+ "/rules/rules_objectWithObjectWithInputParametersInCondition.json";

		JsonElement dataJsonElement = new JsonParser().parse(new FileReader(
				new File(dataJsonFilePath)));
		JsonElement rulesJsonElement = new JsonParser().parse(new FileReader(
				new File(rulesJsonFilePath)));

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("xName", "test1");

		JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement,
				parameters);
		String actualResult = jsonXSLT.apply();

		String expectedResult = "<root><name>test1</name><address><city>Bangalore</city><zip>560001</zip></address></root>";
		Assert.assertEquals(actualResult, expectedResult);

		parameters = new HashMap<String, String>();
		parameters.put("xName", "test2");

		jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement, parameters);
		actualResult = jsonXSLT.apply();

		expectedResult = "<root></root>";
		Assert.assertEquals(actualResult, expectedResult);
	}

}
