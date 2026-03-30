package fr.revoicechat.risk;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.IntStream;

import fr.revoicechat.risk.technicaldata.RiskEntity;
import jakarta.interceptor.InvocationContext;

public interface RisksEntityRetriever {
  default RiskEntity get(InvocationContext context) {
    Method method = context.getMethod();
    Object[] args = context.getParameters();
    Parameter[] params = method.getParameters();
    var parameters = IntStream.range(0, params.length).mapToObj(i -> new DataParameter(params[i], args[i])).toList();
    return get(method, parameters);
  }

  RiskEntity get(Method method, List<DataParameter> parameters);

  record DataParameter(Parameter param, Object arg) {}
}