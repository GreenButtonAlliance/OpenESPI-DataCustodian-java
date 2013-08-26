/*
 * Copyright 2013 EnergyOS.org
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

package org.energyos.espi.datacustodian.support;

import junit.framework.AssertionFailedError;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class TestUtils {

    public static void assertAnnotationPresent(Class clazz, String fieldName, Class annotationClass) {
        if(getAnnotation(clazz, fieldName, annotationClass) == null)
            throw new AssertionFailedError(String.format("'%s' annotation is missing for field '%s'", annotationClass.getCanonicalName(), fieldName));
    }

    public static void assertSizeValidation(Class clazz, String fieldName, int min, int max) {
        assertAnnotationPresent(clazz, fieldName, Size.class);

        Annotation annotation = getAnnotation(clazz, fieldName, Size.class);

        int minValue = ((javax.validation.constraints.Size) annotation).min();
        if (min != minValue)
            throw new AssertionFailedError(String.format("@Size annotation minimum does not match expected '%s'", min));

        int maxValue = ((javax.validation.constraints.Size) annotation).max();
        if (max != maxValue)
            throw new AssertionFailedError(String.format("@Size annotation maximum does not match expected '%s'", max));
    }

    public static void assertXmlElement(Class clazz, String fieldName, Class type) {
        assertAnnotationPresent(clazz, fieldName, XmlElement.class);

        Annotation annotation = getAnnotation(clazz, fieldName, XmlElement.class);

        Class annotationType = ((XmlElement)annotation).type();
        if (type != annotationType)
            throw new AssertionFailedError(String.format("@XmlElement annotation type does not match expected '%s'", type));
    }

    private static Annotation getAnnotation(Class clazz, String fieldName, Class annotationClass) {
        Field field;

        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new AssertionFailedError(String.format("'%s' is missing field '%s'", clazz.getCanonicalName(), fieldName));
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
}
