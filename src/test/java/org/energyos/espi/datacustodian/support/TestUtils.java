/*
 * Copyright 2013 EnergyOS ESPI
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
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class TestUtils {

    public static void assertNotEmptyValidation(Class clazz, String fieldName) {
        if (getAnnotation(clazz, fieldName, NotEmpty.class) == null)
            throw new AssertionFailedError("@NotEmpty annotation missing for field " + fieldName);
    }

    public static void assertSizeValidation(Class clazz, String fieldName, int min, int max) {
        Annotation annotation = getAnnotation(clazz, fieldName, Size.class);
        if (annotation == null)
            throw new AssertionFailedError("@Size annotation missing for field " + fieldName);

        int minValue = ((javax.validation.constraints.Size)annotation).min();
        if (min != minValue)
            throw new AssertionFailedError("@Size annotation minimum does not match expected " + min);

        int maxValue = ((javax.validation.constraints.Size)annotation).max();
        if (max != maxValue)
            throw new AssertionFailedError("@Size annotation maximum does not match expected " + max);
    }

    private static Annotation getAnnotation(Class clazz, String fieldName, Class annotationClass) {
        Annotation[] annotations = null;
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field != null) {
                annotations = field.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().getName() == annotationClass.getCanonicalName()) {
                        return annotation;
                    }
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
