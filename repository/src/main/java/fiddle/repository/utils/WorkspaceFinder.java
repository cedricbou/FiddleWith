package fiddle.repository.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import fiddle.api.WorkspaceId;

public class WorkspaceFinder {

	private final File repo;

	public WorkspaceFinder(final File repo) {
		this.repo = repo;
	}

	public List<WorkspaceId> workspaces() {
		final List<WorkspaceId> workspaces = new LinkedList<WorkspaceId>();

		repo.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					final WorkspaceId id = new WorkspaceId(pathname.getName());
					if (!WorkspaceId.COMMON.equals(id)) {
						workspaces.add(id);
					}
				}
				return false;
			}
		});
		
		return ImmutableList.copyOf(workspaces);
	}
}
