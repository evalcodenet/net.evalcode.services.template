package net.evalcode.services.template;


import javax.inject.Singleton;
import net.evalcode.services.http.service.HttpServiceServletModule;
import net.evalcode.services.template.service.rest.TemplateResource;
import net.evalcode.services.template.service.servlet.TemplateServlet;


/**
 * TemplateComponentServletModule
 *
 * @author carsten.schipke@gmail.com
 */
@Singleton
public class TemplateComponentServletModule extends HttpServiceServletModule
{
  // OVERRIDES/IMPLEMENTS
  @Override
  public String getContextPath()
  {
    return "/template";
  }


  // IMPLEMENTATION
  @Override
  protected void configureServlets()
  {
    super.configureServlets();

    bind(TemplateResource.class);

    serve("/hello").with(TemplateServlet.class);
  }
}
