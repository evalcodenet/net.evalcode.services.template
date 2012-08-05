package net.evalcode.services.template.internal;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * BlockingQueueProcessor
 *
 * <p> Shows how to interruptibly poll for work in a idleing manner
 * (without consuming cpu resources) by using a BlockingQueue
 * implementation.
 *
 * @author carsten.schipke@gmail.com
 *
 * @see QueueProcessor For detailed/manual implementation.
 */
public class BlockingQueueProcessor<T> implements Callable<Long>
{
  // PREDEFINED PROPERTIES
  private static final Logger LOG=LoggerFactory.getLogger(BlockingQueueProcessor.class);


  // MEMBERS
  private final AtomicLong processed=new AtomicLong();

  @Inject
  private BlockingQueue<T> queue;
  @Inject
  private EntityManager entityManager;


  // ACCESSORS/MUTATORS
  public long getCountProcessed()
  {
    return processed.get();
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public Long call()
  {
    while(!Thread.interrupted())
    {
      T element;

      try
      {
        element=queue.take();
      }
      catch(InterruptedException e)
      {
        Thread.currentThread().interrupt();

        break;
      }

      entityManager.getTransaction().begin();

      try
      {
        entityManager.persist(element);
        entityManager.getTransaction().commit();
      }
      catch(final RuntimeException e)
      {
        LOG.error(e.getMessage(), e);
      }
      finally
      {
        if(entityManager.getTransaction().isActive())
          entityManager.getTransaction().rollback();
      }

      processed.incrementAndGet();
    }

    return Long.valueOf(processed.get());
  }
}
