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
package com.lrezek.easydispatch.handle.meta.cache;

import com.lrezek.easydispatch.handle.meta.HandleMeta;
import java.util.Collection;

/**
 * Defines a handle meta cache. An implementation of this is used to get handle 
 * meta objects so we don't have to do reflection scanning for every handler. 
 * 
 * @author Lukas Rezek
 */
public interface HandleMetaCache
{    
    /**
     * Determines if a cache entry exists for the specified class.
     * 
     * @param cls The class.
     * @return True if there is a cache entry, false otherwise.
     */
    boolean contains(Class cls);
    
    /**
     * Gets the handle meta collection for the class, or null if not found.
     * 
     * @param cls The class.
     * @return The cached handle meta information, or null if not found.
     */
    Collection<HandleMeta> get(Class cls);
    
    /**
     * Puts a collection of handle meta objects on the cache, for the specified
     * class.
     * 
     * @param cls The class.
     * @param handlerDataCollection The handle meta information for the class. 
     */
    void put(Class cls, Collection<HandleMeta> handlerDataCollection);
    
    /**
     * Clears the cache.
     */
    void clear();
}