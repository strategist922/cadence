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

package org.robertroland.cadence.types

import org.apache.hadoop.hbase.util.Bytes
import org.junit.Test

import static org.junit.Assert.*

/**
 * 
 * @author robert@robertroland.org
 * @since 3/7/13
 */
class BigDecimalSerializerTest {
    def serializer = new BigDecimalSerializer();

    @Test
    void testDeserialize() {
        assertEquals new BigDecimal("100.12"), serializer.deserialize(Bytes.toBytes(new BigDecimal("100.12")))
    }

    @Test
    void testSerialize() {
        assertArrayEquals Bytes.toBytes(new BigDecimal("123412.12")), serializer.serialize(new BigDecimal("123412.12"))
    }
}
