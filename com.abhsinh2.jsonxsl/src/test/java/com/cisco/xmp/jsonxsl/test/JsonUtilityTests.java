package com.cisco.xmp.jsonxsl.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.cisco.xmp.jsonxsl.JsonUtility;
import com.cisco.xmp.jsonxsl.JsonXSLT;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonUtilityTests {

	public JsonUtilityTests() {
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

	@Test
	public void testConvertJsonToXML() throws Exception {
		String emptyJson = "{}";
		String arrayJson = "{\"host\": [\"a\", \"b\"]}";
		String objectJson = "{\"zone\": \"internal\", \"host_name\": \"abhsinh2-virtual-machine\", \"service\": \"conductor\"}";
		String objectArrayObject = "{\"hosts\": [{\"zone\": \"internal\", \"host_name\": \"machine1\", \"service\": \"conductor\"},"
				+ "{\"zone\": \"internal\", \"host_name\": \"machine2\", \"service\": \"cert\" }]}";

		Assert.assertEquals(
				JsonUtility.convertJsonToXml(emptyJson, "root", "ele"),
				"<root></root>");
		Assert.assertEquals(
				JsonUtility.convertJsonToXml(arrayJson, "root", "ele"),
				"<root><ele>a</ele><ele>b</ele></root>");
		Assert.assertEquals(
				JsonUtility.convertJsonToXml(arrayJson, "root", null),
				"<root><element>a</element><element>b</element></root>");
		Assert.assertEquals(
				JsonUtility.convertJsonToXml(objectJson, "root", null),
				"<root><zone>internal</zone><host_name>abhsinh2-virtual-machine</host_name><service>conductor</service></root>");
		Assert.assertEquals(
				JsonUtility.convertJsonToXml(objectArrayObject, "host", null),
				"<host><element><zone>internal</zone><host_name>machine1</host_name><service>conductor</service></element><element><zone>internal</zone><host_name>machine2</host_name><service>cert</service></element></host>");
	}

	@Test
	public void testApplyXsl() throws Exception {
		String xmlContent = "<hosts>"
				+ "<element><zone>internal</zone><host_name>machine1</host_name><service>conductor</service></element>"
				+ "<element><zone>internal</zone><host_name>machine2</host_name><service>cert</service></element>"
				+ "</hosts>";
		String xslFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/xsls/hosts.xsl";

		File xslFile = new File(xslFilePath);

		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Machines>"
				+ "<Machine><name>machine1</name><type>host</type><parent/><size/></Machine>"
				+ "<Machine><name>machine2</name><type>host</type><parent/><size/></Machine>"
				+ "</Machines>";

		Assert.assertEquals(JsonUtility.applyXslt(xmlContent, xslFile, null),
				expected);

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("parent", "myparent");

		expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Machines>"
				+ "<Machine><name>machine1</name><type>host</type><parent>myparent</parent><size/></Machine>"
				+ "<Machine><name>machine2</name><type>host</type><parent>myparent</parent><size/></Machine>"
				+ "</Machines>";

		Assert.assertEquals(
				JsonUtility.applyXslt(xmlContent, xslFile, parameters),
				expected);
	}

	@Test
	public void testHostsTimingsUsingDirectJsonParsing() throws Exception {
		StringBuilder data = new StringBuilder();

		int size = 3000;

		data.append("{\"hosts\": [");
		for (int i = 0; i < size; i++) {
			data.append("{\"zone\": \"compute\", \"host_name\": \"abhsinh2-virtual-machine\", \"service\": \"conductor\"}");
			if (i != (size - 1)) {
				data.append(",");
			}
		}
		data.append("]}");

		long start = System.currentTimeMillis();

		JsonElement root = new JsonParser().parse(data.toString());
		JsonArray hostsJsonObj = root.getAsJsonObject().get("hosts")
				.getAsJsonArray();
		StringBuilder sb = new StringBuilder();
		String server = "10.104.242.98";

		sb.append("</Machines>");

		for (int i = 0; i < hostsJsonObj.size(); i++) {
			String hostName = hostsJsonObj.get(i).getAsJsonObject()
					.get("host_name").getAsString();
			String zone = hostsJsonObj.get(i).getAsJsonObject().get("zone")
					.getAsString();

			System.out.println(this.getClass().getName() + " hostName:"
					+ hostName + ", zone:" + zone);

			if (!zone.equals("internal")) {
				sb.append("<Machine>");
				sb.append("<name>" + hostName + "</name>");
				sb.append("<type>host</type>");
				sb.append("<parent>" + server + "</parent>");
				sb.append("<size>10</size>");
				sb.append("</Machine>");
			}
		}

		sb.append("</Machines>");

		long end = System.currentTimeMillis();

		System.out.println("Time Taken Using Direct Json Parsing:"
				+ (end - start));
		// System.out.println(sb.toString());
	}

	@Test
	public void testHostsTimingsUsingXSL() throws Exception {
		StringBuilder sb = new StringBuilder();

		int size = 3000;
		String xslFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/xsls/hosts.xsl";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("virOwningEntityId", "10.104.242.98");

		sb.append("{\"hosts\": [");
		for (int i = 0; i < size; i++) {
			sb.append("{\"zone\": \"internal\", \"host_name\": \"abhsinh2-virtual-machine\", \"service\": \"conductor\"}");
			if (i != (size - 1)) {
				sb.append(",");
			}
		}
		sb.append("]}");

		long start = System.currentTimeMillis();
		String xmlContent = JsonUtility.convertJsonToXml(sb.toString(),
				"servers", "element");

		String output = JsonUtility.applyXslt(xmlContent,
				new File(xslFilePath), parameters);
		long end = System.currentTimeMillis();

		System.out.println("Time Taken Using XSL:" + (end - start));

		// System.out.println(output);
	}

	@Test
	public void testHostsTimingsUsingScriptParsing() throws Exception {
		StringBuilder data = new StringBuilder();

		int size = 3000;

		data.append("{\"hosts\": [");
		for (int i = 0; i < size; i++) {
			data.append("{\"zone\": \"compute\", \"host_name\": \"abhsinh2-virtual-machine\", \"service\": \"conductor\"}");
			if (i != (size - 1)) {
				data.append(",");
			}
		}
		data.append("]}");

		String server = "10.104.242.98";

		long start = System.currentTimeMillis();

		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		// read script file
		engine.eval(Files.newBufferedReader(
				Paths.get(System.getProperty("user.dir")
						+ "/src/test/resources" + "/js/hosts.js"),
				StandardCharsets.UTF_8));
		Invocable inv = (Invocable) engine;
		String output = (String) inv.invokeFunction("getHosts",
				data.toString(), server);

		long end = System.currentTimeMillis();

		System.out.println("Time Taken Using Script Parsing:" + (end - start));
		// System.out.println(output);
	}

	@Test
	public void testHostsTimingsUsingMyCustomJsonXSL() throws Exception {
		StringBuilder data = new StringBuilder();

		int size = 3000;

		data.append("{\"hosts\": [");
		for (int i = 0; i < size; i++) {
			data.append("{\"zone\": \"compute\", \"host_name\": \"abhsinh2-virtual-machine\", \"service\": \"conductor\"}");
			if (i != (size - 1)) {
				data.append(",");
			}
		}
		data.append("]}");

		JsonElement dataJsonElement = new JsonParser().parse(data.toString());

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("parent", "10.104.242.98");

		String jsonFilePath = System.getProperty("user.dir")
				+ "/src/test/resources" + "/rules/rules_hosts.json";

		long start = System.currentTimeMillis();

		File jsonFile = new File(jsonFilePath);
		JsonElement ruleJsonElement = new JsonParser().parse(new FileReader(
				jsonFile));

		String output = new JsonXSLT(dataJsonElement, ruleJsonElement,
				parameters).apply();

		long end = System.currentTimeMillis();

		System.out.println("Time Taken Using My Custom Json XSL:"
				+ (end - start));
		// System.out.println(output);
	}
}
