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

package org.robertroland.cadence

import org.apache.hadoop.hbase.KeyValue
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.util.Bytes
import org.robertroland.cadence.schema.TestSchemas

import static org.junit.Assert.*

import org.junit.Test

/**
 *
 * @author robert@robertroland.org
 * @since 2/24/13
 */
class HBaseDeserializerImplTest {
    def instance = new HBaseDeserializerImpl(TestSchemas.facebookSchema())

    @Test
    void nullResultHandled() {
        assertNull instance.deserialize(null)
    }

    @Test
    void emptyResultHandled() {
        def result = new Result()

        assertNotNull instance.deserialize(result)
    }

    @Test
    void fullResult() {
        def result = sampleResult()

        assertNotNull instance.deserialize(result)
    }

    @Test
    void hasRowKey() {
        def result = sampleResult()

        def resultingMap = instance.deserialize(result)

        assertArrayEquals(Bytes.toBytes("12345_6789"),
                resultingMap.get("__rowkey").getRowKey())
    }

    Result sampleResult() {
        def rowKey = Bytes.toBytes("12345_6789")
        def postFamily = Bytes.toBytes("post")

        def result = new Result([
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("facebook_id"),
                        Bytes.toBytes("12345_6789")),
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("created_at"),
                        Bytes.toBytes(1353191589980L)),
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("title"),
                        Bytes.toBytes("a post title")),
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("to|count"),
                        Bytes.toBytes(2))
        ])

        return result
    }
}
