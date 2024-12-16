package com.datastax.oss.driver.internal.core.type;

import com.datastax.oss.driver.api.core.ProtocolVersion;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.codec.ExtraTypeCodecs;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.codec.registry.MutableCodecRegistry;
import com.datastax.oss.driver.internal.core.type.codec.extras.vector.SubtypeOnlyFloatVectorToArrayCodec;
import com.datastax.oss.driver.internal.core.type.codec.registry.DefaultCodecRegistry;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test of the full suite of "subtype only" functionality.  Goal here is to confirm two
 * distinct questions:
 *
 * * If we use the "subtype only" type with a {@link DefaultCodecRegistry}
 *   do we get the same codec regardless of vector dimension?
 * * Can we use the codec we get back from the default codec registry to encode and decode vectors of different sizes?
 */
public class SubtypeOnlyTest {

    @Test
    public void should_find_subtype_only_codec_regardless_of_size() {

        MutableCodecRegistry registry = new DefaultCodecRegistry("subtype_only");
        registry.register(ExtraTypeCodecs.subtypeOnlyFloatVectorToArray());

        AtomicReference<TypeCodec<float[]>> codecRef = new AtomicReference<TypeCodec<float[]>>();
        for (int i = 1; i <= 2000; ++i) {

            TypeCodec<float[]> codec = registry.codecFor(DataTypes.vectorOf(DataTypes.FLOAT, i));
            codecRef.compareAndSet(null, codec);
            assertThat(codec).isInstanceOf(SubtypeOnlyFloatVectorToArrayCodec.class);
            assertThat(codec).isEqualTo(codecRef.get());
        }
    }

    @Test
    public void should_encode_and_decode_vectors_of_arbitrary_size() {

        MutableCodecRegistry registry = new DefaultCodecRegistry("subtype_only");
        registry.register(ExtraTypeCodecs.subtypeOnlyFloatVectorToArray());

        for (int i = 1; i <= 2000; ++i) {

            TypeCodec<float[]> codec = registry.codecFor(DataTypes.vectorOf(DataTypes.FLOAT, i));
            float[] comparison = randomFloatArray(i);
            float[] result = codec.decode(codec.encode(comparison, ProtocolVersion.V4), ProtocolVersion.V4);
            assertThat(result).isEqualTo(comparison);
        }
    }

    private float[] randomFloatArray(int size) {

        Random random = new Random();
        float[] rv = new float[size];
        for (int i = 0; i < size; ++i) {
            rv[0] = random.nextFloat();
         }
        return rv;
    }
}
