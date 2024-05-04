package com.github.jiizuz.booking.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;

/**
 * {@link TypeAdapter} to codec a {@link Instant}.
 *
 * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
 * @see com.google.gson.TypeAdapter
 * @since 1.0
 */
public class InstantTypeAdapter extends TypeAdapter<Instant> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final JsonWriter out, final Instant value) throws IOException {
        out.value(value.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instant read(final JsonReader in) throws IOException {
        return Instant.parse(in.nextString());
    }
}
