package fiddle.config;

import java.util.Map.Entry;

import org.skife.jdbi.v2.DBI;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.yammer.dropwizard.db.DatabaseConfiguration;

import fiddle.api.registry.BaseRegistry;
import fiddle.api.registry.SelectRegistry;
import fiddle.dbi.DecoratedDbi;
import fiddle.dbi.registry.DbiRegistry;
import fiddle.dbi.registry.SelectDbiRegistry;
import fiddle.dbi.registry.SimpleDbiRegistry;

public class Resources {

	public static final Resources EMPTY = new Resources();

	private final SelectRegistry<DbiRegistry> dbis;

	private final FiddleDBIFactory factory;

	private Resources() {
		this.dbis = emptyDbis();
		this.factory = null;
	}

	public Resources(final Optional<Resources> global,
			final FiddleDBIFactory factory, final ResourceConfiguration config) {
		this.factory = factory;

		if (config != null && config.getDatabases() != null) {
			final DbiRegistry local = new SimpleDbiRegistry();
			declareDbis(local, config.getDatabases());

			if (global.isPresent()) {
				this.dbis = BaseRegistry.composed(local, global.get().dbis());
			} else {
				this.dbis = BaseRegistry.single(local);
			}
		} else {
			this.dbis = emptyDbis();
		}

	}

	private SelectRegistry<DbiRegistry> emptyDbis() {
		return new SelectRegistry<DbiRegistry>() {
			@Override
			public DbiRegistry forKey(String key) {
				return new DbiRegistry() {

					@Override
					public DBI get(String key) {
						return null;
					}

					@Override
					public void declare(String key, DBI entry) {
					}

					@Override
					public boolean contains(String key) {
						return false;
					}

					@Override
					public DecoratedDbi getDecoratedDbi(String key) {
						return null;
					}
				};
			}
		};
	}

	public Resources(final FiddleDBIFactory factory,
			final ResourceConfiguration config) {
		this(Optional.<Resources> absent(), factory, config);
	}

	public Resources(final Resources resources, final FiddleDBIFactory factory,
			final ResourceConfiguration config) {
		this(Optional.fromNullable(resources), factory, config);
	}

	private void declareDbis(final DbiRegistry local,
			final ImmutableMap<String, DatabaseConfiguration> databases) {
		for (final Entry<String, DatabaseConfiguration> entry : databases
				.entrySet()) {
			local.declare(entry.getKey(), factory.build(entry.getValue()));
		}
	}

	public DbiRegistry dbis() {
		return new SelectDbiRegistry(dbis);
	}

}
