package fiddle.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;

import fiddle.api.FiddleId;
import fiddle.api.WorkspaceId;

public class FiddleMetrics {

	private static class FiddlePath {
		public final WorkspaceId wId;
		public final FiddleId fId;

		public FiddlePath(WorkspaceId wId, FiddleId fId) {
			this.wId = wId;
			this.fId = fId;
		}

		@Override
		public String toString() {
			return wId.toString() + "." + fId.toString();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof FiddlePath) {
				final FiddlePath fp = (FiddlePath) obj;

				return fId.equals(fp.fId) && wId.equals(fp.wId);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return fId.hashCode() + wId.hashCode();
		}
	}

	private final Map<FiddlePath, Timer> timers = new HashMap<FiddlePath, Timer>();

	public Timer timer(final WorkspaceId wId, final FiddleId fId) {
		final FiddlePath path = new FiddlePath(wId, fId);

		if (!timers.containsKey(path)) {
			timers.put(path, Metrics.newTimer(FiddleMetrics.class,
					path.toString(), TimeUnit.MILLISECONDS, TimeUnit.SECONDS));
		}
		
		return timers.get(path);
	}

}
