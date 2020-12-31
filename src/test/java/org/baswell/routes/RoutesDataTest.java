package org.baswell.routes;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.baswell.routes.RoutesData.*;

public class RoutesDataTest
{
  @Test
  public void testExpandRoutes()
  {
    List<String> routes = expandRoutes(toList(new String[]{"/a", "/b"}, new String[]{"/c", "/d"}));
    assertEquals(4, routes.size());
    assertTrue(routes.contains("/a/c"));
    assertTrue(routes.contains("/a/d"));
    assertTrue(routes.contains("/b/c"));
    assertTrue(routes.contains("/b/c"));

    routes = expandRoutes(toList(new String[]{"a", "b"}, new String[]{"c", "d"}, new String[]{"/e"}));
    assertEquals(4, routes.size());
    assertTrue(routes.contains("a/c/e"));
    assertTrue(routes.contains("a/d/e"));
    assertTrue(routes.contains("b/c/e"));
    assertTrue(routes.contains("b/c/e"));
  }

  @Test
  public void test()
  {
    RoutesData routes = new RoutesData(Three.class);

    List<String> routePaths = Arrays.asList(routes.value());
    assertEquals(8, routePaths.size());
    assertTrue(routePaths.contains("/a/c/e"));
    assertTrue(routePaths.contains("/a/d/e"));
    assertTrue(routePaths.contains("/a/c/f"));
    assertTrue(routePaths.contains("/a/d/f"));
    assertTrue(routePaths.contains("/b/c/e"));
    assertTrue(routePaths.contains("/b/d/e"));
    assertTrue(routePaths.contains("/b/c/f"));
    assertTrue(routePaths.contains("/b/d/f"));

    assertEquals("one/two/three", routes.forwardPath());

    assertEquals("text/xhtml", routes.defaultContentType());

    List<String> mediaTypes = Arrays.asList(routes.acceptTypePatterns());
    assertEquals(2, mediaTypes.size());
    assertTrue(mediaTypes.contains(MIMETypes.CSV));
    assertTrue(mediaTypes.contains(MIMETypes.HTML));

    assertFalse(routes.defaultReturnedStringIsContent()[0]);
    assertTrue(routes.routeUnannotatedPublicMethods()[0]);
  }

  List<String[]> toList(String[]... values)
  {
    List<String[]> list = new ArrayList<String[]>();
    for (String[] value : values)
    {
      list.add(value);
    }
    return list;
  }

  @Routes(value = {"/a", "/b"}, forwardPath = "one", tags = {"one"}, defaultContentType = "text/html", acceptTypePatterns = {MIMETypes.CSV}, defaultReturnedStringIsContent = true, routeUnannotatedPublicMethods = false)
  static class One
  {
  }

  @Routes(value = {"/c", "/d"}, forwardPath = "two", tags = {"two"}, defaultContentType = "text/xhtml", defaultReturnedStringIsContent = false, routeUnannotatedPublicMethods = false)
  static class Two extends One
  {
  }

  @Routes(value = {"/e", "/f"}, forwardPath = "three", tags = {"three"}, acceptTypePatterns = {MIMETypes.HTML}, routeUnannotatedPublicMethods = true)
  static class Three extends Two
  {
  }
}