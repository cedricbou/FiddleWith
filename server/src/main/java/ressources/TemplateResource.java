package ressources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import domain.FiddleEnvironment;
import domain.TemplateId;
import domain.WorkspaceId;

@Path("/fiddle/with/template")
public class TemplateResource {

		private final FiddleEnvironment env;

		public TemplateResource(FiddleEnvironment env) {
			this.env = env;
		}
		
		@GET
		@Path("/{workspaceId}/{tpl}")
		@Produces(MediaType.TEXT_PLAIN)
		public Response compileTemplate(
				@PathParam("workspaceId") @Valid final WorkspaceId workspaceId,
				@PathParam("tpl") @Valid final TemplateId tplId,
				@Context final UriInfo uri) {
			
			final String json = ResourceJsonUtils.autoJson(uri.getQueryParameters());

			try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				env.templates.render(ResourceJsonUtils.jsonToMap(json), workspaceId, 
						tplId, Locale.getDefault(), baos);
				return Response.ok(baos.toString()).build();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}


}
