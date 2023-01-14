package com.gifu.coreservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gifu.coreservice.exception.JsonStringToObjectException;
import com.gifu.coreservice.exception.ObjectToJsonStringException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObjectMapperService {

    @Autowired
    private ObjectMapper mapper;

    public String writeToString(Object object) throws ObjectToJsonStringException {
        String result;
        try {
            result = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ObjectToJsonStringException("While processing object: " + object.toString(), e);
        }

        return result;
    }

    public <T> T readToObject(String json, Class<T> clazz) throws JsonStringToObjectException {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new JsonStringToObjectException("While processing json: " + json, e);
        }
    }
}
