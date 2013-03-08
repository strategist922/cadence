package org.robertroland.cadence.types;

/**
 * @author robert@robertroland.org
 * @since 3/7/13
 */
public interface Serializer<T> {
    /**
     * Deserialize an item, given a byte array
     *
     * @param bytes item bytes
     * @return deserialized object
     */
    public T deserialize(byte[] bytes);

    /**
     * Serialize an object to a byte array
     *
     * @param object what to serialize
     * @return bytes
     */
    public byte[] serialize(T object);
}
