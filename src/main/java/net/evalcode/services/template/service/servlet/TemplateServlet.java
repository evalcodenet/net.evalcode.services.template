package net.evalcode.services.template.service.servlet;


import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.evalcode.services.manager.component.ComponentBundleInterface;
import net.evalcode.services.manager.component.ServiceComponentInterface;
import net.evalcode.services.template.TemplateComponent;
import org.osgi.framework.BundleContext;


/**
 * TemplateServlet
 *
 * <p> Will change with Servlet API 3 implementation to an @WebServlet annotated POJO.
 *
 * @author carsten.schipke@gmail.com
 */
@Singleton
public class TemplateServlet extends HttpServlet
{
  // PREDFINED PROPERTIES
  private static final long serialVersionUID=1L;


  // MEMBERS
  @Inject
  @Named("net.evalcode.services.template.foo")
  private String propertyFoo;
  @Inject
  @Named("net.evalcode.services.template.bar")
  private String propertyBar;
  @Inject
  private transient TemplateComponent templateComponent;
  @Inject
  @Named("net.evalcode.services.template.TemplateComponent")
  private transient ServiceComponentInterface templateComponentInterface;
  @Inject
  private transient ComponentBundleInterface componentBundleInterface;
  @Inject
  private transient BundleContext bundleContext;


  // OVERRIDES/IMPLEMENTS
  @Override
  protected void doGet(final HttpServletRequest httpServletRequest,
    final HttpServletResponse httpServletResponse) throws IOException
  {
    process(httpServletRequest, httpServletResponse);
  }

  @Override
  protected void doPost(final HttpServletRequest httpServletRequest,
    final HttpServletResponse httpServletResponse) throws IOException
  {
    process(httpServletRequest, httpServletResponse);
  }


  // IMPLEMENTATION
  protected void process(
    @SuppressWarnings("unused") final HttpServletRequest httpServletRequest,
    final HttpServletResponse httpServletResponse) throws IOException
  {
    httpServletResponse.getOutputStream().println(
      templateComponentInterface.getInjector()
        .getInstance(templateComponentInterface.getType())
          .toString()
    );

    httpServletResponse.getOutputStream().println(templateComponent.toString());
    httpServletResponse.getOutputStream().println(toString());

    httpServletResponse.getOutputStream().println(
      componentBundleInterface.getBundle().getLocation()
    );
    httpServletResponse.getOutputStream().println(
      bundleContext.getBundle().getSymbolicName()
    );
    httpServletResponse.getOutputStream().println(
      "net.evalcode.services.template.foo: "+propertyFoo
    );
    httpServletResponse.getOutputStream().println(
      "net.evalcode.services.template.bar: "+propertyBar
    );
  }
}
