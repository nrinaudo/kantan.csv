package kantan.csv.engine.jackson;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class JacksonCsv {
    private static final CsvMapper MAPPER;

    static {
        MAPPER  = new CsvMapper();
        MAPPER.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        MAPPER.enable(CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING);
    }

    public static MappingIterator<String[]> parse(Reader reader, char separator) throws IOException {
        return MAPPER.readerFor(String[].class)
                .with(MAPPER.schemaFor(String[].class).withColumnSeparator(separator))
                .readValues(reader);
    }

    public static SequenceWriter write(Writer writer, char separator) throws IOException {
        return MAPPER.writer()
                .with(MAPPER.schemaFor(String[].class).withColumnSeparator(separator).withLineSeparator("\r\n").withoutComments())
                .writeValues(writer);
    }
}