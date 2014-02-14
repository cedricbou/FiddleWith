package resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.yammer.dropwizard.auth.Auth;

import fiddle.password.PasswordManager.UserConfiguration;

@Path("/fiddle/with")
public class FiddleEditorResource {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response fiddleIDE(@Auth final UserConfiguration user)
			throws IllegalAccessException {

		user.assertTag("fiddler");

		return Response.ok(new IDEView()).build();
	}

}
