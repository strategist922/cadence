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

import org.robertroland.cadence.schema.SchemaValidatorImpl

import static org.junit.Assert.*

import org.junit.Test

/**
 * 
 * @author robert@robertroland.org
 * @since 2/24/13
 */
class SchemaValidatorImplTest {
    def instance = new SchemaValidatorImpl()

    @Test
    void validSchema() {
        def validSchema = [
                table: "facebook_post",
                delimiter: "|",
                key: [parts: [[name: "facebook_id", type: "SHA1"]]],
                columns: [
                        [name: "facebook_id", type: "String", family: "post"],
                        [name: "created_at", type: "DateTime", family: "post"]
                ]
        ]

        org.junit.Assert.assertTrue instance.validateSchema(validSchema)
    }
}
