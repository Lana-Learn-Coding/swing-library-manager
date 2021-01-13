package io.lana.library.core.service;

import io.lana.library.core.model.Reader;
import io.lana.library.core.spi.datacenter.ReaderDataCenter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReaderServiceImpl implements ReaderService {
    @Autowired
    private ReaderDataCenter readerDataCenter;

    @Override
    public Optional<Reader> findOne(String query) {
        if (StringUtils.isBlank(query)) {
            throw new ServiceException("Query must not blank");
        }
        return readerDataCenter.stream().filter(reader ->
            query.equals(reader.getIdString()) ||
            query.equals(reader.getEmail()) ||
            query.equals(reader.getPhoneNumber())
        ).findFirst();
    }
}
