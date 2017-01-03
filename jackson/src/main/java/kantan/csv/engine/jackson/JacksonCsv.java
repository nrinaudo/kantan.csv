/*
 * Copyright 2017 Nicolas Rinaudo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kantan.csv.engine.jackson;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import java.io.Reader;
import java.io.Writer;

public class JacksonCsv {
    private static final CsvMapper MAPPER;

    static {
        MAPPER  = new CsvMapper();
        MAPPER.enable(com.fasterxml.jackson.dataformat.csv.CsvParser.Feature.WRAP_AS_ARRAY);
        MAPPER.enable(com.fasterxml.jackson.dataformat.csv.CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING);
    }

    public static MappingIterator<String[]> parse(Reader reader, char separator) throws java.io.IOException {
        return MAPPER.readerFor(String[].class)
                .with(MAPPER.schemaFor(String[].class).withColumnSeparator(separator))
                .readValues(reader);
    }

    public static SequenceWriter write(Writer writer, char separator) throws java.io.IOException {
        return MAPPER.writer()
                .with(MAPPER.schemaFor(String[].class).withColumnSeparator(separator).withLineSeparator("\r\n").withoutComments())
                .writeValues(writer);
    }
}