package gr.cite.notification.web.controllers;

import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.MyPrincipal;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import gr.cite.notification.service.AuthnSandbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProtectedScratchpadController {

	private final AuthnSandbox authnSandbox;
	private final CurrentPrincipalResolver currentPrincipalResolver;
	private final ClaimExtractor claimExtractor;

	@Autowired
	public ProtectedScratchpadController(
			AuthnSandbox authnSandbox,
			CurrentPrincipalResolver currentPrincipalResolver,
			ClaimExtractor claimExtractor) {
		this.authnSandbox = authnSandbox;
		this.currentPrincipalResolver = currentPrincipalResolver;
		this.claimExtractor = claimExtractor;
	}

	@GetMapping("hi")
	public String sayHi() {
		//System.out.println(principalManager.getPrincipal().getName());
		MyPrincipal principal = this.currentPrincipalResolver.currentPrincipal();
		System.out.println(this.claimExtractor.name(principal));
		System.out.println(this.claimExtractor.client(principal));
		return authnSandbox.sayHi();
	}
}
