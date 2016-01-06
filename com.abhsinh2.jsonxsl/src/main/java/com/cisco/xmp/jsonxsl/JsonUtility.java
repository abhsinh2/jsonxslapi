package com.cisco.xmp.jsonxsl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtility {
	/**
	 * 
	 * @param jsonStr
	 *            json string to be converted to XML
	 * @param rootElement
	 *            root element for this Json to be root element for XML
	 * @param arrayElement
	 *            element to be used when encounter array.
	 * @return
	 */
	public static String convertJsonToXml(String jsonStr, String rootElement,
			String arrayElement) {

		if (jsonStr == null || jsonStr.isEmpty()) {
			return null;
		}

		if (rootElement == null || rootElement.isEmpty()) {
			rootElement = "root";
		}

		if (arrayElement == null || arrayElement.isEmpty()) {
			arrayElement = "element";
		}

		JsonElement jsonElement = new JsonParser().parse(jsonStr);
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		StringBuilder sb = new StringBuilder();
		sb.append("<" + rootElement + ">");

		convertJsonObject(jsonObject, arrayElement, sb);

		sb.append("</" + rootElement + ">");

		return sb.toString();
	}

	private static void convertJsonObject(JsonObject jsonObj,
			String arrayElement, StringBuilder sb) {
		Set<Entry<String, JsonElement>> values = jsonObj.entrySet();

		for (Entry<String, JsonElement> value : values) {
			JsonElement element = value.getValue();

			if (element.isJsonArray()) {
				convertJsonArray((JsonArray) element, arrayElement, sb);
			} else if (element.isJsonObject()) {
				convertJsonObject((JsonObject) element, arrayElement, sb);
			} else if (element.isJsonPrimitive()) {
				sb.append("<" + value.getKey() + ">");
				sb.append(element.getAsString());
				sb.append("</" + value.getKey() + ">");
			}
		}
	}

	private static void convertJsonArray(JsonArray jsonArr,
			String arrayElement, StringBuilder sb) {
		for (int i = 0; i < jsonArr.size(); i++) {
			JsonElement element = jsonArr.get(i);

			sb.append("<" + arrayElement + ">");

			if (element.isJsonArray()) {
				convertJsonArray((JsonArray) element, arrayElement, sb);
			} else if (element.isJsonObject()) {
				convertJsonObject((JsonObject) element, arrayElement, sb);
			} else if (element.isJsonPrimitive()) {
				sb.append(element.getAsString());
			}

			sb.append("</" + arrayElement + ">");
		}
	}

	/**
	 * 
	 * @param xmlContent
	 *            xml content on which xsl has to be applied
	 * @param xslFile
	 *            xsl file to be applied
	 * @param parameters
	 *            parameters to be injected to XSL file
	 * @return
	 * @throws FileNotFoundException
	 * @throws TransformerException
	 */
	public static String applyXslt(String xmlContent, File xslFile,
			Map<String, Object> parameters) throws FileNotFoundException,
			TransformerException {
		try {
			// Create transformer factory
			TransformerFactory factory = TransformerFactory.newInstance();

			// Use the factory to create a template containing the xsl file
			Templates template = factory.newTemplates(new StreamSource(
					new FileInputStream(xslFile)));

			// Use the template to create a transformer
			Transformer xformer = template.newTransformer();

			if (parameters != null) {
				for (Entry<String, Object> entry : parameters.entrySet()) {
					xformer.setParameter(entry.getKey(), entry.getValue());
				}
			}

			// Prepare the input and output files
			StringReader sr = new StringReader(xmlContent);
			StringWriter sw = new StringWriter();

			Source source = new StreamSource(sr);
			Result result = new StreamResult(sw);

			// Apply the xsl file to the source file and write the result to the
			// output file
			xformer.transform(source, result);

			return sw.toString();

		} catch (FileNotFoundException e) {
			throw e;
		} catch (TransformerConfigurationException e) {
			System.out.println("An error occurred in the XSL file." + e);
			throw e;
		} catch (TransformerException e) {
			// An error occurred while applying the XSL file
			// Get location of error in input file
			SourceLocator locator = e.getLocator();
			int col = locator.getColumnNumber();
			int line = locator.getLineNumber();
			// String publicId = locator.getPublicId();
			// String systemId = locator.getSystemId();

			System.out.println("An error occurred while applying the XSL file");
			System.out.println("col:" + col + " line:" + line);
			System.out.println("An error occurred while applying the XSL file."
					+ e);

			throw e;
		}
	}

	public static Object executeJsFile(String jsFilePath, String functionName,
			Object... args) throws ScriptException, IOException,
			NoSuchMethodException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");

		engine.eval(Files.newBufferedReader(Paths.get(jsFilePath),
				StandardCharsets.UTF_8));
		Invocable inv = (Invocable) engine;
		return inv.invokeFunction(functionName, args);
	}

	public static Object executeJsFunction(String functionDeclaration,
			String functionName, Object... args) throws ScriptException,
			NoSuchMethodException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");

		// JavaScript code in a String String script1 =
		engine.eval(functionDeclaration);
		Invocable inv = (Invocable) engine;
		return inv.invokeFunction(functionName, args);
	}
}
