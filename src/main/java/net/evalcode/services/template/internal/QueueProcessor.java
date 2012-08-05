package net.evalcode.services.template.internal;


import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * QueueProcessor
 *
 * <p> Shows how to interruptibly poll for work in a idleing manner
 * (without consuming cpu resources).
 *
 * @author carsten.schipke@gmail.com
 *
 * @see BlockingQueueProcessor For simplified implementation.
 */
public class QueueProcessor<T> implements Callable<Long>
{
  // PREDEFINED PROPERTIES
  private static final Logger LOG=LoggerFactory.getLogger(QueueProcessor.class);


  // MEMBERS
  private final AtomicLong processed=new AtomicLong();
  private final ReentrantLock lock=new ReentrantLock();
  private final Condition notEmpty=lock.newCondition();

  @Inject
  private Queue<T> queue;
  @Inject
  private EntityManager entityManager;


  // ACCESSORS/MUTATORS
  public void enqueue(final T element)
  {
    lock.lock();

    try
    {
      if(queue.isEmpty())
        notEmpty.signalAll();

      queue.offer(element);
    }
    finally
    {
      lock.unlock();
    }
  }

  public long getCountProcessed()
  {
    return processed.get();
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public Long call()
  {
    while(true)
    {
      try
      {
        lock.lockInterruptibly();
      }
      catch(final InterruptedException e)
      {
        Thread.currentThread().interrupt();

        break;
      }

      T element;

      try
      {
        while(queue.isEmpty())
        {
          try
          {
            notEmpty.await();
          }
          catch(final InterruptedException e)
          {
            Thread.currentThread().interrupt();

            break;
          }
        }

        if(Thread.currentThread().isInterrupted())
          return Long.valueOf(processed.get());

        element=queue.poll();
      }
      finally
      {
        lock.unlock();
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
