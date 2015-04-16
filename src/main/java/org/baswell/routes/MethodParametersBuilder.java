package org.baswell.routes;

import org.baswell.routes.MethodParameter.RouteMethodParameterType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.baswell.routes.RoutesMethods.*;

class MethodParametersBuilder
{
  List<MethodParameter> buildParameters(Method method)
  {
    return buildParameters(method, null);
  }
  
  List<MethodParameter> buildParameters(Method method, Criteria routeCriteria) throws RoutesException
  {
    List<MethodParameter> routeParameters = new ArrayList<MethodParameter>();
    Type[] parameters = method.getGenericParameterTypes();

    int routeIndex = 0;
    int groupIndex = 0;
    CriterionForPathSegment currentPathSegmentCriteron = null;
    CriterionForParameter currentParameterCriteron = null;

    boolean pathSegmentsProcessed = false;
    
    PARAMETERS_LOOP: for (int i = 0; i < parameters.length; i++)
    {
      Type parameter = parameters[i];
      Class parameterClass = typeToClass(parameter);
      if (parameterClass == HttpServletRequest.class)
      {
        routeParameters.add(new MethodParameter(RouteMethodParameterType.SERVLET_REQUEST));
      }
      else if (parameterClass == HttpServletResponse.class)
      {
        routeParameters.add(new MethodParameter(RouteMethodParameterType.SERVLET_RESPONSE));
      }
      else if (parameterClass == HttpSession.class)
      {
        routeParameters.add(new MethodParameter(RouteMethodParameterType.SESSION));
      }
      else if (parameterClass == RequestContext.class)
      {
        routeParameters.add(new MethodParameter(RouteMethodParameterType.REQUEST_CONTEXT));
      }
      else if (parameterClass == RequestPath.class)
      {
        routeParameters.add(new MethodParameter(RouteMethodParameterType.REQUEST_PATH));
      }
      else if (parameterClass == RequestParameters.class)
      {
        routeParameters.add(new MethodParameter(RouteMethodParameterType.REQUEST_PARAMETERS));
      }
      else if (parameterClass == RequestFormat.class)
      {
        routeParameters.add(new MethodParameter(RouteMethodParameterType.FORMAT));
      }
      else if (parameterClass == Map.class)
      {
        RouteMethodParameterType mapType = getRouteMapParameterType(parameter);
        if (mapType != null)
        {
          routeParameters.add(new MethodParameter(mapType));
        }
        else
        {
          throw new RoutesException("Unsupported Map parameter: " + parameter+ " at index: " + i + " in method: " + method);
        }
      }
      else if (routeCriteria != null)
      {
        MethodParameterType methodParameterType = getRouteMethodParameterType(parameter);

        if (methodParameterType != null)
        {
          if (!pathSegmentsProcessed)
          {
            if ((currentPathSegmentCriteron == null) || (groupIndex >= currentPathSegmentCriteron.numberPatternGroups))
            {
              currentPathSegmentCriteron = null;
              groupIndex = 0;

              while (routeIndex < routeCriteria.pathCriteria.size())
              {
                CriterionForPathSegment pathSegmentCriterion = routeCriteria.pathCriteria.get(routeIndex++);
                if (pathSegmentCriterion.type == CriterionForPathSegment.RequestPathSegmentCrierionType.PATTERN)
                {
                  currentPathSegmentCriteron = pathSegmentCriterion;
                  break;
                }
              }
            }

            if (currentPathSegmentCriteron != null)
            {
              if (parameterClass != List.class)
              {
                routeParameters.add(new MethodParameter(RouteMethodParameterType.ROUTE_PATH, currentPathSegmentCriteron.index, currentPathSegmentCriteron.numberPatternGroups > 0 ? groupIndex++ : null, methodParameterType));
                continue PARAMETERS_LOOP;
              }
              else
              {
                throw new RoutesException("List method parameter: " + parameter+ " at index: " + i + " in method: " + method + " is mapped to path segment (" + currentPathSegmentCriteron.index + "). Path segments cannot be mapped to lists only parameters.");
              }
            }

            pathSegmentsProcessed = true;
            routeIndex = groupIndex =0;
          }

          if ((currentParameterCriteron == null) || groupIndex >= currentParameterCriteron.numberPatternGroups)
          {
            currentParameterCriteron = null;
            groupIndex = 0;

            while (routeIndex < routeCriteria.parameterCriteria.size())
            {
              CriterionForParameter parameterCriterion = routeCriteria.parameterCriteria.get(routeIndex++);
              if (parameterCriterion.type == CriterionForParameter.RequestParameterType.PATTERN)
              {
                currentParameterCriteron = parameterCriterion;
                break;
              }
            }
          }

          if (currentParameterCriteron != null)
          {
            if (currentParameterCriteron.presenceRequired || !parameterClass.isPrimitive())
            {
              RouteMethodParameterType routeMethodParameterType = parameterClass == List.class ? RouteMethodParameterType.ROUTE_PARAMETERS : RouteMethodParameterType.ROUTE_PARAMETER;
              routeParameters.add(new MethodParameter(routeMethodParameterType, currentParameterCriteron.name, groupIndex++, methodParameterType));
              continue PARAMETERS_LOOP;
            }
            else
            {
              throw new RoutesException("Primitive method parameter: " + parameter + " at index " + i + " cannot be mapped to optional parameters in method: " + method);
            }
          }

          throw new RoutesException("Unmapped method parameter: " + parameter + " at index: " + i + " in method: " + method);
        }
        else
        {
          throw new RoutesException("Unsupported method parameter: " + parameter+ " at index: " + i + " in method: " + method);
        }
      }
      else
      {
        throw new RoutesException("Unsupported method parameter: " + parameter+ " at index: " + i + " in method: " + method);
      }
    }
    
    return routeParameters;
  }

