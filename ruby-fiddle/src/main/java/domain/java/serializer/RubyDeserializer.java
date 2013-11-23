package domain.java.serializer;

public interface RubyDeserializer<R, P> {

	public R deserialize(final P entity, final RubyDeserializerProvider prov);
}
