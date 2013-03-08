/*
 * Copyright (c) 2013 Robert Roland
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.robertroland.cadence;

import com.google.common.collect.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;
import org.robertroland.cadence.types.Serializer;
import org.robertroland.cadence.types.TypeSerializer;

import java.util.Map;
import java.util.Set;

/**
 * @author robert@robertroland.org
 * @since 3/7/13
 */
public class TypeSerializerFactory {
    private static final Log LOG = LogFactory.getLog(TypeSerializerFactory.class);

    private static final Map<String, Serializer<?>> serializers = Maps.newHashMap();

    static {
        Reflections reflections = new Reflections("org.robertroland.cadence");

        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(TypeSerializer.class);

        for(Class<?> c: annotated) {
            TypeSerializer s = c.getAnnotation(TypeSerializer.class);
            String typeName = s.typeName();

            try {
                Object instance = c.newInstance();

                if(!(instance instanceof Serializer)) {
                    LOG.error(String.format(
                            "Serializers must implement the TypeSerializer interface, cannot instantiate %s",
                            instance.getClass().getName()));
                } else {
                    LOG.info(String.format("Instantiated type serializer %s", typeName));
                    serializers.put(typeName, (Serializer)instance);
                }
            } catch(InstantiationException ie) {
                LOG.error(String.format("Unable to instantiate serializer for %s", typeName), ie);
            } catch(IllegalAccessException iae) {
                LOG.error(String.format("Unable to instantiate serializer for %s", typeName), iae);
            }
        }
    }

    public static Serializer<?> getSerializer(String typeName) {
        return serializers.get(typeName);
    }
}
