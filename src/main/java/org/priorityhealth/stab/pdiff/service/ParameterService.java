package org.priorityhealth.stab.pdiff.service;

import java.util.HashMap;
import java.util.List;

public class ParameterService {

    private static HashMap<String, String> parameters;

    public static HashMap<String, String> getParameters() {
        return parameters;
    }

    public static void setParameters(HashMap<String, String> parameters) {
        ParameterService.parameters = parameters;
    }

    public static void setParameters(List<String> parameters) {
        HashMap<String, String> params = new HashMap<String, String>();
        for (String param : parameters) {
            String[] parts = param.replace("-", "").split("=");
            if (parts.length > 0) {
                params.put(parts[0], parts.length > 1 ? parts[1] : null);
            }
        }
        ParameterService.parameters = params;
    }

    public static String getParameter(String key) {
        return parameters.containsKey(key) ? parameters.get(key) : null;
    }

    public static String getParameter(String[] keys) {
        for (String key : keys) {
            if (parameters.containsKey(key)) {
                return parameters.get(key);
            }
        }
        return null;
    }
}
