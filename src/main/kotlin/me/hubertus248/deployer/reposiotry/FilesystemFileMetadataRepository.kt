package me.hubertus248.deployer.reposiotry

import me.hubertus248.deployer.data.entity.FileKey
import me.hubertus248.deployer.data.entity.FilesystemFileMetadata
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FilesystemFileMetadataRepository : JpaRepository<FilesystemFileMetadata, Long> {

    @Query("""
        from FilesystemFileMetadata f where f.fileKey=?1
    """)
    fun findFirstByFileKey(key: FileKey): FilesystemFileMetadata?

}