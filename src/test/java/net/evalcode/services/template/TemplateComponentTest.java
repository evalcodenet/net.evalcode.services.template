package net.evalcode.services.template;


import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test {@link TemplateComponent}
 *
 * @author carsten.schipke@gmail.com
 */
public class TemplateComponentTest
{
  // TESTS
  @Test
  public void testPoke()
  {
    /**
     * TODO Implement test bootstrap for net.evalcode.services.manager
     * to make this test work without mocking.
     */

    System.setProperty("net.evalcode.services.template.foo", "foo");
    System.setProperty("net.evalcode.services.template.bar", "bar");

    final TemplateComponent templateComponent=Mockito.mock(TemplateComponent.class);

    Mockito.when(templateComponent.foo())
      .thenReturn(System.getProperty("net.evalcode.services.template.foo"));

    Mockito.when(templateComponent.bar())
      .thenReturn(System.getProperty("net.evalcode.services.template.bar"));

    assertEquals("foo", templateComponent.foo());
    assertEquals("bar", templateComponent.bar());
  }
}
