package fiddle.repository.impl;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.FallbackMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Optional;
import com.google.common.io.Files;

import fiddle.api.TemplateId;
import fiddle.repository.Repository;

public class TemplateRepository implements
		Repository<Mustache, String, TemplateId> {

	private final File repo;

	private final MustacheFactory mf;

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	public TemplateRepository(final File repo) {
		this.repo = repo;
		this.mf = new DefaultMustacheFactory(repo);
	}

	public TemplateRepository(final File repo, final File mustacheFallback) {
		this.repo = repo;

		if (mustacheFallback.exists() && mustacheFallback.isDirectory()) {
			this.mf = new FallbackMustacheFactory(repo, mustacheFallback);
		} else {
			this.mf = new DefaultMustacheFactory(repo);
		}
	}

	@Override
	public Optional<Mustache> open(TemplateId id) {
		try {
			return Optional.of(mf.compile(id.id + ".mustache"));
		} catch (Exception e) {
			LOG.warn("failed to compile mustache : {}", e.getMessage());
			return Optional.absent();
		}
	}

	@Override
	public void write(TemplateId id, String rsc) throws IOException {
		final File outFile = new File(repo, id + ".mustache");

		try {
			Files.write(rsc.getBytes(), outFile);
		} catch (IOException e) {
			throw new IOException("file " + repo.getParentFile().getName()
					+ "/" + outFile.getName() + " is not writeable", e);
		}
	}
}
