package io.lana.library.core.service;

import io.lana.library.core.model.Reader;

import java.util.Optional;

public interface ReaderService {
    Optional<Reader> findOne(String query);
}
