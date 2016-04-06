package com.iselsoft.easyium.support;

import com.iselsoft.easyium.Context;
import com.iselsoft.easyium.Element;
import com.iselsoft.easyium.StaticElement;
import com.iselsoft.easyium.WebDriver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Model {
    protected final Context context;
    public final WebDriver webDriver;

    protected Model(Context context) throws ReflectiveOperationException {
        this.context = context;
        this.webDriver = context.getWebDriver();
        initElements(this.getClass());
    }

    private void initElements(Class<?> thisClass) throws ReflectiveOperationException {
        for (Field field : thisClass.getDeclaredFields()) {
            FoundBy annotation = field.getAnnotation(FoundBy.class);
            field.setAccessible(true);
            if (annotation == null) {
                continue;
            }
            if (field.get(this) != null) {
                throw new AnnotationException("Cannot add @FoundBy for field with value.");
            }
            if (Modifier.isStatic(field.getModifiers())) {
                throw new AnnotationException("Cannot add @FoundBy for static field.");
            }
            Class<?> type = field.getType();
            if (Control.class.isAssignableFrom(type)) {
                Constructor<?> constructor = type.getDeclaredConstructor(Element.class);
                constructor.setAccessible(true);
                field.set(this, constructor.newInstance(new StaticElement(context, annotation.locator())));
                continue;
            }
            if (type.equals(Element.class) || type.equals(StaticElement.class)) {
                field.set(this, new StaticElement(context, annotation.locator()));
                continue;
            }
            throw new AnnotationException("Cannot add @FoundBy for field whose type is not " +
                    "Element or StaticElement or subclass of Control.");
        }
        Class<?> superclass = thisClass.getSuperclass();
        if (!superclass.getPackage().getName().equals("com.iselsoft.easyium.support")) {
            initElements(superclass);
        }
    }
}
