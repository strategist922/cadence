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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.robertroland.cadence.model.Field;
import org.robertroland.cadence.model.RowKey;
import org.robertroland.cadence.model.Schema;
import org.robertroland.cadence.types.Serializer;

import java.util.HashMap;
import java.util.List;
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
            result.put(field.getColumnName(), handleResult(databaseResult, field, null, null));
        }

        return result;
    }

    protected Object handleResult(Result databaseResult, Field field, String typeOverride,
                                  String columnNamePrefixOverride) {
        final String type = typeOverride == null ? field.getTypeName() : typeOverride;

        if("Map".equals(type)) {
            return deserializeMap(databaseResult, field, columnNamePrefixOverride);
        } else if("List".equals(type)) {
            return deserializeList(databaseResult, field, columnNamePrefixOverride);
        } else {
            return deserializeValue(databaseResult, field, null, columnNamePrefixOverride);
        }
    }

    protected Map<Object, Object> deserializeMap(Result databaseResult, Field field, String columnNamePrefixOverride) {
        Map<Object, Object> result = Maps.newHashMap();

        for(Field mapField: field.mapFields()) {
            final String colName;
            if(columnNamePrefixOverride != null) {
                colName = String.format("%s|%s", columnNamePrefixOverride, mapField.getColumnName());
            } else {
                colName = fullFieldName(mapField, null);
            }

            result.put(mapField.getColumnName(),
                    handleResult(databaseResult, mapField, null, colName));
        }

        return result;
    }

    protected List<Object> deserializeList(Result databaseResult, Field field, String columnNamePrefixOverride) {
        List values = Lists.newArrayList();

        long size = getValue(databaseResult,
                Bytes.toBytes(field.getColumnFamily()),
                Bytes.toBytes(fullFieldName(field, "|_count")), 0L);

        for(long l = 0; l < size; l++) {
            String colName = String.format("%s|%019d",
                    columnNamePrefixOverride == null ? fullFieldName(field, null) : columnNamePrefixOverride,
                    l);

            values.add(handleResult(databaseResult, field, field.getListElementType(), colName));
        }

        return values;
    }

    protected Object deserializeValue(Result databaseResult, Field field, String suffix,
                                      String columnNamePrefixOverride) {
        String colName = columnNamePrefixOverride == null ? fullFieldName(field, suffix) :
                columnNamePrefixOverride;

        return getFieldValue(databaseResult, field, colName);
    }

    private Object getFieldValue(Result databaseResult, Field field, String columnName) {
        final String type;
        if("List".equals(field.getTypeName())) {
            type = field.getListElementType();
        } else {
            type = field.getTypeName();
        }

        Serializer serializer = TypeSerializerFactory.getSerializer(type);

        KeyValue kv = databaseResult.getColumnLatest(Bytes.toBytes(field.getColumnFamily()),
                Bytes.toBytes(columnName));

        Object value = null;
        if(kv != null) {
            value = serializer.deserialize(kv.getValue());
        }
        return value;
    }

    private Long getValue(Result dbResult, byte[] family, byte[] qualifier, Long defaultValue) {
        KeyValue kv = dbResult.getColumnLatest(family, qualifier);

        if(kv == null) {
            return defaultValue;
        } else {
            return Bytes.toLong(kv.getValue());
        }
    }

    private String fullFieldName(Field field, String suffix) {
        StringBuilder sb = new StringBuilder();

        Field parent = field.getParent();
        while(parent != null) {
            sb.insert(0, String.format("%s|", parent.getColumnName()));

            parent = parent.getParent();
        }

        sb.append(field.getColumnName());
        if(suffix != null) {
            sb.append(suffix);
        }

        return sb.toString();
    }
}
