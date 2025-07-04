/*
 *
 *    Copyright (c) 2018-2021 Green Button Alliance, Inc.
 *
 *    Portions (c) 2013-2018 EnergyOS.org
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package org.greenbuttonalliance.espi.datacustodian.support;

import com.sun.syndication.feed.atom.Link;
import org.greenbuttonalliance.espi.common.domain.legacy.RetailCustomer;
import org.greenbuttonalliance.espi.common.service.ImportService;
import org.greenbuttonalliance.espi.common.service.UsagePointService;
import org.greenbuttonalliance.espi.common.test.FixtureFactory;
import org.xml.sax.SAXException;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.UUID;

@SuppressWarnings("rawtypes")
public class TestUtils {

	public static void assertAnnotationPresent(Class clazz,
			Class annotationClass) {
		if (getClassAnnotation(clazz, annotationClass) == null)
			throw new AssertionError(String.format(
					"'%s' annotation is missing for class '%s'",
					annotationClass.getCanonicalName(),
					clazz.getCanonicalName()));
	}

	public static void assertAnnotationPresent(Class clazz, String fieldName,
			Class annotationClass) {
		if (getAnnotation(clazz, fieldName, annotationClass) == null)
			throw new AssertionError(String.format(
					"'%s' annotation is missing for field '%s'",
					annotationClass.getCanonicalName(), fieldName));
	}

	public static void assertColumnAnnotation(Class clazz, String fieldName,
			String columnName) {
		Annotation annotation = assertAndGetAnnotation(clazz, fieldName,
				Column.class);

		String name = ((Column) annotation).name();
		if (!name.equals(columnName))
			throw new AssertionError(
					String.format(
							"Column name in @Column annotation not match expected '%s'",
							name));
	}

	public static void assertSizeValidation(Class clazz, String fieldName,
			int min, int max) {
		Annotation annotation = assertAndGetAnnotation(clazz, fieldName,
				Size.class);

		int minValue = ((jakarta.validation.constraints.Size) annotation).min();
		if (min != minValue)
			throw new AssertionError(String.format(
					"@Size annotation minimum does not match expected '%s'",
					min));

		int maxValue = ((jakarta.validation.constraints.Size) annotation).max();
		if (max != maxValue)
			throw new AssertionError(String.format(
					"@Size annotation maximum does not match expected '%s'",
					max));
	}

	public static void assertXmlElement(Class clazz, String fieldName,
			Class type) {
		Annotation annotation = assertAndGetAnnotation(clazz, fieldName,
				XmlElement.class);

		Class annotationType = ((XmlElement) annotation).type();
		if (type != annotationType)
			throw new AssertionError(String.format(
					"@XmlElement annotation type does not match expected '%s'",
					type));
	}

	private static Annotation assertAndGetAnnotation(Class clazz,
			String fieldName, Class annotationClass) {
		assertAnnotationPresent(clazz, fieldName, annotationClass);
		return getAnnotation(clazz, fieldName, annotationClass);
	}

	private static Annotation getAnnotation(Class clazz, String fieldName,
			Class annotationClass) {
		Field field;

		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			throw new AssertionError(String.format(
					"'%s' is missing field '%s'", clazz.getCanonicalName(),
					fieldName));
		}

		Annotation foundAnnotation = null;

		for (Annotation annotation : field.getAnnotations()) {
			if (annotation.annotationType().equals(annotationClass)) {
				foundAnnotation = annotation;
				break;
			}
		}

		return foundAnnotation;
	}

	private static Annotation getClassAnnotation(Class clazz,
			Class annotationClass) {
		Annotation foundAnnotation = null;

		for (Annotation annotation : clazz.getAnnotations()) {
			if (annotation.annotationType().equals(annotationClass)) {
				foundAnnotation = annotation;
				break;
			}
		}

		return foundAnnotation;
	}

	public static void importUsagePoint(ImportService importService,
			UsagePointService usagePointService, RetailCustomer customer,
			UUID uuid) throws JAXBException, IOException,
			ParserConfigurationException, SAXException {
		importService.importData(FixtureFactory.newFeedInputStream(uuid), null);
		usagePointService.associateByUUID(customer, uuid);
	}

	public static <T extends org.greenbuttonalliance.espi.datacustodian.atom.EspiEntry<?>> String findRelatedHref(T entry,
																									   String type) {
		for (Link link : entry.getRelatedLinks()) {
			if (link.getHref().contains(type)) {
				return link.getHref();
			}
		}
		return null;
	}

	public static void assertFieldNotMarshallable(Class clazz, String field) {
		assertAnnotationPresent(clazz, field, XmlTransient.class);
	}
}
