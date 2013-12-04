package fiddle.dbi;

public interface TransactionFunction {

	public Object apply(final DecoratedHandle handle);
}
