package me.hubertus248.deployer.reposiotry

import me.hubertus248.deployer.data.entity.FileKey
import me.hubertus248.deployer.data.entity.FilesystemFileMetadata
import org.springframework.data.jpa.repository.JpaRepository

interface FilesystemFileMetadataRepository : JpaRepository<FilesystemFileMetadata, Long> {

    fun findFirstByFileKey(key: FileKey): FilesystemFileMetadata?

    fun deleteAllByFileKey(key: FileKey)
}