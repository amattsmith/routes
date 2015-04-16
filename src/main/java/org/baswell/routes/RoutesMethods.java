package org.baswell.routes;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.baswell.routes.RequestFormat;
import org.baswell.routes.RequestContext;
import org.baswell.routes.RequestParameters;
import org.baswell.routes.RequestPath;

class RoutesMethods
{
  static boolean hasEntries(Collection collection)
  {
    return (collection != null) && !collection.isEmpty();
  }
  
  static boolean nullEmpty(CharSequence string)
  {
    return ((string == null) || string.toString().trim().isEmpty());
  }

  static Class typeToClass(Type type)
  {
    if (type instanceof Class)
    {
      return (Class)type;
    }
    else if (type instanceof ParameterizedType)
    {
      return (Class)((ParameterizedType)type).getRawType();
    }
    else
    {
      return null;
    }
  }

  static String repeat(String str, int times)
  {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < times; i++) builder.append(str);
    return builder.toString();
  }
  
  static Class getListSingleParameterType(Type listParameter)
  {
    if (listParameter instanceof ParameterizedType)
    {
      Type[] types = ((ParameterizedType)listParameter).getActualTypeArguments();
      return ((types.length == 1) && (types[0] instanceof Class)) ? (Class)types[0] : null;
    }
    if (listParameter instanceof Class)
    {
      return String.class;
    }
    else
    {
      return null;
    }
  }

  static final String BYTE_PATTERN = "(-?)\\d" + repeat("\\d?", 2);

  static final String SHORT_PATTERN = "(-?)\\d" + repeat("\\d?", 4);

  static final String INTEGER_PATTERN = "(-?)\\d" + repeat("\\d?", 9);

  static final String LONG_PATTERN = "(-?)\\d" + repeat("\\d?", 18);

  static final String FLOAT_PATTERN = "(-?)\\d" + repeat("\\d?", 7) + "(\\.\\d" + repeat("\\d?", 22) + ")?" ;

  static final String DOUBLE_PATTERN = "(-?)\\d" + repeat("\\d?", 14) + "(\\.\\d" + repeat("\\d?", 45) + ")?" ;
  
  static final String BOOLEAN_PATTERN = "(((t|T)(r|R)(u|U)(e|E))|((f|F)(a|A)(l|L)(s|S)(e|E)))";
  
  static final String CHAR_PATTERN = ".";

  static final String STRING_PATTERN = ".*";
  
  static final String WILDCARD_PATTERN = STRING_PATTERN;

  static Map<Class, String> typesToPatterns = new HashMap<Class, String>();
  static
  {
    typesToPatterns.put(Byte.class, BYTE_PATTERN);
    typesToPatterns.put(byte.class, BYTE_PATTERN);
    typesToPatterns.put(Short.class, SHORT_PATTERN);
    typesToPatterns.put(short.class, SHORT_PATTERN);
    typesToPatterns.put(Integer.class, INTEGER_PATTERN);
    typesToPatterns.put(int.class, INTEGER_PATTERN);
    typesToPatterns.put(Long.class, LONG_PATTERN);
    typesToPatterns.put(long.class, LONG_PATTERN);
    typesToPatterns.put(Float.class, FLOAT_PATTERN);
    typesToPatterns.put(float.class, FLOAT_PATTERN);
    typesToPatterns.put(Double.class, DOUBLE_PATTERN);
    typesToPatterns.put(double.class, DOUBLE_PATTERN);
    typesToPatterns.put(Boolean.class, BOOLEAN_PATTERN);
    typesToPatterns.put(boolean.class, BOOLEAN_PATTERN);
    typesToPatterns.put(String.class, STRING_PATTERN);
  }
  
  static Set<Class> routesParameterTypes = new HashSet<Class>();
  static
  {
    routesParameterTypes.add(HttpServletRequest.class);
    routesParameterTypes.add(HttpServletResponse.class);
    routesParameterTypes.add(Map.class);
    routesParameterTypes.add(RequestContext.class);
    routesParameterTypes.add(RequestPath.class);
    routesParameterTypes.add(RequestParameters.class);
    routesParameterTypes.add(RequestFormat.class);
  }

}