/*
 * Copyright 2013, 2014, 2015 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.energyos.espi.datacustodian.utils.factories;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

public class FixtureFactory {
	private FixtureFactory() {
	}

	public static InputStream newInputStream(String fileName)
			throws IOException {
		return new ByteArrayInputStream(newXML(fileName).getBytes());
	}

	public static InputStream newFeedInputStream() throws IOException {
		return new ByteArrayInputStream(newXML("/fixtures/test_usage_data.xml")
				.getBytes());
	}

	public static InputStream newFeedInputStream(UUID uuid) throws IOException {
		return new ByteArrayInputStream(newFeedXML(uuid).getBytes());
	}

	public static InputStream newUsagePointInputStream(UUID uuid)
			throws IOException {
		return new ByteArrayInputStream(newUsagePointXML(uuid).getBytes());
	}

	public static InputStream newMeterReadingInputStream(UUID uuid)
			throws IOException {
		return new ByteArrayInputStream(newMeterReadingXML(uuid).getBytes());
	}

	public static String newFeedXML(UUID uuid) throws IOException {
		return newXML(uuid, "/fixtures/test_usage_data.xml");
	}

	public static String newUsagePointXML(UUID uuid) throws IOException {
		return newXML(uuid, "/fixtures/usage_point.xml");
	}

	public static String newMeterReadingXML(UUID uuid) throws IOException {
		return newXML(uuid, "/fixtures/meter_reading.xml");
	}

	public static String newXML(UUID uuid, String fileName) throws IOException {
		String s = newXML(fileName);

		Pattern pattern = Pattern.compile("<id>urn:uuid:([A-Z0-9-]+)</id>");
		StringBuffer myStringBuffer = new StringBuffer();
		Matcher matcher = pattern.matcher(s);
		int i = 0;
		while (matcher.find()) {
			UUID replacement;
			if (i == 1) {
				replacement = uuid;
			} else {
				replacement = UUID.randomUUID();
			}
			matcher.appendReplacement(myStringBuffer, "<id>urn:uuid:"
					+ replacement.toString().toUpperCase() + "</id>");
			i++;
		}
		matcher.appendTail(myStringBuffer);
		String subscription = myStringBuffer.toString().replaceAll("9B6C7066",
				uuid.toString());
		return subscription;
	}

	public static String newXML(String fileName) throws IOException {
		ClassPathResource sourceFile = new ClassPathResource(fileName);
		String xml = FileUtils.readFileToString(sourceFile.getFile());
		return xml;
	}

	public static String loadFixture(String path) throws IOException {
		ClassPathResource sourceFile = new ClassPathResource(path);
		return FileUtils.readFileToString(sourceFile.getFile());
	}
}
