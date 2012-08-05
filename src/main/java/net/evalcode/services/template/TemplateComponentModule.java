package net.evalcode.services.template;


import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.inject.Singleton;
import net.evalcode.services.manager.component.ServiceComponentModule;
import net.evalcode.services.template.entity.Foo;
import net.evalcode.services.template.internal.BlockingQueueProcessor;
import net.evalcode.services.template.internal.QueueProcessor;
import com.google.inject.TypeLiteral;


/**
 * TemplateComponentModule
 *
 * @author carsten.schipke@gmail.com
 */
public class TemplateComponentModule extends ServiceComponentModule
{
  // PREDEFINED PROPERTIES
  static final int DEFAULT_CAPACITY_BLOCKING_QUEUE=2048;


  // IMPLEMENTATION
  @Override
  protected void configure()
  {
    super.configure();

    bind(TemplateComponent.class);
    bind(TemplateComponentServletModule.class);

    bind(new TypeLiteral<Queue<Foo>>() {})
      .toInstance(new ConcurrentLinkedQueue<Foo>());
    bind(new TypeLiteral<BlockingQueue<Foo>>() {})
      .toInstance(new ArrayBlockingQueue<Foo>(DEFAULT_CAPACITY_BLOCKING_QUEUE));

    bind(new TypeLiteral<QueueProcessor<Foo>>() {})
      .in(Singleton.class);
    bind(new TypeLiteral<BlockingQueueProcessor<Foo>>() {})
      .in(Singleton.class);
  }
}
