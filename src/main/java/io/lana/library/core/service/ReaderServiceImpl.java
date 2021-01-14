package io.lana.library.core.service;

import io.lana.library.core.model.Reader;
import io.lana.library.core.spi.FileStorage;
import io.lana.library.core.spi.datacenter.ReaderDataCenter;
import io.lana.library.utils.WorkerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReaderServiceImpl implements ReaderService {
    private ReaderDataCenter readerDataCenter;

    private FileStorage fileStorage;

    @Autowired
    public void setup(ReaderDataCenter readerDataCenter, FileStorage fileStorage) {
        this.fileStorage = fileStorage;
        this.readerDataCenter = readerDataCenter;
    }

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

    @Override
    public void createReader(Reader reader) {
        if (StringUtils.isNotBlank(reader.getEmail()) && existsByEmail(reader.getEmail())) {
            throw new ServiceException("Email already exited");
        }
        if (existsByPhoneNumber(reader.getPhoneNumber())) {
            throw new ServiceException("Phone number already exited");
        }
        if (StringUtils.isNotBlank(reader.getAvatar())) {
            String savedImage = fileStorage.loadFileToStorage(reader.getAvatar());
            reader.setAvatar(savedImage);
        }
        readerDataCenter.save(reader);
    }

    @Override
    public void updateReader(Reader reader) {
        Reader updated = readerDataCenter.findById(reader.getId());
        if (StringUtils.isNotBlank(reader.getEmail()) &&
            !reader.getEmail().equals(updated.getEmail())
            && existsByEmail(reader.getEmail())) {
            throw new ServiceException("Email already exited");
        }
        if (!reader.getPhoneNumber().equals(updated.getPhoneNumber())
            && existsByPhoneNumber(reader.getPhoneNumber())) {
            throw new ServiceException("Phone number already exited");
        }
        updated.setBirth(reader.getBirth());
        updated.setName(reader.getName());
        updated.setEmail(reader.getEmail());
        updated.setPhoneNumber(reader.getPhoneNumber());
        updated.setGender(reader.getGender());
        updated.setLimit(reader.getLimit());
        updated.setAddress(reader.getAddress());
        if (StringUtils.isNotBlank(reader.getAvatar())) {
            String oldAvatar = updated.getAvatar();
            updated.setAvatar(fileStorage.loadFileToStorage(reader.getAvatar()));
            WorkerUtils.runAsync(() -> fileStorage.deleteFileFromStorage(oldAvatar));
        }
        readerDataCenter.update(reader);
    }

    @Override
    public void deleteReader(Reader reader) {
        readerDataCenter.delete(reader);
        WorkerUtils.runAsync(() -> fileStorage.deleteFileFromStorage(reader.getAvatar()));
    }

    private boolean existsByPhoneNumber(String phoneNumber) {
        return readerDataCenter.stream().anyMatch(reader -> phoneNumber.equals(reader.getPhoneNumber()));
    }

    private boolean existsByEmail(String email) {
        return readerDataCenter.stream().anyMatch(reader -> email.equals(reader.getEmail()));
    }
}
