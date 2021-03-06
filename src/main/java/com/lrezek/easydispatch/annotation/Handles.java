/*
 * The MIT License
 *
 * Copyright 2016 Lukas Rezek.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.lrezek.easydispatch.annotation;

import com.lrezek.easydispatch.dispatch.strategy.DispatchStrategy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the handles annotation.
 * 
 * @author Lukas Rezek
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@Repeatable(HandlesContainer.class)
public @interface Handles 
{
    /**
     * The dispatched classes the annotated class or method handles.
     * 
     * @return Array of classes.
     */
    Class[] value() default {};
    
    /**
     * Custom dispatch strategy to use.
     * 
     * @return The dispatch strategy.
     */
    Class<? extends DispatchStrategy> dispatchStrategy() default DispatchStrategy.class;
    
    /**
     * Custom method name to use (Only applies to class level @Handles annotations).
     * 
     * @return The custom method name.
     */
    String method() default "";
}