  static RouteMethodParameterType getRouteMapParameterType(Type mapParameter)
  {
    if (mapParameter instanceof ParameterizedType)
    {
      Type[] types = ((ParameterizedType)mapParameter).getActualTypeArguments();
      if ((types.length == 2) && (types[0] == String.class))
      {
        if ((types[1] instanceof ParameterizedType))
        {
          ParameterizedType pt = (ParameterizedType)types[1];
          if (pt.getRawType() == List.class)
          {
            Type[] listTypes = pt.getActualTypeArguments();
            return ((listTypes.length == 1) && (listTypes[0] == String.class)) ? RouteMethodParameterType.PARAMETER_LIST_MAP : null;
          }
          else
          {
            return null;
          }
        }
        else if (types[1] == String.class)
        {
          return RouteMethodParameterType.PARAMETER_MAP;
        }
      }
      else
      {
        return null;
      }
    }
    if (mapParameter instanceof Class)
    {
      return RouteMethodParameterType.PARAMETER_LIST_MAP;
    }
    else
    {
      return null;
    }
  }
  
  static MethodParameterType getRouteMethodParameterType(Type parameter)
  {
    Class parameterClass = null;;
    if (parameter instanceof Class)
    {
      parameterClass = (Class)parameter;
    }
    else if (parameter instanceof ParameterizedType)
    {
      ParameterizedType pt = (ParameterizedType)parameter;
      if (pt.getRawType() == List.class)
      {
        Type[] types = ((ParameterizedType)parameter).getActualTypeArguments();
        if ((types.length == 1) && (types[0] instanceof Class))
        {
          parameterClass = (Class)types[0];
        }
      }
    }
    
    if (parameterClass != null)
    {
      if (parameterClass == String.class)
      {
        return MethodParameterType.STRING;
      }
      else if ((parameterClass == Character.class) || (parameterClass == char.class))
      {
        return MethodParameterType.CHARCTER;
      }
      else if ((parameterClass == Boolean.class) || (parameterClass == boolean.class))
      {
        return MethodParameterType.BOOLEAN;
      }
      else if ((parameterClass == Byte.class) || (parameterClass == byte.class))
      {
        return MethodParameterType.BYTE;
      }
      else if ((parameterClass == Short.class) || (parameterClass == short.class))
      {
        return MethodParameterType.SHORT;
      }
      else if ((parameterClass == Integer.class) || (parameterClass == int.class))
      {
        return MethodParameterType.INTEGER;
      }
      else if ((parameterClass == Long.class) || (parameterClass == long.class))
      {
        return MethodParameterType.LONG;
      }
      else if ((parameterClass == Float.class) || (parameterClass == float.class))
      {
        return MethodParameterType.FLOAT;
      }
      else if ((parameterClass == Double.class) || (parameterClass == double.class))
      {
        return MethodParameterType.DOUBLE;
      }
    }

    return null;
  }
}