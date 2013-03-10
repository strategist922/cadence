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
    def instance = new HBaseDeserializerImpl(TestSchemas.fullSchema())

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

        def deserialized = instance.deserialize(result)

        assertNotNull "instance is not null", deserialized
        assertEquals "post_id is correct", "12345_6789", deserialized["post_id"]
        assertEquals "post title is correct", "a post title", deserialized["title"]

        assertEquals "testList has the correct number of items", 3, deserialized["testList"].size()
        assertEquals "first entry in testList is correct", "one", deserialized["testList"][0]
        assertEquals "second entry in testList is correct", "two", deserialized["testList"][1]
        assertEquals "third entry in testList is correct", "three", deserialized["testList"][2]

        assertEquals "to has the correct number of items", 2, deserialized["to"].size()
        assertEquals "to's first item has the right id", "user1", deserialized["to"][0]["id"]
        assertEquals "to's first item has the right name", "User Name 1", deserialized["to"][0]["name"]
        assertEquals "to's first item has the right link", "http://example.org/user1", deserialized["to"][0]["link"]

        assertEquals "there should be two trackingIds", 2, deserialized["metadata"]["trackingIds"].size()

        assertEquals "customer name should match", "customer1", deserialized["metadata"]["customer"]["name"]
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
        def metadataFamily = Bytes.toBytes("metadata")

        KeyValue[] kvs = [
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("post_id"),
                        1234L,
                        Bytes.toBytes("12345_6789")),
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("created_at"),
                        1234L,
                        Bytes.toBytes("2012-12-31T16:45:00-0800")),
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("title"),
                        1234L,
                        Bytes.toBytes("a post title")),
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("to|_count"),
                        1234L,
                        Bytes.toBytes(2L)),
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("to|" + String.format("%019d", 0L) + "|name"),
                        1234L,
                        Bytes.toBytes("User Name 1")),
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("to|" + String.format("%019d", 0L) + "|id"),
                        1234L,
                        Bytes.toBytes("user1")),
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("to|" + String.format("%019d", 0L) + "|link"),
                        1234L,
                        Bytes.toBytes("http://example.org/user1")),
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("to|" + String.format("%019d", 1L) + "|name"),
                        1234L,
                        Bytes.toBytes("User Name 2")),
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("to|" + String.format("%019d", 1L) + "|id"),
                        1234L,
                        Bytes.toBytes("user2")),
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("to|" + String.format("%019d", 1L) + "|link"),
                        1234L,
                        Bytes.toBytes("http://example.org/user2")),
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("testList|_count"),
                        1234L,
                        Bytes.toBytes(3L)),
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("testList|" + String.format("%019d", 0L)),
                        1234L,
                        Bytes.toBytes("one")),
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("testList|" + String.format("%019d", 1L)),
                        1234L,
                        Bytes.toBytes("two")),
                new KeyValue(rowKey, postFamily,
                        Bytes.toBytes("testList|" + String.format("%019d", 2L)),
                        1234L,
                        Bytes.toBytes("three")),
                new KeyValue(rowKey, metadataFamily,
                        Bytes.toBytes("metadata|trackingIds|_count"),
                        1234L,
                        Bytes.toBytes(2L)),
                new KeyValue(rowKey, metadataFamily,
                        Bytes.toBytes("metadata|trackingIds|" + String.format("%019d", 0L)),
                        1234L,
                        Bytes.toBytes("tracking1")),
                new KeyValue(rowKey, metadataFamily,
                        Bytes.toBytes("metadata|trackingIds|" + String.format("%019d", 1L)),
                        1234L,
                        Bytes.toBytes("tracking2")),
                new KeyValue(rowKey, metadataFamily,
                        Bytes.toBytes("metadata|customer|name"),
                        1234L,
                        Bytes.toBytes("customer1")),
        ]

        Arrays.sort(kvs, KeyValue.COMPARATOR)

        def result = new Result(kvs)

        return result
    }
}
