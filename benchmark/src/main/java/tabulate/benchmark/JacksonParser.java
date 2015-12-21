/*
 * Copyright (c) 2015, ioSquare SAS. All rights reserved.
 * The information contained in this file is confidential and proprietary.
 * Any reproduction, use or disclosure, in whole or in part, of this
 * information without the express, prior written consent of ioSquare SAS
 * is strictly prohibited.
 */

package tabulate.benchmark;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

public class JacksonParser {
    public static Iterator<String[]> parse(Reader reader) throws IOException {
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        return mapper.reader(String[].class).readValues(reader);
    }
}
