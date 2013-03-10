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

package org.robertroland.cadence.schema

import org.robertroland.cadence.model.Field
import org.robertroland.cadence.model.Schema

/**
 *
 * @author robert@robertroland.org
 * @since 2/24/13
 */
class TestSchemas {
    static Schema fullSchema() {
        def schema = new Schema()

        schema.table = "post"
        schema.delimiter = "|"

        def field = new Field()
        field.columnName = "post_id"
        field.typeName = "String"
        field.formatter = "SHA1"
        schema.compositeKey.add field

        field = new Field()
        field.columnFamily = "post"
        field.columnName = "post_id"
        field.typeName = "String"
        schema.columns.add field

        field = new Field()
        field.columnFamily = "post"
        field.columnName = "created_at"
        field.typeName = "Date"
        schema.columns.add field

        field = new Field()
        field.columnFamily = "post"
        field.columnName = "title"
        field.typeName = "String"
        schema.columns.add field

        field = new Field()
        field.columnFamily = "post"
        field.columnName = "testList"
        field.typeName = "List"
        field.listElementType = "String"
        schema.columns.add field

        field = new Field()
        field.columnFamily = "post"
        field.columnName = "to"
        field.typeName = "List"
        field.listElementType = "Map"

        def toField = new Field()
        toField.columnFamily = "post"
        toField.columnName = "name"
        toField.typeName = "String"
        toField.parent = field
        field.addMapField toField

        toField = new Field()
        toField.columnFamily = "post"
        toField.columnName = "id"
        toField.typeName = "String"
        toField.parent = field
        field.addMapField toField

        toField = new Field()
        toField.columnFamily = "post"
        toField.columnName = "link"
        toField.typeName = "String"
        toField.parent = field
        field.addMapField toField

        schema.columns.add field

        def metadata = new Field()
        metadata.columnFamily = "metadata"
        metadata.columnName = "metadata"
        metadata.typeName = "Map"

        def mapField = new Field()
        mapField.columnFamily = "metadata"
        mapField.columnName = "customer"
        mapField.typeName = "Map"
        mapField.parent = metadata
        metadata.addMapField mapField

        def innerMapField = new Field()
        innerMapField.columnFamily = "metadata"
        innerMapField.columnName = "name"
        innerMapField.typeName = "String"
        innerMapField.parent = mapField
        mapField.addMapField innerMapField

        field = new Field()
        field.columnName = "trackingIds"
        field.columnFamily = "metadata"
        field.typeName = "List"
        field.listElementType = "String"
        field.parent = metadata
        metadata.addMapField field

        schema.columns.add metadata

        return schema
    }
}
