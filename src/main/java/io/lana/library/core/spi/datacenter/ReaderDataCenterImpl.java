package io.lana.library.core.spi.datacenter;

import io.lana.library.core.spi.datacenter.base.AbstractRepositoryDataCenter;
import io.lana.library.core.model.Reader;
import io.lana.library.core.spi.repo.ReaderRepo;
import org.springframework.stereotype.Component;

@Component
public class ReaderDataCenterImpl extends AbstractRepositoryDataCenter<Integer, Reader> implements ReaderDataCenter {
    public ReaderDataCenterImpl(ReaderRepo repo) {
        super(repo);
    }
}
