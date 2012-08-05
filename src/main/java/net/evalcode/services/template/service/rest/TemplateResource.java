package net.evalcode.services.template.service.rest;


import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import net.evalcode.javax.xml.bind.XmlList;
import net.evalcode.javax.xml.bind.XmlSet;
import net.evalcode.services.http.annotation.Transactional;
import net.evalcode.services.http.exception.NotFoundException;
import net.evalcode.services.manager.persistence.GenericDAO;
import net.evalcode.services.template.entity.Foo;
import net.evalcode.services.template.internal.QueueProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TemplateResource
 *
 * @author carsten.schipke@gmail.com
 */
@Path(/*template/rest*/"resource")
@Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
public class TemplateResource
{
  // PREDEFINED PROPERTIES
  private static final Logger LOG=LoggerFactory.getLogger(TemplateResource.class);


  // MEMBERS
  @Inject
  private EntityManager entityManager;
  @Inject
  private Provider<HttpServletRequest> httpServletRequestProvider;
  @Inject
  private QueueProcessor<Foo> queueProcessor;
  @Inject
  private BlockingQueue<Foo> queueBlocking;


  // ACCESSORS/MUTATORS
  @GET
  @Path(/*template/rest/resource*/"poke")
  public Set<Foo> poke()
  {
    final Set<Foo> result=new XmlSet<>();

    final Foo foo=new Foo();
    foo.bar="queue: "+httpServletRequestProvider.get().getSession().getId();

    queueProcessor.enqueue(foo);
    LOG.debug("Queued {}.", foo);

    final Foo bar=new Foo();
    bar.bar="blocking-queue: "+httpServletRequestProvider.get().getSession().getId();

    queueBlocking.offer(bar);
    LOG.debug("Queued {}.", bar);

    result.add(foo);
    result.add(bar);

    return result;
  }

  @GET
  @Path(/*template/rest/resource*/"list")
  public List<Foo> list()
  {
    final List<Foo> list=new XmlList<Foo>();

    list.addAll(entityManager.createNamedQuery(Foo.FIND_ALL, Foo.class).getResultList());

    return list;
  }

  @GET
  @Path(/*template/rest/resource*/"get/{primaryKey}")
  @RolesAllowed({"admin"})
  public Foo get(@PathParam("primaryKey") final Long primaryKey)
  {
    LOG.debug("{} [sessionId: {}].", httpServletRequestProvider.get().getRequestURI(),
      httpServletRequestProvider.get().getSession().getId()
    );

    final Foo foo=GenericDAO.get(entityManager, Foo.class).findByPK(primaryKey);

    if(null==foo)
      throw new NotFoundException("Not Found.");

    Response.status(Status.OK).build();

    return foo;
  }

  @GET
  @Path(/*template/rest/resource*/"add/{name}")
  @Transactional
  @RolesAllowed({"admin"})
  public Foo add(@PathParam("name") final String name)
  {
    LOG.debug("{} [sessionId: {}].", httpServletRequestProvider.get().getRequestURI(),
      httpServletRequestProvider.get().getSession().getId()
    );

    final Foo fu=new Foo();
    fu.bar=name;

    GenericDAO.get(entityManager, Foo.class).save(fu);

    Response.status(Status.OK).build();

    return fu;
  }
}
