/*
 * Copyright 2017 Rudy De Busscher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.atbash.ee.test.util;

import java.lang.reflect.Field;

/**
 *
 */
public final class ReflectionUtil {

    private ReflectionUtil() {
    }

    /**
     * Injects objects into the target (private) fields by matching the type.
     *
     * @param target       The target object where the dependencies are injected
     * @param dependencies The objects we like to set into the target.
     * @throws IllegalAccessException Should not happen since we overrule the accessibility
     */
    public static void injectDependencies(final Object target, final Object... dependencies) throws IllegalAccessException {
        Class targetClass = target.getClass();
        while (targetClass != null && targetClass != Object.class) {
            if (targetClass.getName().contains("$")) {
                targetClass = targetClass.getSuperclass();
            }
            for (Field field : targetClass.getDeclaredFields()) {
                field.setAccessible(true);
                for (Object dependency : dependencies) {
                    if (field.getType().isAssignableFrom(dependency.getClass())) {
                        field.set(target, dependency);
                    }
                }

            }
            targetClass = targetClass.getSuperclass();
        }
    }

    public static void setFieldValue(Object target, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {

        Field field = findInstanceField(target, fieldName);

        field.setAccessible(true);
        field.set(target, value);
    }

    private static Field findInstanceField(Object target, String fieldName) throws NoSuchFieldException {
        Class<?> targetClass = target.getClass();
        return findFieldInHierarchy(targetClass, fieldName);
    }

    private static Field findFieldInHierarchy(Class<?> targetClass, String fieldName) throws NoSuchFieldException {
        Field field = findField(targetClass, fieldName);
        while (field == null && !Object.class.equals(targetClass)) {
            targetClass = targetClass.getSuperclass();
            field = findField(targetClass, fieldName);
        }

        if (field == null) {
            throw new NoSuchFieldException("Field " + fieldName + " not found");
        }
        return field;
    }

    private static Field findField(Class<?> targetClass, String fieldName) {
        Field result = null;
        for (Field field : targetClass.getDeclaredFields()) {
            if (fieldName.equals(field.getName())) {
                result = field;
            }
        }
        return result;
    }

    public static <T> T getFieldValue(Object target, String fieldName) throws NoSuchFieldException, IllegalAccessException {

        Field field = findInstanceField(target, fieldName);

        field.setAccessible(true);
        return (T) field.get(target);
    }

    public static <T> T getStaticFieldValue(Class<?> target, String fieldName) throws NoSuchFieldException, IllegalAccessException {

        Field field = findFieldInHierarchy(target, fieldName);

        field.setAccessible(true);
        return (T) field.get(target);
    }

}
