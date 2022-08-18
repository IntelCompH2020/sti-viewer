package gr.cite.intelcomp.stiviewer.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
		"gr.cite.intelcomp.stiviewer",
		"gr.cite.tools",
		"gr.cite.rabbitmq",
		"gr.cite.queueoutbox",
		"gr.cite.queueinbox",
		"gr.cite.commons"})
@EntityScan({
		"gr.cite.intelcomp.stiviewer.data"})
public class STIViewerApplication {

	public static void main(String[] args) {
		SpringApplication.run(STIViewerApplication.class, args);
	}

}
