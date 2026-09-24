package cn.master.nuts.handler.file;

import cn.master.nuts.constants.StorageType;
import cn.master.nuts.util.CommonBeanFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/9/22, 星期二
 **/
public class FileCenter {
    private FileCenter() {
    }

    public static FileRepository getRepository(StorageType storageType) {
        return switch (storageType) {
            case MINIO -> CommonBeanFactory.getBean(MinioRepository.class);
            case LOCAL -> CommonBeanFactory.getBean(LocalFileRepository.class);
        };
    }

    public static FileRepository getRepository(String storage) {
        Map<String, StorageType> storageTypeMap = new HashMap<>() {{
            put(StorageType.MINIO.name(), StorageType.MINIO);
            put(StorageType.LOCAL.name(), StorageType.LOCAL);
        }};

        return getRepository(storageTypeMap.get(storage.toUpperCase()));
    }

    public static FileRepository getDefaultRepository() {
        return CommonBeanFactory.getBean(MinioRepository.class);
    }
}
