package io.lana.library.core.spi.repo;

import io.lana.library.core.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReaderRepo extends JpaRepository<Reader, Integer> {
    List<Reader> findAllByOrderByUpdatedAtDesc();
}
