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

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.robertroland.cadence.model.Field;
import org.robertroland.cadence.model.RowKey;
import org.robertroland.cadence.model.Schema;
import org.robertroland.cadence.types.Serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * A Deserializer instance that uses the HBase Client API
 *
 * @author robert@robertroland.org
 * @since 2/24/13
 */
public class HBaseDeserializerImpl implements Deserializer<Result> {
    private Schema schema;

    public HBaseDeserializerImpl(Schema schema) {
        this.schema = schema;
    }

    @Override
    public Map<Object, Object> deserialize(Result databaseResult) {
        if(databaseResult == null) {
            return null;
        }

        Map<Object, Object> result = new HashMap<Object, Object>();

        result.put("__rowkey", new RowKey(databaseResult.getRow()));

        for(Field field: schema.getColumns()) {
            Serializer serializer = TypeSerializerFactory.getSerializer(field.getTypeName());

            KeyValue kv = databaseResult.getColumnLatest(Bytes.toBytes(field.getColumnFamily()),
                    Bytes.toBytes(field.getColumnName()));

            Object value = null;
            if(kv != null) {
                value = serializer.deserialize(kv.getValue());
            }

            result.put(field.getColumnName(), value);
        }

        return result;
    }
}
