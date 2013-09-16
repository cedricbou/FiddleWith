package domain.java.serializer;

public interface JavaSerializer<R, P> {

	public R serialize(final P entity, final JavaSerializerProvider prov);
}
