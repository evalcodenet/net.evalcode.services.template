package net.evalcode.services.template.entity;


import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import net.evalcode.services.manager.persistence.SchemaUtil;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


/**
 * Foo
 *
 * <p> Examplary net.evalcode.services JPA entity.
 *
 * @author carsten.schipke@gmail.com
 */
@Entity(name=Foo.NAME)
@Cache(region="net.evalcode.services.cache.entity",
  usage=CacheConcurrencyStrategy.TRANSACTIONAL
)
@NamedQueries({
  @NamedQuery(name=Foo.FIND_ALL,
    query="SELECT e FROM template_foo e"
  )
})
@XmlRootElement(name=Foo.NAME)
@XmlAccessorType(XmlAccessType.FIELD)
public class Foo implements Serializable
{
  // PREDEFINED PROPERTIES
  public static final String NAME="template_foo";
  public static final String FIND_ALL="findAllTemplateFoos";

  private static final long serialVersionUID=1L;


  // PROPERTIES
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  public long id;

  @NotNull
  @Column(nullable=false, length=SchemaUtil.DEFAULT_LENGTH_VARCHAR)
  public String bar;


  // OVERRIDES/IMPLEMENTS
  @Override
  public String toString()
  {
    return String.format("%1$s{id: %2$s, name: %3$s}",
      getClass().getName(), String.valueOf(id), bar
    );
  }
}
