package net.evalcode.services.template;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import net.evalcode.services.http.service.HttpService;
import net.evalcode.services.http.service.HttpServiceServletModule;
import net.evalcode.services.manager.annotation.Activate;
import net.evalcode.services.manager.annotation.Component;
import net.evalcode.services.manager.annotation.Deactivate;
import net.evalcode.services.manager.annotation.Property;
import net.evalcode.services.manager.component.ComponentBundleInterface;
import net.evalcode.services.template.entity.Foo;
import net.evalcode.services.template.internal.BlockingQueueProcessor;
import net.evalcode.services.template.internal.QueueProcessor;
import net.evalcode.services.template.service.TemplateService;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TemplateComponent
 *
 * @author carsten.schipke@gmail.com
 */
@Singleton
@Component(module=TemplateComponentModule.class, properties={
  @Property(name="net.evalcode.services.template.foo", defaultValue="bar"),
  @Property(name="net.evalcode.services.template.bar", defaultValue="foo")
})
public class TemplateComponent implements TemplateService, HttpService
{
  // PREDEFINED PROPERTIES
  private static final Logger LOG=LoggerFactory.getLogger(TemplateComponent.class);


  // MEMBERS
  private final ExecutorService executor=Executors.newCachedThreadPool();

  @Inject
  private QueueProcessor<Foo> queueProcessor;
  @Inject
  private BlockingQueueProcessor<Foo> blockingQueueProcessor;
  @Inject
  @Named("net.evalcode.services.template.foo")
  private String propertyFoo;
  @Inject
  @Named("net.evalcode.services.template.bar")
  private String propertyBar;
  @Inject
  private TemplateComponentServletModule componentServletModule;
  @Inject
  private ComponentBundleInterface componentBundleInterface;
  @Inject
  private BundleContext bundleContext;


  // ACCESSORS/MUTATORS
  @Activate
  public void activate()
  {
    LOG.info("Activating Template component.");

    LOG.info("Name [{}].", bundleContext.getBundle().getSymbolicName());
    LOG.info("Location [{}].", componentBundleInterface.getBundle().getLocation());
    LOG.info("Properties [foo: {}, bar: {}].", propertyFoo, propertyBar);

    executor.submit(queueProcessor);
    executor.submit(blockingQueueProcessor);
  }

  @Deactivate
  public void deactivate()
  {
    LOG.info("Deactivating Template Component.");

    executor.shutdownNow();

    try
    {
      executor.awaitTermination(1L, TimeUnit.SECONDS);
    }
    catch(final InterruptedException e)
    {
      Thread.currentThread().interrupt();
    }

    LOG.info("{} processed {} elements.", QueueProcessor.class.getSimpleName(),
      Long.valueOf(queueProcessor.getCountProcessed())
    );

    LOG.info("{} processed {} elements.", BlockingQueueProcessor.class.getSimpleName(),
      Long.valueOf(blockingQueueProcessor.getCountProcessed())
    );
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public String foo()
  {
    return propertyFoo;
  }

  @Override
  public String bar()
  {
    return propertyBar;
  }

  @Override
  public HttpServiceServletModule getServletModule()
  {
    return componentServletModule;
  }
}
