package com.twb.repository;

import com.twb.entity.FileAsset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileAssetRepository extends JpaRepository<FileAsset, Long> {
}
